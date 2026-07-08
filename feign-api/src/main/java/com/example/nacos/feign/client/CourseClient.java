package com.example.nacos.feign.client;

import com.example.nacos.feign.model.CourseInfo;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient("course-service")
public interface CourseClient {

    @GetMapping("/courses/{id}")
    CourseInfo findById(@PathVariable("id") Long id);
}
