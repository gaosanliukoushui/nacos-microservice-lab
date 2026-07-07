package com.example.nacos.enrollment.service;

import com.example.nacos.enrollment.model.CourseInfo;
import com.example.nacos.enrollment.model.EnrollmentRecord;
import com.example.nacos.enrollment.model.EnrollmentResponse;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

@Service
public class EnrollmentQueryService {

    private static final String COURSE_SERVICE_URL = "http://course-service/courses/{id}";

    private final RestTemplate restTemplate;
    private final Map<Long, EnrollmentRecord> enrollments = Map.of(
            1L, new EnrollmentRecord(1L, "张三", 1L),
            2L, new EnrollmentRecord(2L, "李四", 2L),
            3L, new EnrollmentRecord(3L, "王五", 3L)
    );

    public EnrollmentQueryService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public EnrollmentResponse findEnrollmentById(Long id) {
        EnrollmentRecord record = enrollments.get(id);
        if (record == null) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Enrollment not found: " + id);
        }

        CourseInfo course = restTemplate.getForObject(COURSE_SERVICE_URL, CourseInfo.class, record.courseId());
        if (course == null) {
            throw new ResponseStatusException(HttpStatus.SERVICE_UNAVAILABLE, "Course service returned no data");
        }

        return new EnrollmentResponse(record.id(), record.studentName(), course);
    }
}
