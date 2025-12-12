package com.example.befacerecognitionattendance2025.domain.dto.request;

import com.example.befacerecognitionattendance2025.constant.ErrorMessage;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class ChangePasswordRequest {

    @NotBlank(message = ErrorMessage.Validation.ERR_NOT_BLANK)
    private String oldPassword;

    @NotBlank(message = ErrorMessage.Validation.ERR_NOT_BLANK)
    private String newPassword;
}
