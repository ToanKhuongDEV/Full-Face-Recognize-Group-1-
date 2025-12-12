package com.example.befacerecognitionattendance2025.domain.dto.response;

import com.example.befacerecognitionattendance2025.constant.Gender;
import com.example.befacerecognitionattendance2025.constant.Role;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class EmployeeResponse {

    private String id;

    private Integer employeeCode;

    private String username;

    private String fullName;

    private String departmentName;

    private Gender gender;

    private Role role;

    private LocalDate dateBirth;
    private String phoneNumber;
    private String email;
    private String avatar;

    private LocalDateTime createdAt;

}
