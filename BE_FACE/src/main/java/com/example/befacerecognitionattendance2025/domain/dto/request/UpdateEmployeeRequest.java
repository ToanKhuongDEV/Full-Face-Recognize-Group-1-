package com.example.befacerecognitionattendance2025.domain.dto.request;

import com.example.befacerecognitionattendance2025.constant.ErrorMessage;
import com.example.befacerecognitionattendance2025.constant.Gender;
import com.example.befacerecognitionattendance2025.constant.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class UpdateEmployeeRequest {

    private String fullName;

    private Gender gender;

    private Role role;

    private LocalDate dateBirth;
    private String phoneNumber;

    private String departmentId;

    @Email(message = ErrorMessage.Validation.ERR_INVALID_EMAIL)
    private String email;
}
