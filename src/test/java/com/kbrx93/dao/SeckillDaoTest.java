package com.kbrx93.dao;

import com.kbrx93.entity.Seckill;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.Date;
import java.util.List;

/**
 * SeckillDao测试类
 *
 * @author: kbrx93
 * @since 1.0.0
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({"classpath:spring/spring-dao.xml"})
public class SeckillDaoTest {

    @Autowired
    private SeckillDao dao;

    @Test
    public void reduceNumber() {
        Date killedTime = new Date();
        long seckillId = 1000L;
        int i = dao.reduceNumber(seckillId, killedTime);
        System.out.println("i = " + i);
    }

    @Test
    public void queryById() {
        long seckillId = 1000L;
        Seckill seckill = dao.queryById(seckillId);
        System.out.println("seckill = " + seckill);
    }

    @Test
    public void queryAll() {
        List<Seckill> seckills = dao.queryAll(0, 100);
        for (Seckill seckill : seckills) {
            System.out.println("------------------------------------------");
            System.out.println("seckill = " + seckill);
        }
    }
}