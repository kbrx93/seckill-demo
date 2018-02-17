package com.kbrx93.dao;

import com.kbrx93.entity.SuccessKilled;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * SuccessKilledDao的测试类
 *
 * @author: kbrx93
 * @since 1.0.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("classpath:spring/spring-dao.xml")
public class SuccessKilledDaoTest {

    @Autowired
    private SuccessKilledDao dao;

    @Test
    public void insertSuccessKilled() {
        long seckillId = 1000L;
        long userPhone = 15521296666L;
        int i = dao.insertSuccessKilled(seckillId, userPhone);
        System.out.println("i = " + i);
    }

    @Test
    public void queryByIdWithSeckill() {
        long seckillId = 1000L;
        long userPhone = 15521296666L;
        SuccessKilled successKilled = dao.queryByIdWithSeckill(seckillId, userPhone);
        System.out.println(successKilled);
    }
}