package com.example.nacos.enrollment.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.nacos.enrollment.model.EnrollmentResponse;
import com.example.nacos.feign.client.CourseClient;
import com.example.nacos.feign.model.CourseInfo;
import org.junit.jupiter.api.Test;

class EnrollmentQueryServiceTest {

    @Test
    void callsCourseServiceByFeignClientAndReturnsProviderPort() {
        CourseClient courseClient = id -> new CourseInfo(
                id,
                "Java 微服务基础",
                "学习 Spring Boot、服务注册与服务调用的基础课程",
                "8083"
        );
        EnrollmentQueryService service = new EnrollmentQueryService(courseClient);

        EnrollmentResponse response = service.findEnrollmentById(1L);

        assertThat(response.studentName()).isEqualTo("张三");
        assertThat(response.course().id()).isEqualTo(1L);
        assertThat(response.course().servedByPort()).isEqualTo("8083");
    }
}
