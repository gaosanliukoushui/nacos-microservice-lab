package com.example.nacos.enrollment.service;

import com.example.nacos.enrollment.model.EnrollmentRecord;
import com.example.nacos.enrollment.model.EnrollmentResponse;
import com.example.nacos.feign.client.CourseClient;
import com.example.nacos.feign.model.CourseInfo;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EnrollmentQueryService {

    private final CourseClient courseClient;
    private final Map<Long, EnrollmentRecord> enrollments = Map.of(
            1L, new EnrollmentRecord(1L, "张三", 1L),
            2L, new EnrollmentRecord(2L, "李四", 2L),
            3L, new EnrollmentRecord(3L, "王五", 3L)
    );

    public EnrollmentQueryService(CourseClient courseClient) {
        this.courseClient = courseClient;
    }

    public EnrollmentResponse findEnrollmentById(Long id) {
        EnrollmentRecord record = enrollments.get(id);
        if (record == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found: " + id);
        }

        CourseInfo course = courseClient.findById(record.courseId());
        if (course == null) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Course service returned no data");
        }

        return new EnrollmentResponse(record.id(), record.studentName(), course);
    }
}
