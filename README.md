# 基于 Nacos 注册中心的微服务案例

本项目是 Maven 多模块微服务实验项目，当前包含“实验 1：Nacos 服务注册与发现”和“实验 2：OpenFeign 与 Gateway 网关”。

## 模块说明

```text
nacos-microservice-lab
|- feign-api              OpenFeign 客户端公共模块
|- course-service         课程服务提供者，服务名 course-service，端口 8081/8083
|- enrollment-service     选课服务消费者，服务名 enrollment-service，端口 8082
|- api-gateway            Spring Cloud Gateway 网关，服务名 api-gateway，端口 7000
|- pages                  Tomcat 前端跨域测试页面
|- README.md
|- report-outline.md
`- report2-outline.md
```

## 技术版本

- Java：17
- Spring Boot：`3.2.4`
- Spring Cloud：`2023.0.1`
- Spring Cloud Alibaba：`2023.0.1.0`
- Nacos：`127.0.0.1:8848`
- Tomcat：`localhost:8086`，用于访问 `pages/index.html`

## 数据库脚本

老师要求的微服务数据库脚本放在各模块 `resources` 目录下：

```text
course-service/src/main/resources/course.sql
enrollment-service/src/main/resources/enrollment.sql
```

这两个 SQL 文件分别创建 `course_db` 和 `enrollment_db`，并写入课程、学生、选课测试数据。当前服务运行仍然使用内存固定数据，不依赖 MySQL。

如需导入 MySQL，可以进入 MySQL 客户端后执行：

```sql
SOURCE E:/my_projects/nacos-microservice-lab/course-service/src/main/resources/course.sql;
SOURCE E:/my_projects/nacos-microservice-lab/enrollment-service/src/main/resources/enrollment.sql;
```

## 1. 启动 Nacos

如果容器已经存在：

```powershell
docker start nacos-standalone
docker ps
```

如果本机没有容器，可以创建一个：

```powershell
docker run -d --name nacos-standalone `
  -e MODE=standalone `
  -e NACOS_AUTH_ENABLE=false `
  -p 8848:8848 `
  -p 9848:9848 `
  -p 9849:9849 `
  nacos/nacos-server:v2.3.2
```

Nacos 控制台：

```text
http://127.0.0.1:8848/nacos/
```

注意：查看微服务注册结果时，要进入 **服务管理 / 服务列表**，不是“配置管理 / 配置列表”。

## 2. 编译项目

在项目根目录执行：

```powershell
.\mvnw.cmd clean package
```

预期结果：

```text
BUILD SUCCESS
```

## 3. 启动 course-service 8081

打开第一个 PowerShell：

```powershell
cd E:\my_projects\nacos-microservice-lab
$env:SERVER_PORT = "8081"
java -jar .\course-service\target\course-service-1.0.0-SNAPSHOT.jar
```

## 4. 启动 course-service 8083

打开第二个 PowerShell：

```powershell
cd E:\my_projects\nacos-microservice-lab
$env:SERVER_PORT = "8083"
java -jar .\course-service\target\course-service-1.0.0-SNAPSHOT.jar
```

## 5. 启动 enrollment-service 8082

打开第三个 PowerShell：

```powershell
cd E:\my_projects\nacos-microservice-lab
$env:SERVER_PORT = "8082"
java -jar .\enrollment-service\target\enrollment-service-1.0.0-SNAPSHOT.jar
```

`enrollment-service` 已经改为通过 `feign-api` 中的 `CourseClient` 调用：

```text
@FeignClient("course-service")
GET /courses/{id}
```

返回 JSON 中的 `course.servedByPort` 仍然可以证明远程调用命中了哪个课程服务实例。

## 6. 启动 api-gateway 7000

打开第四个 PowerShell：

```powershell
cd E:\my_projects\nacos-microservice-lab
java -jar .\api-gateway\target\api-gateway-1.0.0-SNAPSHOT.jar
```

网关路由：

```text
/courses/**      -> lb://course-service
/enrollments/**  -> lb://enrollment-service
```

网关通过服务名从 Nacos 发现实例，不写死 8081、8082 或 8083。

## 7. 复制 pages 到 Tomcat

把项目中的 `pages` 目录复制到 Tomcat 的 `webapps/pages`。

PowerShell 示例：

```powershell
$tomcatWebapps = "D:\apache-tomcat-10.1.xx\webapps"
New-Item -ItemType Directory -Force "$tomcatWebapps\pages" | Out-Null
Copy-Item -Recurse -Force .\pages\* "$tomcatWebapps\pages\"
```

把 `$tomcatWebapps` 改成你本机 Tomcat 的真实 `webapps` 路径。

访问页面：

```text
http://localhost:8086/pages/index.html
```

页面会请求：

```text
http://localhost:7000/enrollments/{id}
```

因此页面来源是 `localhost:8086`，接口目标是 `localhost:7000`，属于跨域请求。

## 8. 测试接口地址

直接访问课程服务：

```powershell
Invoke-RestMethod http://127.0.0.1:8081/courses/1
Invoke-RestMethod http://127.0.0.1:8083/courses/1
```

直接访问选课服务：

```powershell
Invoke-RestMethod http://127.0.0.1:8082/enrollments/1
```

通过网关访问：

```powershell
Invoke-RestMethod http://127.0.0.1:7000/courses/1
Invoke-RestMethod http://127.0.0.1:7000/enrollments/1
```

通过网关连续观察负载均衡：

```powershell
1..8 | ForEach-Object {
  (Invoke-RestMethod http://127.0.0.1:7000/enrollments/1).course.servedByPort
}
```

预期可以看到 `8081` 和 `8083` 轮流出现。

## 9. 截图验证跨域失败

最终代码已经添加了 `GlobalCorsConfig`，默认会解决跨域。为了给实验报告截图“跨域失败”，可以临时用下面方式启动网关，关闭跨域配置：

```powershell
java -jar .\api-gateway\target\api-gateway-1.0.0-SNAPSHOT.jar --lab.cors.enabled=false
```

然后访问：

```text
http://localhost:8086/pages/index.html
```

按 `F12` 打开浏览器开发者工具，点击页面中的“查询”。预期现象：

```text
Access to fetch at 'http://localhost:7000/enrollments/1' from origin 'http://localhost:8086' has been blocked by CORS policy
```

截图位置：

- 浏览器页面请求失败结果
- 开发者工具 Console 中的 CORS 报错
- Network 中 `enrollments/1` 请求被浏览器拦截

## 10. 截图验证跨域成功

停止上一步的网关，重新正常启动：

```powershell
java -jar .\api-gateway\target\api-gateway-1.0.0-SNAPSHOT.jar
```

再次访问：

```text
http://localhost:8086/pages/index.html
```

点击“查询”，预期页面显示 JSON：

```json
{
  "httpStatus": 200,
  "ok": true,
  "data": {
    "id": 1,
    "studentName": "张三",
    "course": {
      "id": 1,
      "name": "Java 微服务基础",
      "description": "学习 Spring Boot、服务注册与服务调用的基础课程",
      "servedByPort": "8081"
    }
  }
}
```

`servedByPort` 可能是 `8081`，也可能是 `8083`。

截图位置：

- Tomcat 页面成功显示 JSON
- Network 中 `http://localhost:7000/enrollments/1` 状态码为 200
- Response Headers 中出现 `access-control-allow-origin`
- Nacos 服务列表中出现 `course-service`、`enrollment-service`、`api-gateway`

## 11. 关键文件位置

OpenFeign：

```text
feign-api/src/main/java/com/example/nacos/feign/client/CourseClient.java
feign-api/src/main/java/com/example/nacos/feign/model/CourseInfo.java
enrollment-service/src/main/java/com/example/nacos/enrollment/EnrollmentServiceApplication.java
enrollment-service/src/main/java/com/example/nacos/enrollment/service/EnrollmentQueryService.java
```

Gateway：

```text
api-gateway/src/main/java/com/example/nacos/gateway/GatewayApplication.java
api-gateway/src/main/java/com/example/nacos/gateway/config/GlobalCorsConfig.java
api-gateway/src/main/resources/application.yml
```

前端页面：

```text
pages/index.html
```
