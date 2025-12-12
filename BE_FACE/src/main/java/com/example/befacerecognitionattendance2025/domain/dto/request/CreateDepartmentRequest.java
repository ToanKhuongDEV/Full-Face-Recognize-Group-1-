package com.example.befacerecognitionattendance2025.domain.dto.request;
import com.example.befacerecognitionattendance2025.constant.ErrorMessage;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateDepartmentRequest {

    @NotBlank(message = ErrorMessage.Validation.ERR_NOT_BLANK)
    private String name;

    @NotNull(message = ErrorMessage.Validation.ERR_NOT_BLANK)
    @Positive(message =ErrorMessage.Department.ERR_INVALID_SALARY)
    private Double baseSalary;

    private String description;
}
