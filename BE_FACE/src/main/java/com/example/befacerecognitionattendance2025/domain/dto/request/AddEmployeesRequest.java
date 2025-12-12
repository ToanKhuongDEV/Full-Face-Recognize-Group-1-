package com.example.befacerecognitionattendance2025.domain.dto.request;

import com.example.befacerecognitionattendance2025.constant.ErrorMessage;
import jakarta.validation.constraints.NotEmpty;
import lombok.*;

import java.util.List;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AddEmployeesRequest {

    @NotEmpty(message = ErrorMessage.Validation.ERR_NOT_BLANK)
    private List<String> employeeIds;
}
