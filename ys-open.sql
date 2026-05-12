/*
 Navicat Premium Dump SQL

 Source Server         : 127.0.0.1
 Source Server Type    : MySQL
 Source Server Version : 80029 (8.0.29)
 Source Host           : localhost:3306
 Source Schema         : ys-open

 Target Server Type    : MySQL
 Target Server Version : 80029 (8.0.29)
 File Encoding         : 65001

 Date: 12/05/2026 12:15:08
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_config
-- ----------------------------
DROP TABLE IF EXISTS `sys_config`;
CREATE TABLE `sys_config`  (
  `id` varchar(225) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键id',
  `name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '显示名称',
  `code` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '编码',
  `value` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT '' COMMENT '值，任意字符串',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '使用说明',
  `category` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '分类，备用扩展字段',
  `filter` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '过滤，备用扩展字段',
  `is_delete` tinyint NULL DEFAULT 1 COMMENT '删除状态 0.已删除 1.正常',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(225) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人id',
  `create_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人名称',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `code`(`code` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '系统配置' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_config
-- ----------------------------
INSERT INTO `sys_config` VALUES ('10', '维护模式', 'system.maintenance.mode', 'false', '系统是否处于维护模式', 'system', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('11', 'API速率限制', 'system.api.rate_limit', '1000', 'API每小时请求限制', 'system', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('12', '数据备份周期', 'system.backup.interval', '24', '数据自动备份间隔（小时）', 'system', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('13', '密码最小长度', 'security.password.min_length', '8', '用户密码的最小长度要求', 'security', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('14', '密码复杂度', 'security.password.complexity', 'true', '密码是否需要包含特殊字符', 'security', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('15', '密码过期天数', 'security.password.expiry_days', '90', '密码过期时间（天）', 'security', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('16', '登录失败次数限制', 'security.login.max_attempts', '5', '账户被锁定前允许的登录失败次数', 'security', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('17', '账户锁定时间', 'security.account.lockout_duration', '30', '账户锁定持续时间（分钟）', 'security', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('18', '验证码有效期', 'security.captcha.expiry', '300', '验证码的有效期（秒）', 'security', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('19', 'JWT令牌过期时间', 'security.jwt.expiry', '86400', 'JWT令牌过期时间（秒）', 'security', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('2', '新配置项', 'new.config', 'new_value', '这是一条新插入的配置', 'test', NULL, 1, NULL, NULL, NULL);
INSERT INTO `sys_config` VALUES ('20', '启用双因素认证', 'security.2fa.enabled', 'false', '是否启用双因素认证', 'security', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('21', '登录日志保留天数', 'security.log.retention_days', '30', '登录日志保留天数', 'security', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('22', '权限缓存时间', 'security.permission.cache_time', '600', '权限缓存时间（秒）', 'security', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('23', '数据库连接池大小', 'database.connection.pool_size', '20', '数据库连接池最大连接数', 'database', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('24', '数据库连接超时', 'database.connection.timeout', '30', '数据库连接超时时间（秒）', 'database', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('25', '慢查询阈值', 'database.query.slow_threshold', '1000', '慢查询阈值（毫秒）', 'database', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('26', 'SQL日志级别', 'database.log.level', 'INFO', 'SQL日志记录级别', 'database', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('27', '数据库备份路径', 'database.backup.path', '/backup/database', '数据库备份文件存储路径', 'database', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('28', '读写分离', 'database.readwrite.split', 'false', '是否启用数据库读写分离', 'database', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('29', '连接池最小空闲连接', 'database.connection.min_idle', '5', '连接池最小空闲连接数', 'database', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('3', '系统名称', 'system.basic.name', 'YS博客系统', '系统的基本名称配置', 'system', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('30', '数据库重连次数', 'database.connection.retry_count', '3', '数据库断开后重连次数', 'database', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('31', 'Redis最大连接数', 'cache.redis.max_connections', '50', 'Redis最大连接数', 'cache', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('32', 'Redis连接超时', 'cache.redis.timeout', '2000', 'Redis连接超时时间（毫秒）', 'cache', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('33', '缓存默认过期时间', 'cache.default.expiry', '3600', '缓存默认过期时间（秒）', 'cache', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('34', '缓存预热开关', 'cache.warmup.enabled', 'true', '是否启用缓存预热', 'cache', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('35', 'Redis集群模式', 'cache.redis.cluster_mode', 'false', '是否启用Redis集群模式', 'cache', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('36', '缓存击穿保护', 'cache.breakdown.protection', 'true', '是否启用缓存击穿保护', 'cache', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('37', '本地缓存大小', 'cache.local.max_size', '1000', '本地缓存最大条目数', 'cache', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('38', '缓存更新策略', 'cache.update.strategy', 'refresh-ahead', '缓存更新策略', 'cache', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('39', '文件存储路径', 'file.storage.path', '/data/files', '文件存储根路径', 'file', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('4', '系统版本', 'system.basic.version', '1.0.0', '系统的当前版本号', 'system', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('40', '临时文件过期时间', 'file.temp.expiry_hours', '24', '临时文件过期时间（小时）', 'file', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('41', '文件访问权限', 'file.access.permission', 'private', '文件默认访问权限', 'file', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('42', '图片压缩质量', 'file.image.quality', '80', '图片压缩质量（百分比）', 'file', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('43', '支持的文件类型', 'file.allowed.types', 'jpg,png,gif,doc,docx,pdf', '允许上传的文件类型', 'file', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('44', 'SMTP服务器地址', 'email.smtp.host', 'smtp.example.com', 'SMTP服务器地址', 'email', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('45', 'SMTP服务器端口', 'email.smtp.port', '587', 'SMTP服务器端口', 'email', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('46', '发件人邮箱', 'email.sender.address', 'noreply@example.com', '系统发件人邮箱地址', 'email', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('47', '邮箱验证开关', 'email.verification.enabled', 'true', '是否启用邮箱验证功能', 'email', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('48', '邮件模板路径', 'email.template.path', '/templates/email', '邮件模板文件路径', 'email', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('49', '日志文件保留天数', 'log.file.retention_days', '7', '日志文件保留天数', 'log', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('5', '会话超时时间', 'system.session.timeout', '1800', '用户会话超时时间（秒）', 'system', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('50', '日志级别', 'log.level', 'INFO', '系统日志记录级别', 'log', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('51', '慢接口日志阈值', 'log.slow_api.threshold', '1000', '慢接口日志阈值（毫秒）', 'log', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('52', '日志文件大小限制', 'log.file.max_size', '10MB', '单个日志文件大小限制', 'log', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('6', '最大上传文件大小', 'system.file.max_size', '10MB', '允许上传的最大文件大小', 'system', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('7', '默认语言', 'system.locale.default', 'zh_CN', '系统默认语言设置', 'system', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('8', '默认时区', 'system.timezone.default', 'Asia/Shanghai', '系统默认时区设置', 'system', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);
INSERT INTO `sys_config` VALUES ('9', '启用HTTPS', 'system.security.https_enabled', 'true', '是否启用HTTPS协议', 'system', NULL, 1, '2025-07-31 08:57:22', NULL, NULL);

-- ----------------------------
-- Table structure for sys_depart
-- ----------------------------
DROP TABLE IF EXISTS `sys_depart`;
CREATE TABLE `sys_depart`  (
  `id` varchar(225) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键id',
  `name` varchar(225) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部门名称',
  `short_name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部门简称',
  `code` varchar(225) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '部门编码',
  `pid` varchar(225) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '上级id',
  `category` int NULL DEFAULT NULL COMMENT '部门分类(如:公司、部门、小组等)',
  `dept_type` int NULL DEFAULT NULL COMMENT '部门性质(如:行政、技术、生产等)',
  `level_type` int NULL DEFAULT NULL COMMENT '部门层级(如:总部、分公司、部门等)',
  `area` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '所属地区',
  `email` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部门邮箱',
  `zip_code` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮政编码',
  `address` varchar(800) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '详细地址',
  `fax` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '传真号码',
  `remark` varchar(1000) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `link_man` varchar(200) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '联系人',
  `link_phone` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '联系电话',
  `seq` int NULL DEFAULT 0 COMMENT '显示顺序(值越小越靠前)',
  `icon` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '图标',
  `status` int NULL DEFAULT 1 COMMENT '状态 0.禁用 1.正常',
  `is_delete` tinyint NULL DEFAULT 1 COMMENT '删除状态 0.已删除 1.正常',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(225) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人id',
  `create_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人名称',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_by` varchar(225) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人ID',
  `update_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人名称',
  `leader_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部门负责人ID',
  `leader_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '部门负责人名称',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_sys_depart_pid`(`pid` ASC) USING BTREE,
  INDEX `idx_sys_depart_id_pid`(`id` ASC, `pid` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '部门信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_depart
-- ----------------------------
INSERT INTO `sys_depart` VALUES ('1000000000000000001', '中国软件集团有限公司', '中软集团', 'CSG-001', '0', 1, 1, 1, '北京', 'contact@chinasoft.com', '100000', '北京市海淀区中关村大街1号', '010-88888888', '中国软件集团总公司', '张总', '010-66666666', 1, 'ele-Baseball', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000002', '北京中软信息技术有限公司', '北京中软', 'CSG-BJ01', '1000000000000000001', 1, 1, 2, '北京', 'bj@chinasoft.com', '100001', '北京市朝阳区建国路1号', '010-88888889', '北京分公司', '李经理', '010-66666667', 1, 'ele-Basketball', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000003', '上海中软科技有限公司', '上海中软', 'CSG-SH01', '1000000000000000001', 1, 1, 2, '上海', 'sh@chinasoft.com', '200000', '上海市浦东新区陆家嘴环路1000号', '021-88888888', '上海分公司', '王经理', '021-66666666', 2, 'company', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000004', '广州中软软件有限公司', '广州中软', 'CSG-GZ01', '1000000000000000001', 1, 1, 2, '广州', 'gz@chinasoft.com', '510000', '广州市天河区珠江新城100号', '020-88888888', '广州分公司', '陈经理', '020-66666666', 3, 'company', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000005', '深圳中软创新有限公司', '深圳中软', 'CSG-SZ01', '1000000000000000001', 1, 1, 2, '深圳', 'sz@chinasoft.com', '518000', '深圳市南山区科技园南区200号', '0755-88888888', '深圳分公司', '赵经理', '0755-66666666', 4, 'company', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000006', '杭州中软云服务有限公司', '杭州中软', 'CSG-HZ01', '1000000000000000001', 1, 1, 2, '杭州', 'hz@chinasoft.com', '310000', '杭州市西湖区文三路300号', '0571-88888888', '杭州分公司', '孙经理', '0571-66666666', 5, 'company', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000007', '成都中软西部有限公司', '成都中软', 'CSG-CD01', '1000000000000000001', 1, 1, 2, '成都', 'cd@chinasoft.com', '610000', '成都市高新区天府大道400号', '028-88888888', '成都分公司', '刘经理', '028-66666666', 6, 'company', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000008', '武汉中软中部有限公司', '武汉中软', 'CSG-WH01', '1000000000000000001', 1, 1, 2, '武汉', 'wh@chinasoft.com', '430000', '武汉市江汉区建设大道500号', '027-88888888', '武汉分公司', '周经理', '027-66666666', 7, 'company', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000009', '南京中软东部有限公司', '南京中软', 'CSG-NJ01', '1000000000000000001', 1, 1, 2, '南京', 'nj@chinasoft.com', '210000', '南京市鼓楼区中山路200号', '025-88888888', '南京分公司', '吴经理', '025-66666666', 8, 'company', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000010', '西安中软西北有限公司', '西安中软', 'CSG-XA01', '1000000000000000001', 1, 1, 2, '西安', 'xa@chinasoft.com', '710000', '西安市雁塔区长安南路300号', '029-88888888', '西安分公司', '郑经理', '029-66666666', 9, 'company', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000011', '沈阳中软东北有限公司', '沈阳中软', 'CSG-SY01', '1000000000000000001', 1, 1, 2, '沈阳', 'sy@chinasoft.com', '110000', '沈阳市和平区南京北街400号', '024-88888888', '沈阳分公司', '王经理', '024-66666666', 10, 'company', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000012', '天津中软环渤海有限公司', '天津中软', 'CSG-TJ01', '1000000000000000001', 1, 1, 2, '天津', 'tj@chinasoft.com', '300000', '天津市滨海新区第一大街500号', '022-88888888', '天津分公司', '冯经理', '022-66666666', 11, 'company', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000013', '重庆中软西南有限公司', '重庆中软', 'CSG-CQ01', '1000000000000000001', 1, 1, 2, '重庆', 'cq@chinasoft.com', '400000', '重庆市渝北区金开大道600号', '023-88888888', '重庆分公司', '陈经理', '023-66666666', 12, 'company', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000014', '厦门中软海西有限公司', '厦门中软', 'CSG-XM01', '1000000000000000001', 1, 1, 2, '厦门', 'xm@chinasoft.com', '361000', '厦门市思明区软件园二期800号', '0592-88888888', '厦门分公司', '林经理', '0592-66666666', 13, 'company', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000015', '青岛中软蓝海有限公司', '青岛中软', 'CSG-QD01', '1000000000000000001', 1, 1, 2, '青岛', 'qd@chinasoft.com', '266000', '青岛市市南区香港中路900号', '0532-88888888', '青岛分公司', '黄经理', '0532-66666666', 14, 'company', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000016', '大连中软软件外包有限公司', '大连中软', 'CSG-DL01', '1000000000000000001', 1, 1, 2, '大连', 'dl@chinasoft.com', '116000', '大连市高新园区软件园路1000号', '0411-88888888', '大连分公司', '徐经理', '0411-66666666', 15, 'company', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000017', '北京研发部', '北京研发部', 'CSG-BJ01-RD', '1000000000000000002', 1, 1, 3, '北京', 'bj_rd@chinasoft.com', '100001', '北京市朝阳区建国路1号研发大楼', '010-88888889-101', '北京分公司研发部门', '李经理', '010-66666667-101', 1, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000018', '北京市场部', '北京市场部', 'CSG-BJ01-MKT', '1000000000000000002', 1, 1, 3, '北京', 'bj_mkt@chinasoft.com', '100001', '北京市朝阳区建国路1号市场大楼', '010-88888889-102', '北京分公司市场部门', '王经理', '010-66666667-102', 2, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000019', '北京人事部', '北京人事部', 'CSG-BJ01-HR', '1000000000000000002', 1, 1, 3, '北京', 'bj_hr@chinasoft.com', '100001', '北京市朝阳区建国路1号人事大楼', '010-88888889-103', '北京分公司人事部门', '陈经理', '010-66666667-103', 3, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000020', '北京财务部', '北京财务部', 'CSG-BJ01-FIN', '1000000000000000002', 1, 1, 3, '北京', 'bj_fin@chinasoft.com', '100001', '北京市朝阳区建国路1号财务大楼', '010-88888889-104', '北京分公司财务部门', '赵经理', '010-66666667-104', 4, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000021', '北京运维部', '北京运维部', 'CSG-BJ01-OPT', '1000000000000000002', 1, 1, 3, '北京', 'bj_opt@chinasoft.com', '100001', '北京市朝阳区建国路1号运维大楼', '010-88888889-105', '北京分公司运维部门', '孙经理', '010-66666667-105', 5, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000022', '北京测试部', '北京测试部', 'CSG-BJ01-TST', '1000000000000000002', 1, 1, 3, '北京', 'bj_tst@chinasoft.com', '100001', '北京市朝阳区建国路1号测试大楼', '010-88888889-106', '北京分公司测试部门', '刘经理', '010-66666667-106', 6, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000023', '北京产品部', '北京产品部', 'CSG-BJ01-PD', '1000000000000000002', 1, 1, 3, '北京', 'bj_pd@chinasoft.com', '100001', '北京市朝阳区建国路1号产品大楼', '010-88888889-107', '北京分公司产品部门', '周经理', '010-66666667-107', 7, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000024', '北京设计部', '北京设计部', 'CSG-BJ01-DS', '1000000000000000002', 1, 1, 3, '北京', 'bj_ds@chinasoft.com', '100001', '北京市朝阳区建国路1号设计大楼', '010-88888889-108', '北京分公司设计部门', '吴经理', '010-66666667-108', 8, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000025', '北京安全技术部', '北京安全部', 'CSG-BJ01-SEC', '1000000000000000002', 1, 1, 3, '北京', 'bj_sec@chinasoft.com', '100001', '北京市朝阳区建国路1号安全大楼', '010-88888889-109', '北京分公司安全技术部门', '郑经理', '010-66666667-109', 9, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000026', '北京客户服务部', '北京客服部', 'CSG-BJ01-CS', '1000000000000000002', 1, 1, 3, '北京', 'bj_cs@chinasoft.com', '100001', '北京市朝阳区建国路1号客服大楼', '010-88888889-110', '北京分公司客户服务部门', '王经理', '010-66666667-110', 10, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000027', '上海研发部', '上海研发部', 'CSG-SH01-RD', '1000000000000000003', 1, 1, 3, '上海', 'sh_rd@chinasoft.com', '200000', '上海市浦东新区陆家嘴环路1000号研发大楼', '021-88888888-101', '上海分公司研发部门', '李经理', '021-66666666-101', 1, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000028', '上海市场部', '上海市场部', 'CSG-SH01-MKT', '1000000000000000003', 1, 1, 3, '上海', 'sh_mkt@chinasoft.com', '200000', '上海市浦东新区陆家嘴环路1000号市场大楼', '021-88888888-102', '上海分公司市场部门', '王经理', '021-66666666-102', 2, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000029', '上海人事部', '上海人事部', 'CSG-SH01-HR', '1000000000000000003', 1, 1, 3, '上海', 'sh_hr@chinasoft.com', '200000', '上海市浦东新区陆家嘴环路1000号人事大楼', '021-88888888-103', '上海分公司人事部门', '陈经理', '021-66666666-103', 3, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000030', '上海财务部', '上海财务部', 'CSG-SH01-FIN', '1000000000000000003', 1, 1, 3, '上海', 'sh_fin@chinasoft.com', '200000', '上海市浦东新区陆家嘴环路1000号财务大楼', '021-88888888-104', '上海分公司财务部门', '赵经理', '021-66666666-104', 4, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000031', '上海运维部', '上海运维部', 'CSG-SH01-OPT', '1000000000000000003', 1, 1, 3, '上海', 'sh_opt@chinasoft.com', '200000', '上海市浦东新区陆家嘴环路1000号运维大楼', '021-88888888-105', '上海分公司运维部门', '孙经理', '021-66666666-105', 5, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000032', '上海测试部', '上海测试部', 'CSG-SH01-TST', '1000000000000000003', 1, 1, 3, '上海', 'sh_tst@chinasoft.com', '200000', '上海市浦东新区陆家嘴环路1000号测试大楼', '021-88888888-106', '上海分公司测试部门', '刘经理', '021-66666666-106', 6, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000033', '上海产品部', '上海产品部', 'CSG-SH01-PD', '1000000000000000003', 1, 1, 3, '上海', 'sh_pd@chinasoft.com', '200000', '上海市浦东新区陆家嘴环路1000号产品大楼', '021-88888888-107', '上海分公司产品部门', '周经理', '021-66666666-107', 7, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000034', '上海设计部', '上海设计部', 'CSG-SH01-DS', '1000000000000000003', 1, 1, 3, '上海', 'sh_ds@chinasoft.com', '200000', '上海市浦东新区陆家嘴环路1000号设计大楼', '021-88888888-108', '上海分公司设计部门', '吴经理', '021-66666666-108', 8, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000035', '上海安全技术部', '上海安全部', 'CSG-SH01-SEC', '1000000000000000003', 1, 1, 3, '上海', 'sh_sec@chinasoft.com', '200000', '上海市浦东新区陆家嘴环路1000号安全大楼', '021-88888888-109', '上海分公司安全技术部门', '郑经理', '021-66666666-109', 9, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000036', '上海客户服务部', '上海客服部', 'CSG-SH01-CS', '1000000000000000003', 1, 1, 3, '上海', 'sh_cs@chinasoft.com', '200000', '上海市浦东新区陆家嘴环路1000号客服大楼', '021-88888888-110', '上海分公司客户服务部门', '王经理', '021-66666666-110', 10, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000037', '广州研发部', '广州研发部', 'CSG-GZ01-RD', '1000000000000000004', 1, 1, 3, '广州', 'gz_rd@chinasoft.com', '510000', '广州市天河区珠江新城100号研发大楼', '020-88888888-101', '广州分公司研发部门', '李经理', '020-66666666-101', 1, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000038', '广州市场部', '广州市场部', 'CSG-GZ01-MKT', '1000000000000000004', 1, 1, 3, '广州', 'gz_mkt@chinasoft.com', '510000', '广州市天河区珠江新城100号市场大楼', '020-88888888-102', '广州分公司市场部门', '王经理', '020-66666666-102', 2, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000039', '广州人事部', '广州人事部', 'CSG-GZ01-HR', '1000000000000000004', 1, 1, 3, '广州', 'gz_hr@chinasoft.com', '510000', '广州市天河区珠江新城100号人事大楼', '020-88888888-103', '广州分公司人事部门', '陈经理', '020-66666666-103', 3, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000040', '广州财务部', '广州财务部', 'CSG-GZ01-FIN', '1000000000000000004', 1, 1, 3, '广州', 'gz_fin@chinasoft.com', '510000', '广州市天河区珠江新城100号财务大楼', '020-88888888-104', '广州分公司财务部门', '赵经理', '020-66666666-104', 4, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000041', '广州运维部', '广州运维部', 'CSG-GZ01-OPT', '1000000000000000004', 1, 1, 3, '广州', 'gz_opt@chinasoft.com', '510000', '广州市天河区珠江新城100号运维大楼', '020-88888888-105', '广州分公司运维部门', '孙经理', '020-66666666-105', 5, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000042', '广州测试部', '广州测试部', 'CSG-GZ01-TST', '1000000000000000004', 1, 1, 3, '广州', 'gz_tst@chinasoft.com', '510000', '广州市天河区珠江新城100号测试大楼', '020-88888888-106', '广州分公司测试部门', '刘经理', '020-66666666-106', 6, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000043', '广州产品部', '广州产品部', 'CSG-GZ01-PD', '1000000000000000004', 1, 1, 3, '广州', 'gz_pd@chinasoft.com', '510000', '广州市天河区珠江新城100号产品大楼', '020-88888888-107', '广州分公司产品部门', '周经理', '020-66666666-107', 7, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000044', '广州设计部', '广州设计部', 'CSG-GZ01-DS', '1000000000000000004', 1, 1, 3, '广州', 'gz_ds@chinasoft.com', '510000', '广州市天河区珠江新城100号设计大楼', '020-88888888-108', '广州分公司设计部门', '吴经理', '020-66666666-108', 8, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000045', '广州安全技术部', '广州安全部', 'CSG-GZ01-SEC', '1000000000000000004', 1, 1, 3, '广州', 'gz_sec@chinasoft.com', '510000', '广州市天河区珠江新城100号安全大楼', '020-88888888-109', '广州分公司安全技术部门', '郑经理', '020-66666666-109', 9, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000046', '广州客户服务部', '广州客服部', 'CSG-GZ01-CS', '1000000000000000004', 1, 1, 3, '广州', 'gz_cs@chinasoft.com', '510000', '广州市天河区珠江新城100号客服大楼', '020-88888888-110', '广州分公司客户服务部门', '王经理', '020-66666666-110', 10, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000047', '深圳研发部', '深圳研发部', 'CSG-SZ01-RD', '1000000000000000005', 1, 1, 3, '深圳', 'sz_rd@chinasoft.com', '518000', '深圳市南山区科技园南区200号研发大楼', '0755-88888888-101', '深圳分公司研发部门', '李经理', '0755-66666666-101', 1, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000048', '深圳市场部', '深圳市场部', 'CSG-SZ01-MKT', '1000000000000000005', 1, 1, 3, '深圳', 'sz_mkt@chinasoft.com', '518000', '深圳市南山区科技园南区200号市场大楼', '0755-88888888-102', '深圳分公司市场部门', '王经理', '0755-66666666-102', 2, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000049', '深圳人事部', '深圳人事部', 'CSG-SZ01-HR', '1000000000000000005', 1, 1, 3, '深圳', 'sz_hr@chinasoft.com', '518000', '深圳市南山区科技园南区200号人事大楼', '0755-88888888-103', '深圳分公司人事部门', '陈经理', '0755-66666666-103', 3, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000050', '深圳财务部', '深圳财务部', 'CSG-SZ01-FIN', '1000000000000000005', 1, 1, 3, '深圳', 'sz_fin@chinasoft.com', '518000', '深圳市南山区科技园南区200号财务大楼', '0755-88888888-104', '深圳分公司财务部门', '赵经理', '0755-66666666-104', 4, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000051', '深圳运维部', '深圳运维部', 'CSG-SZ01-OPT', '1000000000000000005', 1, 1, 3, '深圳', 'sz_opt@chinasoft.com', '518000', '深圳市南山区科技园南区200号运维大楼', '0755-88888888-105', '深圳分公司运维部门', '孙经理', '0755-66666666-105', 5, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000052', '深圳测试部', '深圳测试部', 'CSG-SZ01-TST', '1000000000000000005', 1, 1, 3, '深圳', 'sz_tst@chinasoft.com', '518000', '深圳市南山区科技园南区200号测试大楼', '0755-88888888-106', '深圳分公司测试部门', '刘经理', '0755-66666666-106', 6, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000053', '深圳产品部', '深圳产品部', 'CSG-SZ01-PD', '1000000000000000005', 1, 1, 3, '深圳', 'sz_pd@chinasoft.com', '518000', '深圳市南山区科技园南区200号产品大楼', '0755-88888888-107', '深圳分公司产品部门', '周经理', '0755-66666666-107', 7, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000054', '深圳设计部', '深圳设计部', 'CSG-SZ01-DS', '1000000000000000005', 1, 1, 3, '深圳', 'sz_ds@chinasoft.com', '518000', '深圳市南山区科技园南区200号设计大楼', '0755-88888888-108', '深圳分公司设计部门', '吴经理', '0755-66666666-108', 8, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000055', '深圳安全技术部', '深圳安全部', 'CSG-SZ01-SEC', '1000000000000000005', 1, 1, 3, '深圳', 'sz_sec@chinasoft.com', '518000', '深圳市南山区科技园南区200号安全大楼', '0755-88888888-109', '深圳分公司安全技术部门', '郑经理', '0755-66666666-109', 9, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000056', '深圳客户服务部', '深圳客服部', 'CSG-SZ01-CS', '1000000000000000005', 1, 1, 3, '深圳', 'sz_cs@chinasoft.com', '518000', '深圳市南山区科技园南区200号客服大楼', '0755-88888888-110', '深圳分公司客户服务部门', '王经理', '0755-66666666-110', 10, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000057', '杭州研发部', '杭州研发部', 'CSG-HZ01-RD', '1000000000000000006', 1, 1, 3, '杭州', 'hz_rd@chinasoft.com', '310000', '杭州市西湖区文三路300号研发大楼', '0571-88888888-101', '杭州分公司研发部门', '李经理', '0571-66666666-101', 1, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000058', '杭州市场部', '杭州市场部', 'CSG-HZ01-MKT', '1000000000000000006', 1, 1, 3, '杭州', 'hz_mkt@chinasoft.com', '310000', '杭州市西湖区文三路300号市场大楼', '0571-88888888-102', '杭州分公司市场部门', '王经理', '0571-66666666-102', 2, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000059', '杭州人事部', '杭州人事部', 'CSG-HZ01-HR', '1000000000000000006', 1, 1, 3, '杭州', 'hz_hr@chinasoft.com', '310000', '杭州市西湖区文三路300号人事大楼', '0571-88888888-103', '杭州分公司人事部门', '陈经理', '0571-66666666-103', 3, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000060', '杭州财务部', '杭州财务部', 'CSG-HZ01-FIN', '1000000000000000006', 1, 1, 3, '杭州', 'hz_fin@chinasoft.com', '310000', '杭州市西湖区文三路300号财务大楼', '0571-88888888-104', '杭州分公司财务部门', '赵经理', '0571-66666666-104', 4, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000061', '杭州运维部', '杭州运维部', 'CSG-HZ01-OPT', '1000000000000000006', 1, 1, 3, '杭州', 'hz_opt@chinasoft.com', '310000', '杭州市西湖区文三路300号运维大楼', '0571-88888888-105', '杭州分公司运维部门', '孙经理', '0571-66666666-105', 5, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000062', '杭州测试部', '杭州测试部', 'CSG-HZ01-TST', '1000000000000000006', 1, 1, 3, '杭州', 'hz_tst@chinasoft.com', '310000', '杭州市西湖区文三路300号测试大楼', '0571-88888888-106', '杭州分公司测试部门', '刘经理', '0571-66666666-106', 6, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000063', '杭州产品部', '杭州产品部', 'CSG-HZ01-PD', '1000000000000000006', 1, 1, 3, '杭州', 'hz_pd@chinasoft.com', '310000', '杭州市西湖区文三路300号产品大楼', '0571-88888888-107', '杭州分公司产品部门', '周经理', '0571-66666666-107', 7, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000064', '杭州设计部', '杭州设计部', 'CSG-HZ01-DS', '1000000000000000006', 1, 1, 3, '杭州', 'hz_ds@chinasoft.com', '310000', '杭州市西湖区文三路300号设计大楼', '0571-88888888-108', '杭州分公司设计部门', '吴经理', '0571-66666666-108', 8, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000065', '杭州安全技术部', '杭州安全部', 'CSG-HZ01-SEC', '1000000000000000006', 1, 1, 3, '杭州', 'hz_sec@chinasoft.com', '310000', '杭州市西湖区文三路300号安全大楼', '0571-88888888-109', '杭州分公司安全技术部门', '郑经理', '0571-66666666-109', 9, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000066', '杭州客户服务部', '杭州客服部', 'CSG-HZ01-CS', '1000000000000000006', 1, 1, 3, '杭州', 'hz_cs@chinasoft.com', '310000', '杭州市西湖区文三路300号客服大楼', '0571-88888888-110', '杭州分公司客户服务部门', '王经理', '0571-66666666-110', 10, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000067', '成都研发部', '成都研发部', 'CSG-CD01-RD', '1000000000000000007', 1, 1, 3, '成都', 'cd_rd@chinasoft.com', '610000', '成都市高新区天府大道400号研发大楼', '028-88888888-101', '成都分公司研发部门', '李经理', '028-66666666-101', 1, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000068', '成都市场部', '成都市场部', 'CSG-CD01-MKT', '1000000000000000007', 1, 1, 3, '成都', 'cd_mkt@chinasoft.com', '610000', '成都市高新区天府大道400号市场大楼', '028-88888888-102', '成都分公司市场部门', '王经理', '028-66666666-102', 2, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000069', '成都人事部', '成都人事部', 'CSG-CD01-HR', '1000000000000000007', 1, 1, 3, '成都', 'cd_hr@chinasoft.com', '610000', '成都市高新区天府大道400号人事大楼', '028-88888888-103', '成都分公司人事部门', '陈经理', '028-66666666-103', 3, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000070', '成都财务部', '成都财务部', 'CSG-CD01-FIN', '1000000000000000007', 1, 1, 3, '成都', 'cd_fin@chinasoft.com', '610000', '成都市高新区天府大道400号财务大楼', '028-88888888-104', '成都分公司财务部门', '赵经理', '028-66666666-104', 4, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000071', '成都运维部', '成都运维部', 'CSG-CD01-OPT', '1000000000000000007', 1, 1, 3, '成都', 'cd_opt@chinasoft.com', '610000', '成都市高新区天府大道400号运维大楼', '028-88888888-105', '成都分公司运维部门', '孙经理', '028-66666666-105', 5, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000072', '成都测试部', '成都测试部', 'CSG-CD01-TST', '1000000000000000007', 1, 1, 3, '成都', 'cd_tst@chinasoft.com', '610000', '成都市高新区天府大道400号测试大楼', '028-88888888-106', '成都分公司测试部门', '刘经理', '028-66666666-106', 6, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000073', '成都产品部', '成都产品部', 'CSG-CD01-PD', '1000000000000000007', 1, 1, 3, '成都', 'cd_pd@chinasoft.com', '610000', '成都市高新区天府大道400号产品大楼', '028-88888888-107', '成都分公司产品部门', '周经理', '028-66666666-107', 7, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000074', '成都设计部', '成都设计部', 'CSG-CD01-DS', '1000000000000000007', 1, 1, 3, '成都', 'cd_ds@chinasoft.com', '610000', '成都市高新区天府大道400号设计大楼', '028-88888888-108', '成都分公司设计部门', '吴经理', '028-66666666-108', 8, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000075', '成都安全技术部', '成都安全部', 'CSG-CD01-SEC', '1000000000000000007', 1, 1, 3, '成都', 'cd_sec@chinasoft.com', '610000', '成都市高新区天府大道400号安全大楼', '028-88888888-109', '成都分公司安全技术部门', '郑经理', '028-66666666-109', 9, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000076', '成都客户服务部', '成都客服部', 'CSG-CD01-CS', '1000000000000000007', 1, 1, 3, '成都', 'cd_cs@chinasoft.com', '610000', '成都市高新区天府大道400号客服大楼', '028-88888888-110', '成都分公司客户服务部门', '王经理', '028-66666666-110', 10, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000077', '武汉研发部', '武汉研发部', 'CSG-WH01-RD', '1000000000000000008', 1, 1, 3, '武汉', 'wh_rd@chinasoft.com', '430000', '武汉市江汉区建设大道500号研发大楼', '027-88888888-101', '武汉分公司研发部门', '李经理', '027-66666666-101', 1, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000078', '武汉市场部', '武汉市场部', 'CSG-WH01-MKT', '1000000000000000008', 1, 1, 3, '武汉', 'wh_mkt@chinasoft.com', '430000', '武汉市江汉区建设大道500号市场大楼', '027-88888888-102', '武汉分公司市场部门', '王经理', '027-66666666-102', 2, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000079', '武汉人事部', '武汉人事部', 'CSG-WH01-HR', '1000000000000000008', 1, 1, 3, '武汉', 'wh_hr@chinasoft.com', '430000', '武汉市江汉区建设大道500号人事大楼', '027-88888888-103', '武汉分公司人事部门', '陈经理', '027-66666666-103', 3, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000080', '武汉财务部', '武汉财务部', 'CSG-WH01-FIN', '1000000000000000008', 1, 1, 3, '武汉', 'wh_fin@chinasoft.com', '430000', '武汉市江汉区建设大道500号财务大楼', '027-88888888-104', '武汉分公司财务部门', '赵经理', '027-66666666-104', 4, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000081', '武汉运维部', '武汉运维部', 'CSG-WH01-OPT', '1000000000000000008', 1, 1, 3, '武汉', 'wh_opt@chinasoft.com', '430000', '武汉市江汉区建设大道500号运维大楼', '027-88888888-105', '武汉分公司运维部门', '孙经理', '027-66666666-105', 5, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000082', '武汉测试部', '武汉测试部', 'CSG-WH01-TST', '1000000000000000008', 1, 1, 3, '武汉', 'wh_tst@chinasoft.com', '430000', '武汉市江汉区建设大道500号测试大楼', '027-88888888-106', '武汉分公司测试部门', '刘经理', '027-66666666-106', 6, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000083', '武汉产品部', '武汉产品部', 'CSG-WH01-PD', '1000000000000000008', 1, 1, 3, '武汉', 'wh_pd@chinasoft.com', '430000', '武汉市江汉区建设大道500号产品大楼', '027-88888888-107', '武汉分公司产品部门', '周经理', '027-66666666-107', 7, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000084', '武汉设计部', '武汉设计部', 'CSG-WH01-DS', '1000000000000000008', 1, 1, 3, '武汉', 'wh_ds@chinasoft.com', '430000', '武汉市江汉区建设大道500号设计大楼', '027-88888888-108', '武汉分公司设计部门', '吴经理', '027-66666666-108', 8, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000085', '武汉安全技术部', '武汉安全部', 'CSG-WH01-SEC', '1000000000000000008', 1, 1, 3, '武汉', 'wh_sec@chinasoft.com', '430000', '武汉市江汉区建设大道500号安全大楼', '027-88888888-109', '武汉分公司安全技术部门', '郑经理', '027-66666666-109', 9, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000086', '武汉客户服务部', '武汉客服部', 'CSG-WH01-CS', '1000000000000000008', 1, 1, 3, '武汉', 'wh_cs@chinasoft.com', '430000', '武汉市江汉区建设大道500号客服大楼', '027-88888888-110', '武汉分公司客户服务部门', '王经理', '027-66666666-110', 10, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000087', '南京研发部', '南京研发部', 'CSG-NJ01-RD', '1000000000000000009', 1, 1, 3, '南京', 'nj_rd@chinasoft.com', '210000', '南京市鼓楼区中山路200号研发大楼', '025-88888888-101', '南京分公司研发部门', '李经理', '025-66666666-101', 1, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000088', '南京市场部', '南京市场部', 'CSG-NJ01-MKT', '1000000000000000009', 1, 1, 3, '南京', 'nj_mkt@chinasoft.com', '210000', '南京市鼓楼区中山路200号市场大楼', '025-88888888-102', '南京分公司市场部门', '王经理', '025-66666666-102', 2, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000089', '南京人事部', '南京人事部', 'CSG-NJ01-HR', '1000000000000000009', 1, 1, 3, '南京', 'nj_hr@chinasoft.com', '210000', '南京市鼓楼区中山路200号人事大楼', '025-88888888-103', '南京分公司人事部门', '陈经理', '025-66666666-103', 3, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000090', '南京财务部', '南京财务部', 'CSG-NJ01-FIN', '1000000000000000009', 1, 1, 3, '南京', 'nj_fin@chinasoft.com', '210000', '南京市鼓楼区中山路200号财务大楼', '025-88888888-104', '南京分公司财务部门', '赵经理', '025-66666666-104', 4, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000091', '南京运维部', '南京运维部', 'CSG-NJ01-OPT', '1000000000000000009', 1, 1, 3, '南京', 'nj_opt@chinasoft.com', '210000', '南京市鼓楼区中山路200号运维大楼', '025-88888888-105', '南京分公司运维部门', '孙经理', '025-66666666-105', 5, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000092', '南京测试部', '南京测试部', 'CSG-NJ01-TST', '1000000000000000009', 1, 1, 3, '南京', 'nj_tst@chinasoft.com', '210000', '南京市鼓楼区中山路200号测试大楼', '025-88888888-106', '南京分公司测试部门', '刘经理', '025-66666666-106', 6, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000093', '南京产品部', '南京产品部', 'CSG-NJ01-PD', '1000000000000000009', 1, 1, 3, '南京', 'nj_pd@chinasoft.com', '210000', '南京市鼓楼区中山路200号产品大楼', '025-88888888-107', '南京分公司产品部门', '周经理', '025-66666666-107', 7, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000094', '南京设计部', '南京设计部', 'CSG-NJ01-DS', '1000000000000000009', 1, 1, 3, '南京', 'nj_ds@chinasoft.com', '210000', '南京市鼓楼区中山路200号设计大楼', '025-88888888-108', '南京分公司设计部门', '吴经理', '025-66666666-108', 8, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000095', '南京安全技术部', '南京安全部', 'CSG-NJ01-SEC', '1000000000000000009', 1, 1, 3, '南京', 'nj_sec@chinasoft.com', '210000', '南京市鼓楼区中山路200号安全大楼', '025-88888888-109', '南京分公司安全技术部门', '郑经理', '025-66666666-109', 9, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000096', '南京客户服务部', '南京客服部', 'CSG-NJ01-CS', '1000000000000000009', 1, 1, 3, '南京', 'nj_cs@chinasoft.com', '210000', '南京市鼓楼区中山路200号客服大楼', '025-88888888-110', '南京分公司客户服务部门', '王经理', '025-66666666-110', 10, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000097', '西安研发部', '西安研发部', 'CSG-XA01-RD', '1000000000000000010', 1, 1, 3, '西安', 'xa_rd@chinasoft.com', '710000', '西安市雁塔区长安南路300号研发大楼', '029-88888888-101', '西安分公司研发部门', '李经理', '029-66666666-101', 1, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000098', '西安市场部', '西安市场部', 'CSG-XA01-MKT', '1000000000000000010', 1, 1, 3, '西安', 'xa_mkt@chinasoft.com', '710000', '西安市雁塔区长安南路300号市场大楼', '029-88888888-102', '西安分公司市场部门', '王经理', '029-66666666-102', 2, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000099', '西安人事部', '西安人事部', 'CSG-XA01-HR', '1000000000000000010', 1, 1, 3, '西安', 'xa_hr@chinasoft.com', '710000', '西安市雁塔区长安南路300号人事大楼', '029-88888888-103', '西安分公司人事部门', '陈经理', '029-66666666-103', 3, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000100', '西安财务部', '西安财务部', 'CSG-XA01-FIN', '1000000000000000010', 1, 1, 3, '西安', 'xa_fin@chinasoft.com', '710000', '西安市雁塔区长安南路300号财务大楼', '029-88888888-104', '西安分公司财务部门', '赵经理', '029-66666666-104', 4, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000101', '西安运维部', '西安运维部', 'CSG-XA01-OPT', '1000000000000000010', 1, 1, 3, '西安', 'xa_opt@chinasoft.com', '710000', '西安市雁塔区长安南路300号运维大楼', '029-88888888-105', '西安分公司运维部门', '孙经理', '029-66666666-105', 5, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000102', '西安测试部', '西安测试部', 'CSG-XA01-TST', '1000000000000000010', 1, 1, 3, '西安', 'xa_tst@chinasoft.com', '710000', '西安市雁塔区长安南路300号测试大楼', '029-88888888-106', '西安分公司测试部门', '刘经理', '029-66666666-106', 6, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000103', '西安产品部', '西安产品部', 'CSG-XA01-PD', '1000000000000000010', 1, 1, 3, '西安', 'xa_pd@chinasoft.com', '710000', '西安市雁塔区长安南路300号产品大楼', '029-88888888-107', '西安分公司产品部门', '周经理', '029-66666666-107', 7, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000104', '西安设计部', '西安设计部', 'CSG-XA01-DS', '1000000000000000010', 1, 1, 3, '西安', 'xa_ds@chinasoft.com', '710000', '西安市雁塔区长安南路300号设计大楼', '029-88888888-108', '西安分公司设计部门', '吴经理', '029-66666666-108', 8, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000105', '西安安全技术部', '西安安全部', 'CSG-XA01-SEC', '1000000000000000010', 1, 1, 3, '西安', 'xa_sec@chinasoft.com', '710000', '西安市雁塔区长安南路300号安全大楼', '029-88888888-109', '西安分公司安全技术部门', '郑经理', '029-66666666-109', 9, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000106', '西安客户服务部', '西安客服部', 'CSG-XA01-CS', '1000000000000000010', 1, 1, 3, '西安', 'xa_cs@chinasoft.com', '710000', '西安市雁塔区长安南路300号客服大楼', '029-88888888-110', '西安分公司客户服务部门', '王经理', '029-66666666-110', 10, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000107', '沈阳研发部', '沈阳研发部', 'CSG-SY01-RD', '1000000000000000011', 1, 1, 3, '沈阳', 'sy_rd@chinasoft.com', '110000', '沈阳市和平区南京北街400号研发大楼', '024-88888888-101', '沈阳分公司研发部门', '李经理', '024-66666666-101', 1, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000108', '沈阳市场部', '沈阳市场部', 'CSG-SY01-MKT', '1000000000000000011', 1, 1, 3, '沈阳', 'sy_mkt@chinasoft.com', '110000', '沈阳市和平区南京北街400号市场大楼', '024-88888888-102', '沈阳分公司市场部门', '王经理', '024-66666666-102', 2, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000109', '沈阳人事部', '沈阳人事部', 'CSG-SY01-HR', '1000000000000000011', 1, 1, 3, '沈阳', 'sy_hr@chinasoft.com', '110000', '沈阳市和平区南京北街400号人事大楼', '024-88888888-103', '沈阳分公司人事部门', '陈经理', '024-66666666-103', 3, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000110', '沈阳财务部', '沈阳财务部', 'CSG-SY01-FIN', '1000000000000000011', 1, 1, 3, '沈阳', 'sy_fin@chinasoft.com', '110000', '沈阳市和平区南京北街400号财务大楼', '024-88888888-104', '沈阳分公司财务部门', '赵经理', '024-66666666-104', 4, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000111', '沈阳运维部', '沈阳运维部', 'CSG-SY01-OPT', '1000000000000000011', 1, 1, 3, '沈阳', 'sy_opt@chinasoft.com', '110000', '沈阳市和平区南京北街400号运维大楼', '024-88888888-105', '沈阳分公司运维部门', '孙经理', '024-66666666-105', 5, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000112', '沈阳测试部', '沈阳测试部', 'CSG-SY01-TST', '1000000000000000011', 1, 1, 3, '沈阳', 'sy_tst@chinasoft.com', '110000', '沈阳市和平区南京北街400号测试大楼', '024-88888888-106', '沈阳分公司测试部门', '刘经理', '024-66666666-106', 6, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000113', '沈阳产品部', '沈阳产品部', 'CSG-SY01-PD', '1000000000000000011', 1, 1, 3, '沈阳', 'sy_pd@chinasoft.com', '110000', '沈阳市和平区南京北街400号产品大楼', '024-88888888-107', '沈阳分公司产品部门', '周经理', '024-66666666-107', 7, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000114', '沈阳设计部', '沈阳设计部', 'CSG-SY01-DS', '1000000000000000011', 1, 1, 3, '沈阳', 'sy_ds@chinasoft.com', '110000', '沈阳市和平区南京北街400号设计大楼', '024-88888888-108', '沈阳分公司设计部门', '吴经理', '024-66666666-108', 8, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000115', '沈阳安全技术部', '沈阳安全部', 'CSG-SY01-SEC', '1000000000000000011', 1, 1, 3, '沈阳', 'sy_sec@chinasoft.com', '110000', '沈阳市和平区南京北街400号安全大楼', '024-88888888-109', '沈阳分公司安全技术部门', '郑经理', '024-66666666-109', 9, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000116', '沈阳客户服务部', '沈阳客服部', 'CSG-SY01-CS', '1000000000000000011', 1, 1, 3, '沈阳', 'sy_cs@chinasoft.com', '110000', '沈阳市和平区南京北街400号客服大楼', '024-88888888-110', '沈阳分公司客户服务部门', '王经理', '024-66666666-110', 10, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000117', '天津研发部', '天津研发部', 'CSG-TJ01-RD', '1000000000000000012', 1, 1, 3, '天津', 'tj_rd@chinasoft.com', '300000', '天津市滨海新区第一大街500号研发大楼', '022-88888888-101', '天津分公司研发部门', '李经理', '022-66666666-101', 1, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000118', '天津市场部', '天津市场部', 'CSG-TJ01-MKT', '1000000000000000012', 1, 1, 3, '天津', 'tj_mkt@chinasoft.com', '300000', '天津市滨海新区第一大街500号市场大楼', '022-88888888-102', '天津分公司市场部门', '王经理', '022-66666666-102', 2, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000119', '天津人事部', '天津人事部', 'CSG-TJ01-HR', '1000000000000000012', 1, 1, 3, '天津', 'tj_hr@chinasoft.com', '300000', '天津市滨海新区第一大街500号人事大楼', '022-88888888-103', '天津分公司人事部门', '陈经理', '022-66666666-103', 3, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000120', '天津财务部', '天津财务部', 'CSG-TJ01-FIN', '1000000000000000012', 1, 1, 3, '天津', 'tj_fin@chinasoft.com', '300000', '天津市滨海新区第一大街500号财务大楼', '022-88888888-104', '天津分公司财务部门', '赵经理', '022-66666666-104', 4, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000121', '天津运维部', '天津运维部', 'CSG-TJ01-OPT', '1000000000000000012', 1, 1, 3, '天津', 'tj_opt@chinasoft.com', '300000', '天津市滨海新区第一大街500号运维大楼', '022-88888888-105', '天津分公司运维部门', '孙经理', '022-66666666-105', 5, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000122', '天津测试部', '天津测试部', 'CSG-TJ01-TST', '1000000000000000012', 1, 1, 3, '天津', 'tj_tst@chinasoft.com', '300000', '天津市滨海新区第一大街500号测试大楼', '022-88888888-106', '天津分公司测试部门', '刘经理', '022-66666666-106', 6, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000123', '天津产品部', '天津产品部', 'CSG-TJ01-PD', '1000000000000000012', 1, 1, 3, '天津', 'tj_pd@chinasoft.com', '300000', '天津市滨海新区第一大街500号产品大楼', '022-88888888-107', '天津分公司产品部门', '周经理', '022-66666666-107', 7, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000124', '天津设计部', '天津设计部', 'CSG-TJ01-DS', '1000000000000000012', 1, 1, 3, '天津', 'tj_ds@chinasoft.com', '300000', '天津市滨海新区第一大街500号设计大楼', '022-88888888-108', '天津分公司设计部门', '吴经理', '022-66666666-108', 8, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000125', '天津安全技术部', '天津安全部', 'CSG-TJ01-SEC', '1000000000000000012', 1, 1, 3, '天津', 'tj_sec@chinasoft.com', '300000', '天津市滨海新区第一大街500号安全大楼', '022-88888888-109', '天津分公司安全技术部门', '郑经理', '022-66666666-109', 9, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000126', '天津客户服务部', '天津客服部', 'CSG-TJ01-CS', '1000000000000000012', 1, 1, 3, '天津', 'tj_cs@chinasoft.com', '300000', '天津市滨海新区第一大街500号客服大楼', '022-88888888-110', '天津分公司客户服务部门', '王经理', '022-66666666-110', 10, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000127', '重庆研发部', '重庆研发部', 'CSG-CQ01-RD', '1000000000000000013', 1, 1, 3, '重庆', 'cq_rd@chinasoft.com', '400000', '重庆市渝北区金开大道600号研发大楼', '023-88888888-101', '重庆分公司研发部门', '李经理', '023-66666666-101', 1, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000128', '重庆市场部', '重庆市场部', 'CSG-CQ01-MKT', '1000000000000000013', 1, 1, 3, '重庆', 'cq_mkt@chinasoft.com', '400000', '重庆市渝北区金开大道600号市场大楼', '023-88888888-102', '重庆分公司市场部门', '王经理', '023-66666666-102', 2, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000129', '重庆人事部', '重庆人事部', 'CSG-CQ01-HR', '1000000000000000013', 1, 1, 3, '重庆', 'cq_hr@chinasoft.com', '400000', '重庆市渝北区金开大道600号人事大楼', '023-88888888-103', '重庆分公司人事部门', '陈经理', '023-66666666-103', 3, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000130', '重庆财务部', '重庆财务部', 'CSG-CQ01-FIN', '1000000000000000013', 1, 1, 3, '重庆', 'cq_fin@chinasoft.com', '400000', '重庆市渝北区金开大道600号财务大楼', '023-88888888-104', '重庆分公司财务部门', '赵经理', '023-66666666-104', 4, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000131', '重庆运维部', '重庆运维部', 'CSG-CQ01-OPT', '1000000000000000013', 1, 1, 3, '重庆', 'cq_opt@chinasoft.com', '400000', '重庆市渝北区金开大道600号运维大楼', '023-88888888-105', '重庆分公司运维部门', '孙经理', '023-66666666-105', 5, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000132', '重庆测试部', '重庆测试部', 'CSG-CQ01-TST', '1000000000000000013', 1, 1, 3, '重庆', 'cq_tst@chinasoft.com', '400000', '重庆市渝北区金开大道600号测试大楼', '023-88888888-106', '重庆分公司测试部门', '刘经理', '023-66666666-106', 6, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000133', '重庆产品部', '重庆产品部', 'CSG-CQ01-PD', '1000000000000000013', 1, 1, 3, '重庆', 'cq_pd@chinasoft.com', '400000', '重庆市渝北区金开大道600号产品大楼', '023-88888888-107', '重庆分公司产品部门', '周经理', '023-66666666-107', 7, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000134', '重庆设计部', '重庆设计部', 'CSG-CQ01-DS', '1000000000000000013', 1, 1, 3, '重庆', 'cq_ds@chinasoft.com', '400000', '重庆市渝北区金开大道600号设计大楼', '023-88888888-108', '重庆分公司设计部门', '吴经理', '023-66666666-108', 8, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000135', '重庆安全技术部', '重庆安全部', 'CSG-CQ01-SEC', '1000000000000000013', 1, 1, 3, '重庆', 'cq_sec@chinasoft.com', '400000', '重庆市渝北区金开大道600号安全大楼', '023-88888888-109', '重庆分公司安全技术部门', '郑经理', '023-66666666-109', 9, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000136', '重庆客户服务部', '重庆客服部', 'CSG-CQ01-CS', '1000000000000000013', 1, 1, 3, '重庆', 'cq_cs@chinasoft.com', '400000', '重庆市渝北区金开大道600号客服大楼', '023-88888888-110', '重庆分公司客户服务部门', '王经理', '023-66666666-110', 10, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000137', '厦门研发部', '厦门研发部', 'CSG-XM01-RD', '1000000000000000014', 1, 1, 3, '厦门', 'xm_rd@chinasoft.com', '361000', '厦门市思明区软件园二期800号研发大楼', '0592-88888888-101', '厦门分公司研发部门', '李经理', '0592-66666666-101', 1, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000138', '厦门市场部', '厦门市场部', 'CSG-XM01-MKT', '1000000000000000014', 1, 1, 3, '厦门', 'xm_mkt@chinasoft.com', '361000', '厦门市思明区软件园二期800号市场大楼', '0592-88888888-102', '厦门分公司市场部门', '王经理', '0592-66666666-102', 2, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000139', '厦门人事部', '厦门人事部', 'CSG-XM01-HR', '1000000000000000014', 1, 1, 3, '厦门', 'xm_hr@chinasoft.com', '361000', '厦门市思明区软件园二期800号人事大楼', '0592-88888888-103', '厦门分公司人事部门', '陈经理', '0592-66666666-103', 3, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000140', '厦门财务部', '厦门财务部', 'CSG-XM01-FIN', '1000000000000000014', 1, 1, 3, '厦门', 'xm_fin@chinasoft.com', '361000', '厦门市思明区软件园二期800号财务大楼', '0592-88888888-104', '厦门分公司财务部门', '赵经理', '0592-66666666-104', 4, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000141', '厦门运维部', '厦门运维部', 'CSG-XM01-OPT', '1000000000000000014', 1, 1, 3, '厦门', 'xm_opt@chinasoft.com', '361000', '厦门市思明区软件园二期800号运维大楼', '0592-88888888-105', '厦门分公司运维部门', '孙经理', '0592-66666666-105', 5, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000142', '厦门测试部', '厦门测试部', 'CSG-XM01-TST', '1000000000000000014', 1, 1, 3, '厦门', 'xm_tst@chinasoft.com', '361000', '厦门市思明区软件园二期800号测试大楼', '0592-88888888-106', '厦门分公司测试部门', '刘经理', '0592-66666666-106', 6, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000143', '厦门产品部', '厦门产品部', 'CSG-XM01-PD', '1000000000000000014', 1, 1, 3, '厦门', 'xm_pd@chinasoft.com', '361000', '厦门市思明区软件园二期800号产品大楼', '0592-88888888-107', '厦门分公司产品部门', '周经理', '0592-66666666-107', 7, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000144', '厦门设计部', '厦门设计部', 'CSG-XM01-DS', '1000000000000000014', 1, 1, 3, '厦门', 'xm_ds@chinasoft.com', '361000', '厦门市思明区软件园二期800号设计大楼', '0592-88888888-108', '厦门分公司设计部门', '吴经理', '0592-66666666-108', 8, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000145', '厦门安全技术部', '厦门安全部', 'CSG-XM01-SEC', '1000000000000000014', 1, 1, 3, '厦门', 'xm_sec@chinasoft.com', '361000', '厦门市思明区软件园二期800号安全大楼', '0592-88888888-109', '厦门分公司安全技术部门', '郑经理', '0592-66666666-109', 9, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000146', '厦门客户服务部', '厦门客服部', 'CSG-XM01-CS', '1000000000000000014', 1, 1, 3, '厦门', 'xm_cs@chinasoft.com', '361000', '厦门市思明区软件园二期800号客服大楼', '0592-88888888-110', '厦门分公司客户服务部门', '王经理', '0592-66666666-110', 10, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000147', '青岛研发部', '青岛研发部', 'CSG-QD01-RD', '1000000000000000015', 1, 1, 3, '青岛', 'qd_rd@chinasoft.com', '266000', '青岛市市南区香港中路900号研发大楼', '0532-88888888-101', '青岛分公司研发部门', '李经理', '0532-66666666-101', 1, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000148', '青岛市场部', '青岛市场部', 'CSG-QD01-MKT', '1000000000000000015', 1, 1, 3, '青岛', 'qd_mkt@chinasoft.com', '266000', '青岛市市南区香港中路900号市场大楼', '0532-88888888-102', '青岛分公司市场部门', '王经理', '0532-66666666-102', 2, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000149', '青岛人事部', '青岛人事部', 'CSG-QD01-HR', '1000000000000000015', 1, 1, 3, '青岛', 'qd_hr@chinasoft.com', '266000', '青岛市市南区香港中路900号人事大楼', '0532-88888888-103', '青岛分公司人事部门', '陈经理', '0532-66666666-103', 3, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000150', '青岛财务部', '青岛财务部', 'CSG-QD01-FIN', '1000000000000000015', 1, 1, 3, '青岛', 'qd_fin@chinasoft.com', '266000', '青岛市市南区香港中路900号财务大楼', '0532-88888888-104', '青岛分公司财务部门', '赵经理', '0532-66666666-104', 4, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000151', '青岛运维部', '青岛运维部', 'CSG-QD01-OPT', '1000000000000000015', 1, 1, 3, '青岛', 'qd_opt@chinasoft.com', '266000', '青岛市市南区香港中路900号运维大楼', '0532-88888888-105', '青岛分公司运维部门', '孙经理', '0532-66666666-105', 5, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000152', '青岛测试部', '青岛测试部', 'CSG-QD01-TST', '1000000000000000015', 1, 1, 3, '青岛', 'qd_tst@chinasoft.com', '266000', '青岛市市南区香港中路900号测试大楼', '0532-88888888-106', '青岛分公司测试部门', '刘经理', '0532-66666666-106', 6, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000153', '青岛产品部', '青岛产品部', 'CSG-QD01-PD', '1000000000000000015', 1, 1, 3, '青岛', 'qd_pd@chinasoft.com', '266000', '青岛市市南区香港中路900号产品大楼', '0532-88888888-107', '青岛分公司产品部门', '周经理', '0532-66666666-107', 7, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000154', '青岛设计部', '青岛设计部', 'CSG-QD01-DS', '1000000000000000015', 1, 1, 3, '青岛', 'qd_ds@chinasoft.com', '266000', '青岛市市南区香港中路900号设计大楼', '0532-88888888-108', '青岛分公司设计部门', '吴经理', '0532-66666666-108', 8, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000155', '青岛安全技术部', '青岛安全部', 'CSG-QD01-SEC', '1000000000000000015', 1, 1, 3, '青岛', 'qd_sec@chinasoft.com', '266000', '青岛市市南区香港中路900号安全大楼', '0532-88888888-109', '青岛分公司安全技术部门', '郑经理', '0532-66666666-109', 9, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000156', '青岛客户服务部', '青岛客服部', 'CSG-QD01-CS', '1000000000000000015', 1, 1, 3, '青岛', 'qd_cs@chinasoft.com', '266000', '青岛市市南区香港中路900号客服大楼', '0532-88888888-110', '青岛分公司客户服务部门', '王经理', '0532-66666666-110', 10, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000157', '大连研发部', '大连研发部', 'CSG-DL01-RD', '1000000000000000016', 1, 1, 3, '大连', 'dl_rd@chinasoft.com', '116000', '大连市高新园区软件园路1000号研发大楼', '0411-88888888-101', '大连分公司研发部门', '李经理', '0411-66666666-101', 1, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000158', '大连市场部', '大连市场部', 'CSG-DL01-MKT', '1000000000000000016', 1, 1, 3, '大连', 'dl_mkt@chinasoft.com', '116000', '大连市高新园区软件园路1000号市场大楼', '0411-88888888-102', '大连分公司市场部门', '王经理', '0411-66666666-102', 2, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000159', '大连人事部', '大连人事部', 'CSG-DL01-HR', '1000000000000000016', 1, 1, 3, '大连', 'dl_hr@chinasoft.com', '116000', '大连市高新园区软件园路1000号人事大楼', '0411-88888888-103', '大连分公司人事部门', '陈经理', '0411-66666666-103', 3, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000160', '大连财务部', '大连财务部', 'CSG-DL01-FIN', '1000000000000000016', 1, 1, 3, '大连', 'dl_fin@chinasoft.com', '116000', '大连市高新园区软件园路1000号财务大楼', '0411-88888888-104', '大连分公司财务部门', '赵经理', '0411-66666666-104', 4, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000161', '大连运维部', '大连运维部', 'CSG-DL01-OPT', '1000000000000000016', 1, 1, 3, '大连', 'dl_opt@chinasoft.com', '116000', '大连市高新园区软件园路1000号运维大楼', '0411-88888888-105', '大连分公司运维部门', '孙经理', '0411-66666666-105', 5, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000162', '大连测试部', '大连测试部', 'CSG-DL01-TST', '1000000000000000016', 1, 1, 3, '大连', 'dl_tst@chinasoft.com', '116000', '大连市高新园区软件园路1000号测试大楼', '0411-88888888-106', '大连分公司测试部门', '刘经理', '0411-66666666-106', 6, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000165', '大连安全技术部', '大连安全部', 'CSG-DL01-SEC', '1000000000000000016', 1, 1, 3, '大连', 'dl_sec@chinasoft.com', '116000', '大连市高新园区软件园路1000号安全大楼', '0411-88888888-109', '大连分公司安全技术部门', '郑经理', '0411-66666666-109', 9, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000166', '大连客户服务部', '大连客服部', 'CSG-DL01-CS', '1000000000000000016', 1, 1, 3, '大连', 'dl_cs@chinasoft.com', '116000', '大连市高新园区软件园路1000号客服大楼', '0411-88888888-110', '大连分公司客户服务部门', '王经理', '0411-66666666-110', 10, 'department', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000167', '北京研发部前端组', '前端组', 'CSG-BJ01-RD-FE', '1000000000000000017', 1, 1, 4, '北京', 'bj_rd_fe@chinasoft.com', '100001', '北京市朝阳区建国路1号研发大楼3层', '010-88888889-101-01', '北京分公司研发部前端开发组', '李组长', '010-66666667-101-01', 1, 'team', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000168', '北京研发部后端组', '后端组', 'CSG-BJ01-RD-BE', '1000000000000000017', 1, 1, 4, '北京', 'bj_rd_be@chinasoft.com', '100001', '北京市朝阳区建国路1号研发大楼3层', '010-88888889-101-02', '北京分公司研发部后端开发组', '王组长', '010-66666667-101-02', 2, 'team', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000169', '北京市场部市场一组', '市场一组', 'CSG-BJ01-MKT-G1', '1000000000000000018', 1, 1, 4, '北京', 'bj_mkt_g1@chinasoft.com', '100001', '北京市朝阳区建国路1号市场大楼2层', '010-88888889-102-01', '北京分公司市场部市场一组', '陈组长', '010-66666667-102-01', 1, 'team', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000170', '北京市场部市场二组', '市场二组', 'CSG-BJ01-MKT-G2', '1000000000000000018', 1, 1, 4, '北京', 'bj_mkt_g2@chinasoft.com', '100001', '北京市朝阳区建国路1号市场大楼2层', '010-88888889-102-02', '北京分公司市场部市场二组', '赵组长', '010-66666667-102-02', 2, 'team', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000171', '北京人事部招聘组', '招聘组', 'CSG-BJ01-HR-RC', '1000000000000000019', 1, 1, 4, '北京', 'bj_hr_rc@chinasoft.com', '100001', '北京市朝阳区建国路1号人事大楼1层', '010-88888889-103-01', '北京分公司人事部招聘组', '孙组长', '010-66666667-103-01', 1, 'team', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000172', '北京人事部培训组', '培训组', 'CSG-BJ01-HR-TS', '1000000000000000019', 1, 1, 4, '北京', 'bj_hr_ts@chinasoft.com', '100001', '北京市朝阳区建国路1号人事大楼1层', '010-88888889-103-02', '北京分公司人事部培训组', '刘组长', '010-66666667-103-02', 2, 'team', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000173', '北京财务部会计组', '会计组', 'CSG-BJ01-FIN-AC', '1000000000000000020', 1, 1, 4, '北京', 'bj_fin_ac@chinasoft.com', '100001', '北京市朝阳区建国路1号财务大楼4层', '010-88888889-104-01', '北京分公司财务部会计组', '周组长', '010-66666667-104-01', 1, 'team', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000174', '北京财务部审计组', '审计组', 'CSG-BJ01-FIN-AU', '1000000000000000020', 1, 1, 4, '北京', 'bj_fin_au@chinasoft.com', '100001', '北京市朝阳区建国路1号财务大楼4层', '010-88888889-104-02', '北京分公司财务部审计组', '吴组长', '010-66666667-104-02', 2, 'team', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000175', '北京运维部系统组', '系统组', 'CSG-BJ01-OPT-SYS', '1000000000000000021', 1, 1, 4, '北京', 'bj_opt_sys@chinasoft.com', '100001', '北京市朝阳区建国路1号运维大楼5层', '010-88888889-105-01', '北京分公司运维部系统组', '郑组长', '010-66666667-105-01', 1, 'team', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000176', '北京运维部网络组', '网络组', 'CSG-BJ01-OPT-NET', '1000000000000000021', 1, 1, 4, '北京', 'bj_opt_net@chinasoft.com', '100001', '北京市朝阳区建国路1号运维大楼5层', '010-88888889-105-02', '北京分公司运维部网络组', '王组长', '010-66666667-105-02', 2, 'team', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000177', '北京测试部功能组', '功能组', 'CSG-BJ01-TST-FN', '1000000000000000022', 1, 1, 4, '北京', 'bj_tst_fn@chinasoft.com', '100001', '北京市朝阳区建国路1号测试大楼2层', '010-88888889-106-01', '北京分公司测试部功能测试组', '李组长', '010-66666667-106-01', 1, 'team', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000178', '北京测试部性能组', '性能组', 'CSG-BJ01-TST-PF', '1000000000000000022', 1, 1, 4, '北京', 'bj_tst_pf@chinasoft.com', '100001', '北京市朝阳区建国路1号测试大楼2层', '010-88888889-106-02', '北京分公司测试部性能测试组', '王组长', '010-66666667-106-02', 2, 'team', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000179', '北京产品部设计组', '设计组', 'CSG-BJ01-PD-DS', '1000000000000000023', 1, 1, 4, '北京', 'bj_pd_ds@chinasoft.com', '100001', '北京市朝阳区建国路1号产品大楼3层', '010-88888889-107-01', '北京分公司产品部设计组', '陈组长', '010-66666667-107-01', 1, 'team', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000180', '北京产品部调研组', '调研组', 'CSG-BJ01-PD-RS', '1000000000000000023', 1, 1, 4, '北京', 'bj_pd_rs@chinasoft.com', '100001', '北京市朝阳区建国路1号产品大楼3层', '010-88888889-107-02', '北京分公司产品部调研组', '赵组长', '010-66666667-107-02', 2, 'team', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000181', '北京设计部UI组', 'UI组', 'CSG-BJ01-DS-UI', '1000000000000000024', 1, 1, 4, '北京', 'bj_ds_ui@chinasoft.com', '100001', '北京市朝阳区建国路1号设计大楼4层', '010-88888889-108-01', '北京分公司设计部UI组', '孙组长', '010-66666667-108-01', 1, 'team', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000182', '北京设计部UE组', 'UE组', 'CSG-BJ01-DS-UE', '1000000000000000024', 1, 1, 4, '北京', 'bj_ds_ue@chinasoft.com', '100001', '北京市朝阳区建国路1号设计大楼4层', '010-88888889-108-02', '北京分公司设计部UE组', '刘组长', '010-66666667-108-02', 2, 'team', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000183', '北京安全部渗透组', '渗透组', 'CSG-BJ01-SEC-PT', '1000000000000000025', 1, 1, 4, '北京', 'bj_sec_pt@chinasoft.com', '100001', '北京市朝阳区建国路1号安全大楼1层', '010-88888889-109-01', '北京分公司安全部渗透测试组', '周组长', '010-66666667-109-01', 1, 'team', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000184', '北京安全部防护组', '防护组', 'CSG-BJ01-SEC-PR', '1000000000000000025', 1, 1, 4, '北京', 'bj_sec_pr@chinasoft.com', '100001', '北京市朝阳区建国路1号安全大楼1层', '010-88888889-109-02', '北京分公司安全部安全防护组', '吴组长', '010-66666667-109-02', 2, 'team', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000185', '北京客服部热线组', '热线组', 'CSG-BJ01-CS-HL', '1000000000000000026', 1, 1, 4, '北京', 'bj_cs_hl@chinasoft.com', '100001', '北京市朝阳区建国路1号客服大楼3层', '010-88888889-110-01', '北京分公司客服部热线组', '郑组长', '010-66666667-110-01', 1, 'team', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000186', '北京客服部在线组', '在线组', 'CSG-BJ01-CS-OL', '1000000000000000026', 1, 1, 4, '北京', 'bj_cs_ol@chinasoft.com', '100001', '北京市朝阳区建国路1号客服大楼3层', '010-88888889-110-02', '北京分公司客服部在线客服组', '王组长', '010-66666667-110-02', 2, 'team', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000187', '上海研发部前端组', '前端组', 'CSG-SH01-RD-FE', '1000000000000000027', 1, 1, 4, '上海', 'sh_rd_fe@chinasoft.com', '200000', '上海市浦东新区陆家嘴环路1000号研发大楼3层', '021-88888888-101-01', '上海分公司研发部前端开发组', '李组长', '021-66666666-101-01', 1, 'team', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000188', '上海研发部后端组', '后端组', 'CSG-SH01-RD-BE', '1000000000000000027', 1, 1, 4, '上海', 'sh_rd_be@chinasoft.com', '200000', '上海市浦东新区陆家嘴环路1000号研发大楼3层', '021-88888888-101-02', '上海分公司研发部后端开发组', '王组长', '021-66666666-101-02', 2, 'team', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000189', '上海市场部市场一组', '市场一组', 'CSG-SH01-MKT-G1', '1000000000000000028', 1, 1, 4, '上海', 'sh_mkt_g1@chinasoft.com', '200000', '上海市浦东新区陆家嘴环路1000号市场大楼2层', '021-88888888-102-01', '上海分公司市场部市场一组', '陈组长', '021-66666666-102-01', 1, 'team', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000190', '上海市场部市场二组', '市场二组', 'CSG-SH01-MKT-G2', '1000000000000000028', 1, 1, 4, '上海', 'sh_mkt_g2@chinasoft.com', '200000', '上海市浦东新区陆家嘴环路1000号市场大楼2层', '021-88888888-102-02', '上海分公司市场部市场二组', '赵组长', '021-66666666-102-02', 2, 'team', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000191', '上海人事部招聘组', '招聘组', 'CSG-SH01-HR-RC', '1000000000000000029', 1, 1, 4, '上海', 'sh_hr_rc@chinasoft.com', '200000', '上海市浦东新区陆家嘴环路1000号人事大楼1层', '021-88888888-103-01', '上海分公司人事部招聘组', '孙组长', '021-66666666-103-01', 1, 'team', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000192', '上海人事部培训组', '培训组', 'CSG-SH01-HR-TS', '1000000000000000029', 1, 1, 4, '上海', 'sh_hr_ts@chinasoft.com', '200000', '上海市浦东新区陆家嘴环路1000号人事大楼1层', '021-88888888-103-02', '上海分公司人事部培训组', '刘组长', '021-66666666-103-02', 2, 'team', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000193', '上海财务部会计组', '会计组', 'CSG-SH01-FIN-AC', '1000000000000000030', 1, 1, 4, '上海', 'sh_fin_ac@chinasoft.com', '200000', '上海市浦东新区陆家嘴环路1000号财务大楼4层', '021-88888888-104-01', '上海分公司财务部会计组', '周组长', '021-66666666-104-01', 1, 'team', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000194', '上海财务部审计组', '审计组', 'CSG-SH01-FIN-AU', '1000000000000000030', 1, 1, 4, '上海', 'sh_fin_au@chinasoft.com', '200000', '上海市浦东新区陆家嘴环路1000号财务大楼4层', '021-88888888-104-02', '上海分公司财务部审计组', '吴组长', '021-66666666-104-02', 2, 'team', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000195', '上海运维部系统组', '系统组', 'CSG-SH01-OPT-SYS', '1000000000000000031', 1, 1, 4, '上海', 'sh_opt_sys@chinasoft.com', '200000', '上海市浦东新区陆家嘴环路1000号运维大楼5层', '021-88888888-105-01', '上海分公司运维部系统组', '郑组长', '021-66666666-105-01', 1, 'team', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000196', '上海运维部网络组', '网络组', 'CSG-SH01-OPT-NET', '1000000000000000031', 1, 1, 4, '上海', 'sh_opt_net@chinasoft.com', '200000', '上海市浦东新区陆家嘴环路1000号运维大楼5层', '021-88888888-105-02', '上海分公司运维部网络组', '王组长', '021-66666666-105-02', 2, 'team', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000197', '广州研发部前端组', '前端组', 'CSG-GZ01-RD-FE', '1000000000000000037', 1, 1, 4, '广州', 'gz_rd_fe@chinasoft.com', '510000', '广州市天河区珠江新城100号研发大楼3层', '020-88888888-101-01', '广州分公司研发部前端开发组', '李组长', '020-66666666-101-01', 1, 'team', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000198', '广州研发部后端组', '后端组', 'CSG-GZ01-RD-BE', '1000000000000000037', 1, 1, 4, '广州', 'gz_rd_be@chinasoft.com', '510000', '广州市天河区珠江新城100号研发大楼3层', '020-88888888-101-02', '广州分公司研发部后端开发组', '王组长', '020-66666666-101-02', 2, 'team', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000199', '广州市场部市场一组', '市场一组', 'CSG-GZ01-MKT-G1', '1000000000000000038', 1, 1, 4, '广州', 'gz_mkt_g1@chinasoft.com', '510000', '广州市天河区珠江新城100号市场大楼2层', '020-88888888-102-01', '广州分公司市场部市场一组', '陈组长', '020-66666666-102-01', 1, 'team', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);
INSERT INTO `sys_depart` VALUES ('1000000000000000200', '广州市场部市场二组', '市场二组', 'CSG-GZ01-MKT-G2', '1000000000000000038', 1, 1, 4, '广州', 'gz_mkt_g2@chinasoft.com', '510000', '广州市天河区珠江新城100号市场大楼2层', '020-88888888-102-02', '广州分公司市场部市场二组', '赵组长', '020-66666666-102-02', 2, 'team', 1, 1, '2025-09-10 15:00:00', 'admin', '管理员', '2025-09-10 15:00:00', 'admin', '管理员', NULL, NULL);

-- ----------------------------
-- Table structure for sys_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_menu`;
CREATE TABLE `sys_menu`  (
  `id` varchar(225) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键id',
  `title` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '菜单名称',
  `pid` varchar(225) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '上级菜单id',
  `name` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '路由名称',
  `component` varchar(225) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '组件路径',
  `redirect` varchar(225) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '重定向路径',
  `path` varchar(225) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '路由路径',
  `permission` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '权限标识',
  `icon` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '图标',
  `link` varchar(225) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '外链地址',
  `seq` int NULL DEFAULT 0 COMMENT '排序',
  `type` int NULL DEFAULT 0 COMMENT '菜单类型 1目录 2菜单 3按钮',
  `is_link` int NULL DEFAULT 0 COMMENT '是否外链 0否 1是',
  `is_hide` int NULL DEFAULT 0 COMMENT '是否隐藏 0否 1是',
  `is_cache` int NULL DEFAULT 0 COMMENT '是否缓存 0否 1是',
  `is_affix` int NULL DEFAULT 0 COMMENT '是否固定 0否 1是',
  `is_iframe` int NULL DEFAULT 0 COMMENT '是否iframe嵌入 0否(新窗口打开) 1是',
  `is_delete` tinyint NULL DEFAULT 1 COMMENT '删除状态 0.已删除 1.正常',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(225) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人id',
  `create_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '菜单表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_menu
-- ----------------------------
INSERT INTO `sys_menu` VALUES ('10000000000000000003', '系统管理', '0', 'system', 'layout/routerView/parent', '/system/config', '/system', '', 'ri-settings-3-line', '', 1, 1, 0, 0, 0, 0, 0, 1, '2025-07-31 09:24:15', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('11000000000000000011', '菜单管理', '10000000000000000003', 'systemMenu', '/system/menu/index.vue', '', '/system/menu', 'system:menu:list', 'ri-apps-line', '', 1, 2, 0, 0, 0, 0, 0, 1, '2025-07-31 09:24:15', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('11000000000000000012', '角色管理', '10000000000000000003', 'systemRole', '/system/role/index.vue', '', '/system/role', 'system:role:list', 'cn cn-quanxianguanli', '', 2, 2, 0, 0, 0, 0, 0, 1, '2025-07-31 09:24:15', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('11000000000000000013', '部门管理', '10000000000000000003', 'systemDept', '/system/dept/index.vue', '', '/system/dept', 'system:dept:list', 'cn cn-department_fill', '', 3, 2, 0, 0, 0, 0, 0, 1, '2025-07-31 09:24:15', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('11000000000000000015', '参数配置', '10000000000000000003', 'systemConfig', '/system/config/index.vue', '', '/system/config', 'system:config:list', 'cn cn-canshuguanli', '', 6, 2, 0, 0, 0, 0, 0, 1, '2025-07-31 09:24:15', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('11000000000000000020', '用户管理', '10000000000000000003', 'systemUser', '/system/user/index.vue', '', '/system/user', 'system:user:list', 'cn cn-renyuanjianguan', '', 3, 2, 0, 0, 0, 0, 0, 1, '2025-09-11 16:03:39', '1949738202609135617', '超级管理员');
INSERT INTO `sys_menu` VALUES ('19000000000000000029', '用户管理-查询', '11000000000000000020', NULL, '', '', '', 'system:user:list', '', '', 1, 3, 0, 0, 0, 0, 0, 1, '2025-07-31 09:27:03', '', '');
INSERT INTO `sys_menu` VALUES ('19000000000000000030', '用户管理-新增', '11000000000000000020', NULL, '', '', '', 'system:user:add', '', '', 2, 3, 0, 0, 0, 0, 0, 1, '2025-07-31 09:27:03', '', '');
INSERT INTO `sys_menu` VALUES ('19000000000000000031', '用户管理-修改', '11000000000000000020', NULL, '', '', '', 'system:user:edit', '', '', 3, 3, 0, 0, 0, 0, 0, 1, '2025-07-31 09:27:03', '', '');
INSERT INTO `sys_menu` VALUES ('19000000000000000032', '用户管理-删除', '11000000000000000020', NULL, '', '', '', 'system:user:delete', '', '', 4, 3, 0, 0, 0, 0, 0, 1, '2025-07-31 09:27:03', '', '');
INSERT INTO `sys_menu` VALUES ('19000000000000000033', '用户管理-冻结/解冻', '11000000000000000020', NULL, '', '', '', 'system:user:freeze', '', '', 5, 3, 0, 0, 0, 0, 0, 1, '2025-07-31 09:27:03', '', '');
INSERT INTO `sys_menu` VALUES ('19000000000000000034', '用户管理-重置密码', '11000000000000000020', NULL, '', '', '', 'system:user:resetPwd', '', '', 6, 3, 0, 0, 0, 0, 0, 1, '2025-07-31 09:27:03', '', '');
INSERT INTO `sys_menu` VALUES ('19000000000000000035', '菜单管理-查询', '11000000000000000011', NULL, NULL, NULL, NULL, 'system:menu:query', NULL, NULL, 1, 3, 0, 0, 0, 0, 0, 1, '2025-07-31 09:27:03', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('19000000000000000036', '菜单管理-新增', '11000000000000000011', NULL, NULL, NULL, NULL, 'system:menu:add', NULL, NULL, 2, 3, 0, 0, 0, 0, 0, 1, '2025-07-31 09:27:03', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('19000000000000000037', '菜单管理-修改', '11000000000000000011', NULL, NULL, NULL, NULL, 'system:menu:edit', NULL, NULL, 3, 3, 0, 0, 0, 0, 0, 1, '2025-07-31 09:27:03', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('19000000000000000038', '菜单管理-删除', '11000000000000000011', NULL, NULL, NULL, NULL, 'system:menu:delete', NULL, NULL, 4, 3, 0, 0, 0, 0, 0, 1, '2025-07-31 09:27:03', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('19000000000000000039', '角色管理-查询', '11000000000000000012', NULL, NULL, NULL, NULL, 'system:role:query', NULL, NULL, 1, 3, 0, 0, 0, 0, 0, 1, '2025-07-31 09:27:03', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('19000000000000000040', '角色管理-新增', '11000000000000000012', NULL, NULL, NULL, NULL, 'system:role:add', NULL, NULL, 2, 3, 0, 0, 0, 0, 0, 1, '2025-07-31 09:27:03', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('19000000000000000041', '角色管理-修改', '11000000000000000012', NULL, NULL, NULL, NULL, 'system:role:edit', NULL, NULL, 3, 3, 0, 0, 0, 0, 0, 1, '2025-07-31 09:27:03', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('19000000000000000042', '角色管理-删除', '11000000000000000012', NULL, NULL, NULL, NULL, 'system:role:delete', NULL, NULL, 4, 3, 0, 0, 0, 0, 0, 1, '2025-07-31 09:27:03', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('19000000000000000043', '角色管理-分配权限', '11000000000000000012', NULL, '', '', '', 'system:role:updateAuth', '', '', 5, 3, 0, 0, 0, 0, 0, 1, '2025-07-31 09:27:03', '', '');
INSERT INTO `sys_menu` VALUES ('19000000000000000044', '角色管理-人员配置', '11000000000000000012', '', NULL, NULL, NULL, 'system:role:userConfig', NULL, NULL, 6, 3, 0, 0, 0, 0, 0, 1, '2025-09-19 12:27:53', '1949738202609135617', '超级管理员');
INSERT INTO `sys_menu` VALUES ('19000000000000000045', '角色管理-数据权限', '11000000000000000012', '', NULL, NULL, NULL, 'system:role:dataPermission', NULL, NULL, 7, 3, 0, 0, 0, 0, 0, 1, '2026-01-14 10:58:02', '1949738202609135617', '超级管理员');
INSERT INTO `sys_menu` VALUES ('19000000000000000046', '部门管理-查询', '11000000000000000013', NULL, NULL, NULL, NULL, 'system:dept:query', NULL, NULL, 1, 3, 0, 0, 0, 0, 0, 1, '2025-07-31 09:27:03', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('19000000000000000047', '部门管理-新增', '11000000000000000013', NULL, NULL, NULL, NULL, 'system:dept:add', NULL, NULL, 2, 3, 0, 0, 0, 0, 0, 1, '2025-07-31 09:27:03', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('19000000000000000048', '部门管理-修改', '11000000000000000013', NULL, NULL, NULL, NULL, 'system:dept:edit', NULL, NULL, 3, 3, 0, 0, 0, 0, 0, 1, '2025-07-31 09:27:03', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('19000000000000000049', '部门管理-删除', '11000000000000000013', NULL, NULL, NULL, NULL, 'system:dept:delete', NULL, NULL, 4, 3, 0, 0, 0, 0, 0, 1, '2025-07-31 09:27:03', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('19000000000000000054', '参数配置-查询', '11000000000000000015', NULL, '', '', '', 'system:config:list', '', '', 1, 3, 0, 0, 0, 0, 0, 1, '2025-07-31 09:27:03', '', '');
INSERT INTO `sys_menu` VALUES ('19000000000000000055', '参数配置-新增', '11000000000000000015', NULL, '', '', '', 'system:config:add', '', '', 2, 3, 0, 0, 0, 0, 0, 1, '2025-07-31 09:27:03', '', '');
INSERT INTO `sys_menu` VALUES ('19000000000000000056', '参数配置-修改', '11000000000000000015', NULL, '', '', '', 'system:config:edit', '', '', 3, 3, 0, 0, 0, 0, 0, 1, '2025-07-31 09:27:03', '', '');
INSERT INTO `sys_menu` VALUES ('19000000000000000057', '参数配置-删除', '11000000000000000015', NULL, '', '', '', 'system:config:delete', '', '', 4, 3, 0, 0, 0, 0, 0, 1, '2025-07-31 09:27:03', '', '');

-- ----------------------------
-- Table structure for sys_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_role`;
CREATE TABLE `sys_role`  (
  `id` varchar(225) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键id',
  `code` varchar(225) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色编码',
  `name` varchar(225) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色名称',
  `remark` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `seq` int NULL DEFAULT NULL COMMENT '排序',
  `status` int NULL DEFAULT 1 COMMENT '状态 0.禁用 1.正常',
  `is_delete` tinyint NULL DEFAULT 1 COMMENT '删除状态 0.已删除 1.正常',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(225) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人id',
  `create_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人名称',
  PRIMARY KEY (`id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '角色信息表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role
-- ----------------------------
INSERT INTO `sys_role` VALUES ('1949738202609135222', 'admin', '管理员', '', 1, 1, 1, NULL, NULL, NULL);
INSERT INTO `sys_role` VALUES ('1955154995175673857', 'visitor', '游客', '', 2, 1, 1, '2025-08-12 14:30:52', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955155180035428353', 'member', '会员', '', 3, 1, 1, '2025-08-12 14:31:35', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000001', 'test_role_1', '测试角色1', '测试数据', 4, 1, 1, '2025-08-15 15:21:44', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000002', 'test_role_2', '测试角色2', '测试数据', 5, 1, 1, '2025-08-15 15:21:44', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000003', 'test_role_3', '测试角色3', '测试数据', 6, 1, 1, '2025-08-15 15:21:44', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000004', 'test_role_4', '测试角色4', '测试数据', 7, 1, 1, '2025-08-15 15:21:44', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000005', 'test_role_5', '测试角色5', '测试数据', 8, 1, 1, '2025-08-15 15:21:44', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000006', 'test_role_6', '测试角色6', '测试数据', 9, 1, 1, '2025-08-15 15:21:44', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000007', 'test_role_7', '测试角色7', '测试数据', 10, 1, 1, '2025-08-15 15:21:44', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000008', 'test_role_8', '测试角色8', '测试数据', 11, 1, 1, '2025-08-15 15:21:44', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000009', 'test_role_9', '测试角色9', '测试数据', 12, 1, 1, '2025-08-15 15:21:44', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000010', 'test_role_10', '测试角色10', '测试数据', 13, 1, 1, '2025-08-15 15:21:44', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000011', 'test_role_11', '测试角色11', '测试数据', 14, 1, 1, '2025-08-15 15:21:58', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000012', 'test_role_12', '测试角色12', '测试数据', 15, 1, 1, '2025-08-15 15:21:58', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000013', 'test_role_13', '测试角色13', '测试数据', 16, 1, 1, '2025-08-15 15:21:58', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000014', 'test_role_14', '测试角色14', '测试数据', 17, 1, 1, '2025-08-15 15:21:58', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000015', 'test_role_15', '测试角色15', '测试数据', 18, 1, 1, '2025-08-15 15:21:58', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000016', 'test_role_16', '测试角色16', '测试数据', 19, 1, 1, '2025-08-15 15:21:58', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000017', 'test_role_17', '测试角色17', '测试数据', 20, 1, 1, '2025-08-15 15:21:58', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000018', 'test_role_18', '测试角色18', '测试数据', 21, 1, 1, '2025-08-15 15:21:58', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000019', 'test_role_19', '测试角色19', '测试数据', 22, 1, 1, '2025-08-15 15:21:58', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000020', 'test_role_20', '测试角色20', '测试数据', 23, 1, 1, '2025-08-15 15:21:58', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000021', 'test_role_21', '测试角色21', '测试数据', 24, 1, 1, '2025-08-15 15:22:11', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000022', 'test_role_22', '测试角色22', '测试数据', 25, 1, 1, '2025-08-15 15:22:11', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000023', 'test_role_23', '测试角色23', '测试数据', 26, 1, 1, '2025-08-15 15:22:11', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000024', 'test_role_24', '测试角色24', '测试数据', 27, 1, 1, '2025-08-15 15:22:11', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000025', 'test_role_25', '测试角色25', '测试数据', 28, 1, 1, '2025-08-15 15:22:11', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000026', 'test_role_26', '测试角色26', '测试数据', 29, 1, 1, '2025-08-15 15:22:11', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000027', 'test_role_27', '测试角色27', '测试数据', 30, 1, 1, '2025-08-15 15:22:11', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000028', 'test_role_28', '测试角色28', '测试数据', 31, 1, 1, '2025-08-15 15:22:11', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000029', 'test_role_29', '测试角色29', '测试数据', 32, 1, 1, '2025-08-15 15:22:11', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000030', 'test_role_30', '测试角色30', '测试数据', 33, 1, 1, '2025-08-15 15:22:11', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000031', 'test_role_31', '测试角色31', '测试数据', 34, 1, 1, '2025-08-15 15:22:28', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000032', 'test_role_32', '测试角色32', '测试数据', 35, 1, 1, '2025-08-15 15:22:28', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000033', 'test_role_33', '测试角色33', '测试数据', 36, 1, 1, '2025-08-15 15:22:28', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000034', 'test_role_34', '测试角色34', '测试数据', 37, 1, 1, '2025-08-15 15:22:28', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000035', 'test_role_35', '测试角色35', '测试数据', 38, 1, 1, '2025-08-15 15:22:28', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000036', 'test_role_36', '测试角色36', '测试数据', 39, 1, 1, '2025-08-15 15:22:28', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000037', 'test_role_37', '测试角色37', '测试数据', 40, 1, 1, '2025-08-15 15:22:28', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000038', 'test_role_38', '测试角色38', '测试数据', 41, 1, 1, '2025-08-15 15:22:28', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000039', 'test_role_39', '测试角色39', '测试数据', 42, 1, 1, '2025-08-15 15:22:28', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000040', 'test_role_40', '测试角色40', '测试数据', 43, 1, 1, '2025-08-15 15:22:28', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000041', 'test_role_41', '测试角色41', '测试数据', 44, 1, 1, '2025-08-15 15:22:40', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000042', 'test_role_42', '测试角色42', '测试数据', 45, 1, 1, '2025-08-15 15:22:40', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000043', 'test_role_43', '测试角色43', '测试数据', 46, 1, 1, '2025-08-15 15:22:40', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000044', 'test_role_44', '测试角色44', '测试数据', 47, 1, 1, '2025-08-15 15:22:40', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000045', 'test_role_45', '测试角色45', '测试数据', 48, 1, 1, '2025-08-15 15:22:40', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000046', 'test_role_46', '测试角色46', '测试数据', 49, 1, 1, '2025-08-15 15:22:40', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000047', 'test_role_47', '测试角色47', '测试数据', 50, 1, 1, '2025-08-15 15:22:40', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000048', 'test_role_48', '测试角色48', '测试数据', 51, 1, 1, '2025-08-15 15:22:40', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000049', 'test_role_49', '测试角色49', '测试数据', 52, 1, 1, '2025-08-15 15:22:40', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000050', 'test_role_50', '测试角色50', '测试数据', 53, 1, 1, '2025-08-15 15:22:40', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000051', 'test_role_51', '测试角色51', '测试数据', 54, 1, 1, '2025-08-15 15:22:51', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000052', 'test_role_52', '测试角色52', '测试数据', 55, 1, 1, '2025-08-15 15:22:51', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000053', 'test_role_53', '测试角色53', '测试数据', 56, 1, 1, '2025-08-15 15:22:51', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000054', 'test_role_54', '测试角色54', '测试数据', 57, 1, 1, '2025-08-15 15:22:51', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000055', 'test_role_55', '测试角色55', '测试数据', 58, 1, 1, '2025-08-15 15:22:51', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000056', 'test_role_56', '测试角色56', '测试数据', 59, 1, 1, '2025-08-15 15:22:51', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000057', 'test_role_57', '测试角色57', '测试数据', 60, 1, 1, '2025-08-15 15:22:51', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000058', 'test_role_58', '测试角色58', '测试数据', 61, 1, 1, '2025-08-15 15:22:51', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000059', 'test_role_59', '测试角色59', '测试数据', 62, 1, 1, '2025-08-15 15:22:51', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000060', 'test_role_60', '测试角色60', '测试数据', 63, 1, 1, '2025-08-15 15:22:51', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000061', 'test_role_61', '测试角色61', '测试数据', 64, 1, 1, '2025-08-15 15:23:01', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000062', 'test_role_62', '测试角色62', '测试数据', 65, 1, 1, '2025-08-15 15:23:01', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000063', 'test_role_63', '测试角色63', '测试数据', 66, 1, 1, '2025-08-15 15:23:01', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000064', 'test_role_64', '测试角色64', '测试数据', 67, 1, 1, '2025-08-15 15:23:01', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000065', 'test_role_65', '测试角色65', '测试数据', 68, 1, 1, '2025-08-15 15:23:01', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000066', 'test_role_66', '测试角色66', '测试数据', 69, 1, 1, '2025-08-15 15:23:01', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000067', 'test_role_67', '测试角色67', '测试数据', 70, 1, 1, '2025-08-15 15:23:01', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000068', 'test_role_68', '测试角色68', '测试数据', 71, 1, 1, '2025-08-15 15:23:01', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000069', 'test_role_69', '测试角色69', '测试数据', 72, 1, 1, '2025-08-15 15:23:01', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000070', 'test_role_70', '测试角色70', '测试数据', 73, 1, 1, '2025-08-15 15:23:01', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000071', 'test_role_71', '测试角色71', '测试数据', 74, 1, 1, '2025-08-15 15:23:15', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000072', 'test_role_72', '测试角色72', '测试数据', 75, 1, 1, '2025-08-15 15:23:15', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000073', 'test_role_73', '测试角色73', '测试数据', 76, 1, 1, '2025-08-15 15:23:15', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000074', 'test_role_74', '测试角色74', '测试数据', 77, 1, 1, '2025-08-15 15:23:15', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000075', 'test_role_75', '测试角色75', '测试数据', 78, 1, 1, '2025-08-15 15:23:15', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000076', 'test_role_76', '测试角色76', '测试数据', 79, 1, 1, '2025-08-15 15:23:15', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000077', 'test_role_77', '测试角色77', '测试数据', 80, 1, 1, '2025-08-15 15:23:15', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000078', 'test_role_78', '测试角色78', '测试数据', 81, 1, 1, '2025-08-15 15:23:15', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000079', 'test_role_79', '测试角色79', '测试数据', 82, 1, 1, '2025-08-15 15:23:15', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000080', 'test_role_80', '测试角色80', '测试数据', 83, 1, 1, '2025-08-15 15:23:15', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000081', 'test_role_81', '测试角色81', '测试数据', 84, 1, 1, '2025-08-15 15:23:27', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000082', 'test_role_82', '测试角色82', '测试数据', 85, 1, 1, '2025-08-15 15:23:27', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000083', 'test_role_83', '测试角色83', '测试数据', 86, 1, 1, '2025-08-15 15:23:27', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000084', 'test_role_84', '测试角色84', '测试数据', 87, 1, 1, '2025-08-15 15:23:27', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000085', 'test_role_85', '测试角色85', '测试数据', 88, 1, 1, '2025-08-15 15:23:27', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000086', 'test_role_86', '测试角色86', '测试数据', 89, 1, 1, '2025-08-15 15:23:27', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000087', 'test_role_87', '测试角色87', '测试数据', 90, 1, 1, '2025-08-15 15:23:27', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000088', 'test_role_88', '测试角色88', '测试数据', 91, 1, 1, '2025-08-15 15:23:27', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000089', 'test_role_89', '测试角色89', '测试数据', 92, 1, 1, '2025-08-15 15:23:27', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000090', 'test_role_90', '测试角色90', '测试数据', 93, 1, 1, '2025-08-15 15:23:27', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000091', 'test_role_91', '测试角色91', '测试数据', 94, 1, 1, '2025-08-15 15:23:38', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000092', 'test_role_92', '测试角色92', '测试数据', 95, 1, 1, '2025-08-15 15:23:38', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000093', 'test_role_93', '测试角色93', '测试数据', 96, 1, 1, '2025-08-15 15:23:38', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000094', 'test_role_94', '测试角色94', '测试数据', 97, 1, 1, '2025-08-15 15:23:38', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000095', 'test_role_95', '测试角色95', '测试数据', 98, 1, 1, '2025-08-15 15:23:38', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000096', 'test_role_96', '测试角色96', '测试数据', 99, 1, 1, '2025-08-15 15:23:38', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000097', 'test_role_97', '测试角色97', '测试数据', 100, 1, 1, '2025-08-15 15:23:38', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000098', 'test_role_98', '测试角色98', '测试数据', 101, 1, 1, '2025-08-15 15:23:38', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000099', 'test_role_99', '测试角色99', '测试数据', 102, 1, 1, '2025-08-15 15:23:38', '1949738202609135617', '超级管理员');
INSERT INTO `sys_role` VALUES ('1955156000000000100', 'test_role_100', '测试角色100', '测试数据', 103, 1, 1, '2025-08-15 15:23:38', '1949738202609135617', '超级管理员');

-- ----------------------------
-- Table structure for sys_role_menu
-- ----------------------------
DROP TABLE IF EXISTS `sys_role_menu`;
CREATE TABLE `sys_role_menu`  (
  `role_id` varchar(225) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色id',
  `menu_id` varchar(225) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '菜单id',
  PRIMARY KEY (`role_id`, `menu_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '角色菜单权限表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_role_menu
-- ----------------------------
INSERT INTO `sys_role_menu` VALUES ('1949738202609135222', '10000000000000000003');
INSERT INTO `sys_role_menu` VALUES ('1949738202609135222', '11000000000000000011');
INSERT INTO `sys_role_menu` VALUES ('1949738202609135222', '11000000000000000012');
INSERT INTO `sys_role_menu` VALUES ('1949738202609135222', '11000000000000000013');
INSERT INTO `sys_role_menu` VALUES ('1949738202609135222', '11000000000000000015');
INSERT INTO `sys_role_menu` VALUES ('1949738202609135222', '11000000000000000020');
INSERT INTO `sys_role_menu` VALUES ('1949738202609135222', '19000000000000000029');
INSERT INTO `sys_role_menu` VALUES ('1949738202609135222', '19000000000000000030');
INSERT INTO `sys_role_menu` VALUES ('1949738202609135222', '19000000000000000031');
INSERT INTO `sys_role_menu` VALUES ('1949738202609135222', '19000000000000000032');
INSERT INTO `sys_role_menu` VALUES ('1949738202609135222', '19000000000000000033');
INSERT INTO `sys_role_menu` VALUES ('1949738202609135222', '19000000000000000034');
INSERT INTO `sys_role_menu` VALUES ('1949738202609135222', '19000000000000000035');
INSERT INTO `sys_role_menu` VALUES ('1949738202609135222', '19000000000000000036');
INSERT INTO `sys_role_menu` VALUES ('1949738202609135222', '19000000000000000037');
INSERT INTO `sys_role_menu` VALUES ('1949738202609135222', '19000000000000000038');
INSERT INTO `sys_role_menu` VALUES ('1949738202609135222', '19000000000000000039');
INSERT INTO `sys_role_menu` VALUES ('1949738202609135222', '19000000000000000040');
INSERT INTO `sys_role_menu` VALUES ('1949738202609135222', '19000000000000000041');
INSERT INTO `sys_role_menu` VALUES ('1949738202609135222', '19000000000000000042');
INSERT INTO `sys_role_menu` VALUES ('1949738202609135222', '19000000000000000043');
INSERT INTO `sys_role_menu` VALUES ('1949738202609135222', '19000000000000000044');
INSERT INTO `sys_role_menu` VALUES ('1949738202609135222', '19000000000000000045');
INSERT INTO `sys_role_menu` VALUES ('1949738202609135222', '19000000000000000046');
INSERT INTO `sys_role_menu` VALUES ('1949738202609135222', '19000000000000000047');
INSERT INTO `sys_role_menu` VALUES ('1949738202609135222', '19000000000000000048');
INSERT INTO `sys_role_menu` VALUES ('1949738202609135222', '19000000000000000049');
INSERT INTO `sys_role_menu` VALUES ('1949738202609135222', '19000000000000000054');
INSERT INTO `sys_role_menu` VALUES ('1949738202609135222', '19000000000000000055');
INSERT INTO `sys_role_menu` VALUES ('1949738202609135222', '19000000000000000056');
INSERT INTO `sys_role_menu` VALUES ('1949738202609135222', '19000000000000000057');

-- ----------------------------
-- Table structure for sys_user
-- ----------------------------
DROP TABLE IF EXISTS `sys_user`;
CREATE TABLE `sys_user`  (
  `id` varchar(225) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '主键id',
  `login_name` varchar(225) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '登录名',
  `password` varchar(225) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '密码',
  `hex_key` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '密码加密密钥',
  `user_name` varchar(225) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '真实姓名',
  `code` varchar(225) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '编号',
  `depart_id` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '所属组织机构id',
  `depart_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '所属组织机构名称',
  `user_type` int NOT NULL DEFAULT 1 COMMENT '用户类型（0：游客，1：普通用户 2：会员，3：管理员（中后台））',
  `phone` varchar(20) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '手机号码',
  `email` varchar(100) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '邮箱',
  `third_type` int NULL DEFAULT NULL COMMENT '第三方类型（字典）',
  `third_account` varchar(150) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '第三方登录账号',
  `remark` varchar(500) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '备注',
  `status` int NULL DEFAULT 1 COMMENT '状态 0.禁用 1.正常',
  `is_lock` int NULL DEFAULT 1 COMMENT '锁定状态 0.已锁定 1.正常',
  `is_delete` tinyint NULL DEFAULT 1 COMMENT '删除状态 0.已删除 1.正常',
  `create_time` datetime NULL DEFAULT NULL COMMENT '创建时间',
  `create_by` varchar(225) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人id',
  `create_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '创建人名称',
  `update_time` datetime NULL DEFAULT NULL COMMENT '更新时间',
  `update_by` varchar(225) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人id',
  `update_name` varchar(255) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '更新人名称',
  `login_ip` varchar(64) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '最后登录ip',
  `login_time` datetime NULL DEFAULT NULL COMMENT '最后登录时间',
  `password_update_time` datetime NULL DEFAULT NULL COMMENT '密码最后更新时间',
  `position_id` varchar(32) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '岗位ID',
  `position_name` varchar(50) CHARACTER SET utf8 COLLATE utf8_general_ci NULL DEFAULT NULL COMMENT '岗位名称',
  PRIMARY KEY (`id`) USING BTREE,
  UNIQUE INDEX `login_name`(`login_name` ASC) USING BTREE,
  INDEX `idx_email`(`email` ASC) USING BTREE,
  INDEX `idx_phone`(`phone` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户基础表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user
-- ----------------------------
INSERT INTO `sys_user` VALUES ('1949738202609135617', 'admin', '123456', '21749f300a04e033a7723129f92552fe', '超级管理员', '304740339c3c4b19b9a2ddace93e747e', '1000000000000000003', '上海中软科技有限公司', 3, '18888888888', '', 1, NULL, NULL, 1, 1, 1, '2025-07-28 15:46:27', NULL, NULL, NULL, NULL, NULL, '172.16.4.233', '2026-05-12 09:57:23', NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('1949739899905564673', 'liuq', '123456', 'b1c9c3e595f1e1f02e006b5ca8504ecc', '刘潜', 'ef03b2d02e3c41a1bd45f8b7ea3bc3c2', '1000000000000000001', '中国软件集团有限公司', 1, '185****8585', '', 2, NULL, '', 1, 1, 1, '2025-07-28 15:53:11', NULL, NULL, '2025-09-19 15:43:18', NULL, NULL, '172.16.3.244', '2026-01-15 10:45:29', '2025-09-19 15:43:18', NULL, NULL);
INSERT INTO `sys_user` VALUES ('1968938273577877506', '222', '123456', '1eb45c36eafc586d1a3605f2f22bbfad', '2222', 'dd45a556139e451aa419f177e3aba770', '1000000000000000003', '', 1, '1848847898', '', NULL, NULL, '', 1, 1, 1, '2025-09-19 15:20:40', '1949738202609135617', '超级管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000001', 'zhangwei', '123456', 'abc123', '张伟', 'U001', '1000000000000000001', '中国软件集团有限公司', 1, '13812345601', 'zhangwei@chinasoft.com', NULL, NULL, '北京研发部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000002', 'lisi', '123456', 'abc123', '李四', 'U002', '1000000000000000001', '中国软件集团有限公司', 1, '13812345602', 'lisi@chinasoft.com', NULL, NULL, '北京研发部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000003', 'wangwu', '123456', 'abc123', '王五', 'U003', '1000000000000000017', '北京研发部', 1, '13812345603', 'wangwu@chinasoft.com', NULL, NULL, '北京研发部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000004', 'zhaoliu', '123456', 'abc123', '赵六', 'U004', '1000000000000000001', '中国软件集团有限公司', 1, '13812345604', 'zhaoliu@chinasoft.com', NULL, NULL, '上海研发部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000005', 'sunqi', '123456', 'abc123', '孙七', 'U005', '1000000000000000001', '中国软件集团有限公司', 1, '13812345605', 'sunqi@chinasoft.com', NULL, NULL, '上海研发部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000006', 'zhouba', '123456', 'abc123', '周八', 'U006', '1000000000000000037', '广州研发部', 1, '13812345606', 'zhouba@chinasoft.com', NULL, NULL, '广州研发部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000007', 'wujiu', '123456', 'abc123', '吴九', 'U007', '1000000000000000037', '广州研发部', 1, '13812345607', 'wujiu@chinasoft.com', NULL, NULL, '广州研发部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000008', 'zhengshi', '123456', 'abc123', '郑十', 'U008', '1000000000000000047', '深圳研发部', 1, '138****5608', 'zhengshi@chinasoft.com', NULL, NULL, '深圳研发部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, 'POS001', '首席执行官');
INSERT INTO `sys_user` VALUES ('2000000000000000009', 'liyuan', '123456', 'abc123', '李元', 'U009', '1000000000000000047', '深圳研发部', 1, '13812345609', 'liyuan@chinasoft.com', NULL, NULL, '深圳研发部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000010', 'chenyi', '123456', 'abc123', '陈一', 'U010', '1000000000000000057', '杭州研发部', 1, '13812345610', 'chenyi@chinasoft.com', NULL, NULL, '杭州研发部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000011', 'huanger', '123456', 'abc123', '胡二', 'U011', '1000000000000000057', '杭州研发部', 1, '13812345611', 'huanger@chinasoft.com', NULL, NULL, '杭州研发部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000012', 'zhangsanfeng', '123456', 'abc123', '张三丰', 'U012', '1000000000000000067', '成都研发部', 1, '13812345612', 'zhangsanfeng@chinasoft.com', NULL, NULL, '成都研发部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000013', 'zhaoyun', '123456', 'abc123', '赵云', 'U013', '1000000000000000067', '成都研发部', 1, '13812345613', 'zhaoyun@chinasoft.com', NULL, NULL, '成都研发部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000014', 'liubei', '123456', 'abc123', '刘备', 'U014', '1000000000000000077', '武汉研发部', 1, '13812345614', 'liubei@chinasoft.com', NULL, NULL, '武汉研发部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000015', 'guanyu', '123456', 'abc123', '关羽', 'U015', '1000000000000000077', '武汉研发部', 1, '13812345615', 'guanyu@chinasoft.com', NULL, NULL, '武汉研发部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000016', 'zhangfei', '123456', 'abc123', '张飞', 'U016', '1000000000000000087', '南京研发部', 1, '13812345616', 'zhangfei@chinasoft.com', NULL, NULL, '南京研发部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000017', 'caocao', '123456', 'abc123', '曹操', 'U017', '1000000000000000087', '南京研发部', 1, '13812345617', 'caocao@chinasoft.com', NULL, NULL, '南京研发部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000018', 'sunquan', '123456', 'abc123', '孙权', 'U018', '1000000000000000097', '西安研发部', 1, '13812345618', 'sunquan@chinasoft.com', NULL, NULL, '西安研发部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000019', 'zhugeliang', '123456', 'abc123', '诸葛亮', 'U019', '1000000000000000097', '西安研发部', 1, '13812345619', 'zhugeliang@chinasoft.com', NULL, NULL, '西安研发部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000020', 'simayi', '123456', 'abc123', '司马懿', 'U020', '1000000000000000107', '沈阳研发部', 1, '13812345620', 'simayi@chinasoft.com', NULL, NULL, '沈阳研发部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000021', 'diaochan', '123456', 'abc123', '貂蝉', 'U021', '1000000000000000107', '沈阳研发部', 1, '13812345621', 'diaochan@chinasoft.com', NULL, NULL, '沈阳研发部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000022', 'lvbu', '123456', 'abc123', '吕布', 'U022', '1000000000000000117', '天津研发部', 1, '13812345622', 'lvbu@chinasoft.com', NULL, NULL, '天津研发部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000023', 'daqiao', '123456', 'abc123', '大乔', 'U023', '1000000000000000117', '天津研发部', 1, '13812345623', 'daqiao@chinasoft.com', NULL, NULL, '天津研发部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000024', 'xiaoqiao', '123456', 'abc123', '小乔', 'U024', '1000000000000000127', '重庆研发部', 1, '13812345624', 'xiaoqiao@chinasoft.com', NULL, NULL, '重庆研发部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000025', 'machao', '123456', 'abc123', '马超', 'U025', '1000000000000000127', '重庆研发部', 1, '13812345625', 'machao@chinasoft.com', NULL, NULL, '重庆研发部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000026', 'huangzhong', '123456', 'abc123', '黄忠', 'U026', '1000000000000000137', '厦门研发部', 1, '13812345626', 'huangzhong@chinasoft.com', NULL, NULL, '厦门研发部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000027', 'weiyan', '123456', 'abc123', '魏延', 'U027', '1000000000000000137', '厦门研发部', 1, '13812345627', 'weiyan@chinasoft.com', NULL, NULL, '厦门研发部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000028', 'jiangwei', '123456', 'abc123', '姜维', 'U028', '1000000000000000147', '青岛研发部', 1, '13812345628', 'jiangwei@chinasoft.com', NULL, NULL, '青岛研发部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000029', 'pangtong', '123456', 'abc123', '庞统', 'U029', '1000000000000000147', '青岛研发部', 1, '13812345629', 'pangtong@chinasoft.com', NULL, NULL, '青岛研发部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000030', 'zhouyu', '123456', 'abc123', '周瑜', 'U030', '1000000000000000157', '大连研发部', 1, '13812345630', 'zhouyu@chinasoft.com', NULL, NULL, '大连研发部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000031', 'luxun', '123456', 'abc123', '陆逊', 'U031', '1000000000000000157', '大连研发部', 1, '13812345631', 'luxun@chinasoft.com', NULL, NULL, '大连研发部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000032', 'huanggai', '123456', 'abc123', '黄盖', 'U032', '1000000000000000018', '北京市场部', 1, '13812345632', 'huanggai@chinasoft.com', NULL, NULL, '北京市场部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000033', 'ganxing', '123456', 'abc123', '甘兴', 'U033', '1000000000000000018', '北京市场部', 1, '13812345633', 'ganxing@chinasoft.com', NULL, NULL, '北京市场部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000034', 'taiyoubing', '123456', 'abc123', '太史慈', 'U034', '1000000000000000028', '上海市场部', 1, '13812345634', 'taiyoubing@chinasoft.com', NULL, NULL, '上海市场部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000035', 'chengpu', '123456', 'abc123', '程普', 'U035', '1000000000000000028', '上海市场部', 1, '13812345635', 'chengpu@chinasoft.com', NULL, NULL, '上海市场部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000036', 'zhoutai', '123456', 'abc123', '周泰', 'U036', '1000000000000000038', '广州市场部', 1, '13812345636', 'zhoutai@chinasoft.com', NULL, NULL, '广州市场部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000037', 'xusheng', '123456', 'abc123', '徐盛', 'U037', '1000000000000000038', '广州市场部', 1, '13812345637', 'xusheng@chinasoft.com', NULL, NULL, '广州市场部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000038', 'lingcao', '123456', 'abc123', '凌操', 'U038', '1000000000000000048', '深圳市场部', 1, '13812345638', 'lingcao@chinasoft.com', NULL, NULL, '深圳市场部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000039', 'wuguang', '123456', 'abc123', '吴广', 'U039', '1000000000000000048', '深圳市场部', 1, '13812345639', 'wuguang@chinasoft.com', NULL, NULL, '深圳市场部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000040', 'liuyao', '123456', 'abc123', '刘繇', 'U040', '1000000000000000058', '杭州市场部', 1, '13812345640', 'liuyao@chinasoft.com', NULL, NULL, '杭州市场部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000041', 'liufeng', '123456', 'abc123', '刘封', 'U041', '1000000000000000058', '杭州市场部', 1, '13812345641', 'liufeng@chinasoft.com', NULL, NULL, '杭州市场部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000042', 'guansuo', '123456', 'abc123', '关索', 'U042', '1000000000000000068', '成都市场部', 1, '13812345642', 'guansuo@chinasoft.com', NULL, NULL, '成都市场部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000043', 'guanping', '123456', 'abc123', '关平', 'U043', '1000000000000000068', '成都市场部', 1, '13812345643', 'guanping@chinasoft.com', NULL, NULL, '成都市场部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000044', 'guanxing', '123456', 'abc123', '关兴', 'U044', '1000000000000000078', '武汉市场部', 1, '13812345644', 'guanxing@chinasoft.com', NULL, NULL, '武汉市场部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000045', 'zhangbao', '123456', 'abc123', '张宝', 'U045', '1000000000000000078', '武汉市场部', 1, '13812345645', 'zhangbao@chinasoft.com', NULL, NULL, '武汉市场部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000046', 'zhangliang', '123456', 'abc123', '张梁', 'U046', '1000000000000000088', '南京市场部', 1, '13812345646', 'zhangliang@chinasoft.com', NULL, NULL, '南京市场部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000047', 'liuhai', '123456', 'abc123', '刘嗨', 'U047', '1000000000000000088', '南京市场部', 1, '13812345647', 'liuhai@chinasoft.com', NULL, NULL, '南京市场部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000048', 'caoren', '123456', 'abc123', '曹仁', 'U048', '1000000000000000098', '西安市场部', 1, '13812345648', 'caoren@chinasoft.com', NULL, NULL, '西安市场部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000049', 'caohong', '123456', 'abc123', '曹洪', 'U049', '1000000000000000098', '西安市场部', 1, '13812345649', 'caohong@chinasoft.com', NULL, NULL, '西安市场部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);
INSERT INTO `sys_user` VALUES ('2000000000000000050', 'xiahoudun', '123456', 'abc123', '夏侯惇', 'U050', '1000000000000000108', '沈阳市场部', 1, '13812345650', 'xiahoudun@chinasoft.com', NULL, NULL, '沈阳市场部员工', 1, 1, 1, '2025-09-10 09:00:00', 'admin', '管理员', NULL, NULL, NULL, NULL, NULL, NULL, NULL, NULL);

-- ----------------------------
-- Table structure for sys_user_role
-- ----------------------------
DROP TABLE IF EXISTS `sys_user_role`;
CREATE TABLE `sys_user_role`  (
  `user_id` varchar(225) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '用户id',
  `role_id` varchar(225) CHARACTER SET utf8 COLLATE utf8_general_ci NOT NULL COMMENT '角色id',
  PRIMARY KEY (`user_id`, `role_id`) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8 COLLATE = utf8_general_ci COMMENT = '用户角色权限表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- Records of sys_user_role
-- ----------------------------
INSERT INTO `sys_user_role` VALUES ('1949738202609135617', '1949738202609135222');
INSERT INTO `sys_user_role` VALUES ('1949739899905564673', '1949738202609135222');
INSERT INTO `sys_user_role` VALUES ('1949739899905564673', '1955154995175673857');
INSERT INTO `sys_user_role` VALUES ('1949739899905564673', '1955155180035428353');
INSERT INTO `sys_user_role` VALUES ('1949739899905564673', '1955156000000000001');
INSERT INTO `sys_user_role` VALUES ('1968938273577877506', '1949738202609135222');
INSERT INTO `sys_user_role` VALUES ('1968938273577877506', '1955154995175673857');
INSERT INTO `sys_user_role` VALUES ('2000000000000000001', '1949738202609135222');
INSERT INTO `sys_user_role` VALUES ('2000000000000000002', '1949738202609135222');
INSERT INTO `sys_user_role` VALUES ('2000000000000000003', '1949738202609135222');
INSERT INTO `sys_user_role` VALUES ('2000000000000000008', '1955154995175673857');
INSERT INTO `sys_user_role` VALUES ('2000000000000000008', '1955155180035428353');

SET FOREIGN_KEY_CHECKS = 1;
