package com.kbrx93.entity;

import java.util.Date;

/**
 * 秒杀成功明细实体
 *
 * @author: kbrx93
 * @since 1.0.0
 */
public class SuccessKilled {

    /**
     * 秒杀商品ID
     */
    private long seckillId;

    /**
     * 用户号码
     */
    private long userPhone;

    /**
     * 商品状态
     */
    private short state;

    /**
     * 创建时间
     */
    private Date createTime;

    /**
     * 记录对应的秒杀商品
     */
    private Seckill seckill;

    public long getSeckillId() {
        return seckillId;
    }

    public void setSeckillId(long seckillId) {
        this.seckillId = seckillId;
    }

    public long getUserPhone() {
        return userPhone;
    }

    public void setUserPhone(long userPhone) {
        this.userPhone = userPhone;
    }

    public short getState() {
        return state;
    }

    public void setState(short state) {
        this.state = state;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public Seckill getSeckill() {
        return seckill;
    }

    public void setSeckill(Seckill seckill) {
        this.seckill = seckill;
    }

    @Override
    public String toString() {
        return "SuccessKilled{" +
                "seckillId=" + seckillId +
                ", userPhone=" + userPhone +
                ", state=" + state +
                ", createTime=" + createTime +
                ", seckill=" + seckill +
                '}';
    }
}
