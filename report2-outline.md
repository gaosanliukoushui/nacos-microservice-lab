# 实验操作手册2：OpenFeign 与网关报告提纲

## 一、实验目的

说明本实验在已有 Nacos 微服务项目基础上，使用 OpenFeign 替代 RestTemplate 完成服务调用，并新增 Spring Cloud Gateway 作为统一入口，同时通过 Tomcat 页面验证跨域问题和跨域解决方案。

截图占位：无。

## 二、实验环境

说明 Java 17、Maven、Spring Boot 3.2.4、Spring Cloud 2023.0.1、Spring Cloud Alibaba 2023.0.1.0、Nacos 127.0.0.1:8848、Tomcat 8086。

截图占位：

- `java -version`
- `.\mvnw.cmd -version`
- `docker ps` 中 Nacos 容器运行状态
- Tomcat 8086 首页或 pages 页面访问地址

## 三、项目模块结构

说明项目新增了 `feign-api` 和 `api-gateway` 两个模块，原有 `course-service`、`enrollment-service` 保留。

截图占位：

- IDE 项目结构截图
- 父工程 `pom.xml` 中四个模块配置截图

## 四、OpenFeign 改造

说明 `feign-api` 中定义 `CourseClient`，使用 `@FeignClient("course-service")`，接口路径为 `GET /courses/{id}`，方法名为 `findById(Long id)`。

说明 `enrollment-service` 引入 `feign-api`，启动类通过 `@EnableFeignClients(clients = CourseClient.class)` 扫描 Feign 客户端，`EnrollmentQueryService` 注入 `CourseClient` 完成远程调用。

截图占位：

- `feign-api/pom.xml` 依赖截图
- `CourseClient.java` 代码截图
- `EnrollmentServiceApplication.java` 中 `@EnableFeignClients` 截图
- `EnrollmentQueryService.java` 中 `courseClient.findById(record.courseId())` 截图

## 五、Gateway 网关配置

说明新增 `api-gateway` 模块，端口为 `7000`，服务名为 `api-gateway`，注册到 Nacos。

说明网关通过以下服务名路由转发：

```text
/courses/**      -> lb://course-service
/enrollments/**  -> lb://enrollment-service
```

截图占位：

- `api-gateway/pom.xml` 依赖截图
- `GatewayApplication.java` 启动类截图
- `api-gateway/src/main/resources/application.yml` 路由配置截图

## 六、Nacos 服务注册验证

说明启动 `course-service` 8081、`course-service` 8083、`enrollment-service` 8082、`api-gateway` 7000 后，在 Nacos 的“服务管理 / 服务列表”中查看服务。

截图占位：

- Nacos 服务列表截图，包含 `course-service`、`enrollment-service`、`api-gateway`
- `course-service` 实例详情截图，显示 8081 和 8083 两个实例

## 七、网关调用与负载均衡验证

说明通过网关访问 `http://127.0.0.1:7000/enrollments/1`，网关先路由到 `enrollment-service`，选课服务再通过 OpenFeign 调用 `course-service`。

说明连续访问网关接口时，返回 JSON 中的 `course.servedByPort` 会出现 `8081` 和 `8083`，证明远程调用和负载均衡有效。

截图占位：

- `Invoke-RestMethod http://127.0.0.1:7000/enrollments/1` 返回结果截图
- 连续请求输出 `8081`、`8083` 的 PowerShell 截图

## 八、跨域测试页面

说明 `pages/index.html` 会从 `http://localhost:8086/pages/index.html` 发起请求，目标地址为 `http://localhost:7000/enrollments/{id}`，两者端口不同，因此属于跨域场景。

截图占位：

- `pages/index.html` 文件位置截图
- Tomcat `webapps/pages/index.html` 路径截图
- 浏览器访问 `http://localhost:8086/pages/index.html` 页面截图

## 九、跨域失败验证

说明为了截图跨域失败，可以临时用 `--lab.cors.enabled=false` 启动网关，关闭 `GlobalCorsConfig`。

截图占位：

- 网关关闭 CORS 的启动命令截图
- 浏览器 Console 中 CORS 报错截图
- 页面显示请求失败截图

## 十、跨域成功验证

说明正常启动网关后，`GlobalCorsConfig` 中的 `CorsWebFilter` 放行所有 origin、header、method，并使用 `addAllowedOriginPattern("*")`，Tomcat 页面可以成功访问网关接口。

截图占位：

- `GlobalCorsConfig.java` 代码截图
- Tomcat 页面成功显示 JSON 截图
- Network 中 `enrollments/1` 状态码 200 截图
- Response Headers 中 `access-control-allow-origin` 截图

## 十一、运行结果

整理本实验最终运行结果：

- OpenFeign 替代 RestTemplate 成功
- Gateway 通过服务名转发成功
- Nacos 服务注册成功
- 课程服务双实例负载均衡成功
- Tomcat 页面跨域访问网关成功

截图占位：可合并放关键运行结果截图。

## 十二、实验总结

总结 OpenFeign 将远程 HTTP 调用抽象成接口调用，Gateway 提供统一入口并基于 Nacos 服务名转发，`CorsWebFilter` 可以集中解决前端跨域访问问题。本实验在保持原有业务含义不变的前提下完成了服务调用方式和网关入口的升级。

截图占位：无。
