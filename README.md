# 基于 Nacos 注册中心的微服务案例

本项目是一个可以直接运行的 Maven 多模块实验项目，包含两个独立的 Spring Boot 微服务：

- `course-service`：课程服务提供者，服务名为 `course-service`，默认端口 `8081`。
- `enrollment-service`：选课服务消费者，服务名为 `enrollment-service`，端口 `8082`。

消费者通过 `RestTemplate` 调用 `http://course-service/courses/{id}`，并在 `RestTemplate` 上使用 `@LoadBalanced`，所以业务代码中没有硬编码 `localhost:8081` 或 `localhost:8083`。课程服务启动两个实例后，可以通过返回结果中的 `servedByPort` 观察客户端负载均衡效果。

按照老师补充要求，项目中还提供了每个微服务对应的 SQL 数据库脚本和测试数据。为了保持 Nacos 注册发现实验简单，当前运行代码仍使用内存固定数据，不需要启动 MySQL 才能运行服务。

## 版本说明

- Java：17
- Maven：使用项目自带 Maven Wrapper
- Spring Boot：`3.2.4`
- Spring Cloud：`2023.0.1`
- Spring Cloud Alibaba：`2023.0.1.0`
- Nacos：本地 Docker，地址 `127.0.0.1:8848`

## 项目结构

```text
nacos-microservice-lab
|- pom.xml
|- mvnw.cmd
|- README.md
|- report-outline.md
|- course-service
|  |- pom.xml
|  |- src/main/java/com/example/nacos/course
|  |  |- CourseServiceApplication.java
|  |  |- controller/CourseController.java
|  |  |- model/CourseInfo.java
|  |  |- model/CourseResponse.java
|  |  `- service/CourseCatalogService.java
|  `- src/main/resources
|     |- application.yml
|     `- course.sql
`- enrollment-service
   |- pom.xml
   |- src/main/java/com/example/nacos/enrollment
   |  |- EnrollmentServiceApplication.java
   |  |- config/RestTemplateConfig.java
   |  |- controller/EnrollmentController.java
   |  |- model/CourseInfo.java
   |  |- model/EnrollmentRecord.java
   |  |- model/EnrollmentResponse.java
   |  `- service/EnrollmentQueryService.java
   `- src/main/resources
      |- application.yml
      `- enrollment.sql
```

## 1. 启动 Nacos

如果本机还没有启动 Nacos，可以在 PowerShell 中执行：

```powershell
docker run -d --name nacos-standalone `
  -e MODE=standalone `
  -e NACOS_AUTH_ENABLE=false `
  -p 8848:8848 `
  -p 9848:9848 `
  -p 9849:9849 `
  nacos/nacos-server:v2.3.2
```

如果容器已经创建过，只需要启动：

```powershell
docker start nacos-standalone
```

Nacos 控制台地址：

```text
http://127.0.0.1:8848/nacos
```

进入控制台后，可以在“服务管理 / 服务列表”中看到 `course-service` 和 `enrollment-service`。

## 2. 数据库脚本和测试数据

SQL 文件位置：

```text
course-service/src/main/resources/course.sql
enrollment-service/src/main/resources/enrollment.sql
```

两个脚本会分别创建：

```text
course_db
enrollment_db
```

`course_db` 中有课程表 `tb_course`，测试数据如下：

```text
1 Java 微服务基础
2 Nacos 注册中心实验
3 Spring Cloud LoadBalancer
```

`enrollment_db` 中有学生表 `tb_student` 和选课记录表 `tb_enrollment`，测试数据如下：

```text
1 张三 课程ID 1
2 李四 课程ID 2
3 王五 课程ID 3
```

如果需要真的导入 MySQL，可以在 PowerShell 中进入 MySQL 客户端：

```powershell
mysql --default-character-set=utf8mb4 -u root -p
```

进入 MySQL 后执行：

```sql
SOURCE E:/my_projects/nacos-microservice-lab/course-service/src/main/resources/course.sql;
SOURCE E:/my_projects/nacos-microservice-lab/enrollment-service/src/main/resources/enrollment.sql;
```

验证测试数据：

```sql
USE course_db;
SELECT * FROM tb_course;

USE enrollment_db;
SELECT * FROM tb_student;
SELECT * FROM tb_enrollment;
```

说明：当前 Spring Boot 服务没有配置 `spring.datasource`，也没有引入 MyBatis。SQL 文件用于满足“创建自己的微服务对应数据库，并提供测试数据”的实验材料要求；微服务运行仍然只依赖 Nacos。

## 3. 编译项目

在项目根目录执行：

```powershell
.\mvnw.cmd clean package
```

预期结果：

```text
BUILD SUCCESS
```

## 4. 启动课程服务提供者 8081

打开第一个 PowerShell，进入项目根目录：

```powershell
$env:SERVER_PORT = "8081"
java -jar .\course-service\target\course-service-1.0.0-SNAPSHOT.jar
```

预期启动端口：`8081`。该实例会以服务名 `course-service` 注册到 Nacos。

## 5. 启动课程服务提供者 8083

打开第二个 PowerShell，进入项目根目录：

```powershell
$env:SERVER_PORT = "8083"
java -jar .\course-service\target\course-service-1.0.0-SNAPSHOT.jar
```

预期启动端口：`8083`。该实例也会以服务名 `course-service` 注册到 Nacos。

此时在 Nacos 控制台中查看 `course-service`，应能看到两个实例：

```text
127.0.0.1:8081
127.0.0.1:8083
```

## 6. 启动选课服务消费者 8082

打开第三个 PowerShell，进入项目根目录：

```powershell
$env:SERVER_PORT = "8082"
java -jar .\enrollment-service\target\enrollment-service-1.0.0-SNAPSHOT.jar
```

预期启动端口：`8082`。该实例会以服务名 `enrollment-service` 注册到 Nacos。

## 7. 直接测试课程服务

分别访问两个课程服务实例：

```powershell
Invoke-RestMethod http://127.0.0.1:8081/courses/1
Invoke-RestMethod http://127.0.0.1:8083/courses/1
```

预期结果中可以看到不同的 `servedByPort`：

```json
{
  "id": 1,
  "name": "Java 微服务基础",
  "description": "学习 Spring Boot、服务注册与服务调用的基础课程",
  "servedByPort": "8081"
}
```

```json
{
  "id": 1,
  "name": "Java 微服务基础",
  "description": "学习 Spring Boot、服务注册与服务调用的基础课程",
  "servedByPort": "8083"
}
```

## 8. 通过消费者测试服务调用

访问选课服务：

```powershell
Invoke-RestMethod http://127.0.0.1:8082/enrollments/1
```

预期结果：

```json
{
  "id": 1,
  "studentName": "张三",
  "course": {
    "id": 1,
    "name": "Java 微服务基础",
    "description": "学习 Spring Boot、服务注册与服务调用的基础课程",
    "servedByPort": "8081"
  }
}
```

其中 `course.servedByPort` 可能是 `8081`，也可能是 `8083`，表示本次请求最终由哪个课程服务实例处理。

## 9. 连续请求观察负载均衡

确认两个 `course-service` 实例都已经注册到 Nacos 后，执行：

```powershell
1..8 | ForEach-Object {
  (Invoke-RestMethod http://127.0.0.1:8082/enrollments/1).course.servedByPort
}
```

预期可以看到 `8081` 和 `8083` 轮流或交替出现，例如：

```text
8081
8083
8081
8083
8081
8083
8081
8083
```

如果刚启动后没有马上轮流出现，等待 5 到 10 秒，让消费者刷新 Nacos 服务实例列表后再执行一次。

## 10. 可测试的接口地址

```text
GET http://127.0.0.1:8081/courses/1
GET http://127.0.0.1:8083/courses/1
GET http://127.0.0.1:8082/enrollments/1
GET http://127.0.0.1:8082/enrollments/2
GET http://127.0.0.1:8082/enrollments/3
```

## 11. 停止服务

在三个运行服务的 PowerShell 窗口中分别按：

```text
Ctrl + C
```

停止 Nacos 容器：

```powershell
docker stop nacos-standalone
```

## 关键代码和脚本位置

- 课程服务注册配置：`course-service/src/main/resources/application.yml`
- 课程服务数据库脚本：`course-service/src/main/resources/course.sql`
- 课程查询接口：`course-service/src/main/java/com/example/nacos/course/controller/CourseController.java`
- 课程端口返回逻辑：`course-service/src/main/java/com/example/nacos/course/service/CourseCatalogService.java`
- 消费者注册配置：`enrollment-service/src/main/resources/application.yml`
- 选课服务数据库脚本：`enrollment-service/src/main/resources/enrollment.sql`
- `@LoadBalanced RestTemplate`：`enrollment-service/src/main/java/com/example/nacos/enrollment/config/RestTemplateConfig.java`
- 通过服务名调用课程服务：`enrollment-service/src/main/java/com/example/nacos/enrollment/service/EnrollmentQueryService.java`
