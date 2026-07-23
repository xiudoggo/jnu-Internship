# 技术栈升级方案

> 基于 `tech_stack_summary.md`（课程文档）与当前项目实际技术栈的差异分析，
> 制定渐进式升级路线。**本方案仅作规划参考，不直接修改项目代码。**

---

## Context

对比课程文档规定的技术栈与当前项目实际实现，发现以下差距：

| 类别 | 差距项 | 影响 |
|------|--------|------|
| 架构 | 缺少 Service 层 | Controller 直接调用 Mapper，业务逻辑耦合在控制器中 |
| 架构 | 缺少 DTO/VO/Query 分离 | Entity 直接暴露给前端，存在字段泄露风险 |
| 认证 | 缺少拦截器（HandlerInterceptor） | 每个 Controller 方法手动调用 `AuthUtil.requireAdmin()` |
| 中间件 | 缺少 Redis | 无缓存机制，高并发下数据库压力大 |
| 前端 | 缺少 ECharts | 管理端无数据统计可视化 |
| 前端 | 缺少 qs 库 | Axios 参数序列化不够规范 |
| 配置 | application.properties | 文档推荐 YAML 格式 |

**已对齐项（无需升级）**：Knife4j 4.5.0、Lombok、Vue 3 + Vite、Vue Router、Pinia、Axios、Element Plus、SCSS（项目已使用 sass）、JWT、CORS、RESTful API、统一响应模型。

---

## 升级路线图

```
Phase 1（基础架构）         Phase 2（功能增强）         Phase 3（工程优化）
┌─────────────────┐      ┌─────────────────┐      ┌─────────────────┐
│ 1.1 Service 层   │      │ 2.1 Redis 集成   │      │ 3.1 qs 参数序列化│
│ 1.2 DTO/VO 分离  │ ──►  │ 2.2 ECharts 仪表盘│ ──►  │ 3.2 YAML 配置迁移│
│ 1.3 拦截器认证   │      │ 2.3 数据统计 API  │      │ 3.3 代码规范统一 │
└─────────────────┘      └─────────────────┘      └─────────────────┘
   预估 2-3 天                预估 2-3 天                预估 1 天
```

---

## Phase 1：基础架构完善（P0 - 高优先级）

### 1.1 引入 Service 层

**现状**：Controller 直接注入 Mapper 并处理所有业务逻辑。

```java
// 当前：CartController.java
@RestController
public class CartController {
    private final CartItemMapper cartMapper;
    private final ProductMapper productMapper;
    // 业务逻辑全部写在 Controller 中
    @GetMapping("/list")
    public Result<List<Map<String,Object>>> list(HttpServletRequest request) {
        Long userId = AuthUtil.getLoginUserId(request);
        // 查询、组装、返回 —— 全部在控制器
    }
}
```

**目标**：Controller → Service → ServiceImpl → Mapper

**新增文件**：
```
mall-backend/src/main/java/com/mall/backend/service/
├── CartService.java
├── FavoriteService.java
├── OrderService.java
├── ProductService.java
├── UserService.java
├── CategoryService.java
├── BannerService.java
├── SearchService.java
├── impl/
│   ├── CartServiceImpl.java
│   ├── FavoriteServiceImpl.java
│   ├── OrderServiceImpl.java
│   ├── ProductServiceImpl.java
│   ├── UserServiceImpl.java
│   ├── CategoryServiceImpl.java
│   ├── BannerServiceImpl.java
│   └── SearchServiceImpl.java
├── AdminProductService.java
├── AdminOrderService.java
├── AdminUserService.java
└── impl/
    ├── AdminProductServiceImpl.java
    ├── AdminOrderServiceImpl.java
    └── AdminUserServiceImpl.java
```

**改造要点**：
- 将现有 Controller 中的业务逻辑下沉到 ServiceImpl
- Controller 仅负责参数接收、权限校验、调用 Service、返回响应
- Service 接口定义业务方法契约
- ServiceImpl 注入 Mapper，处理数据组装逻辑

### 1.2 DTO/VO/Query 对象分离

**现状**：Entity 直接作为请求参数和响应数据，存在字段泄露风险。

**目标**：
```
mall-backend/src/main/java/com/mall/backend/
├── dto/          # 数据传输对象（请求体）
│   ├── LoginDTO.java
│   ├── RegisterDTO.java
│   ├── AddCartDTO.java
│   ├── CreateOrderDTO.java
│   └── ...
├── vo/           # 视图对象（响应体）
│   ├── CartItemVO.java
│   ├── OrderDetailVO.java
│   ├── ProductVO.java
│   └── ...
└── query/        # 查询条件对象
    ├── ProductQuery.java
    └── OrderQuery.java
```

**改造要点**：
- DTO：接收前端请求参数，添加 `@NotNull`/`@NotBlank` 校验注解
- VO：组装返回给前端的数据（如 CartItemVO 含 productName、productImage）
- Query：封装多条件查询参数（keyword、categoryId、sort、page、pageSize）
- Entity 保持纯净，仅映射数据库字段

### 1.3 拦截器认证改造

**现状**：每个 Controller 方法手动调用 `AuthUtil.requireAdmin(request)` / `AuthUtil.getLoginUserId(request)`。

**目标**：使用 Spring MVC HandlerInterceptor 统一处理。

**新增文件**：
```
mall-backend/src/main/java/com/mall/backend/
├── interceptor/
│   ├── AuthInterceptor.java       # 登录校验拦截器
│   └── AdminInterceptor.java      # 管理员权限拦截器（可选，或合并）
└── config/
    └── WebMvcConfig.java          # 注册拦截器 + CORS（整合现有 CorsConfig）
```

**改造要点**：
- `AuthInterceptor.preHandle()`：从 Cookie 中提取 Token → 校验合法性 → 将 userId/role 存入 `request.setAttribute()`
- `AdminInterceptor.preHandle()`：检查 role 是否为 admin
- 配置 excludePathPatterns 放行登录/注册/Knife4j 等公开接口
- Controller 方法移除 `AuthUtil.requireAdmin()` 调用，改为从 request attribute 获取用户信息

---

## Phase 2：功能增强（P1 - 中优先级）

### 2.1 Redis 集成

**目标**：引入 Redis 作为缓存层，提升性能。

**Maven 依赖**：
```xml
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-redis</artifactId>
</dependency>
<dependency>
    <groupId>org.apache.commons</groupId>
    <artifactId>commons-pool2</artifactId>
</dependency>
```

**应用场景**：

| 场景 | 说明 |
|------|------|
| 热门商品缓存 | `product::hot` → List\<Product\>，TTL 30min |
| 新品缓存 | `product::new` → List\<Product\>，TTL 30min |
| 分类树缓存 | `category::tree` → List\<Category\>，TTL 1h |
| 用户 Session | `token::{userId}` → JWT，TTL 2h |
| 轮播图缓存 | `banner::list` → List\<Banner\>，TTL 1h |

**新增文件**：
```
mall-backend/src/main/java/com/mall/backend/
├── config/
│   └── RedisConfig.java          # Redis 序列化配置
└── util/
    └── RedisUtil.java            # Redis 操作工具类
```

**application.properties 新增**：
```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
spring.data.redis.timeout=5000ms
spring.data.redis.lettuce.pool.max-active=8
spring.data.redis.lettuce.pool.max-idle=8
spring.data.redis.lettuce.pool.min-idle=0
```

### 2.2 ECharts 数据统计仪表盘

**目标**：管理端首页新增数据统计可视化。

**NPM 依赖**：
```bash
npm install echarts vue-echarts
```

**新增页面**：管理端 Dashboard 仪表盘

```
mall-frontend/src/views/admin/
└── Dashboard.vue    # 数据统计仪表盘
```

**图表设计**：

| 图表 | 类型 | 数据来源 |
|------|------|---------|
| 近7日订单量趋势 | 折线图 | GET /api/admin/stat/order-trend |
| 商品销量 Top10 | 柱状图 | GET /api/admin/stat/top-products |
| 订单状态分布 | 饼图 | GET /api/admin/stat/order-status |
| 用户注册趋势 | 折线图 | GET /api/admin/stat/user-trend |
| 今日概览卡片 | 数字卡片 | GET /api/admin/stat/today-overview |

**新增后端 API**：
```
mall-backend/src/main/java/com/mall/backend/
├── controller/
│   └── AdminStatController.java    # 统计接口
├── service/
│   ├── StatService.java
│   └── impl/StatServiceImpl.java
└── mapper/
    └── StatMapper.java             # 统计 SQL（可直接用 Mapper 聚合查询）
```

### 2.3 数据统计 API 设计

| 方法 | 路径 | 说明 |
|------|------|------|
| GET | /api/admin/stat/today-overview | 今日订单数、销售额、新增用户 |
| GET | /api/admin/stat/order-trend | 近7/30日订单趋势 |
| GET | /api/admin/stat/top-products | 销量 Top N |
| GET | /api/admin/stat/order-status | 各状态订单数量 |
| GET | /api/admin/stat/user-trend | 用户增长趋势 |

---

## Phase 3：工程优化（P2 - 低优先级）

### 3.1 qs 参数序列化

**NPM 依赖**：
```bash
npm install qs
```

**改造点**：
- Axios 请求拦截器中使用 `qs.stringify()` 将对象参数序列化为 `key=value&key=value` 格式
- 主要影响 GET 请求的参数传递

**改造文件**：`mall-frontend/src/api/` 下的 Axios 封装

### 3.2 application.yml 迁移

**目标**：将 `application.properties` 迁移为 `application.yml`。

**优势**：
- 层级结构更清晰
- 减少重复前缀
- 文档推荐格式

**改造文件**：
- 新建 `application.yml`
- 删除 `application.properties`

### 3.3 代码规范统一

| 项目 | 说明 |
|------|------|
| 包名规范 | 统一 `com.mall.backend.{layer}` 结构 |
| 类名后缀 | Service → `XxxService`，ServiceImpl → `XxxServiceImpl` |
| 方法命名 | Controller 方法名与 HTTP 方法语义对应 |
| 异常处理 | 全部收敛到 `GlobalExceptionHandler` |

---

## 受影响文件汇总

### Phase 1 新增文件（约 20 个）

| 目录 | 文件数 | 说明 |
|------|--------|------|
| `service/` | 11 个接口 | 每个 Controller 对应一个 Service 接口 |
| `service/impl/` | 11 个实现 | Service 实现类 |
| `dto/` | ~6 个 | 请求 DTO（LoginDTO, RegisterDTO, AddCartDTO, CreateOrderDTO 等） |
| `vo/` | ~6 个 | 响应 VO（CartItemVO, OrderDetailVO, ProductVO 等） |
| `query/` | ~3 个 | 查询条件（ProductQuery, OrderQuery 等） |
| `interceptor/` | 2 个 | AuthInterceptor, AdminInterceptor |
| `config/` | 1 个 | WebMvcConfig（整合拦截器注册） |

### Phase 1 修改文件（约 15 个）

- 所有现有 Controller：移除 Mapper 注入，改为 Service 注入；移除业务逻辑
- `config/CorsConfig.java`：合并到 WebMvcConfig

### Phase 2 新增文件（约 8 个）

- RedisConfig、RedisUtil
- AdminStatController、StatService、StatServiceImpl、StatMapper
- Dashboard.vue
- 路由注册修改

### Phase 3 修改文件（约 3 个）

- Axios 封装：加入 qs 序列化
- application.yml 新建
- application.properties 删除

---

## 风险评估

| 风险 | 等级 | 缓解措施 |
|------|------|---------|
| Service 层重构改动大 | 中 | 分模块逐步迁移，先迁移 Cart/Favorite 验证模式 |
| Redis 依赖外部服务 | 低 | 提供 fallback 到数据库的逻辑 |
| 拦截器 Cookie 读取兼容性 | 低 | 保留 AuthUtil 工具类作为兜底 |
| DTO/VO 转换工作量大 | 中 | 引入 MapStruct 或手动 BeanUtils.copyProperties |

---

## 验证方案

1. **Phase 1**：启动后端 → 走通注册/登录/加购/收藏/下单全流程 → 确认 API 响应不变
2. **Phase 2**：启动 Redis → 访问首页/分类 → 确认缓存命中 → 管理端查看 Dashboard
3. **Phase 3**：启动后端 → 确认 application.yml 加载成功 → 前端请求参数序列化正确

---

*方案生成时间：2026-07-23*
