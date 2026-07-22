# 潮购电商平台（AI + 低代码协同）

基于 Spring Boot 3 + Vue 3 的前后端分离电商平台，融入 AI 辅助开发和低代码开发模式。

## 技术栈

| 层级 | 技术 |
|------|------|
| 后端框架 | Spring Boot 3.2.5 / Spring MVC |
| ORM | MyBatis-Plus 3.5.7 |
| 数据库 | MySQL 8 |
| 认证 | JWT (jjwt 0.11.5) |
| 接口文档 | Knife4j 4.5.0 (OpenAPI 3.0) |
| 前端框架 | Vue 3 + Vite |
| UI 组件 | Element Plus |
| 状态管理 | Pinia |
| 构建工具 | Maven (后端) / npm (前端) |
| Java 版本 | JDK 17 |

## 项目结构

```
shixun/
├── mall-backend/                 # 后端 Spring Boot 工程
│   ├── src/main/java/com/mall/backend/
│   │   ├── MallBackendApplication.java    # 启动类
│   │   ├── config/               # 配置（CORS / Knife4j）
│   │   ├── controller/           # 控制器（8 个模块）
│   │   │   ├── UserController        # 用户：登录/注册/信息
│   │   │   ├── ProductController     # 商品：列表/详情/评论/热门/新品
│   │   │   ├── CategoryController    # 分类：分类树
│   │   │   ├── CartController        # 购物车：增删改查
│   │   │   ├── FavoriteController    # 收藏：切换/列表
│   │   │   ├── OrderController       # 订单：创建/支付/取消
│   │   │   ├── BannerController      # 轮播图：列表
│   │   │   └── SearchController      # 搜索：关键词搜索
│   │   ├── dto/                  # 数据传输对象（Result / PageResult）
│   │   ├── entity/               # 数据库实体（7 张表）
│   │   ├── mapper/               # MyBatis 接口 + XML
│   │   └── security/             # JWT 工具 + 认证拦截
│   └── src/main/resources/
│       ├── application.properties    # 应用配置
│       └── mapper/ProductMapper.xml  # 商品动态 SQL
│
├── mall-frontend/                # 前端 Vue 3 工程
│   ├── src/
│   │   ├── views/                # 页面
│   │   ├── router/               # 路由
│   │   ├── stores/               # Pinia 状态管理
│   │   └── api/                  # Axios 接口封装
│   └── ...
│
├── .gitignore                    # Git 忽略规则
└── README.md                     # 本文件
```

## 功能模块

### 用户端
- [x] 注册 / 登录（JWT Token）
- [x] 首页轮播图
- [x] 商品列表（分页 / 分类筛选 / 排序）
- [x] 商品搜索
- [x] 商品详情 + 评论
- [x] 收藏管理
- [x] 购物车
- [x] 下单
- [x] 模拟支付
- [x] 订单管理（取消 / 支付）

### 后台管理（规划中）
- [ ] 用户管理
- [ ] 商品 CRUD
- [ ] 分类管理
- [ ] 订单管理
- [ ] 数据统计

## 快速启动

### 环境要求

- JDK 17+
- MySQL 8.0+
- Maven 3.8+
- Node.js 18+

### 1. 数据库初始化

```sql
CREATE DATABASE mall DEFAULT CHARACTER SET utf8mb4;
```

导入 `mall-backend/src/main/resources/sql/` 下的建表脚本（如有）。

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

## RESTful API 一览

所有接口采用标准 RESTful 风格，资源 ID 使用 `@PathVariable`。

| 模块 | 方法 | 路径 | 说明 |
|------|------|------|------|
| 用户 | `POST` | `/api/user/login` | 登录 |
| 用户 | `POST` | `/api/user/register` | 注册 |
| 用户 | `GET` | `/api/user/info` | 当前用户信息 |
| 商品 | `GET` | `/api/product/list/{page}/{pageSize}` | 分页列表 |
| 商品 | `GET` | `/api/product/list/{page}/{pageSize}/{sort}` | 带排序列表 |
| 商品 | `GET` | `/api/product/list/category/{id}/{page}/{pageSize}` | 按分类筛选 |
| 商品 | `GET` | `/api/product/{id}` | 商品详情 |
| 商品 | `GET` | `/api/product/{id}/reviews` | 商品评论 |
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

## .gitignore 说明

项目根目录 `.gitignore` 已配置忽略以下内容：

| 忽略项 | 原因 |
|--------|------|
| `target/` | Maven 编译产物 |
| `.idea/` / `*.iml` | IntelliJ IDEA 配置文件 |
| `.vscode/` | VS Code 配置文件 |
| `node_modules/` | 前端依赖（可通过 npm install 恢复） |
| `dist/` / `dist-ssr/` | 前端构建产物 |
| `*.log` | 日志文件 |
| `*.class` / `*.jar` | Java 编译产物 |
| `.DS_Store` / `Thumbs.db` | 操作系统文件 |

## Git 分支规范

| 分支 | 用途 |
|------|------|
| `main` | 生产环境 |
| `develop` | 开发主线 |
| `feature/*` | 功能分支 |
| `release/*` | 发布分支 |
| `hotfix/*` | 紧急修复 |

## 开发约定

- 后端 RESTful 接口统一使用 `@PathVariable` 传参
- 统一响应格式：`Result<T> { code, message, data }`
- 分页格式：`PageResult<T> { total, list, page, pageSize }`
- JWT Token 通过请求头 `Authorization: Bearer <token>` 传递
- AI 生成代码须经人工 Review 后方可合入
