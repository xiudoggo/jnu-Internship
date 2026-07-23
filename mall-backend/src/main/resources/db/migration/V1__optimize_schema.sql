-- ============================================
-- 潮购电商平台 — 数据库优化迁移脚本
-- 执行方式: mysql -u root -p123456 mall < V1__optimize_schema.sql
-- ============================================
USE mall;

-- 1. 删除 cart 表冗余列
ALTER TABLE cart DROP COLUMN product_name;
ALTER TABLE cart DROP COLUMN product_image;
ALTER TABLE cart DROP COLUMN product_price;

-- 2. 删除 favorite 表冗余列
ALTER TABLE favorite DROP COLUMN product_name;
ALTER TABLE favorite DROP COLUMN product_image;
ALTER TABLE favorite DROP COLUMN product_price;

-- 3. 删除 product 表冗余列
ALTER TABLE product DROP COLUMN category_name;
ALTER TABLE product DROP COLUMN brand;

-- 4. 删除 orders 表冗余列 (JSON items 改为 order_item 表)
ALTER TABLE orders DROP COLUMN status_text;
ALTER TABLE orders DROP COLUMN items;

-- 5. 创建规范化 order_item 表 (仅保留必要属性)
CREATE TABLE IF NOT EXISTS order_item (
  id BIGINT AUTO_INCREMENT PRIMARY KEY,
  order_id BIGINT NOT NULL COMMENT '订单ID',
  product_id BIGINT NOT NULL COMMENT '商品ID',
  price DECIMAL(10,2) NOT NULL COMMENT '单价快照',
  quantity INT NOT NULL COMMENT '数量',
  total_price DECIMAL(10,2) NOT NULL COMMENT '小计',
  INDEX idx_order (order_id)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='订单项表';

-- 6. 删除多余的时间列
ALTER TABLE cart DROP COLUMN create_time;
ALTER TABLE favorite DROP COLUMN create_time;
ALTER TABLE mall_user DROP COLUMN create_time;
ALTER TABLE mall_user DROP COLUMN update_time;
ALTER TABLE product DROP COLUMN create_time;
ALTER TABLE product DROP COLUMN update_time;
ALTER TABLE category DROP COLUMN create_time;
ALTER TABLE category DROP COLUMN update_time;

-- 7. 删除评论相关
DROP TABLE IF EXISTS review;
ALTER TABLE product DROP COLUMN rating;
ALTER TABLE product DROP COLUMN review_count;
ALTER TABLE product DROP COLUMN deleted;

-- 8. 添加外键约束
ALTER TABLE cart ADD CONSTRAINT fk_cart_user FOREIGN KEY (user_id) REFERENCES mall_user(id);
ALTER TABLE cart ADD CONSTRAINT fk_cart_product FOREIGN KEY (product_id) REFERENCES product(id);
ALTER TABLE favorite ADD CONSTRAINT fk_favorite_user FOREIGN KEY (user_id) REFERENCES mall_user(id);
ALTER TABLE favorite ADD CONSTRAINT fk_favorite_product FOREIGN KEY (product_id) REFERENCES product(id);
ALTER TABLE product ADD CONSTRAINT fk_product_category FOREIGN KEY (category_id) REFERENCES category(id);
ALTER TABLE orders ADD CONSTRAINT fk_orders_user FOREIGN KEY (user_id) REFERENCES mall_user(id);
ALTER TABLE order_item ADD CONSTRAINT fk_order_item_order FOREIGN KEY (order_id) REFERENCES orders(id);
ALTER TABLE order_item ADD CONSTRAINT fk_order_item_product FOREIGN KEY (product_id) REFERENCES product(id);
