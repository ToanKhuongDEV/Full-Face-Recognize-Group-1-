package com.example.befacerecognitionattendance2025.domain.dto.response;

import com.example.befacerecognitionattendance2025.domain.entity.Employee;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class PayrollResponse {
    private String id;
    private String employeeCode;
    private String employeeName;
    private String departmentName;

    private Integer month;
    private Integer year;

    private Double totalHours;
    private Double baseSalary;
    private Double bonus;
    private Double deduction;
    private Double finalSalary;


    private LocalDateTime createdAt;
}
