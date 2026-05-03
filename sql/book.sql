-- =============================================
-- 二手图书交易平台 - 数据库初始化脚本
-- 适用数据库：MySQL 5.7 / 8.0
-- 字符集：utf8mb4
-- =============================================

-- 1. 创建数据库（如果还没建的话）
CREATE DATABASE IF NOT EXISTS `book_trading_platform` 
DEFAULT CHARACTER SET utf8mb4 
COLLATE utf8mb4_unicode_ci;

USE `book_trading_platform`;

-- 2. 用户表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '用户ID',
  `username` VARCHAR(50) NOT NULL COMMENT '登录账号',
  `password` VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
  `phone` VARCHAR(11) DEFAULT NULL COMMENT '手机号',
  `avatar` VARCHAR(255) DEFAULT NULL COMMENT '头像URL',
  `role` TINYINT DEFAULT 0 COMMENT '角色：0-普通用户，1-管理员',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-禁用，1-正常',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '注册时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 默认管理员账号（用户名：admin  密码：admin123）
INSERT INTO `user` (`username`, `password`, `role`, `status`) VALUES
('admin', '$2b$10$arHQu44D6SFxw9Dl/EJON.E0j.4m1pylqSvLV3xF/4e2TJdkp5tLC', 1, 1);

-- 3. 图书分类表
DROP TABLE IF EXISTS `book_category`;
CREATE TABLE `book_category` (
  `id` INT NOT NULL AUTO_INCREMENT COMMENT '分类ID',
  `name` VARCHAR(50) NOT NULL COMMENT '分类名称',
  `sort_order` INT DEFAULT 0 COMMENT '排序权重',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书分类表';

-- 初始化一些默认分类
INSERT INTO `book_category` (`name`, `sort_order`) VALUES 
('计算机/软件', 1),
('文学/小说', 2),
('考研/教辅', 3),
('外语/出国', 4),
('经管/社科', 5),
('生活/其他', 6);

-- 4. 图书商品表
DROP TABLE IF EXISTS `book`;
CREATE TABLE `book` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '图书ID',
  `title` VARCHAR(100) NOT NULL COMMENT '书名',
  `author` VARCHAR(50) DEFAULT NULL COMMENT '作者',
  `isbn` VARCHAR(20) DEFAULT NULL COMMENT 'ISBN号',
  `category_id` INT DEFAULT NULL COMMENT '分类ID',
  `original_price` DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
  `price` DECIMAL(10,2) NOT NULL COMMENT '售价',
  `condition` TINYINT DEFAULT 1 COMMENT '新旧程度：1-全新，2-九五新，3-九新，4-八新，5-较旧',
  `description` TEXT COMMENT '描述/卖点',
  `cover_image` VARCHAR(255) DEFAULT NULL COMMENT '封面图片URL',
  `images` TEXT DEFAULT NULL COMMENT '多图JSON或逗号分隔',
  `seller_id` BIGINT NOT NULL COMMENT '卖家ID',
  `status` TINYINT DEFAULT 1 COMMENT '状态：0-已下架，1-在售，2-已售出',
  `view_count` INT DEFAULT 0 COMMENT '浏览量',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '发布时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  KEY `idx_category_id` (`category_id`),
  KEY `idx_seller_id` (`seller_id`),
  KEY `idx_title` (`title`),
  KEY `idx_status_create_time` (`status`, `create_time`),
  KEY `idx_seller_status` (`seller_id`, `status`),
  FULLTEXT KEY `ft_title_author` (`title`, `author`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='图书商品表';

-- 5. 购物车表
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '购物车项ID',
  `user_id` BIGINT NOT NULL COMMENT '用户ID',
  `book_id` BIGINT NOT NULL COMMENT '图书ID',
  `quantity` INT DEFAULT 1 COMMENT '数量',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '加入时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_user_book` (`user_id`, `book_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- 6. 订单表
DROP TABLE IF EXISTS `order_info`;
CREATE TABLE `order_info` (
  `id` BIGINT NOT NULL AUTO_INCREMENT COMMENT '订单ID',
  `order_no` VARCHAR(32) NOT NULL COMMENT '订单号（唯一）',
  `buyer_id` BIGINT NOT NULL COMMENT '买家ID',
  `seller_id` BIGINT NOT NULL COMMENT '卖家ID',
  `book_id` BIGINT NOT NULL COMMENT '图书ID',
  `book_title` VARCHAR(100) DEFAULT NULL COMMENT '快照：书名',
  `book_price` DECIMAL(10,2) DEFAULT NULL COMMENT '快照：成交单价',
  `quantity` INT DEFAULT 1 COMMENT '数量',
  `total_amount` DECIMAL(10,2) NOT NULL COMMENT '总金额',
  `status` TINYINT DEFAULT 0 COMMENT '状态：0-待付款，1-已付款待发货，2-已发货，3-已完成，4-已取消',
  `remark` VARCHAR(255) DEFAULT NULL COMMENT '买家备注',
  `payment_time` DATETIME DEFAULT NULL COMMENT '付款时间',
  `complete_time` DATETIME DEFAULT NULL COMMENT '完成时间',
  `create_time` DATETIME DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_order_no` (`order_no`),
  KEY `idx_buyer_id` (`buyer_id`),
  KEY `idx_seller_id` (`seller_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';