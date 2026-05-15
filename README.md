<div align="center">
  <img src="https://gitee.com/lqclf/typora-image/raw/master/img/20260512231304503.png" alt="YsCode AI Cloud Logo" width="120">
  <h1>忆笙智云 (YsCode AI Cloud)</h1>
  <p>企业级AI低代码开发平台</p>
  <p><strong>YsCode AI Cloud Platform</strong></p>
  <p>让开发更高效、更智能、更简单</p>
</div>

***

<p align="center">
 <img src="https://img.shields.io/badge/Spring%20Boot-3.3.3-brightgreen.svg" alt="Spring Boot">
 <img src="https://img.shields.io/badge/Vue.js-3.4.21-brightgreen.svg" alt="Vue.js">
 <img src="https://img.shields.io/badge/Java-21-orange.svg" alt="Java">
 <img src="https://img.shields.io/badge/TypeScript-5.4.2-blue.svg" alt="TypeScript">
 <img src="https://img.shields.io/badge/Spring%20AI-1.1.2-purple.svg" alt="Spring AI">
 <img src="https://img.shields.io/badge/License-MIT-yellow.svg" alt="License">
</p>

<p align="center">
 <img src="https://img.shields.io/badge/AI模块-18+-brightgreen.svg" alt="AI Modules">
 <img src="https://img.shields.io/badge/系统模块-30+-blue.svg" alt="System Modules">
 <img src="https://img.shields.io/badge/数据大屏-12-orange.svg" alt="Data Screens">
 <img src="https://img.shields.io/badge/数据库表-67-yellowgreen.svg" alt="Database Tables">
 <img src="https://img.shields.io/badge/存储方式-6+-9cf.svg" alt="Storage">
</p>

***

## 核心价值

### 项目定位

**忆笙智云 (YsCode AI Cloud Platform)** 是一套面向企业级应用开发的AI低代码开发平台，采用业界领先的前后端分离架构，深度融合 Spring AI 智能技术，旨在帮助开发团队和企业大幅降低软件开发成本，提升交付效率，实现业务快速落地。

### 目标用户

- **软件开发团队**：快速搭建企业级中后台管理系统
- **企业 IT 部门**：降低开发成本，缩短项目交付周期
- **创业公司**：快速验证产品原型，加速业务上线
- **技术学习者**：学习企业级架构设计和最佳实践

### 解决的核心问题

| 痛点         | 解决方案                  |
| ---------- | --------------------- |
| 重复CRUD开发耗时 | 可视化代码生成器，一键生成前后端代码    |
| 数据导入导出复杂   | Excel处理中心，百万级数据流式处理   |
| 系统权限难以管控   | 企业级RBAC权限体系，数据权限精细化控制 |
| AI集成门槛高    | 多供应商AI统一接入，开箱即用       |
| 文件存储不统一    | 支持6种存储方式，灵活切换         |
| 系统监控缺失     | 实时监控服务器、Redis、在线用户    |

### 独特优势

- **AI原生设计**：深度融合 Spring AI，支持多模型流式对话、RAG知识库、Function Calling
- **企业级安全**：Sa-Token权限框架，敏感数据自动脱敏，敏感词过滤
- **高性能架构**：Redis缓存、异步任务、WebSocket实时推送
- **多数据库支持**：MySQL、Oracle、PostgreSQL、SQL Server、达梦、人大金仓
- **现代化UI**：Element Plus + Vxe-Table，5种布局模式
- **丰富可视化**：12+行业数据大屏，ECharts + Three.js 3D效果

***

## 项目简介

**忆笙智云** 是一套企业级AI低代码开发平台，采用前后端分离架构，集成了系统管理、代码生成器、AI智能助手、系统监控、数据大屏等丰富功能，旨在帮助开发者快速构建企业级中后台管理系统。

### 核心特性

- **低代码开发**：可视化代码生成器，支持多数据源、自定义模板、五步向导
- **AI智能助手**：内置18+AI模块，集成多供应商大模型（OpenAI、DeepSeek、智谱AI、MiniMax、Ollama、豆包、Qwen等），支持流式对话、RAG知识库、智能表单/代码生成
- **Excel处理中心**：百万行流式处理，智能字段映射，全维度数据校验
- **企业级安全**：Sa-Token权限框架，数据权限控制，敏感数据脱敏，敏感词过滤
- **多存储支持**：本地、MinIO、阿里云OSS、腾讯云COS、AWS S3、RustFS
- **高性能**：Redis缓存、异步任务、WebSocket实时推送
- **现代化UI**：Element Plus + Vxe-Table，支持多种布局模式
- **数据大屏**：12+行业数据大屏模板，3D可视化效果，迭代中...，

***

## 系统架构图

### 系统整体技术架构图

![系统整体技术架构图](https://gitee.com/lqclf/typora-image/raw/master/images/tech-architecture.svg)

> 忆笙智云技术架构采用五层技术栈设计：客户端层(浏览器/移动端/桌面端) → 前端技术层(Vue 3 + TypeScript + Element Plus + Vite + Pinia + Vue Router) → 后端技术层(Spring Boot 3.3 + Java 21 + Spring AI 1.1 + MyBatis-Plus + Sa-Token + Knife4j) → 中间件层(Nginx + Redis + MySQL + WebSocket + MinIO/OSS) → AI供应商层(OpenAI/DeepSeek/智谱AI/MiniMax/Ollama/豆包/Qwen)。各层之间通过RESTful API和WebSocket进行通信。

### 业务整体架构图

![业务整体架构图](https://gitee.com/lqclf/typora-image/raw/master/images/architecture.svg)

> 忆笙智云采用前后端分离架构，前端基于 Vue 3 + TypeScript，后端基于 Spring Boot 3.3 + Java 21，通过 Spring AI 统一适配多供应商大模型，数据层支持 MySQL + Redis + 多种对象存储。业务服务层包含12个核心模块，覆盖系统管理、AI智能、代码生成等完整功能。

### 整体功能架构图

![feature-architecture](https://gitee.com/lqclf/typora-image/raw/master/images/feature-architecture.svg)

> 忆笙智云功能架构分为五大层级：核心业务层(系统管理/AI智能助手/代码生成器) → 基础设施层(文件上传/Excel处理/定时任务/数据权限) → 安全防护层(Sa-Token认证/敏感词过滤/数据脱敏/IP规则) → 监控运维层(服务器/Redis/在线用户/API性能) → 数据存储层(MySQL/Redis/本地存储/对象存储/向量存储)。

### AI智能模块架构图

![AI智能模块架构图](https://gitee.com/lqclf/typora-image/raw/master/images/ai-architecture.svg)

> AI模块采用六层架构设计：用户输入层 → 意图识别引擎 → 核心处理层(RAG知识库检索/LLM核心引擎/Function Calling工具层) → 多模型适配层(Spring AI统一适配器) → AI模型供应商层(7大供应商) → 流式响应输出层。支持流式响应、知识增强、工具调用等高级能力。

### 代码生成器流程图

![代码生成器流程图](https://gitee.com/lqclf/typora-image/raw/master/images/codegen-flow.svg)

> 代码生成器采用五步向导模式：选择数据源 → 选择数据表 → 选择模板集 → 参数配置 → 预览生成。支持生成 Entity、DTO、Mapper、Service、Controller、Vue 等六种代码类型，模板支持 FreeMarker/Velocity 双引擎。

***

## 项目架构

```
YsCode/
├── ys-boot-pro/              # 后端项目（Spring Boot 3.3.3 + Java 21）
│   ├── ys-common/            # 公共支撑层（注解、常量、枚举、异常、工具类）
│   ├── ys-infra/             # 基础设施层
│   │   ├── ys-infra-file/    # 文件上传模块（6种存储方式）
│   │   ├── ys-infra-log/     # 系统日志模块（操作/登录/异常）
│   │   ├── ys-infra-dict/    # 字典翻译模块
│   │   ├── ys-infra-enum/    # 枚举翻译模块
│   │   ├── ys-infra-sensitive/# 敏感数据脱敏模块
│   │   ├── ys-infra-quartz/  # 定时任务模块（集群支持）
│   │   ├── ys-infra-datapermission/  # 数据权限模块
│   │   ├── ys-infra-excel/   # Excel导入导出模块（百万级）
│   │   ├── ys-infra-codegen/ # 代码生成器模块（多数据源）
│   │   ├── ys-infra-ai/      # AI智能模块（多模型适配）
│   │   ├── ys-infra-redis/   # Redis缓存模块
│   │   └── ys-infra-monitor/ # 系统监控模块
│   ├── ys-system/            # 系统业务模块（用户/角色/菜单/部门/岗位）
│   ├── ys-module/            # 业务扩展模块
│   └── ys-starter/           # 应用启动模块
│
└── ys-vue-pro/               # 前端项目（Vue 3.4 + TypeScript 5.4）
    ├── src/
    │   ├── api/              # API接口层（按模块分组）
    │   ├── components/       # 公共组件（YsTable/YsDialog/YsUpload/YsAIAssistant等）
    │   ├── views/            # 页面视图
    │   │   ├── ai/           # AI模块页面（17个）
    │   │   ├── codegen/      # 代码生成器页面
    │   │   ├── system/       # 系统管理页面
    │   │   ├── home/         # 首页仪表盘
    │   │   └── home-work/    # 工作台（CMS/CRM/监控大屏）
    │   ├── stores/           # Pinia状态管理
    │   ├── router/           # 路由配置
    │   ├── layout/           # 布局组件（5种布局）
    │   └── utils/            # 工具函数
    └── ...
```

***

## 技术栈

### 后端技术栈

| 技术               | 版本       | 说明                                                 |
| ---------------- | -------- | -------------------------------------------------- |
| **Spring Boot**  | 3.3.3    | 核心框架                                               |
| **Spring Cloud** | 2023.0.3 | 微服务框架（预留）                                          |
| **Spring AI**    | 1.1.2    | AI框架（OpenAI、DeepSeek、智谱AI、MiniMax、Ollama、豆包、Qwen等） |
| **MyBatis-Plus** | 3.5.14   | ORM框架                                              |
| **Sa-Token**     | 1.43.0   | 认证授权框架                                             |
| **MySQL**        | 8.4.0    | 数据库                                                |
| **Redis**        | 8.3      | 缓存数据库 + 向量数据库                                      |
| **Druid**        | 1.2.28   | 数据库连接池                                             |
| **Knife4j**      | 4.4.0    | API文档                                              |
| **EasyExcel**    | 4.0.3    | Excel处理                                            |
| **Quartz**       | 2.5.0    | 定时任务                                               |
| **Hutool**       | 5.8.24   | 工具类库                                               |
| **Lombok**       | 1.18.32  | 代码简化                                               |
| **MinIO**        | 8.6.0    | 对象存储                                               |
| **FastJSON2**    | 2.0.57   | JSON处理                                             |

### 前端技术栈

| 技术                     | 版本      | 说明         |
| ---------------------- | ------- | ---------- |
| **Vue**                | 3.4.21  | 前端框架       |
| **TypeScript**         | 5.4.2   | 类型系统       |
| **Vite**               | 5.1.6   | 构建工具       |
| **Element Plus**       | 2.6.1   | UI组件库      |
| **vxe-table**          | 4.15.2  | 高性能表格      |
| **vue-element-plus-x** | 1.3.98  | AI聊天组件库    |
| **Pinia**              | 2.1.7   | 状态管理       |
| **Vue Router**         | 4.3.0   | 路由管理       |
| **ECharts**            | 5.6.0   | 图表库        |
| **Three.js**           | 0.183.2 | 3D可视化      |
| **Monaco Editor**      | -       | 代码编辑器      |
| **WangEditor**         | 5.1.23  | 富文本编辑器     |
| **axios**              | 1.6.8   | HTTP请求     |
| **markdown-it**        | 14.1.0  | Markdown渲染 |

***

## 数据库设计

系统共包含 **67张数据表**，涵盖AI智能、代码生成、系统管理、定时任务等完整业务领域。

### 数据表分类

| 分类   | 表数量 | 主要表                                                           |
| ---- | --- | ------------------------------------------------------------- |
| AI智能 | 16  | ai\_model, ai\_provider, ai\_prompt, ai\_knowledge\_doc等      |
| 代码生成 | 8   | gen\_config, gen\_data\_source, gen\_template等                |
| 系统管理 | 22  | sys\_user, sys\_role, sys\_menu, sys\_depart, sys\_position 等 |
| 定时任务 | 12  | qrtz\_\* 系列（Quartz标准表）、quartz\_job、quartz\_running\_log       |
| 导入任务 | 1   | import\_task                                                  |

### 设计规范

- 统一使用 **utf8mb4** 字符集，**InnoDB** 引擎
- 主键统一为 **varchar(32)**
- 必须包含审计字段（create\_by, create\_name, create\_time, update\_by, update\_name, update\_time）
- 必须包含 **is\_delete** 逻辑删除字段（0:已删除, 1:正常）
- 索引命名规范：唯一索引 `uk_字段名`，普通索引 `idx_字段名`
- 所有表和字段均有中文注释

***

## 快速开始

### 环境要求

- **JDK**: 21+
- **Node.js**: 18+
- **MySQL**: 8.0+
- **Redis**: 6.0+
- **Maven**: 3.8+

### 后端启动

```bash
# 进入后端目录
cd ys-boot-pro

# 导入数据库（创建数据库后执行）
# 将 ys-blog.sql 导入到 MySQL

# 修改配置文件
# 编辑 ys-starter/src/main/resources/application-dev.yml
# 配置数据库连接、Redis连接等

# 编译打包
mvn clean package -DskipTests

# 启动应用（方式1：IDEA运行 YsApplication.java）
# 启动应用（方式2：命令行）
mvn spring-boot:run -pl ys-starter

# 访问地址
# API文档: http://localhost:8910/ysblog/doc.html
# Druid监控: http://localhost:8910/ysblog/druid/
```

### 前端启动

```bash
# 进入前端目录
cd ys-vue-pro

# 安装依赖
npm install

# 开发模式启动
npm run dev

# 访问地址: http://localhost:8888
```

### 生产部署

```bash
# 前端构建
npm run build

# 后端打包
mvn clean package -DskipTests

# 部署 ys-starter/target/ys-starter-*.jar
java -jar ys-starter-*.jar
```

***

## 功能截图

### AI智能助手

#### AI对话

![AI对话-新会话](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220329573.png)

![AI对话-消息交互](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220334651.png)

> 支持多轮对话、流式输出、深度思考、多模型切换、附件上传、提示词润色、联网搜索

#### AI助手管理

![AI助手管理-列表](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910594.png)

![AI助手管理-配置](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220337503.png)

> AI助手配置、角色设定、能力描述、模型绑定、个性化定制

#### 助手会话

![助手会话列表](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220346529.png)

> 助手对话记录、会话管理、多助手并发对话

#### 模型管理

![模型管理-列表](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910595.png)

![模型管理-编辑](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910596.png)

> 多供应商AI模型配置、参数设置、模型关联、调用限额

#### 供应商管理

![供应商管理-列表](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910597.png)

![供应商管理-新增](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910598.png)

> AI供应商配置管理、API Key管理、基础URL配置

#### 知识库管理

![知识文档列表](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910599.png)

![知识分片列表](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910600.png)

![知识库测试](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910601.png)

> RAG知识库、文档向量化、智能分块、语义检索、知识分片管理

#### 意图规则

![意图规则-列表](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910603.png)

![意图规则-详情](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910604.png)

> 智能意图识别、规则配置、自动分类、业务路由

#### Function Calling

![Function Calling列表](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910605.png)

> 函数调用管理、工具注册、参数映射、自动执行

#### 页面上下文

![页面上下文-列表](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910606.png)

![页面上下文-编辑](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910607.png)

> 页面状态管理、上下文缓存、会话保持

#### 报表元数据

![报表元数据列表](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910608.png)

> 报表数据源配置、字段映射、查询条件定义、动态报表生成

#### 错误知识库

![错误知识列表](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910609.png)

> 错误解决方案积累、智能推荐、问题分类、快速检索

#### API调用日志

![API调用日志](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910610.png)

![API调用详情](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910611.png)

> API调用记录、Token消耗统计、响应时间监控、错误日志

#### Token配置

![Token配置](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910612.png)

> Token消耗配置、限额管理、使用统计、成本控制

***

### 代码生成器

#### 数据源管理

![数据源管理](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910613.png)

![编辑数据源](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910614.png)

> 多数据源配置、数据库连接测试，支持MySQL/Oracle/PostgreSQL/SQLServer/达梦/人大金仓

#### 模板管理

![模板管理](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910615.png)

![编辑模板-实体类](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910616.png)

![编辑模板-Mapper接口](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910617.png)

> Monaco编辑器、FreeMarker/Velocity语法、在线预览、模板分类管理

#### 模板集管理

![模板集管理](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910618.png)

![编辑模板集](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910619.png)

![模板集文件列表](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910620.png)

> 模板集CRUD、模板关联、团队共享、版本管理

#### 参数配置

![参数配置](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910621.png)

> 包名配置、作者信息、表前缀、字段映射

#### 五步代码生成向导

![步骤1-选择数据源](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910622.png)

![步骤2-选择数据表](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910623.png)

![步骤3-选择模板集](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910624.png)

![步骤4-配置字段](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910625.png)

![步骤5-预览与生成](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910626.png)

> 可视化引导：数据源 → 表选择 → 模板集 → 参数配置 → 预览生成

***

### 系统管理

#### 用户管理

![用户管理](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910627.png)

![新增用户](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910628.png)

> 用户增删改查、角色分配、部门分配、密码管理、导入导出

#### 角色管理

![角色管理](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910629.png)

![新增角色](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910630.png)

![权限配置](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910631.png)

![角色分配用户](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910632.png)

![角色数据导入](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910633.png)

> 角色权限配置、菜单权限、数据权限、用户分配、数据导入

#### 菜单管理

![菜单管理](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910634.png)

![新增菜单](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910635.png)

> 动态路由配置、菜单权限控制、按钮权限

#### 部门管理

![部门管理](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910636.png)

![新增部门](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910637.png)

![部门用户列表](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910638.png)

> 组织架构管理、树形结构展示、部门人员管理

#### 岗位管理

![岗位管理](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910639.png)

![新增岗位](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910640.png)

> 岗位CRUD、职级管理

#### 字典管理

![字典管理](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910641.png)

![字典数据](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910642.png)

> 数据字典维护、字典项管理、自动翻译

#### 参数配置

![参数配置](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910643.png)

> 系统参数配置、动态修改生效

#### 数据权限

![数据权限规则列表](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910644.png)

![数据权限配置](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910645.png)

![新增数据权限规则](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910646.png)

> 数据权限规则配置、角色绑定、灵活的数据权限控制

#### 文件管理

![文件管理列表](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910647.png)

![文件上传](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910648.png)

![文件预览-图片](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910649.png)

![文件预览-Markdown](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910650.png)

> 文件上传、分片上传、断点续传、6种存储方式、在线预览

#### 操作日志

![操作日志](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910651.png)

![异常日志](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910652.png)

![查询日志](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910653.png)

![日志统计分析](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910654.png)

> 操作审计日志、登录日志、异常记录、日志分析统计

#### 通知公告

![通知公告列表](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910655.png)

![新增通知公告](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910656.png)

![阅读统计](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910657.png)

> 公告发布、富文本编辑、阅读统计、弹窗通知

#### 接收者配置

![接收者配置](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910658.png)

> 通知接收者配置、定向推送

***

### 站内信管理

![消息管理列表](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910659.png)

![发送站内信](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910660.png)

![消息记录](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910661.png)

![模板管理](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910662.png)

> 站内信发送、消息模板、消息记录、阅读状态追踪

***

### 敏感词管理

![敏感词配置](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910663.png)

![新增敏感词](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910664.png)

![敏感词白名单](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910665.png)

![敏感词过滤日志](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910666.png)

> 敏感词库、白名单、检测日志、自动过滤、替换策略

***

### 系统监控

#### 服务器监控

![服务器监控](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910667.png)

> CPU、内存、磁盘、网络、JVM实时监控

#### Redis监控

![Redis监控](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910668.png)

> Redis监控、内存使用、键值统计、连接信息

#### 定时任务

![定时任务列表](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910669.png)

![定时任务详情](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910670.png)

![新增定时任务](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910671.png)

![定时任务统计面板](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910672.png)

![定时任务执行统计](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910673.png)

> 定时任务管理、Cron表达式、执行日志、集群支持、执行统计

***

### 工作台

#### CMS内容管理

![CMS内容管理](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910674.png)

> 文章发布审核、数据统计分析、内容管理

#### CRM客户管理

![CRM客户管理](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910675.png)

> 销售业绩追踪、待跟进客户管理、合同回款监控

#### 系统监控仪表板

![系统监控仪表板](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910676.png)

> 服务器状态、关键指标概览、数据库监控

#### 运营管理

![运营管理](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910677.png)

![运营管理完整视图](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910678.png)

> 全渠道运营、数据分析、营销工具

#### 工作流审批

![工作流审批](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910679.png)

> 流程设计、审批管理、任务分配、进度追踪

***

### 数据大屏

![校园大屏](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910680.png)

![电商大屏](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910681.png)

![能源大屏](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910682.png)

![金融大屏](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910683.png)

![物流大屏](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910684.png)

> 多行业数据大屏模板，包含校园、电商、能源、金融、物流等12+行业模板，支持3D可视化效果

***

### 个人中心

![个人信息](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910685.png)

![安全设置](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910686.png)

![我的权限](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910688.png)

![消息中心](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910689.png)

![日志中心](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910690.png)

![账号管理](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910691.png)

![布局配置](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910692.png)

> 个人信息修改、安全设置、权限查看、消息中心、日志中心、账号管理、布局配置

***

### 通知中心

![通知公告](https://gitee.com/lqclf/typora-image/raw/master/img/20260514220910693.png)

> 通知公告查看、已读未读管理

***

## 核心功能模块

### 1. 系统管理

| 功能     | 说明                         | 特点                          |
| ------ | -------------------------- | --------------------------- |
| 用户管理   | 用户CRUD、角色分配、部门分配、密码管理、导入导出 | Excel批量导入导出、数据权限隔离          |
| 角色管理   | 角色CRUD、菜单权限、数据权限配置         | 支持数据范围控制（全部/本部门/本部门及以下/仅本人） |
| 菜单管理   | 菜单CRUD、按钮权限、动态路由配置         | 支持目录、菜单、按钮三种类型              |
| 部门管理   | 组织架构管理、树形结构展示              | 支持无限层级部门结构                  |
| 岗位管理   | 岗位CRUD、职级管理                | 支持岗位编码、等级                   |
| 字典管理   | 数据字典维护、字典项翻译               | 支持字典缓存、自动翻译                 |
| 参数配置   | 系统参数配置、动态修改生效              | 支持参数分类管理                    |
| 通知公告   | 公告发布、定向推送、状态追踪             | 支持全体/指定用户推送                 |
| 文件管理   | 文件上传、存储配置、文件预览             | 6种存储方式、在线预览                 |
| 日志管理   | 操作日志、登录日志、异常日志             | 异步记录、日志分析统计                 |
| 敏感词管理  | 敏感词库、白名单、检测日志              | 自动过滤、替换策略                   |
| 数据权限规则 | 数据权限规则配置、角色绑定              | 灵活的数据权限控制                   |
| 站内信管理  | 站内信发送、接收、阅读状态追踪            | 支持多种消息类型、消息模板               |
| 个人中心   | 个人信息修改、安全设置、权限查看           | 用户自助管理                      |

### 2. AI智能助手

| 功能               | 说明                                                               | 技术亮点              |
| ---------------- | ---------------------------------------------------------------- | ----------------- |
| AI对话             | 多轮对话、流式输出、深度思考、历史会话管理、多模型切换                                      | WebFlux响应式流、SSE推送 |
| 模型管理             | 多供应商配置（OpenAI、DeepSeek、智谱AI、MiniMax、Moonshot、Doubao、Ollama、Qwen） | Spring AI统一适配     |
| 供应商管理            | AI供应商配置管理、API Key管理、基础URL配置、模型关联                                 | 多供应商统一接入          |
| 提示词管理            | 提示词模板、变量替换、快捷使用、分类管理、默认提示词设置                                     | 支持占位符动态替换         |
| 会话管理             | 历史会话、会话标题、删除管理、消息统计                                              | 会话持久化、消息上下文       |
| 知识库管理            | RAG检索增强、文档向量化、智能分块、知识文档管理、知识分片管理、语义检索                            | Redis向量存储、语义检索    |
| 意图规则             | 智能意图识别、规则配置、自动分类、业务路由                                            | 基于AI的意图分类         |
| 错误知识库            | 错误解决方案积累、智能推荐、问题分类、快速定位                                          | 知识沉淀、快速检索         |
| 代码生成             | AI辅助代码生成、智能补全、代码优化、多语言支持                                         | 多语言支持、代码质量分析      |
| 表单生成             | 智能表单生成、字段智能识别、拖拽式配置、实时预览                                         | AI驱动的表单设计         |
| Function Calling | 函数调用管理、工具注册、参数映射、自动执行                                            | 注解注册+自动执行         |
| 助手管理             | AI助手配置、角色设定、能力描述、模型绑定                                            | 个性化助手定制           |
| 助手会话             | 助手对话记录、会话管理、消息追踪                                                 | 多助手并发对话           |
| API调用日志          | API调用记录、Token消耗统计、响应时间监控、错误日志                                    | 异步记录、性能监控         |
| 报表元数据            | 报表数据源配置、字段映射、查询条件定义                                              | 动态报表生成            |
| 页面上下文            | 页面状态管理、上下文缓存、会话保持                                                | 上下文持久化            |
| 知识库测试            | 知识库检索测试、向量相似度查询、检索结果验证                                           | 语义检索测试工具          |
| Token配置          | Token消耗配置、限额管理、使用统计                                              | 成本控制、用量追踪         |

### 3. 代码生成器

| 功能    | 说明                                   | 特点                                          |
| ----- | ------------------------------------ | ------------------------------------------- |
| 数据源管理 | 多数据源配置、数据库连接测试                       | 支持MySQL/Oracle/PostgreSQL/SQLServer/达梦/人大金仓 |
| 表选择   | 数据表浏览、字段查看、主键识别                      | 元数据自动读取                                     |
| 模板集管理 | 模板集CRUD、模板关联、团队共享                    | 支持版本管理                                      |
| 模板管理  | Monaco编辑器、FreeMarker/Velocity语法、在线预览 | 语法高亮、实时预览                                   |
| 参数配置  | 包名配置、作者信息、表前缀、字段映射                   | 灵活配置                                        |
| 代码预览  | 实时预览、批量生成、下载代码                       | 支持单表/多表批量生成                                 |
| 五步向导  | 数据源→表选择→模板集→参数配置→预览生成                | 可视化引导                                       |

### 4. Excel处理中心

| 功能   | 说明                   | 性能指标          |
| ---- | -------------------- | ------------- |
| 导入配置 | 字段映射、校验规则、别名库        | 百万级数据流式处理     |
| 导入任务 | 文件上传、数据预览、批量导入、错误报告  | 异步处理、进度实时推送   |
| 导出配置 | 导出模板、字段选择、条件配置、样式配置  | 支持复杂样式、大数据量导出 |
| 导出任务 | 异步导出、进度追踪、文件下载       | WebSocket进度推送 |
| 任务中心 | 任务列表、状态查看、重新执行       | 任务持久化、失败重试    |
| 数据校验 | 全维度校验（必填/格式/唯一性/自定义） | 错误精确定位        |

### 5. 系统监控

| 功能      | 说明                     | 监控维度          |
| ------- | ---------------------- | ------------- |
| 服务器监控   | CPU、内存、磁盘、JVM、网络实时监控   | WebSocket实时推送 |
| Redis监控 | 内存使用、键值统计、连接信息         | 命令统计、慢查询分析    |
| 在线用户    | 在线用户列表、强制下线            | Session管理     |
| 定时任务    | 任务管理、Cron表达式、执行日志、暂停恢复 | 集群支持、分布式锁     |
| 数据权限    | 数据规则配置、角色数据范围          | 动态SQL拦截       |

### 6. 数据大屏

| 大屏     | 说明               | 应用场景  |
| ------ | ---------------- | ----- |
| 总览大屏   | 系统整体运行状态、关键指标监控  | 管理驾驶舱 |
| 校园大屏   | 校园运营数据、学生管理、教学监控 | 教育行业  |
| 电商大屏   | 销售数据、订单分析、用户行为   | 电商平台  |
| 能源大屏   | 能源消耗、设备状态、异常预警   | 能源管理  |
| 金融大屏   | 交易数据、风险监控、业务指标   | 金融行业  |
| 政务大屏   | 政务服务、民生数据、政策执行   | 政务系统  |
| 人力资源大屏 | 员工数据、考勤管理、薪酬分析   | 企业HR  |
| 物联网大屏  | 设备状态、数据采集、故障预警   | 物联网平台 |
| 物流大屏   | 物流轨迹、仓储管理、配送分析   | 物流行业  |
| 制造大屏   | 生产数据、设备状态、质量监控   | 制造业   |
| 医疗大屏   | 患者数据、医疗资源、就诊分析   | 医疗行业  |
| 用户行为大屏 | 用户活跃、行为分析、转化漏斗   | 产品运营  |

### 7. 工作台

| 工作台     | 说明                   | 特点          |
| ------- | -------------------- | ----------- |
| CMS内容管理 | 文章发布、栏目管理、评论审核、SEO优化 | 富文本编辑器、内容管理 |
| CRM客户管理 | 客户跟进、销售漏斗、合同管理、业绩统计  | 客户画像、销售管理   |
| 运营管理    | 活动管理、用户运营、数据分析、营销工具  | 全渠道运营、数据分析  |
| 系统监控仪表板 | 服务器状态、数据库监控、关键指标概览   | 实时监控、告警     |
| 工作流审批   | 流程设计、审批管理、任务分配、进度追踪  | 可视化流程、智能审批  |

***

## 配置说明

### 后端配置

**数据库配置** (`application-dev.yml`):

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/xxx?characterEncoding=UTF-8&useUnicode=true&useSSL=false&serverTimezone=Asia/Shanghai
    username: root
    password: your_password
```

**Redis配置**:

```yaml
spring:
  data:
    redis:
      host: 127.0.0.1
      port: 6379
      password: your_password
      database: 0
```

**文件存储配置**:

```yaml
file:
  storage:
    type: local  # 可选: local/minio/oss/cos/s3/rustfs
    local:
      upload-path: /data/upload
```

**AI配置**:

```yaml
spring:
  ai:
    openai:
      api-key: your_api_key
      base-url: https://api.openai.com
    deepseek:
      api-key: your_api_key
    zhipuai:
      api-key: your_api_key
```

### 前端配置

**环境配置** (`.env.development`):

```bash
VITE_API_URL = http://127.0.0.1:8910/ysblog/
VITE_PORT = 8888
```

***

## 开发规范

### 后端注解使用

| 注解                   | 功能      | 使用位置         |
| -------------------- | ------- | ------------ |
| `@MyLog`             | 记录操作日志  | Controller方法 |
| `@DataPermission`    | 数据权限控制  | Controller方法 |
| `@DictTranslate`     | 字典自动翻译  | 实体类字段        |
| `@EnumTranslate`     | 枚举自动翻译  | 实体类字段        |
| `@SensitiveField`    | 敏感数据脱敏  | 实体类字段        |
| `@SaCheckPermission` | 权限校验    | Controller方法 |
| `@RateLimiter`       | 限流控制    | Controller方法 |
| `@AutoDict`          | 自动字典翻译  | Controller方法 |
| `@AutoEnum`          | 自动枚举翻译  | Controller方法 |
| `@Sensitive`         | 敏感数据编解码 | Controller方法 |

### 前端开发规范

- **页面组件**: 使用 `<script setup>` 语法，添加 `name` 属性
- **API封装**: 按模块组织，使用 `useXxxApi()` 函数式封装
- **表格组件**: 使用 `YsTable` 组件，支持自动高度、分页、跨页选中
- **弹窗组件**: 使用 `YsDialog` 组件，统一弹窗样式
- **懒加载**: 弹窗等非首屏组件使用 `defineAsyncComponent` 懒加载

### 代码分层架构

```
Controller层 → Service层 → Mapper层 → 数据库
    ↓           ↓          ↓
   参数      业务逻辑     SQL操作
   校验       事务       数据访问
```

***

## 适用场景

- 企业级中后台管理系统快速开发
- 电商平台管理后台
- 金融系统运营后台
- 教育系统管理平台
- 医疗系统管理后台
- 政务系统管理平台
- 物联网设备管理平台
- 大数据可视化平台

***

## 适用人群与二次开发指南

### 适用人群

| 人群类型           | 技术背景               | 使用方式                     |
| -------------- | ------------------ | ------------------------ |
| **全栈开发者**      | 熟悉Java + Vue       | 直接使用代码生成器生成CRUD，快速搭建业务系统 |
| **后端工程师**      | 精通Java/Spring Boot | 专注于业务逻辑开发，前端使用平台生成的标准页面  |
| **前端工程师**      | 精通Vue3/TypeScript  | 基于平台提供的组件库，快速构建交互界面      |
| **技术团队Leader** | 架构设计经验             | 作为团队基础框架，统一技术栈和开发规范      |
| **独立开发者**      | 全栈能力               | 快速交付项目，降低开发成本，提高接单效率     |
| **企业IT部门**     | 内部系统开发             | 构建企业内部应用，实现数字化转型         |

### 基于本平台可二次开发的项目

#### 企业管理系统

- **OA办公系统**：流程审批、考勤管理、会议管理、公文流转
- **CRM客户关系管理**：客户跟进、销售漏斗、合同管理、业绩统计
- **ERP企业资源计划**：采购管理、库存管理、生产管理、财务管理
- **HRM人力资源管理**：招聘管理、员工档案、薪酬绩效、培训发展
- **项目管理**：任务分配、进度跟踪、工时统计、资源调度

#### 电商平台

- **B2B/B2C商城管理后台**：商品管理、订单处理、营销活动、会员管理
- **供应链管理系统**：供应商管理、采购计划、物流跟踪、仓储管理
- **跨境电商ERP**：多平台店铺管理、海外仓管理、报关清关

#### 行业解决方案

- **智慧教育系统**：课程管理、在线考试、学籍管理、家校互通
- **智慧医疗平台**：预约挂号、电子病历、药品管理、医保对接
- **智慧政务系统**：事项审批、一网通办、数据共享、监管大屏
- **智慧物业系统**：业主管理、收费管理、报修工单、设备巡检
- **智慧园区平台**：门禁管理、停车管理、能耗监控、安防监控

#### 工具类应用

- **内容管理系统(CMS)**：文章发布、栏目管理、评论审核、SEO优化
- **工单系统**：问题提交、工单分配、处理跟踪、满意度评价
- **问卷调查系统**：问卷设计、数据收集、统计分析、报表导出
- **知识库系统**：文档管理、全文检索、版本控制、权限管理

### 二次开发优势

| 优势       | 说明                |
| -------- | ----------------- |
| **快速起步** | 基础功能开箱即用，省去重复造轮子  |
| **规范统一** | 遵循企业级开发规范，代码质量有保障 |
| **易于扩展** | 模块化架构，按需引入功能模块    |
| **AI加持** | 内置AI能力，可快速集成智能功能  |
| **文档完善** | 详细的代码注释和使用文档      |
| **社区支持** | 活跃的技术社区，问题快速响应    |

***

## 后续开发计划（Roadmap）

### Phase 1: 工作流引擎（Workflow Engine）+ 动态表单

| 功能模块         | 功能描述                 | 预期效果       |
| ------------ | -------------------- | ---------- |
| **可视化流程设计器** | 拖拽式流程图设计，支持BPMN2.0规范 | 零代码设计业务流程  |
| **流程模型管理**   | 流程版本控制、发布、下线         | 流程全生命周期管理  |
| **审批节点配置**   | 会签、或签、转办、加签、委托       | 灵活适配各种审批场景 |
| **条件分支**     | 基于表单字段、用户角色的动态路由     | 智能流程流转     |
| **流程监控**     | 实时查看流程实例状态、干预异常流程    | 运维可视化      |
| **流程分析**     | 流程耗时分析、瓶颈识别、优化建议     | 持续优化业务流程   |
| **集成代码生成**   | 根据流程自动生成业务表单和代码      | 快速落地工作流应用  |

### Phase 2: AI 数据分析与可视化

| 功能           | 描述                       | 使用方式      |
| ------------ | ------------------------ | --------- |
| **上传即分析**    | 上传Excel/CSV表格，AI自动分析数据特征 | 拖拽上传，秒级响应 |
| **自动结论生成**   | 智能识别数据趋势、异常、关联性，生成分析结论   | 自然语言报告    |
| **数据洞察**     | 自动发现数据中的关键指标和业务洞察        | 辅助决策      |
| **自然语言生成图表** | "展示近半年销售额趋势" → 自动生成折线图   | 对话式BI     |
| **智能图表推荐**   | 根据数据特征推荐最适合的图表类型         | 最佳可视化     |
| **报表自动排版**   | 多个图表智能布局，生成仪表盘           | 专业报表      |

### Phase 3: AI 智能搜索中心

| 功能         | 描述             | 技术实现   |
| ---------- | -------------- | ------ |
| **统一搜索入口** | 一个搜索框检索全系统数据   | 多源数据聚合 |
| **权限感知**   | 只搜索用户有权限查看的数据  | 数据安全   |
| **分类筛选**   | 按模块、类型、时间等维度筛选 | 精准定位   |
| **搜索历史**   | 保存搜索记录，快速复用    | 便捷体验   |

### Phase 4: 其他规划功能

| 模块      | 功能         | 说明         |
| ------- | ---------- | ---------- |
| **多租户** | SaaS化多租户架构 | 一套系统服务多个客户 |

> 欢迎提需求：如果您有想要的功能，欢迎在 Issues 中提出，我们会优先考虑！

***

## 参与贡献

1. Fork 本仓库
2. 新建分支 (`git checkout -b feature/xxx`)
3. 提交更改 (`git commit -am 'Add some features'`)
4. 推送分支 (`git push origin feature/xxx`)
5. 创建 Pull Request

***

## 开源协议

本项目基于 [MIT](LICENSE) 协议开源。

***

## 联系我们

- **微信公众号**: ”Eric的技术杂货库“，可留言需求 
  
  <img src="https://gitee.com/lqclf/typora-image/raw/master/images/Eric微信公众号二维码.jpg" alt="Eric的技术杂货库" width="250">
  
- **Gitee**: [https://gitee.com/lqclf/ys-lowcode-open](https://gitee.com/lqclf/ys-lowcode-open)
- **GitHub**: [https://github.com/lqclf/ys-code-ai-open](https://github.com/lqclf/ys-code-ai-open)
- **邮箱**: <1921277843@qq.com>
- **官网**: <https://yscode.dev> (即将上线)

***

> 如果这个项目对您有帮助，请给它一个 Star！
>
> 您的支持是我们持续更新的动力！

