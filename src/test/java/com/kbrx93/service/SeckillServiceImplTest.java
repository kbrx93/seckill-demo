package com.kbrx93.service;

import com.kbrx93.dto.Exposer;
import com.kbrx93.dto.SeckillExecution;
import com.kbrx93.entity.Seckill;
import com.kbrx93.exception.RepeatKillException;
import com.kbrx93.exception.SeckillCloseException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;

/**
 *
 *
 * @author: kbrx93
 * @since 1.0.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({
        "classpath:spring/spring-dao.xml",
        "classpath:spring/spring-service.xml"
})
public class SeckillServiceImplTest {

    private final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private SeckillService seckillService;

    @Test
    public void getSeckillList() {
        List<Seckill> seckillList = seckillService.getSeckillList();
        for (Seckill seckill : seckillList) {
            logger.info(seckill.toString());
        }
    }

    @Test
    public void getById() {
        long seckillId = 1000L;
        Seckill seckill = seckillService.getById(seckillId);
        logger.info(seckill.toString());
    }

    @Test//完整逻辑代码测试，注意可重复执行
    public void testSeckillLogic() {
        long seckillId = 1000;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        if (exposer.isExposed()) {

            System.out.println(exposer);

            long userPhone = 13476191876L;
            String md5 = exposer.getMd5();

            try {
                SeckillExecution seckillExecution = seckillService.executeSeckill(seckillId, userPhone, md5);
                System.out.println(seckillExecution);
            } catch (RepeatKillException e) {
                e.printStackTrace();
            } catch (SeckillCloseException e1) {
                e1.printStackTrace();
            }
        } else {
            //秒杀未开启
            logger.warn(exposer.toString());
        }
    }

    @Test
    public void executeSeckillProcedure() {
        long seckillId = 1001;
        long phone = 1368011101;
        Exposer exposer = seckillService.exportSeckillUrl(seckillId);
        if (exposer.isExposed()) {
            String md5 = exposer.getMd5();
            SeckillExecution execution = seckillService.executeSeckillByProcedure(seckillId, phone, md5);
            logger.info(execution.getStateInfo());
        }
    }
}