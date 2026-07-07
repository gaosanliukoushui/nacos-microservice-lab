package com.example.nacos.course.service;

import com.example.nacos.course.model.CourseInfo;
import com.example.nacos.course.model.CourseResponse;
import java.util.Map;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class CourseCatalogService {

    private final String serverPort;
    private final Map<Long, CourseInfo> courses = Map.of(
            1L, new CourseInfo(1L, "Java 微服务基础", "学习 Spring Boot、服务注册与服务调用的基础课程"),
            2L, new CourseInfo(2L, "Nacos 注册中心实验", "使用 Nacos 完成服务注册、发现与客户端负载均衡"),
            3L, new CourseInfo(3L, "Spring Cloud LoadBalancer", "通过服务名调用多个服务实例并观察轮询效果")
    );

    public CourseCatalogService(@Value("${server.port}") String serverPort) {
        this.serverPort = serverPort;
    }

    public CourseResponse findCourseById(Long id) {
        CourseInfo course = courses.getOrDefault(
                id,
                new CourseInfo(id, "临时示例课程", "该课程来自内存中的默认示例数据")
        );
        System.out.printf("[course-service:%s] handled GET /courses/%d%n", serverPort, id);
        return new CourseResponse(course.id(), course.name(), course.description(), serverPort);
    }
}
