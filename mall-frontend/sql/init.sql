-- ============================================
-- 潮购电商平台 — 数据库初始化脚本
-- ============================================

CREATE DATABASE IF NOT EXISTS mall DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE mall;

-- ============================================
-- 1. 商城用户表
-- ============================================
DROP TABLE IF EXISTS mall_user;
CREATE TABLE mall_user (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  phone VARCHAR(20) NOT NULL UNIQUE COMMENT '手机号',
  nickname VARCHAR(50) DEFAULT '' COMMENT '昵称',
  password VARCHAR(128) NOT NULL COMMENT 'BCrypt 加密密码',
  avatar VARCHAR(255) DEFAULT '' COMMENT '头像URL',
  email VARCHAR(100) DEFAULT '' COMMENT '邮箱',
  role INT DEFAULT 0 COMMENT '角色 0=普通用户 1=管理员'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商城用户表';

-- ============================================
-- 2. 商品分类表（树形结构）
-- ============================================
DROP TABLE IF EXISTS category;
CREATE TABLE category (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(50) NOT NULL COMMENT '分类名称',
  icon VARCHAR(50) DEFAULT '' COMMENT '图标',
  sort INT DEFAULT 0 COMMENT '排序',
  parent_id BIGINT DEFAULT 0 COMMENT '父级ID，0=一级分类'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品分类表';

-- ============================================
-- 3. 商品表
-- ============================================
DROP TABLE IF EXISTS product;
CREATE TABLE product (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  name VARCHAR(200) NOT NULL COMMENT '商品名称',
  price DECIMAL(10,2) NOT NULL COMMENT '售价',
  original_price DECIMAL(10,2) DEFAULT NULL COMMENT '原价',
  cover_image VARCHAR(500) DEFAULT '' COMMENT '封面图',
  images TEXT COMMENT '详情图JSON数组',
  description VARCHAR(500) DEFAULT '' COMMENT '简介',
  stock INT DEFAULT 0 COMMENT '库存',
  sales INT DEFAULT 0 COMMENT '销量',
  is_hot TINYINT DEFAULT 0 COMMENT '是否热门',
  is_new TINYINT DEFAULT 0 COMMENT '是否新品',
  category_id BIGINT NOT NULL COMMENT '分类ID',
  status TINYINT DEFAULT 1 COMMENT '状态 1上架 0下架',
  INDEX idx_category (category_id),
  INDEX idx_hot (is_hot),
  INDEX idx_new (is_new)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='商品表';

-- ============================================
-- 4. 购物车表
-- ============================================
DROP TABLE IF EXISTS cart;
CREATE TABLE cart (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL COMMENT '用户ID',
  product_id BIGINT NOT NULL COMMENT '商品ID',
  quantity INT DEFAULT 1 COMMENT '数量',
  selected TINYINT DEFAULT 1 COMMENT '是否选中',
  INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物车表';

-- ============================================
-- 5. 订单表
-- ============================================
DROP TABLE IF EXISTS orders;
CREATE TABLE orders (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_no VARCHAR(32) NOT NULL UNIQUE COMMENT '订单编号',
  user_id BIGINT NOT NULL COMMENT '用户ID',
  total_amount DECIMAL(10,2) NOT NULL COMMENT '总金额',
  pay_amount DECIMAL(10,2) NOT NULL COMMENT '实付金额',
  status TINYINT DEFAULT 0 COMMENT '0待支付 1已支付 2已发货 3已完成 4已取消',
  receiver_name VARCHAR(50) DEFAULT '' COMMENT '收件人',
  receiver_phone VARCHAR(20) DEFAULT '' COMMENT '收件人电话',
  receiver_address VARCHAR(255) DEFAULT '' COMMENT '收货地址',
  create_time DATETIME DEFAULT CURRENT_TIMESTAMP,
  pay_time DATETIME DEFAULT NULL COMMENT '支付时间',
  delivery_time DATETIME DEFAULT NULL COMMENT '发货时间',
  complete_time DATETIME DEFAULT NULL COMMENT '完成时间',
  INDEX idx_user (user_id),
  INDEX idx_status (status)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单表';

-- ============================================
-- 6. 订单项表
-- ============================================
DROP TABLE IF EXISTS order_item;
CREATE TABLE order_item (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_id BIGINT NOT NULL COMMENT '订单ID',
  product_id BIGINT NOT NULL COMMENT '商品ID',
  price DECIMAL(10,2) NOT NULL COMMENT '单价快照',
  quantity INT NOT NULL COMMENT '数量',
  total_price DECIMAL(10,2) NOT NULL COMMENT '小计',
  INDEX idx_order (order_id),
  CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES orders(id),
  CONSTRAINT fk_order_item_product FOREIGN KEY (product_id) REFERENCES product(id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单项表';

-- ============================================
-- 7. 轮播图表
-- ============================================
DROP TABLE IF EXISTS banner;
CREATE TABLE banner (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  title VARCHAR(100) DEFAULT '' COMMENT '标题',
  image_url VARCHAR(500) NOT NULL COMMENT '图片URL',
  link_url VARCHAR(500) DEFAULT '' COMMENT '跳转链接',
  sort INT DEFAULT 0 COMMENT '排序',
  status TINYINT DEFAULT 1 COMMENT '状态'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='轮播图表';

-- ============================================
-- 8. 收藏表
-- ============================================
DROP TABLE IF EXISTS favorite;
CREATE TABLE favorite (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  user_id BIGINT NOT NULL COMMENT '用户ID',
  product_id BIGINT NOT NULL COMMENT '商品ID',
  UNIQUE KEY uk_user_product (user_id, product_id),
  INDEX idx_user (user_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='收藏表';

-- ============================================
-- 种子数据
-- 注意：密码需为 BCrypt 加密值，生产环境请通过注册接口生成
-- 以下 hash 对应明文密码 123456
-- ============================================

-- 测试用户（role: 1=管理员, 0=普通用户）
INSERT INTO mall_user (phone, nickname, password, avatar, email, role) VALUES
('13800000001', '小明', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'https://picsum.photos/seed/avatar1/100/100', '', 1),
('13800000002', '购物达人', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', 'https://picsum.photos/seed/avatar2/100/100', '', 0);

-- 分类数据
INSERT INTO category (id, name, icon, sort, parent_id) VALUES
(1, '数码电子', 'Monitor', 1, 0),
(2, '潮流服饰', 'Present', 2, 0),
(3, '美妆护肤', 'MagicStick', 3, 0),
(4, '食品饮料', 'Food', 4, 0),
(5, '家居生活', 'House', 5, 0),
(6, '运动户外', 'Bicycle', 6, 0),
(11, '手机', '', 1, 1),
(12, '电脑', '', 2, 1),
(13, '耳机音箱', '', 3, 1),
(21, '男装', '', 1, 2),
(22, '女装', '', 2, 2),
(31, '面部护理', '', 1, 3),
(32, '彩妆', '', 2, 3),
(41, '休闲零食', '', 1, 4),
(42, '饮料冲调', '', 2, 4),
(51, '家具', '', 1, 5),
(52, '家纺', '', 2, 5),
(61, '运动装备', '', 1, 6),
(62, '户外用品', '', 2, 6);

-- 商品数据（列顺序与建表顺序一致）
INSERT INTO product (name, price, original_price, cover_image, images, description, stock, sales, is_hot, is_new, category_id) VALUES
('华为Mate 60 Pro 智能手机', 6999.00, 7999.00, 'https://picsum.photos/seed/prod1/400/400', '["https://picsum.photos/seed/detail1a/800/800","https://picsum.photos/seed/detail1b/800/800","https://picsum.photos/seed/detail1c/800/800"]', '2024新款旗舰手机，卫星通话，超强影像', 120, 3560, 1, 1, 11),
('小米14 Ultra 徕卡影像', 5999.00, 6499.00, 'https://picsum.photos/seed/prod2/400/400', '["https://picsum.photos/seed/detail2a/800/800","https://picsum.photos/seed/detail2b/800/800"]', '徕卡专业影像，骁龙8Gen3处理器', 85, 2890, 1, 1, 11),
('iPhone 16 Pro Max', 9999.00, 10999.00, 'https://picsum.photos/seed/prod3/400/400', '["https://picsum.photos/seed/detail3a/800/800","https://picsum.photos/seed/detail3b/800/800"]', 'A18 Pro芯片，钛金属设计', 60, 5120, 1, 1, 11),
('华为MateBook X Pro', 8999.00, 9999.00, 'https://picsum.photos/seed/prod4/400/400', '["https://picsum.photos/seed/detail4a/800/800"]', '轻薄商务本，3K OLED触控屏', 45, 1890, 1, 0, 12),
('小米笔记本电脑Pro', 5499.00, 6299.00, 'https://picsum.photos/seed/prod5/400/400', '["https://picsum.photos/seed/detail5a/800/800"]', '12代酷睿i7，2.5K高清屏', 70, 2340, 0, 1, 12),
('AirPods Pro 2代', 1799.00, 1999.00, 'https://picsum.photos/seed/prod6/400/400', '["https://picsum.photos/seed/detail6a/800/800","https://picsum.photos/seed/detail6b/800/800"]', '主动降噪，自适应透明模式', 200, 6780, 1, 0, 13),
('华为FreeBuds Pro 3', 1399.00, 1599.00, 'https://picsum.photos/seed/prod7/400/400', '["https://picsum.photos/seed/detail7a/800/800"]', '星闪连接，超CD级音质', 150, 4320, 1, 1, 13),
('Nike Air Max 270 男款跑鞋', 899.00, 1199.00, 'https://picsum.photos/seed/prod8/400/400', '["https://picsum.photos/seed/detail8a/800/800","https://picsum.photos/seed/detail8b/800/800"]', '经典气垫跑鞋，舒适透气', 90, 2150, 1, 0, 21),
('Adidas 三叶草卫衣', 459.00, 599.00, 'https://picsum.photos/seed/prod9/400/400', '["https://picsum.photos/seed/detail9a/800/800"]', '经典三条纹设计，纯棉面料', 180, 3450, 1, 1, 21),
('ZARA 女士连衣裙', 359.00, 499.00, 'https://picsum.photos/seed/prod10/400/400', '["https://picsum.photos/seed/detail10a/800/800"]', '法式复古碎花，夏季新款', 110, 1890, 1, 0, 22),
('兰蔻菁纯面霜 60ml', 2699.00, 2999.00, 'https://picsum.photos/seed/prod11/400/400', '["https://picsum.photos/seed/detail11a/800/800","https://picsum.photos/seed/detail11b/800/800"]', '抗老修护，紧致淡纹', 40, 1670, 1, 1, 31),
('雅诗兰黛DW持妆粉底液', 420.00, 480.00, 'https://picsum.photos/seed/prod12/400/400', '["https://picsum.photos/seed/detail12a/800/800"]', '24小时持妆，控油遮瑕', 130, 4560, 0, 1, 32),
('三只松鼠坚果大礼包 1888g', 128.00, 169.00, 'https://picsum.photos/seed/prod13/400/400', '["https://picsum.photos/seed/detail13a/800/800"]', '12袋混合装，年货送礼必备', 300, 9870, 1, 0, 41),
('星巴克咖啡豆 1kg', 198.00, 238.00, 'https://picsum.photos/seed/prod14/400/400', '["https://picsum.photos/seed/detail14a/800/800"]', '中度烘焙，阿拉比卡咖啡豆', 95, 3210, 0, 0, 42),
('MUJI风实木书架', 599.00, 799.00, 'https://picsum.photos/seed/prod15/400/400', '["https://picsum.photos/seed/detail15a/800/800","https://picsum.photos/seed/detail15b/800/800"]', '简约设计，多层收纳', 25, 890, 1, 0, 51),
('水星家纺四件套', 359.00, 599.00, 'https://picsum.photos/seed/prod16/400/400', '["https://picsum.photos/seed/detail16a/800/800"]', '100%纯棉，亲肤柔软', 160, 4560, 0, 1, 52),
('Nike Dri-FIT 训练T恤', 259.00, 329.00, 'https://picsum.photos/seed/prod17/400/400', '["https://picsum.photos/seed/detail17a/800/800"]', '速干面料，运动必备', 210, 5430, 1, 0, 61),
('探路者登山背包 40L', 329.00, 459.00, 'https://picsum.photos/seed/prod18/400/400', '["https://picsum.photos/seed/detail18a/800/800","https://picsum.photos/seed/detail18b/800/800"]', '防水耐磨，户外徒步专用', 55, 1230, 0, 1, 62),
('戴森V15无线吸尘器', 4990.00, 5490.00, 'https://picsum.photos/seed/prod19/400/400', '["https://picsum.photos/seed/detail19a/800/800"]', '激光探测微尘，强劲吸力', 20, 2340, 1, 1, 51),
('SK-II神仙水 230ml', 1370.00, 1590.00, 'https://picsum.photos/seed/prod20/400/400', '["https://picsum.photos/seed/detail20a/800/800"]', 'PITERA精华，改善肤质', 65, 6780, 1, 0, 31);

-- 轮播图数据
INSERT INTO banner (title, image_url, link_url, sort) VALUES
('618年中大促', 'https://picsum.photos/seed/banner1/1200/400', '/products', 1),
('数码新品首发', 'https://picsum.photos/seed/banner2/1200/400', '/products?categoryId=11', 2),
('夏季穿搭指南', 'https://picsum.photos/seed/banner3/1200/400', '/products?categoryId=21', 3);
