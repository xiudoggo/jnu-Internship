# 潮购电商平台

基于 Spring Boot 3 + Vue 3 的前后端分离电商平台。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端框架 | Spring Boot 3.2.5 / Spring MVC |
| ORM | MyBatis-Plus 3.5.7 |
| 数据库 | MySQL 8 |
| 缓存 | Redis（Lettuce 客户端） |
| 认证 | JWT (jjwt 0.11.5) + HttpOnly Cookie |
| 接口文档 | Knife4j 4.5.0 (OpenAPI 3.0) |
| 前端框架 | Vue 3 + Vite |
| UI 组件 | Element Plus |
| 图表 | ECharts 5 + vue-echarts |
| 状态管理 | Pinia |
| HTTP 库 | Axios + qs |
| 构建工具 | Maven (后端) / npm (前端) |
| Java 版本 | JDK 17 |

## 项目结构

```
shixun/
├── mall-backend/                         # 后端 Spring Boot 工程
│   └── src/main/java/com/mall/backend/
│       ├── MallBackendApplication.java
│       ├── config/                       # WebMvc / Redis / Knife4j / 异常处理 / 请求日志
│       ├── controller/                   # 控制器（用户端 + 管理端）
│       ├── service/                      # Service 接口（11 个）
│       │   └── impl/                     # Service 实现（含缓存策略）
│       ├── dto/                          # 请求 DTO（Login / Register / UpdatePassword 等）
│       ├── vo/                           # 响应 VO（ProductVO / OrderDetailVO / CartItemVO 等）
│       ├── query/                        # 查询条件对象
│       ├── interceptor/                  # 拦截器（AuthInterceptor / AdminInterceptor）
│       ├── entity/                       # 数据库实体（8 个）
│       ├── enums/                        # 枚举（OrderStatus）
│       ├── mapper/                       # MyBatis 接口
│       ├── security/                     # JWT / BCrypt 密码加密
│       └── util/                         # 工具类（RedisUtil）
│
├── mall-frontend/                        # 前端 Vue 3 工程
│   ├── sql/init.sql                      # 数据库初始化（建表 + 种子数据）
│   └── src/
│       ├── views/                        # 页面（用户端 12 + 管理端 4）
│       │   ├── admin/
│       │   │   ├── Dashboard.vue         # 数据统计仪表盘
│       │   │   ├── ProductManage.vue
│       │   │   ├── OrderManage.vue
│       │   │   └── UserManage.vue
│       │   ├── LoginView.vue             # 登录（历史用户 / 记住密码）
│       │   ├── RegisterView.vue
│       │   ├── ChangePasswordView.vue    # 两步式修改密码
│       │   └── ...
│       ├── components/                   # 公共组件（Layout / NavHeader / ...）
│       ├── router/                       # 路由 + 导航守卫
│       ├── stores/                       # Pinia（user / cart）
│       ├── api/                          # Axios 封装
│       └── styles/                       # SCSS 全局变量
│
├── .gitignore
└── README.md
```

## 功能模块

### 用户端

- [x] 注册 / 登录（历史用户记录 / 记住密码）
- [x] 两步式修改密码（原密码验证 → 新密码确认）
- [x] 首页轮播图
- [x] 商品列表（分页 / 分类筛选 / 排序 / Redis 缓存）
- [x] 商品搜索
- [x] 商品详情
- [x] 收藏管理
- [x] 购物车
- [x] 下单
- [x] 模拟支付
- [x] 订单管理（取消 / 支付）

### 后台管理

- [x] 数据统计仪表盘（今日概览 / 订单趋势 / 销量 Top10 / 订单状态 / 用户趋势）
- [x] 用户管理（增删改查 / 密码重置）
- [x] 商品管理（增删改查 / 上架下架）
- [x] 订单管理（状态变更 / 商品项编辑）

## 数据库

### 初始化

```sql
CREATE DATABASE mall DEFAULT CHARACTER SET utf8mb4;
```

导入种子数据：

```bash
mysql -u root -p mall < mall-frontend/sql/init.sql
```

### 表结构

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

## 快速启动

### 环境要求

- JDK 17+
- MySQL 8.0+
- Redis（可选，无 Redis 时自动 fallback 到数据库）
- Maven 3.8+
- Node.js 18+

### 1. 初始化数据库

```bash
mysql -u root -p -e "CREATE DATABASE IF NOT EXISTS mall DEFAULT CHARACTER SET utf8mb4;"
mysql -u root -p mall < mall-frontend/sql/init.sql
```

### 2. 配置数据库连接

编辑 `mall-backend/src/main/resources/application.yml`：

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/mall?useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: 你的密码
  data:
    redis:
      host: localhost
      port: 6379
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

### 测试账号

| 角色 | 手机号 | 密码 |
|------|--------|------|
| 普通用户 | `13800000002` | `123456` |
| 管理员 | `13800000001` | `123456` |

## 接口文档

启动后端后访问：

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
| 用户 | `POST` | `/api/user/password/verify` | 验证原密码 |
| 用户 | `PUT` | `/api/user/password` | 修改密码 |
| 商品 | `GET` | `/api/product/list/{page}/{pageSize}` | 分页列表 |
| 商品 | `GET` | `/api/product/list/{page}/{pageSize}/{sort}` | 带排序列表 |
| 商品 | `GET` | `/api/product/list/category/{id}/{page}/{pageSize}` | 按分类筛选 |
| 商品 | `GET` | `/api/product/{id}` | 商品详情 |
| 商品 | `GET` | `/api/product/hot` | 热门商品（缓存） |
| 商品 | `GET` | `/api/product/new` | 新品推荐（缓存） |
| 分类 | `GET` | `/api/category/tree` | 分类树（缓存） |
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
| 轮播图 | `GET` | `/api/banner/list` | 轮播图列表（缓存） |

### 管理端（需管理员角色）

| 模块 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 统计 | `GET` | `/api/admin/stat/today-overview` | 今日概览 |
| 统计 | `GET` | `/api/admin/stat/order-trend` | 近 7 日订单趋势 |
| 统计 | `GET` | `/api/admin/stat/top-products` | 销量 Top10 |
| 统计 | `GET` | `/api/admin/stat/order-status` | 订单状态分布 |
| 统计 | `GET` | `/api/admin/stat/user-trend` | 近 7 日用户趋势 |
| 商品 | `GET` | `/api/admin/product/list/{page}/{pageSize}` | 商品列表 |
| 商品 | `GET` | `/api/admin/product/{id}` | 商品详情 |
| 商品 | `POST` | `/api/admin/product` | 新增商品 |
| 商品 | `PUT` | `/api/admin/product/{id}` | 编辑商品 |
| 商品 | `PUT` | `/api/admin/product/{id}/toggle-status` | 上架/下架 |
| 商品 | `DELETE` | `/api/admin/product/{id}` | 删除商品 |
| 订单 | `GET` | `/api/admin/order/list` | 全部订单 |
| 订单 | `GET` | `/api/admin/order/{id}` | 订单详情 |
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
| `.idea/` / `*.iml` | IntelliJ IDEA |
| `.vscode/` | VS Code 配置 |
| `.claude/` | Claude Code 配置（含密钥，绝不可提交） |
| `node_modules/` | 前端依赖 |
| `dist/` / `dist-ssr/` | 前端构建产物 |
| `*.log` | 日志文件 |
| `*.class` / `*.jar` | Java 编译产物 |
| `.DS_Store` / `Thumbs.db` | 操作系统文件 |

## 版本历史

| 版本 | 日期 | 内容 |
|------|------|------|
| v0.1 | 2026-07 | 项目初始化：用户端基础功能（商品/购物车/收藏/订单） |
| v0.2 | 2026-07 | 数据库架构优化、后台管理系统、UI 优化 |
| v0.3 | 2026-07 | Service 层重构 + DTO/VO 分离 + 拦截器认证、Redis 缓存 + ECharts 仪表盘、登录历史记录 + 记住密码 + 修改密码、qs 序列化 + YAML 配置迁移 |

## 开发约定

- RESTful 接口统一使用 `@PathVariable` + `@RequestBody`
- 统一响应格式：`Result<T> { code, message, data }`
- JWT Token 通过 HttpOnly Cookie 传递（前端无感）
- Controller → Service → ServiceImpl → Mapper 分层调用
- 权限通过 AuthInterceptor / AdminInterceptor 统一拦截
- 热门商品 / 新品 / 分类树 / 轮播图使用 Redis 缓存，TTL 30~60 分钟
