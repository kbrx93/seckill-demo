package com.kbrx93.service;

import com.kbrx93.dao.SeckillDao;
import com.kbrx93.dao.SuccessKilledDao;
import com.kbrx93.dto.Exposer;
import com.kbrx93.dto.SeckillExecution;
import com.kbrx93.entity.Seckill;
import com.kbrx93.entity.SuccessKilled;
import com.kbrx93.enums.SeckillStatEnum;
import com.kbrx93.exception.RepeatKillException;
import com.kbrx93.exception.SeckillCloseException;
import com.kbrx93.exception.SeckillException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.Date;
import java.util.List;

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
    private SuccessKilledDao successKilledDao;

    /**
     * 混淆字符串
     */
    private String slat = "werweriowucfjkj]]34823897*(&*&&^)&";

    public List<Seckill> getSeckillList() {
        return seckillDao.queryAll(0, 4);
    }

    public Seckill getById(long seckillId) {
        return seckillDao.queryById(seckillId);
    }

    public Exposer exportSeckillUrl(long seckillId) {
        Seckill seckill = seckillDao.queryById(seckillId);

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

    @Transactional
    public SeckillExecution executeSeckill(long seckillId, long userPhone, String md5) throws SeckillException {
        if (md5 == null || !getMd5(seckillId).equals(md5)) {
            throw new SeckillException("seckill data rewrite");
        }
        try {
            // 执行秒杀逻辑：减库存+增加明细
            int updateCount = seckillDao.reduceNumber(seckillId, new Date());
            if (updateCount <= 0) {
                //没有更新记录
                throw new SeckillCloseException("seckill close");
            } else {
                //记录购买行为
                int insertCount = successKilledDao.insertSuccessKilled(seckillId, userPhone);
                if (insertCount <= 0) {
                    //重复秒杀
                    throw new RepeatKillException("seckill repeat");
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
}