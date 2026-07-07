/*
 基于 Nacos 注册中心的微服务案例：选课服务数据库脚本

 说明：
 1. 本脚本用于创建 enrollment-service 对应的独立数据库和测试数据。
 2. enrollment-service 只保存选课记录和学生信息，课程详情由 course-service 提供。
 3. 当前实验运行代码仍使用内存固定数据，避免引入 MySQL/MyBatis 后增加服务注册与负载均衡实验复杂度。
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE IF NOT EXISTS `enrollment_db`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE `enrollment_db`;

-- ----------------------------
-- Table structure for tb_student
-- ----------------------------
DROP TABLE IF EXISTS `tb_student`;
CREATE TABLE `tb_student` (
  `id` bigint NOT NULL COMMENT '学生ID',
  `name` varchar(50) NOT NULL COMMENT '学生姓名',
  `student_no` varchar(30) NOT NULL COMMENT '学号',
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_student_no` (`student_no`)
) ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = '学生信息表';

-- ----------------------------
-- Records of tb_student
-- ----------------------------
INSERT INTO `tb_student` (`id`, `name`, `student_no`) VALUES
  (1, '张三', 'S2026001'),
  (2, '李四', 'S2026002'),
  (3, '王五', 'S2026003');

-- ----------------------------
-- Table structure for tb_enrollment
-- ----------------------------
DROP TABLE IF EXISTS `tb_enrollment`;
CREATE TABLE `tb_enrollment` (
  `id` bigint NOT NULL COMMENT '选课记录ID',
  `student_id` bigint NOT NULL COMMENT '学生ID',
  `course_id` bigint NOT NULL COMMENT '课程ID，由 course-service 管理',
  `semester` varchar(30) NOT NULL COMMENT '学期',
  PRIMARY KEY (`id`),
  KEY `idx_student_id` (`student_id`),
  KEY `idx_course_id` (`course_id`)
) ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = '选课记录表';

-- ----------------------------
-- Records of tb_enrollment
-- ----------------------------
INSERT INTO `tb_enrollment` (`id`, `student_id`, `course_id`, `semester`) VALUES
  (1, 1, 1, '2026 春季学期'),
  (2, 2, 2, '2026 春季学期'),
  (3, 3, 3, '2026 春季学期');

SET FOREIGN_KEY_CHECKS = 1;
