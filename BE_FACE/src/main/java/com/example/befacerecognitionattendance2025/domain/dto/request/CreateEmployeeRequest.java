package com.example.befacerecognitionattendance2025.domain.dto.request;

import com.example.befacerecognitionattendance2025.constant.ErrorMessage;
import com.example.befacerecognitionattendance2025.constant.Gender;
import com.example.befacerecognitionattendance2025.constant.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class CreateEmployeeRequest {

    @NotBlank(message = ErrorMessage.Validation.ERR_NOT_BLANK)
    private String username;

    @NotBlank(message = ErrorMessage.Validation.ERR_NOT_BLANK)
    private String password;

    private String fullName;

    private Gender gender;

    private LocalDate dateBirth;

    @Size(min = 10 , max = 10 , message = ErrorMessage.Validation.ERR_INVALID_PHONE_NUMBER)
    private String phoneNumber;

    @NotBlank(message = ErrorMessage.Validation.ERR_NOT_BLANK)
    @Email(message = ErrorMessage.Validation.ERR_INVALID_EMAIL)
    private String email;

    @NotBlank(message = ErrorMessage.Validation.ERR_NOT_BLANK)
    private String departmentId;

}
