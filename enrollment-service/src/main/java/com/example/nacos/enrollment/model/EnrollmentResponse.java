package com.example.nacos.enrollment.model;

import com.example.nacos.feign.model.CourseInfo;

public record EnrollmentResponse(Long id, String studentName, CourseInfo course) {
}
