package com.example.befacerecognitionattendance2025.domain.dto.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PayrollEntryRequest {
    private Double bonus;
    private Double deduction;
}
