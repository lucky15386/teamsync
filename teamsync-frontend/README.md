# TeamSync - 团队协作管理平台

## 技术栈

### 后端
- Spring Boot 2.7.18
- MyBatis 2.3.2
- MySQL 8.0
- JWT 认证
- BCrypt 密码加密

### 前端
- Vue 3 + Vite
- Element Plus UI
- Vue Router + Pinia
- Axios

## 快速开始

### 1. 初始化数据库

打开 MySQL Workbench 或命令行，执行 `sql/init.sql`：

```bash
mysql -u root -p < teamsync-backend/sql/init.sql
```

### 2. 修改数据库配置

编辑 `teamsync-backend/src/main/resources/application.yml`，修改数据库用户名密码：

```yaml
spring:
  datasource:
    username: root
    password: 你的密码
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

前端运行在 http://localhost:5173

### 5. 登录

| 账号 | 密码 | 角色 |
|------|------|------|
| admin | 123456 | 管理员 |
| zhangsan | 123456 | 组长 |
| lisi | 123456 | 成员 |

## 项目结构

```
teamsync-backend/
├── pom.xml
├── sql/init.sql                    # 数据库初始化脚本
└── src/main/java/com/teamsync/
    ├── TeamSyncApplication.java    # 启动类
    ├── common/                     # 统一响应封装
    ├── config/                     # 配置类
    ├── controller/                 # 控制器 (7个)
    ├── dto/                        # 数据传输对象 (9个)
    ├── entity/                     # 实体类 (8个)
    ├── exception/                  # 全局异常处理
    ├── interceptor/                # JWT拦截器
    ├── mapper/                     # MyBatis接口 (8个)
    ├── service/                    # 业务层 (8个接口+8个实现)
    └── util/                       # JWT工具类

teamsync-frontend/
├── index.html
├── package.json
├── vite.config.js
└── src/
    ├── api/                        # API请求模块 (7个)
    ├── layout/                     # 布局组件
    ├── router/                     # 路由配置
    ├── stores/                     # Pinia状态管理
    └── views/                      # 页面 (9个)
```

## 功能列表

- ✅ 用户登录/注册（JWT鉴权）
- ✅ 工作台仪表盘（统计卡片+任务概览）
- ✅ 项目管理（CRUD + 成员管理）
- ✅ 任务看板（待办/进行中/已完成 + 拖拽移动）
- ✅ 文档管理（上传/下载/分类）
- ✅ 消息通知（未读提醒 + 全部已读）
- ✅ 用户管理（角色/状态管理，仅管理员）
- ✅ 个人中心（修改信息/密码）
