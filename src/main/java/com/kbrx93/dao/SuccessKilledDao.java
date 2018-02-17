package com.kbrx93.dao;

import com.kbrx93.entity.SuccessKilled;
import org.apache.ibatis.annotations.Param;

/**
 * 秒杀成功明细Dao接口
 *
 * @author: kbrx93
 * @since 1.0.0
 */
public interface SuccessKilledDao {

    /**
     * 插入成功秒杀数据
     * @param seckillId
     * @param userPhone
     * @return
     */
    int insertSuccessKilled(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);

    /**
     * 通过Id查询带秒杀商品对象的明细记录
     * @param seckillId
     * @return
     */
    SuccessKilled queryByIdWithSeckill(@Param("seckillId") long seckillId, @Param("userPhone") long userPhone);
}
