# AI+低代码协同电商平台项目开发指导文档

> 基于《AI+低代码+AI协同电商平台全流程开发实战》扩展形成的企业级项目开发指导文档。

# 1. 项目概述

## 1.1 项目目标

开发一个前后端分离的"潮购"电商平台，包含用户商城、后台管理系统、AI增强能力和低代码开发。

## 1.2 系统架构

``` text
Browser
   │
Vue3 + Element Plus
   │
Axios
   │
SpringBoot REST API
   │
MyBatis-Plus
   │
MySQL + Redis
```

# 2. 技术栈

## 前端

-   Vue3
-   Vite
-   Vue Router
-   Pinia
-   Axios
-   Element Plus
-   ECharts
-   SCSS

## 后端

-   JDK17
-   SpringBoot3
-   Spring MVC
-   MyBatis-Plus
-   MySQL8
-   Redis
-   JWT
-   Swagger/OpenAPI
-   Lombok
-   Maven

## AI工具

-   Cursor
-   GitHub Copilot
-   ChatGPT
-   DeepSeek
-   Claude

## 低代码

-   若依(RuoYi-Vue3)

# 3. 项目目录

``` text
mall
├── frontend
│   ├── src
│   ├── router
│   ├── stores
│   ├── views
│   └── api
└── backend
    ├── controller
    ├── service
    ├── mapper
    ├── entity
    ├── config
    ├── common
    └── security
```

# 4. 数据库设计

核心表： - user - product - category - cart - orders - order_item -
banner - admin - role - permission

# 5. 功能模块

## 用户端

-   注册登录
-   首页
-   商品列表
-   商品详情
-   搜索
-   收藏
-   购物车
-   下单
-   支付（预留）
-   我的订单

## 后台

-   用户管理
-   商品管理
-   分类管理
-   订单管理
-   权限管理
-   数据统计

## AI模块

-   AI商品推荐
-   AI搜索联想
-   AI客服
-   AI评论分析

# 6. 开发步骤

## 第一阶段：需求分析

输出需求规格说明书、用户故事、功能清单、页面清单。

## 第二阶段：原型设计

使用MasterGo完成低保真、高保真原型及交互设计。

## 第三阶段：数据库设计

绘制ER图，设计表结构，生成SQL。

## 第四阶段：SpringBoot搭建

完成统一响应、异常处理、JWT、Swagger、MyBatis配置。

## 第五阶段：Vue开发

完成路由、状态管理、Axios封装及公共组件。

## 第六阶段：后台CRUD

利用若依生成基础CRUD，AI补充复杂业务。

## 第七阶段：前端页面

实现首页、商品、购物车、订单、后台页面。

## 第八阶段：接口联调

完成Token、跨域、分页、上传下载等功能。

## 第九阶段：测试

功能测试、接口测试、异常测试、压力测试。

## 第十阶段：部署

Nginx部署前端，SpringBoot Jar部署后端，MySQL+Redis部署数据库。

# 7. Git规范

-   main
-   develop
-   feature/\*
-   release/\*
-   hotfix/\*

采用Pull Request进行代码评审。

# 8. AI协同开发规范

-   AI生成代码必须人工Review
-   Prompt版本统一管理
-   关键业务不得直接复制AI输出
-   每次提交附AI使用说明

# 9. 项目交付物

-   需求文档
-   原型设计
-   数据库设计
-   前端源码
-   后端源码
-   Swagger接口文档
-   测试报告
-   部署文档
-   README
-   项目答辩PPT

# 10. 开发里程碑

  阶段   成果
------ ------------
  需求   需求文档
  设计   原型+ER图
  开发   前后端功能
  联调   完整业务
  测试   测试报告
  部署   线上运行
  答辩   最终演示
