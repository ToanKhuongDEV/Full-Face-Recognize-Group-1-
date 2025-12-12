package com.example.befacerecognitionattendance2025.domain.dto.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class FaceSearchResponse {
    private String status;
    private String name;

    @JsonProperty("id")
    private String employeeId;

    private Double distance;
}