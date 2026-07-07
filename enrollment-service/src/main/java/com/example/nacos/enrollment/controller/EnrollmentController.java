package com.example.nacos.enrollment.controller;

import com.example.nacos.enrollment.model.EnrollmentResponse;
import com.example.nacos.enrollment.service.EnrollmentQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/enrollments")
public class EnrollmentController {

    private final EnrollmentQueryService enrollmentQueryService;

    public EnrollmentController(EnrollmentQueryService enrollmentQueryService) {
        this.enrollmentQueryService = enrollmentQueryService;
    }

    @GetMapping("/{id}")
    public EnrollmentResponse getEnrollment(@PathVariable Long id) {
        return enrollmentQueryService.findEnrollmentById(id);
    }
}
