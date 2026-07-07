package com.example.nacos.enrollment.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

import com.example.nacos.enrollment.model.EnrollmentResponse;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;

class EnrollmentQueryServiceTest {

    @Test
    void callsCourseServiceByServiceNameAndReturnsProviderPort() {
        RestTemplate restTemplate = new RestTemplate();
        MockRestServiceServer mockServer = MockRestServiceServer.bindTo(restTemplate).build();
        mockServer.expect(requestTo("http://course-service/courses/1"))
                .andRespond(withSuccess("""
                        {
                          "id": 1,
                          "name": "Java 微服务基础",
                          "description": "学习 Spring Boot、服务注册与服务调用的基础课程",
                          "servedByPort": "8083"
                        }
                        """, MediaType.APPLICATION_JSON));

        EnrollmentQueryService service = new EnrollmentQueryService(restTemplate);

        EnrollmentResponse response = service.findEnrollmentById(1L);

        assertThat(response.studentName()).isEqualTo("张三");
        assertThat(response.course().id()).isEqualTo(1L);
        assertThat(response.course().servedByPort()).isEqualTo("8083");
        mockServer.verify();
    }
}
