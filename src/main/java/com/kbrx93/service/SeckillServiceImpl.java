package com.kbrx93.service;

import com.kbrx93.dao.SeckillDao;
import com.kbrx93.dao.SuccessKilledDao;
import com.kbrx93.dao.cache.RedisDao;
import com.kbrx93.dto.Exposer;
import com.kbrx93.dto.SeckillExecution;
import com.kbrx93.entity.Seckill;
import com.kbrx93.entity.SuccessKilled;
import com.kbrx93.enums.SeckillStatEnum;
import com.kbrx93.exception.RepeatKillException;
import com.kbrx93.exception.SeckillCloseException;
import com.kbrx93.exception.SeckillException;
import org.apache.commons.collections.MapUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * SeckillService的实现类
 *
 * @author: kbrx93
 * @since 1.0.0
 */
@Service
public class SeckillServiceImpl implements SeckillService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SeckillServiceImpl.class);

    @Autowired
    private SeckillDao seckillDao;

    @Autowired
    private RedisDao redisDao;

    @Autowired
    private SuccessKilledDao successKilledDao;

    /**
     * 混淆字符串
     */
    private String slat = "werweriowucfjkj]]34823897*(&*&&^)&";

    @Override
    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0, 4);
    }

    @Override
    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    @Override
    public Exposer exportSeckillUrl(long seckillId) {
        //优化点:缓存优化:超时的基础上维护一致性
        //1。访问redi


        Seckill seckill = redisDao.getSeckill(seckillId);
        if (seckill == null) {
            //2.访问数据库
            seckill = seckillDao.queryById(seckillId);
            if (seckill == null) {
                //说明查不到这个秒杀产品的记录
                return new Exposer(false, seckillId);
            } else {
                //3,放入redis
                redisDao.putSeckill(seckill);
            }

        }

        // seckill为空则说明没有此秒杀对象
        if (seckill == null) {
            return new Exposer(false, seckillId);
        }
        Date startTime = seckill.getStartTime();
        Date endTime = seckill.getEndTime();
        Date nowTime = new Date();
        //还没开始或已结束
        if (nowTime.getTime() < startTime.getTime()
                || nowTime.getTime() > endTime.getTime()) {
            return new Exposer(false, seckillId, nowTime.getTime(), startTime.getTime(), endTime.getTime());
        }
        String md5 = getMd5(seckillId);
        return new Exposer(true, md5, seckill.getSeckillId());
    }

    private String getMd5(Long seckillId) {
        String base = seckillId + "/" + slat;
        String md5 = DigestUtils.md5DigestAsHex(base.getBytes());
        return md5;
    }

    @Override
    @Transactional(rollbackFor = {SeckillCloseException.class,
            RepeatKillException.class, SeckillException.class})
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException {
        if (md5 == null || !getMd5(seckillId).equals(md5)) {
            throw new SeckillException("seckill data rewrite");
        }
        try {
            // 执行秒杀逻辑：减库存+增加明细
            // 调整顺序：增加明细 ==> 减库存
            //记录购买行为
            int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
            if (insertCount <= 0) {
                //重复秒杀
                throw new RepeatKillException("seckill repeat");
            } else {
                int updateCount = seckillDao.reduceNumber(seckillId, new Date());
                if (updateCount <= 0) {
                    //没有更新记录
                    throw new SeckillCloseException("seckill close");
                } else {
                    SuccessKilled successKilled = successKilledDao.queryByIdWithSeckill(seckillId, userPhone);
                    return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, successKilled);
                }

            }


        } catch (SeckillCloseException e1) {
            throw e1;
        } catch (RepeatKillException e2) {
            throw e2;
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            throw new SeckillException("seckill inner exception" + e.getMessage());
        }
    }

    @Override
    public SeckillExecution executeSeckillByProcedure(long seckillId, long userPhone, String md5) {
        if (md5 == null || !getMd5(seckillId).equals(md5)) {
            throw new SeckillException("seckill data rewrite");
        }
        Date killTime = new Date();
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("seckillId", seckillId);
        map.put("phone", userPhone);
        map.put("killTime", killTime);
        map.put("result", null);
        //执行存储过程,result被复制
        try {
            seckillDao.killByProcedure(map);
            //获取result
            int result = MapUtils.getInteger(map, "result", -2);
            if (result == 1) {
                SuccessKilled sk = successKilledDao.
                        queryByIdWithSeckill(seckillId, userPhone);
                return new SeckillExecution(seckillId, SeckillStatEnum.SUCCESS, sk);
            } else {
                return new SeckillExecution(seckillId, SeckillStatEnum.stateOf(result));
            }
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
            return new SeckillExecution(seckillId, SeckillStatEnum.INNER_ERROR);

        }
    }
}
