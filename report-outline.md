# 实验报告提纲：基于 Nacos 注册中心的微服务案例

## 一、实验目的

说明本实验用于学习 Spring Boot 微服务拆分、Nacos 服务注册与发现、消费者通过服务名调用提供者、Spring Cloud LoadBalancer 客户端负载均衡，以及为微服务设计对应数据库脚本和测试数据。

截图占位：无。

## 二、实验环境

说明本实验使用 Java 17、Maven、Spring Boot 3.2.4、Spring Cloud 2023.0.1、Spring Cloud Alibaba 2023.0.1.0、Docker Nacos，Nacos 地址为 `127.0.0.1:8848`。

截图占位：可放 `java -version`、`.\mvnw.cmd -version`、`docker ps` 的命令行截图。

## 三、项目结构

说明项目是 Maven 多模块结构，父工程为 `nacos-microservice-lab`，包含 `course-service` 和 `enrollment-service` 两个独立 Spring Boot 服务。

说明 `course-service/src/main/resources/course.sql` 是课程服务数据库脚本，`enrollment-service/src/main/resources/enrollment.sql` 是选课服务数据库脚本。

截图占位：可放 IDE 或文件管理器中的项目目录结构截图，重点截出两个模块 `resources` 目录下的 SQL 文件。

## 四、数据库设计与测试数据

说明 `course.sql` 创建 `course_db` 数据库和 `tb_course` 课程表，测试数据包括“Java 微服务基础”“Nacos 注册中心实验”“Spring Cloud LoadBalancer”三门课程。

说明 `enrollment.sql` 创建 `enrollment_db` 数据库，包含 `tb_student` 学生表和 `tb_enrollment` 选课记录表，测试数据包括张三、李四、王五三名学生及三条选课记录。

说明当前项目为了突出 Nacos 注册发现和负载均衡，运行时仍使用内存固定数据，SQL 文件用于展示微服务对应数据库和测试数据设计。

截图占位：可放 `course.sql`、`enrollment.sql` 的关键建表和插入数据代码截图；如果导入了 MySQL，也可放 `SELECT * FROM tb_course;`、`SELECT * FROM tb_student;`、`SELECT * FROM tb_enrollment;` 的查询结果截图。

## 五、Nacos 启动

说明 Nacos 使用 Docker 单机模式启动，服务地址为 `http://127.0.0.1:8848/nacos`，微服务通过 `spring.cloud.nacos.discovery.server-addr` 连接该注册中心。

截图占位：可放 Docker 启动命令截图和 Nacos 控制台首页截图。

## 六、服务注册

说明 `course-service` 分别以端口 `8081` 和 `8083` 启动两个实例，`enrollment-service` 以端口 `8082` 启动一个实例，三个实例都会注册到 Nacos。

截图占位：可放 Nacos 服务列表截图，以及 `course-service` 实例详情中 `8081`、`8083` 两个实例的截图。

## 七、服务调用

说明 `enrollment-service` 中配置了带 `@LoadBalanced` 的 `RestTemplate`，业务代码调用地址为 `http://course-service/courses/{id}`，通过服务名而不是固定 IP 和端口访问课程服务。

截图占位：可放 `RestTemplateConfig.java` 和 `EnrollmentQueryService.java` 中关键代码截图。

## 八、负载均衡验证

说明连续访问 `http://127.0.0.1:8082/enrollments/1`，消费者会从 Nacos 获取 `course-service` 的两个实例，并由 Spring Cloud LoadBalancer 在客户端选择具体实例。

截图占位：可放 PowerShell 连续请求命令及输出截图，重点标出 `servedByPort` 中出现 `8081` 和 `8083`。

## 九、运行结果

说明直接访问 `course-service` 可以看到课程信息和当前处理请求的端口；访问 `enrollment-service` 可以看到选课记录、学生姓名、课程信息以及课程服务返回的 `servedByPort`。

截图占位：可放以下接口的实际返回结果截图：

```text
http://127.0.0.1:8081/courses/1
http://127.0.0.1:8083/courses/1
http://127.0.0.1:8082/enrollments/1
```

## 十、总结

总结本实验完成了两个独立微服务的搭建，使用 Nacos 完成服务注册与发现，消费者通过服务名调用提供者，并通过启动两个课程服务实例验证了客户端负载均衡效果。同时，项目为两个微服务分别提供了数据库脚本和测试数据，方便在实验报告中展示微服务数据设计。

截图占位：无。
