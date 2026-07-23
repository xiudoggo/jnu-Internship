# 技术栈总结

> 基于 6 天课程资料整理（Node/NPM → Java 基础 → Spring Boot → Vue → 前后端联调）

---

## 一、开发环境 & 工具

| 技术/工具 | 说明 |
|-----------|------|
| **Node.js** | JavaScript 运行时环境，v12.14.0 LTS |
| **NPM** | Node 包管理工具，默认随 Node 安装 |
| **JDK** | Java 开发工具包 |
| **Maven** | Java 项目构建与依赖管理工具 |
| **IntelliJ IDEA** | Java 集成开发环境 |
| **MySQL 8.0** | 关系型数据库 |
| **Navicat** | 数据库可视化操作工具 |

### 环境配置要点
- **NPM 镜像**：配置淘宝镜像 `https://registry.npmmirror.com`
- **NPM 全局目录**：设置 `prefix` 和 `cache` 路径，并配置环境变量
- **Maven**：配置 `MAVEN_HOME` 和 `PATH`
- **MySQL**：通过 `my.ini` 配置端口(3306)、字符集(utf8mb4)、存储引擎(InnoDB)

---

## 二、Java 基础

### 2.1 集合框架

| 集合类 | 底层结构 | 特点 | 优点 | 缺点 |
|--------|----------|------|------|------|
| **ArrayList** | 动态数组 | 有序、可重复 | 根据地址查询快 | 插入/删除涉及数据移动，效率低 |
| **LinkedList** | 双向链表 | 有序、可重复 | 插入/删除效率高 | 只能逐一查询，速度慢 |
| **HashMap** | 散列表 | 键值对存储 | 查询效率高 | 存在哈希冲突 |

### 2.2 常用方法
- `add()` / `remove()` / `set()` / `get()`
- `size()` / `isEmpty()` / `contains()`
- **泛型**：`ArrayList<String>` 限制数据类型

---

## 三、后端技术栈

### 3.1 Spring Boot

| 特性 | 说明 |
|------|------|
| **核心理念** | 习惯优于配置（Convention over Configuration） |
| **内置服务器** | 内置 Tomcat，无需额外安装 |
| **自动配置** | 基于自动配置类，减少程序员配置工作量 |
| **版本** | Spring Boot 3.3.3，Java 17 |

#### 项目结构
```
├── pom.xml                 # Maven 依赖配置
├── application.yml         # 核心配置文件（推荐 YAML 格式）
├── Day0XApplication.java   # 启动类
├── controller/             # 控制器层
├── service/                # 业务逻辑层
│   └── impl/               # 实现类
├── mapper/                 # 数据访问层
└── domain/vo/dto/query/   # 实体/视图/传输/查询对象
```

### 3.2 HTTP 请求与响应

#### 请求方式（RESTful）
| 方法 | 操作 | 用途 |
|------|------|------|
| **GET** | Read | 查询资源 |
| **POST** | Create | 新增资源 |
| **PUT** | Update | 更新完整资源（幂等） |
| **DELETE** | Delete | 删除资源（幂等） |
| **PATCH** | Update | 更新部分资源 |

#### 状态码
| 状态码 | 含义 |
|--------|------|
| 200 | 成功 |
| 404 | 资源不存在（检查路径） |
| 405 | 方法不允许（检查请求方式） |
| 500 | 服务器内部错误 |

### 3.3 后端分层架构

```
Controller（控制器层）
    ↓ 接收请求参数，调用 Service
Service（业务逻辑层）
    ↓ 处理业务逻辑（统计、分析、转账等）
Mapper（数据访问层）
    ↓ 操作数据库
Database（数据库）
```

### 3.4 MyBatis

| 特性 | 说明 |
|------|------|
| **本质** | 对 JDBC 的封装 |
| **核心** | Mapper 接口 + XML 映射文件 |
| **注解** | `@Mapper` |

#### 动态 SQL 标签
- `<if>` - 条件判断
- `<where>` - 智能添加 WHERE 并去除多余 AND/OR
- `<set>` - 智能添加 SET 并去除多余逗号
- `<choose>` / `<trim>` / `<foreach>`

### 3.5 Lombok

- **依赖**：`org.projectlombok:lombok`
- **核心注解**：`@Data`（自动生成 getter/setter/toString 等）

### 3.6 统一响应模型

```java
@Data
public class R<T> {
    private int code = 200;      // 200成功，500失败
    private String msg;          // 描述信息
    private Long total;          // 记录数
    private T data;              // 响应数据
}
```

### 3.7 Swagger / Knife4j

| 工具 | 说明 |
|------|------|
| **Swagger** | RESTful 接口文档在线自动生成 + 功能测试 |
| **Knife4j** | Swagger2 + OpenAPI3 的增强解决方案 |

#### 常用注解
- `@Tag` - 控制器类说明
- `@Operation` - 接口方法说明
- `@Parameter` / `@Parameters` - 参数说明
- `@Schema` - 模型/属性说明

---

## 四、前端技术栈

### 4.1 Vue 3

| 特性 | 说明 |
|------|------|
| **定位** | 渐进式 JavaScript 框架 |
| **创建命令** | `npm create vue@latest` |
| **启动命令** | `npm run dev`（默认端口 5173） |
| **状态管理** | Pinia |
| **路由** | Vue Router |

#### 核心概念
- **MVVM 模式**：Model（数据）↔ ViewModel（桥梁）↔ View（视图）
- **双向绑定**：`v-model` 实现数据与视图同步
- **插值表达式**：`{{ }}` 获取 model 属性值
- **响应式数据**：`ref()` / `reactive()`

#### 常用指令
| 指令 | 作用 |
|------|------|
| `v-model` | 双向数据绑定 |
| `v-for` | 循环渲染 |
| `v-if` / `v-else` | 条件渲染 |
| `@click` | 点击事件绑定 |
| `:class` / `:src` | 动态属性绑定 |

### 4.2 Vue Router

- **配置文件**：`router/index.js`
- **路由跳转**：
  - `router.push(path)` - 产生浏览历史
  - `router.replace(path)` - 不产生浏览历史
- **组件显示**：`<RouterView />`

### 4.3 Axios

| 特性 | 说明 |
|------|------|
| **定位** | 基于 Promise 的 HTTP 库 |
| **安装** | `npm install axios qs` |
| **封装** | 配置 baseURL、请求/响应拦截器 |

#### 请求拦截器用途
- 添加 Token 到请求头
- 参数预处理

#### 响应拦截器用途
- 统一错误处理
- 数据格式转换

---

## 五、安全与认证

### 5.1 JWT（JSON Web Token）

| 特性 | 说明 |
|------|------|
| **标准** | RFC 7519 |
| **结构** | `Header.Payload.Signature` |
| **用途** | 授权认证、信息交换 |
| **特点** | 无状态、紧凑、自包含 |

#### 组成
1. **Header** - 加密算法信息
2. **Payload** - 用户数据（claims）
3. **Signature** - 签名（防篡改）

#### 登录流程
```
前端登录 → 后端验证 → 生成 Token → 前端存储 Token
                ↓
        后续请求携带 Token（请求头）→ 后端校验合法性
```

### 5.2 拦截器（Interceptor）

| 方法 | 执行时机 |
|------|----------|
| `preHandle()` | Controller 方法调用前（重点：登录验证） |
| `postHandle()` | Controller 方法调用后，视图渲染前 |
| `afterCompletion()` | 整个请求完成后 |

#### 应用场景
- Token 校验
- 日志记录
- 权限检查
- 请求参数预处理

### 5.3 CORS（跨域资源共享）

| 配置项 | 说明 |
|--------|------|
| `allowedOrigins("*")` | 允许所有来源 |
| `allowedMethods("*")` | 允许所有 HTTP 方法 |
| `allowedHeaders("*")` | 允许所有请求头 |
| `maxAge(3600)` | 预检请求缓存时间 |

#### 跨域条件
- 协议相同
- 域名相同
- 端口相同

---

## 六、数据库

### 6.1 MySQL 配置要点
```ini
port=3306
character-set-server=utf8mb4
default-storage-engine=INNODB
default_authentication_plugin=mysql_native_password
```

### 6.2 初始化命令
```bash
mysqld --initialize --console
mysqld --install
net start mysql
```

---

## 七、开发流程总结

```
1. 需求分析 → 2. 数据库设计 → 3. 后端接口开发（Controller/Service/Mapper）
                                                ↓
4. 前端页面开发（Vue组件） ← 5. 接口联调测试（Swagger/Axios）
                                                ↓
                                    6. 登录认证（JWT + 拦截器）
                                                ↓
                                    7. 跨域处理（CORS）
```

---

## 八、关键技术依赖

### Maven 依赖清单

```xml
<!-- Spring Boot Web -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<!-- MyBatis -->
<dependency>
    <groupId>org.mybatis.spring.boot</groupId>
    <artifactId>mybatis-spring-boot-starter</artifactId>
    <version>3.0.3</version>
</dependency>

<!-- MySQL 驱动 -->
<dependency>
    <groupId>com.mysql</groupId>
    <artifactId>mysql-connector-j</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- Lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
</dependency>

<!-- JWT -->
<dependency>
    <groupId>com.auth0</groupId>
    <artifactId>java-jwt</artifactId>
    <version>3.13.0</version>
</dependency>

<!-- Knife4j -->
<dependency>
    <groupId>com.github.xiaoymin</groupId>
    <artifactId>knife4j-openapi3-jakarta-spring-boot-starter</artifactId>
    <version>4.5.0</version>
</dependency>
```

### NPM 依赖清单

```bash
# Vue 3 项目
npm create vue@latest

# HTTP 请求
npm install axios

# 参数序列化
npm install qs

# UI 组件库（Element Plus）
npm install element-plus @element-plus/icons-vue
```

---

*文档生成时间：2026-07-23*
