package com.example.befacerecognitionattendance2025.domain.dto.response;

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
public class AttendanceSummaryDTO {
    private String employeeId;
    private String employeeName;
    private LocalDate workDate;
    private Double totalHours;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
}
