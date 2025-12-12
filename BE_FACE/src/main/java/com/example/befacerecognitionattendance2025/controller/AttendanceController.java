package com.example.befacerecognitionattendance2025.controller;

import com.example.befacerecognitionattendance2025.base.RestApiV1;
import com.example.befacerecognitionattendance2025.base.RestData;
import com.example.befacerecognitionattendance2025.base.VsResponseUtil;
import com.example.befacerecognitionattendance2025.constant.UrlConstant;
import com.example.befacerecognitionattendance2025.domain.dto.request.TimeFilterRequest;
import com.example.befacerecognitionattendance2025.domain.dto.response.AttendanceSummaryDTO;
import com.example.befacerecognitionattendance2025.service.AttendanceService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestApiV1
@RequiredArgsConstructor
public class AttendanceController {

    private final AttendanceService attendanceService;

    @Operation(summary = "Lấy công điểm của nhân viên theo thời gian", description = "yêu cầu quyền manager")
    @PreAuthorize("hasAuthority('MANAGER')")
    @GetMapping(UrlConstant.Attendance.TOTAL_WORK_HOUR)
    public ResponseEntity<RestData<?>> getAttendanceSummary(
            @PathVariable String employeeId,
            @Valid @ModelAttribute TimeFilterRequest filterRequest
    ) {
        List<AttendanceSummaryDTO> result = attendanceService.getWorkingHoursByFilter(employeeId, filterRequest);
        return VsResponseUtil.success(result);
    }

    @Operation(summary = "chấm công")
    @PostMapping(
            value = UrlConstant.Attendance.CHECK,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<RestData<?>> recordAttendanceByFace(
            @Parameter(description = "File ảnh khuôn mặt để nhận diện")
            @RequestPart(value = "image") MultipartFile imageFile
    ) {
        AttendanceSummaryDTO result = attendanceService.recordFaceAttendance(imageFile);
        return VsResponseUtil.success(result);
    }

    @Operation(summary = "xem công điểm của bản thâm theo thời gian")
    @GetMapping(UrlConstant.Attendance.TOTAL_WORK_ME)
    public ResponseEntity<RestData<?>> getAttendanceSummary(
            @Valid @ModelAttribute TimeFilterRequest filterRequest
    ){
        List<AttendanceSummaryDTO> result = attendanceService.getMyWorkingHoursByFilter(filterRequest);
        return VsResponseUtil.success(result);
    }
}