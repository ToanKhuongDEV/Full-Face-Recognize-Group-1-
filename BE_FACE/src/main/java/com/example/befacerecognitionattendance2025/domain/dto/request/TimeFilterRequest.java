package com.example.befacerecognitionattendance2025.domain.dto.request;

import com.example.befacerecognitionattendance2025.constant.ErrorMessage;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TimeFilterRequest {
    private Integer day;
    private Integer month;

    @NotNull(message = ErrorMessage.Validation.ERR_NOT_BLANK)
    private Integer year;
}
