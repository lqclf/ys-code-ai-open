-- ======================================================================
-- 系统日志模块（ys-infra-log）增量脚本
-- 说明：在已有 ys-open.sql 初始化完成的库上执行本脚本即可启用日志功能
-- 包含：sys_logger 建表、菜单与按钮权限数据、超级管理员角色授权
-- ======================================================================

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for sys_logger
-- ----------------------------
DROP TABLE IF EXISTS `sys_logger`;
CREATE TABLE `sys_logger`  (
  `id` varchar(225) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NOT NULL COMMENT '主键id',
  `log_type` int NULL DEFAULT NULL COMMENT '日志类型 0登录 1登出 2业务操作 3异常',
  `modul` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '功能模块',
  `type` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作类型',
  `remark` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作描述',
  `req_param` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '请求参数',
  `resp_param` longtext CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '返回参数',
  `user_id` varchar(225) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作用户ID',
  `login_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作用户登录名',
  `user_name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作用户姓名',
  `method` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作方法',
  `url` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '请求URL',
  `ip` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '请求IP',
  `address` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '请求IP物理地址',
  `win` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作系统',
  `user_agent` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '浏览器的信息',
  `version` varchar(64) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '操作版本号',
  `trace_id` varchar(32) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '请求追踪ID',
  `log_level` tinyint(1) NULL DEFAULT 1 COMMENT '日志级别 0DEBUG 1INFO 2WARN 3ERROR 4FATAL',
  `log_time` datetime NULL DEFAULT NULL COMMENT '操作时间',
  `exc_name` varchar(1024) CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL DEFAULT NULL COMMENT '异常名称',
  `exc_msg` text CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci NULL COMMENT '异常信息',
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_trace_id`(`trace_id` ASC) USING BTREE,
  INDEX `idx_log_level`(`log_level` ASC) USING BTREE,
  INDEX `idx_log_type`(`log_type` ASC) USING BTREE,
  INDEX `idx_user_id`(`user_id` ASC) USING BTREE,
  INDEX `idx_log_time`(`log_time` ASC) USING BTREE
) ENGINE = InnoDB CHARACTER SET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = '系统日志表' ROW_FORMAT = DYNAMIC;

-- ----------------------------
-- 菜单与按钮权限
-- ----------------------------
DELETE FROM `sys_menu` WHERE `id` IN (
  '11000000000000000016',
  '19000000000000000090',
  '19000000000000000091',
  '19000000000000000092'
);

-- 日志管理菜单（挂在“系统管理”目录 10000000000000000003 下）
INSERT INTO `sys_menu` VALUES ('11000000000000000016', '日志管理', '10000000000000000003', 'systemLogger', '/system/logger/index.vue', '', '/system/logger', 'system:logger:list', 'ri-file-list-3-line', '', 7, 2, 0, 0, 0, 0, 0, 1, '2026-08-10 10:00:00', NULL, NULL);

-- 日志管理按钮权限
INSERT INTO `sys_menu` VALUES ('19000000000000000090', '日志管理-查询', '11000000000000000016', NULL, NULL, NULL, NULL, 'system:logger:list', NULL, NULL, 1, 3, 0, 0, 0, 0, 0, 1, '2026-08-10 10:00:00', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('19000000000000000091', '日志管理-详情', '11000000000000000016', NULL, NULL, NULL, NULL, 'system:logger:detail', NULL, NULL, 2, 3, 0, 0, 0, 0, 0, 1, '2026-08-10 10:00:00', NULL, NULL);
INSERT INTO `sys_menu` VALUES ('19000000000000000092', '日志管理-删除', '11000000000000000016', NULL, NULL, NULL, NULL, 'system:logger:delete', NULL, NULL, 3, 3, 0, 0, 0, 0, 0, 1, '2026-08-10 10:00:00', NULL, NULL);

-- ----------------------------
-- 超级管理员角色授权
-- 说明：自动为拥有“系统管理”目录权限的角色补充日志管理相关权限
-- ----------------------------
DELETE FROM `sys_role_menu` WHERE `menu_id` IN (
  '11000000000000000016',
  '19000000000000000090',
  '19000000000000000091',
  '19000000000000000092'
);

INSERT INTO `sys_role_menu` (`role_id`, `menu_id`)
SELECT rm.`role_id`, m.`menu_id`
FROM (SELECT DISTINCT `role_id` FROM `sys_role_menu` WHERE `menu_id` = '10000000000000000003') rm
CROSS JOIN (
  SELECT '11000000000000000016' AS `menu_id`
  UNION ALL SELECT '19000000000000000090'
  UNION ALL SELECT '19000000000000000091'
  UNION ALL SELECT '19000000000000000092'
) m;

SET FOREIGN_KEY_CHECKS = 1;
