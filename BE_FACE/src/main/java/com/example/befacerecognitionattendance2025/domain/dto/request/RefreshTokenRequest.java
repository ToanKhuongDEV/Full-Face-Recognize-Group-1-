package com.example.befacerecognitionattendance2025.domain.dto.request;

import com.example.befacerecognitionattendance2025.constant.ErrorMessage;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
public class RefreshTokenRequest {

    @NotBlank(message = ErrorMessage.Validation.ERR_NOT_BLANK)
    String refreshToken;
}
