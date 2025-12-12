package com.example.befacerecognitionattendance2025.domain.dto.request;

import com.example.befacerecognitionattendance2025.constant.ErrorMessage;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class LoginRequest {

    @NotBlank(message = ErrorMessage.Validation.ERR_NOT_BLANK)
    private String username;

    @NotBlank(message = ErrorMessage.Validation.ERR_NOT_BLANK)
    private String password;

}
