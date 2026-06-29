# TeamSync 3.0 - 企业级团队协作管理平台

## 项目简介

TeamSync 3.0 是一个基于 Spring Boot + Vue 3 的企业级团队协作管理平台，支持项目管理、任务看板、文档共享、日程管理、团队协作、消息通知等功能。
该项目采用现代化技术栈，具有良好的架构设计和用户体验。

## 技术栈

### 后端技术
- **核心框架**: Spring Boot 2.7
- **持久层**: MyBatis
- **数据库**: MySQL 8.0
- **缓存**: Spring Cache
- **实时通信**: WebSocket
- **认证授权**: JWT
- **日志**: AOP + SLF4J
- **工具库**: Hutool, Apache POI, Lombok
- **加密**: Spring Security BCrypt

### 前端技术
- **核心框架**: Vue 3 + Vite
- **UI组件库**: Element Plus
- **状态管理**: Pinia
- **图表库**: ECharts
- **日程管理**: FullCalendar
- **富文本编辑**: WangEditor
- **路由**: Vue Router
- **HTTP客户端**: Axios
- **日期处理**: Day.js

## 功能特性

### 核心功能
- ✅ **用户系统**: 登录/注册、JWT认证、角色权限（管理员/组长/组员）、个人信息管理、深色主题切换
- ✅ **项目管理**: 创建项目、项目成员管理、项目状态管理、项目统计
- ✅ **任务管理**: 任务CRUD、看板拖拽、优先级/截止日期/负责人、任务评论、附件、甘特图
- ✅ **文档管理**: 文件上传/下载、分类管理、下载次数统计
- ✅ **团队管理**: 团队创建、成员管理、团队协作
- ✅ **日程管理**: 月/周/日视图、日程创建、编辑、删除、拖拽调整
- ✅ **项目里程碑**: 里程碑规划、状态追踪
- ✅ **消息通知**: 任务分配通知、实时推送、已读/未读管理
- ✅ **数据统计**: 仪表盘、任务分布图、项目进度、甘特图
- ✅ **数据导出**: Excel格式数据导出
- ✅ **操作日志**: 系统操作记录、管理员审计

### 高级特性
- 🚀 **实时通知**: WebSocket实时消息推送
- 🎨 **深色主题**: 支持浅色/深色模式切换
- 📱 **响应式设计**: 支持多种屏幕尺寸
- 🔒 **权限控制**: 基于角色的访问控制
- 💾 **缓存优化**: Spring Cache性能优化
- 📝 **AOP日志**: 自动操作日志记录

## 项目结构

```
teamsync/
├── teamsync-backend/                    # 后端模块
│   ├── pom.xml                          # Maven配置
│   ├── sql/
│   │   └── init.sql                     # 数据库初始化脚本
│   └── src/main/
│       ├── java/com/teamsync/
│       │   ├── TeamSyncApplication.java# 启动类
│       │   ├── common/                  # 通用类
│       │   ├── config/                  # 配置类（CORS、拦截器、WebSocket、缓存）
│       │   ├── controller/              # 控制器层
│       │   ├── dto/                     # 数据传输对象
│       │   ├── entity/                  # 实体类
│       │   ├── exception/               # 全局异常处理
│       │   ├── interceptor/             # JWT拦截器
│       │   ├── mapper/                  # MyBatis Mapper接口
│       │   ├── service/                 # 业务逻辑层
│       │   ├── util/                    # 工具类（JWT、Excel导出等）
│       │   └── aspect/                  # AOP切面（日志）
│       └── resources/
│           ├── application.yml          # 应用配置
│           └── mapper/                  # MyBatis XML映射文件
│
└── teamsync-frontend/                   # 前端模块
    ├── package.json                     # NPM配置
    ├── vite.config.js                   # Vite配置
    ├── index.html
    └── src/
        ├── main.js                      # 入口
        ├── App.vue                      # 根组件
        ├── router/index.js              # 路由配置
        ├── stores/user.js               # Pinia状态管理
        ├── utils/request.js             # Axios封装
        ├── utils/websocket.js           # WebSocket客户端
        ├── api/                         # API接口模块
        ├── layout/MainLayout.vue        # 布局框架
        └── views/                       # 页面组件
            ├── Login.vue                # 登录
            ├── Dashboard.vue            # 工作台（含甘特图）
            ├── Projects.vue             # 项目管理
            ├── ProjectDetail.vue        # 项目详情
            ├── MyTasks.vue              # 我的任务
            ├── Documents.vue            # 文档中心
            ├── Schedule.vue             # 日程管理（新增）
            ├── Notifications.vue        # 消息通知
            ├── Profile.vue              # 个人中心
            ├── Users.vue                # 用户管理
            └── OperationLogs.vue        # 操作日志
```

## 数据库设计

### 核心数据表
- **user**: 用户表（新增department、position、theme字段）
- **team**: 团队表（新增）
- **team_member**: 团队成员表（新增）
- **project**: 项目表
- **project_member**: 项目成员表
- **milestone**: 项目里程碑表（新增）
- **task**: 任务表
- **task_comment**: 任务评论表
- **task_attachment**: 任务附件表（新增）
- **document**: 文档表
- **notification**: 通知表
- **schedule**: 日程表（新增）
- **operation_log**: 操作日志表

## 快速开始

### 环境要求
- JDK 1.8+
- Maven 3.6+
- MySQL 8.0+
- Node.js 16+ (前端开发)

### 1. 数据库初始化

```bash
# 登录 MySQL，执行初始化脚本
mysql -u root -p < sql/init.sql
```

或在 MySQL 客户端中执行 `sql/init.sql` 文件中的 SQL 语句。

### 2. 修改数据库配置

编辑 `src/main/resources/application.yml`:

```yaml
spring:
  datasource:
    url: jdbc:mysql://localhost:3306/teamsync?useSSL=false&serverTimezone=Asia/Shanghai&characterEncoding=utf8&allowPublicKeyRetrieval=true
    username: root        # 改为你的 MySQL 用户名
    password: 123456      # 改为你的 MySQL 密码
```

### 3. 启动后端

```bash
cd teamsync-backend
mvn spring-boot:run
```

后端运行在 http://localhost:8080

### 4. 启动前端

```bash
cd teamsync-frontend
npm install
npm run dev
```

前端运行在 http://localhost:3000 或自动分配的端口

### 5. 登录系统

默认管理员账号:
- **用户名**: `admin`
- **密码**: `123456`

其他测试账号:
- `zhangsan` / `123456` (组长)
- `lisi` / `123456` (组员，默认深色主题)
- `wangwu` / `123456` (组员)

## API 接口文档

### 认证模块
- `POST /api/auth/login` - 登录
- `POST /api/auth/register` - 注册

### 用户模块
- `GET /api/user/info` - 获取当前用户信息
- `GET /api/user/list` - 用户列表
- `PUT /api/user/update` - 更新用户信息
- `PUT /api/user/password` - 修改密码
- `PUT /api/user/theme` - 切换主题

### 项目模块
- `GET /api/project/list` - 项目列表
- `POST /api/project/create` - 创建项目
- `GET /api/project/{id}` - 项目详情
- `PUT /api/project/{id}` - 更新项目
- `DELETE /api/project/{id}` - 删除项目

### 任务模块
- `GET /api/task/list?projectId=1` - 任务列表
- `POST /api/task/create` - 创建任务
- `PUT /api/task/{id}` - 更新任务
- `PUT /api/task/{id}/status` - 更新任务状态
- `DELETE /api/task/{id}` - 删除任务

### 团队模块（新增）
- `GET /api/teams/list` - 团队列表
- `POST /api/teams/create` - 创建团队
- `GET /api/teams/{id}` - 团队详情
- `PUT /api/teams/{id}` - 更新团队
- `DELETE /api/teams/{id}` - 删除团队

### 里程碑模块（新增）
- `GET /api/milestones/list?projectId=1` - 里程碑列表
- `POST /api/milestones/create` - 创建里程碑
- `PUT /api/milestones/{id}` - 更新里程碑
- `DELETE /api/milestones/{id}` - 删除里程碑

### 日程模块（新增）
- `GET /api/schedules/list` - 日程列表
- `POST /api/schedules/create` - 创建日程
- `PUT /api/schedules/{id}` - 更新日程
- `DELETE /api/schedules/{id}` - 删除日程

### 文档模块
- `POST /api/document/upload` - 上传文件
- `GET /api/document/download/{id}` - 下载文件
- `GET /api/document/list` - 文档列表

### 通知模块
- `GET /api/notification/list` - 通知列表
- `GET /api/notification/unread-count` - 未读通知数
- `PUT /api/notification/{id}/read` - 标记已读

### 导出模块（新增）
- `GET /api/export/tasks` - 导出任务Excel
- `GET /api/export/projects` - 导出项目Excel

### WebSocket（新增）
- `ws://localhost:8080/ws/notification/{userId}` - 实时通知推送

## 核心技术亮点

1. **WebSocket实时通信**: 实现了实时通知推送，支持消息即时送达
2. **Excel数据导出**: 集成Apache POI，支持业务数据导出
3. **AOP日志切面**: 自动记录用户操作，便于审计和追踪
4. **Spring Cache缓存**: 优化系统性能，减少数据库压力
5. **深色主题切换**: 现代化UI体验，用户可自由切换
6. **FullCalendar日程**: 强大的日程管理，支持多种视图和交互
7. **ECharts可视化**: 丰富的数据图表，直观展示项目进度
8. **甘特图进度**: 清晰的项目时间线展示

## 默认用户

| 用户名 | 密码 | 角色 | 备注 |
|--------|------|------|------|
| admin | 123456 | 管理员 | 系统管理员 |
| zhangsan | 123456 | 组长 | 产品经理 |
| lisi | 123456 | 成员 | 后端工程师，默认深色主题 |
| wangwu | 123456 | 成员 | 前端工程师 |
| zhaoliu | 123456 | 成员 | UI设计师 |
| sunqi | 123456 | 成员 | 测试工程师 |

## 开发说明

### 后端开发规范
- 使用 Lombok 简化实体类
- 统一使用 Result 包装返回值
- 使用 @Validated 进行参数校验
- Service 层使用接口 + 实现模式
- 异常使用 GlobalExceptionHandler 统一处理

### 前端开发规范
- 使用 Vue 3 Composition API
- 状态管理使用 Pinia
- UI 组件使用 Element Plus
- 接口请求使用封装的 request 工具

## 版本历史

### v3.0 (当前版本)
- ✨ 新增日程管理功能，支持月/周/日视图
- ✨ 新增团队管理功能
- ✨ 新增项目里程碑管理
- ✨ 新增任务附件功能
- ✨ 新增WebSocket实时通知
- ✨ 新增Excel数据导出
- ✨ 新增深色主题切换
- ✨ 新增AOP操作日志
- ✨ 新增项目甘特图
- ✨ 美化登录页和Dashboard
- 🔧 优化数据库设计，增加部门/职位字段
- 📝 完善项目文档

### v2.0
- 用户系统、项目管理、任务看板、文档共享
- JWT认证、ECharts统计

### v1.0
- 初始版本，基础功能

## 许可证

MIT License

## 联系方式

如有问题，请提交 Issue 或 PR。
