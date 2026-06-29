-- ============================================
-- TeamSync 团队协作管理平台 - 数据库初始化脚本
-- ============================================

CREATE DATABASE IF NOT EXISTS teamsync DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;
USE teamsync;

-- 用户表
DROP TABLE IF EXISTS `user`;
CREATE TABLE `user` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '用户ID',
    `username` VARCHAR(50) NOT NULL UNIQUE COMMENT '用户名',
    `password` VARCHAR(255) NOT NULL COMMENT '密码(BCrypt加密)',
    `real_name` VARCHAR(50) COMMENT '真实姓名',
    `role` VARCHAR(20) NOT NULL DEFAULT 'MEMBER' COMMENT '角色: ADMIN/LEADER/MEMBER',
    `avatar` VARCHAR(500) COMMENT '头像URL',
    `email` VARCHAR(100) COMMENT '邮箱',
    `phone` VARCHAR(20) COMMENT '手机号',
    `department` VARCHAR(100) COMMENT '部门',
    `position` VARCHAR(100) COMMENT '职位',
    `status` TINYINT NOT NULL DEFAULT 1 COMMENT '状态: 0禁用 1启用',
    `theme` VARCHAR(20) DEFAULT 'light' COMMENT '主题: light/dark',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='用户表';

-- 团队表
DROP TABLE IF EXISTS `team`;
CREATE TABLE `team` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '团队ID',
    `name` VARCHAR(100) NOT NULL COMMENT '团队名称',
    `description` TEXT COMMENT '团队描述',
    `avatar` VARCHAR(500) COMMENT '团队头像',
    `creator_id` BIGINT NOT NULL COMMENT '创建者ID',
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE/ARCHIVED',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='团队表';

-- 团队成员表
DROP TABLE IF EXISTS `team_member`;
CREATE TABLE `team_member` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `team_id` BIGINT NOT NULL COMMENT '团队ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role` VARCHAR(20) NOT NULL DEFAULT 'MEMBER' COMMENT '团队内角色: OWNER/ADMIN/MEMBER',
    `join_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_team_user` (`team_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='团队成员表';

-- 项目里程碑表
DROP TABLE IF EXISTS `milestone`;
CREATE TABLE `milestone` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '里程碑ID',
    `project_id` BIGINT NOT NULL COMMENT '项目ID',
    `name` VARCHAR(100) NOT NULL COMMENT '里程碑名称',
    `description` TEXT COMMENT '描述',
    `deadline` DATE COMMENT '截止日期',
    `status` VARCHAR(20) NOT NULL DEFAULT 'PENDING' COMMENT '状态: PENDING/IN_PROGRESS/COMPLETED',
    `sort_order` INT DEFAULT 0 COMMENT '排序',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目里程碑表';

-- 任务附件表
DROP TABLE IF EXISTS `task_attachment`;
CREATE TABLE `task_attachment` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '附件ID',
    `task_id` BIGINT NOT NULL COMMENT '任务ID',
    `name` VARCHAR(200) NOT NULL COMMENT '文件名',
    `file_path` VARCHAR(500) NOT NULL COMMENT '文件存储路径',
    `file_size` BIGINT COMMENT '文件大小(字节)',
    `file_type` VARCHAR(50) COMMENT '文件类型',
    `uploader_id` BIGINT NOT NULL COMMENT '上传者ID',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务附件表';

-- 项目表
DROP TABLE IF EXISTS `project`;
CREATE TABLE `project` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '项目ID',
    `name` VARCHAR(100) NOT NULL COMMENT '项目名称',
    `description` TEXT COMMENT '项目描述',
    `creator_id` BIGINT NOT NULL COMMENT '创建者ID',
    `status` VARCHAR(20) NOT NULL DEFAULT 'ACTIVE' COMMENT '状态: ACTIVE/ARCHIVED/COMPLETED',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目表';

-- 项目成员关联表
DROP TABLE IF EXISTS `project_member`;
CREATE TABLE `project_member` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `project_id` BIGINT NOT NULL COMMENT '项目ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `role` VARCHAR(20) NOT NULL DEFAULT 'MEMBER' COMMENT '项目内角色: OWNER/ADMIN/MEMBER',
    `join_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_project_user` (`project_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='项目成员表';

-- 任务表
DROP TABLE IF EXISTS `task`;
CREATE TABLE `task` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '任务ID',
    `project_id` BIGINT NOT NULL COMMENT '所属项目ID',
    `title` VARCHAR(200) NOT NULL COMMENT '任务标题',
    `description` TEXT COMMENT '任务描述',
    `status` VARCHAR(20) NOT NULL DEFAULT 'TODO' COMMENT '状态: TODO/DOING/DONE',
    `priority` VARCHAR(10) NOT NULL DEFAULT 'MED' COMMENT '优先级: HIGH/MED/LOW',
    `assignee_id` BIGINT COMMENT '负责人ID',
    `creator_id` BIGINT NOT NULL COMMENT '创建者ID',
    `deadline` DATE COMMENT '截止日期',
    `sort_order` INT DEFAULT 0 COMMENT '排序序号',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务表';

-- 任务评论表
DROP TABLE IF EXISTS `task_comment`;
CREATE TABLE `task_comment` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '评论ID',
    `task_id` BIGINT NOT NULL COMMENT '任务ID',
    `user_id` BIGINT NOT NULL COMMENT '评论者ID',
    `content` TEXT NOT NULL COMMENT '评论内容',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='任务评论表';

-- 文档表
DROP TABLE IF EXISTS `document`;
CREATE TABLE `document` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '文档ID',
    `project_id` BIGINT COMMENT '所属项目ID',
    `name` VARCHAR(200) NOT NULL COMMENT '文件名',
    `file_path` VARCHAR(500) NOT NULL COMMENT '文件存储路径',
    `file_url` VARCHAR(500) COMMENT '文件访问URL',
    `file_size` BIGINT COMMENT '文件大小(字节)',
    `file_type` VARCHAR(50) COMMENT '文件类型',
    `uploader_id` BIGINT NOT NULL COMMENT '上传者ID',
    `category` VARCHAR(50) DEFAULT '其他' COMMENT '分类: 需求文档/设计文档/测试文档/其他',
    `download_count` INT DEFAULT 0 COMMENT '下载次数',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='文档表';

-- 通知表
DROP TABLE IF EXISTS `notification`;
CREATE TABLE `notification` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '通知ID',
    `user_id` BIGINT NOT NULL COMMENT '接收者ID',
    `title` VARCHAR(200) NOT NULL COMMENT '通知标题',
    `content` TEXT COMMENT '通知内容',
    `type` VARCHAR(20) NOT NULL COMMENT '类型: TASK/PROJECT/SYSTEM',
    `related_id` BIGINT COMMENT '关联业务ID',
    `is_read` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读: 0未读 1已读',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='通知表';

-- 操作日志表
DROP TABLE IF EXISTS `operation_log`;
CREATE TABLE `operation_log` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `user_id` BIGINT COMMENT '操作者ID',
    `username` VARCHAR(50) COMMENT '操作者用户名',
    `operation` VARCHAR(100) NOT NULL COMMENT '操作描述',
    `method` VARCHAR(200) COMMENT '请求方法',
    `ip` VARCHAR(50) COMMENT '请求IP',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='操作日志表';

-- ============================================
-- 初始化数据
-- ============================================

-- 插入管理员用户 (密码: 123456, BCrypt加密)
INSERT INTO `user` (`username`, `password`, `real_name`, `role`, `avatar`, `email`, `phone`, `department`, `position`, `status`, `theme`) VALUES
('admin', '$2b$10$3ywED4X.Puv3DKRCt2Kzaeafbmk0K1DNLgNuZ2ZDLHaMUfb.Q4Zfi', '系统管理员', 'ADMIN', '', 'admin@teamsync.com', '13800138000', '技术部', '技术总监', 1, 'light'),
('zhangsan', '$2b$10$3ywED4X.Puv3DKRCt2Kzaeafbmk0K1DNLgNuZ2ZDLHaMUfb.Q4Zfi', '张三', 'LEADER', '', 'zhangsan@teamsync.com', '13800138001', '产品部', '产品经理', 1, 'light'),
('lisi', '$2b$10$3ywED4X.Puv3DKRCt2Kzaeafbmk0K1DNLgNuZ2ZDLHaMUfb.Q4Zfi', '李四', 'MEMBER', '', 'lisi@teamsync.com', '13800138002', '技术部', '后端工程师', 1, 'dark'),
('wangwu', '$2b$10$3ywED4X.Puv3DKRCt2Kzaeafbmk0K1DNLgNuZ2ZDLHaMUfb.Q4Zfi', '王五', 'MEMBER', '', 'wangwu@teamsync.com', '13800138003', '技术部', '前端工程师', 1, 'light'),
('zhaoliu', '$2b$10$3ywED4X.Puv3DKRCt2Kzaeafbmk0K1DNLgNuZ2ZDLHaMUfb.Q4Zfi', '赵六', 'MEMBER', '', 'zhaoliu@teamsync.com', '13800138004', '设计部', 'UI设计师', 1, 'light'),
('sunqi', '$2b$10$3ywED4X.Puv3DKRCt2Kzaeafbmk0K1DNLgNuZ2ZDLHaMUfb.Q4Zfi', '孙七', 'MEMBER', '', 'sunqi@teamsync.com', '13800138005', '测试部', '测试工程师', 1, 'light');

-- 插入团队数据
INSERT INTO `team` (`name`, `description`, `creator_id`, `status`) VALUES
('产品研发团队', '负责产品研发和技术创新', 1, 'ACTIVE'),
('设计团队', '负责产品设计和用户体验', 2, 'ACTIVE'),
('测试质量团队', '负责产品测试和质量保障', 1, 'ACTIVE');

-- 团队成员
INSERT INTO `team_member` (`team_id`, `user_id`, `role`) VALUES
(1, 1, 'OWNER'), (1, 2, 'ADMIN'), (1, 3, 'MEMBER'), (1, 4, 'MEMBER'), (1, 5, 'MEMBER'), (1, 6, 'MEMBER'),
(2, 2, 'OWNER'), (2, 5, 'ADMIN'),
(3, 1, 'OWNER'), (3, 6, 'ADMIN');

-- 项目里程碑
INSERT INTO `milestone` (`project_id`, `name`, `description`, `deadline`, `status`, `sort_order`) VALUES
(1, '项目启动', '项目启动，需求确认', '2026-06-10', 'COMPLETED', 1),
(1, '开发阶段', '核心功能开发', '2026-06-30', 'IN_PROGRESS', 2),
(1, '测试阶段', '系统测试和修复', '2026-07-10', 'PENDING', 3),
(1, '上线发布', '系统正式上线', '2026-07-15', 'PENDING', 4);

-- 插入示例项目
INSERT INTO `project` (`name`, `description`, `creator_id`, `status`) VALUES
('TeamSync 2.0', '团队协作管理平台 - 课程设计项目，涵盖用户管理、任务看板、文档共享与消息通知', 1, 'ACTIVE'),
('移动端适配', 'TeamSync 移动端 H5 响应式适配与交互优化', 2, 'ACTIVE'),
('数据分析模块', '团队效能统计、任务完成率分析与可视化报表', 1, 'ACTIVE'),
('旧版系统迁移', '从 TeamSync 1.0 迁移历史数据与用户账号', 1, 'COMPLETED');

-- 项目成员
INSERT INTO `project_member` (`project_id`, `user_id`, `role`) VALUES
(1, 1, 'OWNER'), (1, 2, 'ADMIN'), (1, 3, 'MEMBER'), (1, 4, 'MEMBER'), (1, 5, 'MEMBER'), (1, 6, 'MEMBER'),
(2, 2, 'OWNER'), (2, 3, 'ADMIN'), (2, 4, 'MEMBER'), (2, 5, 'MEMBER'),
(3, 1, 'OWNER'), (3, 2, 'ADMIN'), (3, 5, 'MEMBER'), (3, 6, 'MEMBER'),
(4, 1, 'OWNER'), (4, 2, 'MEMBER');

-- 插入示例任务
INSERT INTO `task` (`project_id`, `title`, `description`, `status`, `priority`, `assignee_id`, `creator_id`, `deadline`) VALUES
(1, '完成数据库设计', '设计并创建所有数据库表结构，编写初始化脚本', 'DONE', 'HIGH', 1, 1, '2026-06-20'),
(1, '开发用户登录注册模块', '实现 JWT 鉴权的登录注册功能，含密码加密与 Token 校验', 'DOING', 'HIGH', 2, 1, '2026-06-25'),
(1, '开发任务管理模块', '实现任务 CRUD 及看板拖拽功能，支持评论与状态流转', 'TODO', 'HIGH', 3, 1, '2026-06-28'),
(1, '开发文档管理模块', '实现文件上传下载及分类管理，支持多项目文档隔离', 'TODO', 'MED', 4, 1, '2026-06-30'),
(1, '开发前端页面', '使用 Vue3 + Element Plus 实现所有业务页面与路由守卫', 'DOING', 'HIGH', 5, 1, '2026-07-01'),
(1, '编写开发说明文档', '撰写项目开发说明文档与 API 接口文档', 'TODO', 'MED', 6, 1, '2026-07-05'),
(1, '接口联调与测试', '前后端联调，修复字段不一致问题，补充边界测试', 'TODO', 'HIGH', 2, 1, '2026-07-08'),
(1, '部署上线准备', '配置生产环境数据库、Nginx 反向代理与 HTTPS', 'TODO', 'LOW', 1, 1, '2026-07-15'),
(2, '移动端布局设计', '设计移动端导航栏、侧边栏折叠与卡片式任务列表', 'DOING', 'HIGH', 3, 2, '2026-07-02'),
(2, '触摸手势优化', '优化看板拖拽在触摸屏上的体验', 'TODO', 'MED', 4, 2, '2026-07-06'),
(2, 'PWA 离线支持', '评估并实现 Service Worker 离线缓存策略', 'TODO', 'LOW', 5, 2, '2026-07-10'),
(3, 'ECharts 统计图表', '在工作台接入任务分布饼图与项目进度柱状图', 'DOING', 'HIGH', 5, 1, '2026-06-28'),
(3, '成员工作量报表', '按成员统计任务完成数与逾期率', 'TODO', 'MED', 6, 1, '2026-07-03'),
(3, '数据导出功能', '支持将任务与项目数据导出为 Excel', 'TODO', 'LOW', 2, 1, '2026-07-12'),
(4, '用户数据迁移', '迁移 1.0 版本用户表与密码哈希', 'DONE', 'HIGH', 2, 1, '2026-05-15'),
(4, '历史任务归档', '将旧版任务数据导入并标记为已完成', 'DONE', 'MED', 3, 1, '2026-05-20');

-- 任务评论
INSERT INTO `task_comment` (`task_id`, `user_id`, `content`) VALUES
(1, 1, '数据库表结构设计已完成，共 8 张核心表。'),
(1, 2, '建议 task 表增加 sort_order 字段支持看板排序。'),
(2, 2, 'JWT 拦截器已实现，登录接口返回嵌套 user 对象。'),
(2, 1, '注意密码使用 BCrypt 加密，前端不要明文传输。'),
(3, 3, '看板拖拽准备用 HTML5 DnD 实现，无需额外依赖。'),
(5, 5, 'Dashboard 页面已接入 ECharts，效果还不错。'),
(12, 5, '饼图和柱状图已接入，数据来自 dashboard API。'),
(12, 6, '建议增加近 7 天任务完成趋势折线图。');

-- 插入示例通知
INSERT INTO `notification` (`user_id`, `title`, `content`, `type`, `related_id`, `is_read`) VALUES
(2, '任务分配通知', '你被分配了新任务：开发用户登录注册模块', 'TASK', 2, 0),
(3, '任务分配通知', '你被分配了新任务：开发任务管理模块', 'TASK', 3, 0),
(4, '任务分配通知', '你被分配了新任务：开发文档管理模块', 'TASK', 4, 0),
(5, '任务分配通知', '你被分配了新任务：开发前端页面', 'TASK', 5, 0),
(5, '任务分配通知', '你被分配了新任务：ECharts 统计图表', 'TASK', 12, 0),
(6, '任务分配通知', '你被分配了新任务：编写开发说明文档', 'TASK', 6, 0),
(1, '系统通知', 'TeamSync 2.0 项目已创建，请各成员查看任务分工', 'PROJECT', 1, 1),
(2, '项目邀请', '你已被添加为「移动端适配」项目管理员', 'PROJECT', 2, 0),
(3, '评论提醒', '张三在任务「开发任务管理模块」中添加了评论', 'TASK', 3, 0),
(1, '系统通知', '操作日志模块已上线，管理员可在后台查看', 'SYSTEM', NULL, 0);

-- 操作日志
INSERT INTO `operation_log` (`user_id`, `username`, `operation`, `method`, `ip`) VALUES
(1, 'admin', '用户登录', 'POST /api/auth/login', '127.0.0.1'),
(1, 'admin', '创建项目 TeamSync 2.0', 'POST /api/projects', '127.0.0.1'),
(1, 'admin', '创建任务：完成数据库设计', 'POST /api/tasks', '127.0.0.1'),
(2, 'zhangsan', '用户登录', 'POST /api/auth/login', '192.168.1.101'),
(2, 'zhangsan', '更新任务状态为进行中', 'PUT /api/tasks/2/status', '192.168.1.101'),
(1, 'admin', '禁用用户测试账号', 'PUT /api/users/6/status', '127.0.0.1'),
(5, 'wangwu', '上传文档 requirements.pdf', 'POST /api/documents/upload', '192.168.1.105'),
(1, 'admin', '查看操作日志', 'GET /api/operation-logs', '127.0.0.1');

-- ============================================
-- 聊天功能表
-- ============================================

-- 聊天会话表
DROP TABLE IF EXISTS `chat_session`;
CREATE TABLE `chat_session` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '会话ID',
    `type` VARCHAR(20) NOT NULL COMMENT '会话类型: PRIVATE(私聊)/PROJECT(项目群)/TEAM(团队群)',
    `related_id` BIGINT COMMENT '关联ID: 私聊为对方用户ID，群聊为项目/团队ID',
    `name` VARCHAR(100) COMMENT '会话名称(群聊)',
    `avatar` VARCHAR(500) COMMENT '会话头像(群聊)',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    `update_time` DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天会话表';

-- 聊天消息表
DROP TABLE IF EXISTS `chat_message`;
CREATE TABLE `chat_message` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT COMMENT '消息ID',
    `session_id` BIGINT NOT NULL COMMENT '会话ID',
    `sender_id` BIGINT NOT NULL COMMENT '发送者ID',
    `content` TEXT NOT NULL COMMENT '消息内容',
    `type` VARCHAR(20) NOT NULL DEFAULT 'TEXT' COMMENT '消息类型: TEXT(文本)/IMAGE(图片)/FILE(文件)/SYSTEM(系统)',
    `file_url` VARCHAR(500) COMMENT '文件/图片URL',
    `file_name` VARCHAR(200) COMMENT '文件名',
    `file_size` BIGINT COMMENT '文件大小',
    `is_read` TINYINT NOT NULL DEFAULT 0 COMMENT '是否已读: 0未读 1已读',
    `create_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='聊天消息表';

-- 会话成员表
DROP TABLE IF EXISTS `chat_session_member`;
CREATE TABLE `chat_session_member` (
    `id` BIGINT PRIMARY KEY AUTO_INCREMENT,
    `session_id` BIGINT NOT NULL COMMENT '会话ID',
    `user_id` BIGINT NOT NULL COMMENT '用户ID',
    `unread_count` INT NOT NULL DEFAULT 0 COMMENT '未读消息数',
    `last_read_time` DATETIME COMMENT '最后阅读时间',
    `join_time` DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE KEY `uk_session_user` (`session_id`, `user_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='会话成员表';

-- 聊天会话索引
CREATE INDEX idx_chat_session_type ON `chat_session`(`type`);
CREATE INDEX idx_chat_session_related ON `chat_session`(`related_id`);

-- 聊天消息索引
CREATE INDEX idx_chat_message_session ON `chat_message`(`session_id`);
CREATE INDEX idx_chat_message_sender ON `chat_message`(`sender_id`);
CREATE INDEX idx_chat_message_time ON `chat_message`(`create_time`);

-- 会话成员索引
CREATE INDEX idx_chat_session_member_user ON `chat_session_member`(`user_id`);

-- 插入示例聊天数据
INSERT INTO `chat_session` (`type`, `related_id`, `name`, `avatar`) VALUES
('PROJECT', 1, 'TeamSync 2.0 项目群', ''),
('PRIVATE', 2, NULL, NULL),
('TEAM', 1, '产品研发团队群', '');

-- 会话成员
INSERT INTO `chat_session_member` (`session_id`, `user_id`, `unread_count`) VALUES
(1, 1, 0), (1, 2, 2), (1, 3, 1), (1, 4, 3), (1, 5, 0), (1, 6, 0),
(2, 1, 0), (2, 2, 1),
(3, 1, 0), (3, 2, 0), (3, 3, 1), (3, 4, 0), (3, 5, 0), (3, 6, 2);

-- 示例聊天消息
INSERT INTO `chat_message` (`session_id`, `sender_id`, `content`, `type`, `is_read`) VALUES
(1, 1, '大家好，欢迎加入 TeamSync 2.0 项目群！', 'TEXT', 1),
(1, 2, '收到，今天的任务安排是什么？', 'TEXT', 1),
(1, 1, '主要完成用户模块和任务模块的开发，详细看任务看板', 'TEXT', 1),
(1, 3, '好的，我今天先把任务模块的后端接口写好', 'TEXT', 0),
(1, 4, '我负责前端页面，等接口出来就对接', 'TEXT', 0),
(2, 1, '张三，关于产品评审会的时间你看一下日程', 'TEXT', 1),
(2, 2, '好的，我确认一下明天下午的时间是否合适', 'TEXT', 0),
(3, 1, '研发团队的兄弟们加油，月底前完成第一版！', 'TEXT', 1),
(3, 2, '没问题，产品需求已经梳理清楚了', 'TEXT', 1),
(3, 5, '设计稿我这边也准备好了，一会上传到项目文档', 'TEXT', 0);
