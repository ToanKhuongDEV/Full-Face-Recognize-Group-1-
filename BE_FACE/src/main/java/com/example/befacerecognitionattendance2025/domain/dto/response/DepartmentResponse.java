package com.example.befacerecognitionattendance2025.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class DepartmentResponse {

    private String id;
    private String name;
    private Double baseSalary;
    private String description;

    private List<String> employeeNames;

}
