package com.example.nacos.course.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.nacos.course.model.CourseResponse;
import org.junit.jupiter.api.Test;

class CourseCatalogServiceTest {

    @Test
    void returnsCurrentServerPortInResponse() {
        CourseCatalogService service = new CourseCatalogService("8081");

        CourseResponse response = service.findCourseById(1L);

        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.name()).isEqualTo("Java 微服务基础");
        assertThat(response.servedByPort()).isEqualTo("8081");
    }
}
