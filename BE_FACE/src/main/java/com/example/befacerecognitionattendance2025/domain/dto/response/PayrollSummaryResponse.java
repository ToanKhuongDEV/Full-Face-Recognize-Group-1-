package com.example.befacerecognitionattendance2025.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonFormat;

public record PayrollSummaryResponse(
        Integer employeeCode,
        String employeeName,
        String departmentName,
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "#,###")
        Double finalSalary
) {}