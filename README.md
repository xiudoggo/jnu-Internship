# 潮购电商平台（AI + 低代码协同）

基于 Spring Boot 3 + Vue 3 的前后端分离电商平台，融入 AI 辅助开发和低代码开发模式。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端框架 | Spring Boot 3.2.5 / Spring MVC |
| ORM | MyBatis-Plus 3.5.7 |
| 数据库 | MySQL 8 |
| 认证 | JWT (jjwt 0.11.5) + HttpOnly Cookie |
| 接口文档 | Knife4j 4.5.0 (OpenAPI 3.0) |
| 前端框架 | Vue 3 + Vite |
| UI 组件 | Element Plus |
| 状态管理 | Pinia |
| 构建工具 | Maven (后端) / npm (前端) |
| Java 版本 | JDK 17 |

## 项目结构

```
shixun/
├── mall-backend/                     # 后端 Spring Boot 工程
│   ├── src/main/java/com/mall/backend/
│   │   ├── MallBackendApplication.java
│   │   ├── config/                   # 配置（CORS / Knife4j / 异常处理 / 请求日志）
│   │   ├── controller/               # 控制器（用户端 8 + 管理端 3）
│   │   │   ├── BannerController      # 轮播图
│   │   │   ├── CartController        # 购物车
│   │   │   ├── CategoryController    # 分类
│   │   │   ├── FavoriteController    # 收藏
│   │   │   ├── OrderController       # 订单（用户端）
│   │   │   ├── ProductController     # 商品（用户端）
│   │   │   ├── SearchController      # 搜索
│   │   │   ├── UserController        # 用户（登录/注册/信息）
│   │   │   ├── AdminProductController  # 管理端-商品管理
│   │   │   ├── AdminOrderController    # 管理端-订单管理
│   │   │   └── AdminUserController     # 管理端-用户管理
│   │   ├── dto/                      # 数据传输对象（Result / PageResult）
│   │   ├── entity/                   # 数据库实体（8 个）
│   │   ├── enums/                    # 枚举（OrderStatus）
│   │   ├── mapper/                   # MyBatis 接口 + XML
│   │   └── security/                 # JWT / 密码加密 / 权限认证
│   └── src/main/resources/
│       ├── application.properties
│       ├── mapper/ProductMapper.xml
│       └── db/migration/             # 数据库迁移脚本
│
├── mall-frontend/                    # 前端 Vue 3 工程
│   ├── sql/
│   │   └── init.sql                  # 数据库初始化脚本（含种子数据）
│   ├── src/
│   │   ├── views/                    # 用户端页面
│   │   │   └── admin/               # 管理端页面
│   │   ├── components/               # 公共组件
│   │   ├── router/                   # 路由
│   │   ├── stores/                   # Pinia 状态管理
│   │   └── api/                      # Axios 接口封装
│   └── ...
│
├── .gitignore
└── README.md
```

## 功能模块

### 用户端
- [x] 注册 / 登录（JWT + Cookie）
- [x] 首页轮播图
- [x] 商品列表（分页 / 分类筛选 / 排序）
- [x] 商品搜索
- [x] 商品详情
- [x] 收藏管理
- [x] 购物车
- [x] 下单
- [x] 模拟支付
- [x] 订单管理（取消 / 支付）

### 后台管理
- [x] 用户管理（增删改查 / 密码重置）
- [x] 商品管理（增删改查 / 上架下架）
- [x] 订单管理（状态变更 / 商品项编辑）

## 数据库

### 初始化

```sql
CREATE DATABASE mall DEFAULT CHARACTER SET utf8mb4;
```

导入 `mall-frontend/sql/init.sql`：

```bash
mysql -u root -p mall < mall-frontend/sql/init.sql
```

该脚本包含完整的建表语句和种子数据（测试用户 2 个、分类 18 个、商品 20 个、轮播图 3 张）。

### 表结构（v0.2 优化后）

| 表名 | 说明 | 关键外键 |
|------|------|---------|
| `mall_user` | 用户表 | — |
| `category` | 商品分类（树形） | — |
| `product` | 商品表 | `category_id` → category |
| `cart` | 购物车 | `user_id` → mall_user, `product_id` → product |
| `favorite` | 收藏 | `user_id` → mall_user, `product_id` → product |
| `orders` | 订单 | `user_id` → mall_user |
| `order_item` | 订单项 | `order_id` → orders, `product_id` → product |
| `banner` | 轮播图 | — |

共 8 张表，8 个外键约束，无冗余列。

## 快速启动

### 环境要求

- JDK 17+
- MySQL 8.0+
- Maven 3.8+
- Node.js 18+

### 1. 数据库初始化

```bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS mall DEFAULT CHARACTER SET utf8mb4;"
mysql -u root -p mall < mall-frontend/sql/init.sql
```

### 2. 配置数据库连接

编辑 `mall-backend/src/main/resources/application.properties`：

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/mall?useSSL=false&serverTimezone=Asia/Shanghai
spring.datasource.username=root
spring.datasource.password=你的密码
```

### 3. 启动后端

```bash
cd mall-backend
./mvnw spring-boot:run
```

后端运行在 `http://localhost:8080`。

### 4. 启动前端

```bash
cd mall-frontend
npm install
npm run dev
```

前端运行在 `http://localhost:5173`。

## 接口文档

启动后端后，访问以下地址：

| 文档 | 地址 |
|------|------|
| Knife4j 增强 UI | http://localhost:8080/doc.html |
| Swagger UI | http://localhost:8080/swagger-ui.html |
| OpenAPI JSON | http://localhost:8080/v3/api-docs |

## API 一览

### 用户端

| 模块 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 用户 | `POST` | `/api/user/login` | 登录 |
| 用户 | `POST` | `/api/user/register` | 注册 |
| 用户 | `POST` | `/api/user/logout` | 退出登录 |
| 用户 | `GET` | `/api/user/info` | 当前用户信息 |
| 商品 | `GET` | `/api/product/list/{page}/{pageSize}` | 分页列表 |
| 商品 | `GET` | `/api/product/list/{page}/{pageSize}/{sort}` | 带排序列表 |
| 商品 | `GET` | `/api/product/list/category/{id}/{page}/{pageSize}` | 按分类筛选 |
| 商品 | `GET` | `/api/product/{id}` | 商品详情 |
| 商品 | `GET` | `/api/product/hot` | 热门商品 |
| 商品 | `GET` | `/api/product/new` | 新品推荐 |
| 分类 | `GET` | `/api/category/tree` | 分类树 |
| 搜索 | `GET` | `/api/search/{keyword}` | 关键词搜索 |
| 搜索 | `GET` | `/api/search/{keyword}/{page}/{pageSize}` | 搜索分页 |
| 购物车 | `GET` | `/api/cart/list` | 购物车列表 |
| 购物车 | `POST` | `/api/cart/add` | 加入购物车 |
| 购物车 | `PUT` | `/api/cart/{id}` | 修改数量 |
| 购物车 | `DELETE` | `/api/cart/{id}` | 删除商品 |
| 收藏 | `GET` | `/api/favorite/list` | 收藏列表 |
| 收藏 | `POST` | `/api/favorite/toggle` | 切换收藏 |
| 订单 | `POST` | `/api/order/create` | 创建订单 |
| 订单 | `GET` | `/api/order/list` | 订单列表 |
| 订单 | `GET` | `/api/order/{id}` | 订单详情 |
| 订单 | `PUT` | `/api/order/{id}/cancel` | 取消订单 |
| 订单 | `PUT` | `/api/order/{id}/pay` | 模拟支付 |
| 轮播图 | `GET` | `/api/banner/list` | 轮播图列表 |

### 管理端（需管理员权限）

| 模块 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 商品 | `GET` | `/api/admin/product/list/{page}/{pageSize}` | 商品列表 |
| 商品 | `GET` | `/api/admin/product/{id}` | 商品详情 |
| 商品 | `POST` | `/api/admin/product` | 新增商品 |
| 商品 | `PUT` | `/api/admin/product/{id}` | 编辑商品 |
| 商品 | `PUT` | `/api/admin/product/{id}/toggle-status` | 上架/下架 |
| 商品 | `DELETE` | `/api/admin/product/{id}` | 删除商品 |
| 商品 | `GET` | `/api/admin/product/search/{keyword}` | 搜索商品 |
| 订单 | `GET` | `/api/admin/order/list` | 全部订单 |
| 订单 | `GET` | `/api/admin/order/{id}` | 订单详情 |
| 订单 | `POST` | `/api/admin/order` | 新增订单 |
| 订单 | `PUT` | `/api/admin/order/{id}` | 编辑订单 |
| 订单 | `PUT` | `/api/admin/order/{id}/status` | 修改状态 |
| 订单 | `DELETE` | `/api/admin/order/{id}` | 删除订单 |
| 用户 | `GET` | `/api/admin/user/list` | 用户列表 |
| 用户 | `POST` | `/api/admin/user` | 新增用户 |
| 用户 | `PUT` | `/api/admin/user/{id}` | 编辑用户 |
| 用户 | `PUT` | `/api/admin/user/{id}/password` | 重置密码 |
| 用户 | `DELETE` | `/api/admin/user/{id}` | 删除用户 |

## .gitignore 说明

| 忽略项 | 原因 |
|--------|------|
| `target/` | Maven 编译产物 |
| `.idea/` / `*.iml` | IntelliJ IDEA 配置文件 |
| `.vscode/` | VS Code 配置文件 |
| `node_modules/` | 前端依赖 |
| `dist/` / `dist-ssr/` | 前端构建产物 |
| `*.log` | 日志文件 |
| `*.class` / `*.jar` | Java 编译产物 |
| `.DS_Store` / `Thumbs.db` | 操作系统文件 |

## 版本历史

| 版本 | 日期 | 内容 |
|------|------|------|
| v0.1 | 2026-07 | 项目初始化：用户端基础功能（商品/购物车/收藏/订单） |
| v0.2 | 2026-07 | 数据库架构优化（删冗余列、外键约束、order_item 规范化）、后台管理系统（商品/订单/用户管理）、评论移除、UI 本地更新优化 |

## 开发约定

- 后端 RESTful 接口统一使用 `@PathVariable` 传参
- 统一响应格式：`Result<T> { code, message, data }`
- 分页格式：`PageResult<T> { total, list, page, pageSize }`
- JWT Token 通过 HttpOnly Cookie 传递（前端无感）
- AI 生成代码须经人工 Review 后方可合入
