-- 创建数据库
CREATE DATABASE IF NOT EXISTS seckill_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;

USE seckill_db;

-- 用户表
CREATE TABLE IF NOT EXISTS `tb_user` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL COMMENT '用户名',
    `password` VARCHAR(100) NOT NULL COMMENT '密码',
    `nickname` VARCHAR(50) DEFAULT NULL COMMENT '昵称',
    `phone` VARCHAR(20) DEFAULT NULL COMMENT '手机号',
    `email` VARCHAR(50) DEFAULT NULL COMMENT '邮箱',
    `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像',
    `status` TINYINT(1) DEFAULT 0 COMMENT '状态（0正常 1禁用）',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除（0未删除 1已删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_username` (`username`),
    KEY `idx_phone` (`phone`),
    KEY `idx_email` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 商品表
CREATE TABLE IF NOT EXISTS `tb_product` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '商品ID',
    `name` VARCHAR(100) NOT NULL COMMENT '商品名称',
    `description` TEXT COMMENT '商品描述',
    `image` VARCHAR(255) DEFAULT NULL COMMENT '商品图片',
    `price` DECIMAL(10,2) NOT NULL COMMENT '商品价格',
    `stock` INT(11) NOT NULL DEFAULT 0 COMMENT '库存数量',
    `version` INT(11) NOT NULL DEFAULT 0 COMMENT '版本号（乐观锁）',
    `sales` INT(11) DEFAULT 0 COMMENT '销量',
    `status` TINYINT(1) DEFAULT 1 COMMENT '状态（0下架 1上架）',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除（0未删除 1已删除）',
    PRIMARY KEY (`id`),
    KEY `idx_name` (`name`),
    KEY `idx_status` (`status`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- 秒杀活动表
CREATE TABLE IF NOT EXISTS `tb_seckill_activity` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '活动ID',
    `product_id` BIGINT(20) NOT NULL COMMENT '商品ID',
    `seckill_price` DECIMAL(10,2) NOT NULL COMMENT '秒杀价格',
    `seckill_stock` INT(11) NOT NULL COMMENT '秒杀库存',
    `version` INT(11) NOT NULL DEFAULT 0 COMMENT '版本号（乐观锁）',
    `start_time` DATETIME NOT NULL COMMENT '开始时间',
    `end_time` DATETIME NOT NULL COMMENT '结束时间',
    `status` TINYINT(1) DEFAULT 0 COMMENT '状态（0未开始 1进行中 2已结束）',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除（0未删除 1已删除）',
    PRIMARY KEY (`id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_start_time` (`start_time`),
    KEY `idx_end_time` (`end_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='秒杀活动表';

-- 订单表
CREATE TABLE IF NOT EXISTS `tb_order` (
    `id` BIGINT(20) NOT NULL AUTO_INCREMENT COMMENT '订单ID',
    `order_no` VARCHAR(50) NOT NULL COMMENT '订单号',
    `user_id` BIGINT(20) NOT NULL COMMENT '用户ID',
    `product_id` BIGINT(20) NOT NULL COMMENT '商品ID',
    `activity_id` BIGINT(20) DEFAULT NULL COMMENT '秒杀活动ID',
    `product_name` VARCHAR(100) NOT NULL COMMENT '商品名称',
    `product_image` VARCHAR(255) DEFAULT NULL COMMENT '商品图片',
    `quantity` INT(11) NOT NULL DEFAULT 1 COMMENT '购买数量',
    `price` DECIMAL(10,2) NOT NULL COMMENT '商品单价',
    `total_amount` DECIMAL(10,2) NOT NULL COMMENT '订单总价',
    `status` TINYINT(1) DEFAULT 0 COMMENT '订单状态（0待支付 1已支付 2已取消 3已完成）',
    `pay_time` DATETIME DEFAULT NULL COMMENT '支付时间',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
    `deleted` TINYINT(1) DEFAULT 0 COMMENT '是否删除（0未删除 1已删除）',
    PRIMARY KEY (`id`),
    UNIQUE KEY `uk_order_no` (`order_no`),
    KEY `idx_user_id` (`user_id`),
    KEY `idx_product_id` (`product_id`),
    KEY `idx_activity_id` (`activity_id`),
    KEY `idx_create_time` (`create_time`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- 插入测试数据

-- 插入测试用户（密码：123456，已加密）
INSERT INTO `tb_user` (`username`, `password`, `nickname`, `phone`, `email`) VALUES
('admin', 'e10adc3949ba59abbe56e057f20f883e', '管理员', '13800138000', 'admin@seckill.com'),
('user1', 'e10adc3949ba59abbe56e057f20f883e', '用户1', '13800138001', 'user1@seckill.com'),
('user2', 'e10adc3949ba59abbe56e057f20f883e', '用户2', '13800138002', 'user2@seckill.com');

-- 插入测试商品
INSERT INTO `tb_product` (`name`, `description`, `image`, `price`, `stock`, `sales`) VALUES
('iPhone 15 Pro', '苹果最新旗舰手机，搭载A17 Pro芯片', 'https://example.com/iphone15.jpg', 7999.00, 1000, 0),
('小米14', '骁龙8 Gen3处理器，徕卡光学镜头', 'https://example.com/mi14.jpg', 3999.00, 2000, 0),
('华为Mate 60 Pro', '麒麟9000S芯片，卫星通信', 'https://example.com/mate60.jpg', 6999.00, 1500, 0),
('MacBook Pro 16', 'M3 Max芯片，16GB内存', 'https://example.com/macbook.jpg', 19999.00, 500, 0),
('AirPods Pro 2', '主动降噪无线耳机', 'https://example.com/airpods.jpg', 1899.00, 3000, 0);

-- 插入秒杀活动
INSERT INTO `tb_seckill_activity` (`product_id`, `seckill_price`, `seckill_stock`, `start_time`, `end_time`, `status`) VALUES
(1, 6999.00, 100, '2024-12-01 10:00:00', '2024-12-31 23:59:59', 1),
(2, 2999.00, 200, '2024-12-01 10:00:00', '2024-12-31 23:59:59', 1),
(3, 5999.00, 150, '2024-12-01 10:00:00', '2024-12-31 23:59:59', 1),
(4, 17999.00, 50, '2024-12-01 10:00:00', '2024-12-31 23:59:59', 1),
(5, 1499.00, 300, '2024-12-01 10:00:00', '2024-12-31 23:59:59', 1);

