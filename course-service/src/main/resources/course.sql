/*
 基于 Nacos 注册中心的微服务案例：课程服务数据库脚本

 说明：
 1. 本脚本用于创建 course-service 对应的独立数据库和测试数据。
 2. 当前实验运行代码仍使用内存固定数据，避免引入 MySQL/MyBatis 后增加服务注册与负载均衡实验复杂度。
 3. 表中的测试数据与 course-service 接口返回的示例课程保持一致。
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

CREATE DATABASE IF NOT EXISTS `course_db`
  DEFAULT CHARACTER SET utf8mb4
  DEFAULT COLLATE utf8mb4_0900_ai_ci;

USE `course_db`;

-- ----------------------------
-- Table structure for tb_course
-- ----------------------------
DROP TABLE IF EXISTS `tb_course`;
CREATE TABLE `tb_course` (
  `id` bigint NOT NULL COMMENT '课程ID',
  `name` varchar(100) NOT NULL COMMENT '课程名称',
  `description` varchar(255) NOT NULL COMMENT '课程简介',
  PRIMARY KEY (`id`)
) ENGINE = InnoDB
  DEFAULT CHARACTER SET = utf8mb4
  COLLATE = utf8mb4_0900_ai_ci
  COMMENT = '课程信息表';

-- ----------------------------
-- Records of tb_course
-- ----------------------------
INSERT INTO `tb_course` (`id`, `name`, `description`) VALUES
  (1, 'Java 微服务基础', '学习 Spring Boot、服务注册与服务调用的基础课程'),
  (2, 'Nacos 注册中心实验', '使用 Nacos 完成服务注册、发现与客户端负载均衡'),
  (3, 'Spring Cloud LoadBalancer', '通过服务名调用多个服务实例并观察轮询效果');

SET FOREIGN_KEY_CHECKS = 1;
