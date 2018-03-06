# 删除数据库
DROP DATABASE IF EXISTS seckill;

# 创建新数据库
CREATE DATABASE seckill;

# 使用对应数据库
USE seckill;

# 创建秒杀表
CREATE TABLE seckill (
  `seckill_id`  BIGINT       NOT NULL AUTO_INCREMENT
  COMMENT '商品库存ID',
  `name`        VARCHAR(120) NOT NULL
  COMMENT '秒杀商品名称',
  `number`      INT          NOT NULL
  COMMENT '库存数量',
  `start_time`  TIMESTAMP    NOT NULL
  COMMENT '秒杀开始时间',
  end_time    TIMESTAMP    NOT NULL
  COMMENT '秒杀结束时间',
  `create_time` TIMESTAMP    NOT NULL DEFAULT current_timestamp
  COMMENT '创建时间',
  PRIMARY KEY (seckill_id),
  KEY idx_start_time(start_time),
  KEY idx_end_time(end_time),
  KEY idx_create_time(create_time)
)
  ENGINE = InnoDB
  AUTO_INCREMENT = 1000
  DEFAULT CHARSET = UTF8
  COMMENT = '秒杀表';

# 初始数据
INSERT INTO seckill (name, number, start_time, end_time)
VALUES
  ('1000元秒杀iphone6', 100, '2018-02-17 00:00:00', '2018-02-18 00:00:00'),
  ('800元秒杀ipad', 200, '2018-02-17 00:00:00', '2018-02-18 00:00:00'),
  ('6600元秒杀mac book pro', 300, '2018-02-17 00:00:00', '2018-02-18 00:00:00'),
  ('7000元秒杀iMac', 400, '2018-02-17 00:00:00', '2018-02-18 00:00:00');

# 秒杀成功明细表
# 用户登录认证相关信息(简化为手机号)
CREATE TABLE success_killed (
  `seckill_id` BIGINT NOT NULL COMMENT '秒杀商品ID',
  `user_phone` BIGINT NOT NULL COMMENT '用户电话号码',
  `state` TINYINT NOT NULL COMMENT '状态标识:-1:无效 0:成功 1:已付款 2:已发货',
  `create_time` TIMESTAMP NOT NULL COMMENT '创建时间',
  PRIMARY KEY (seckill_id, user_phone), # 联合主键
  KEY idx_create_time(create_time)
) ENGINE = InnoDB DEFAULT CHARSET = UTF8 COMMENT = '秒杀成功明细表';