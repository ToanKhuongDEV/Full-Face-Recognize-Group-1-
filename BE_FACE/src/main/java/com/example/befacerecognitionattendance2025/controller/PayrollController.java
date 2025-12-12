package com.example.befacerecognitionattendance2025.controller;

import com.example.befacerecognitionattendance2025.base.RestApiV1;
import com.example.befacerecognitionattendance2025.base.RestData;
import com.example.befacerecognitionattendance2025.base.VsResponseUtil;
import com.example.befacerecognitionattendance2025.constant.UrlConstant;
import com.example.befacerecognitionattendance2025.domain.dto.request.PayrollEntryRequest;
import com.example.befacerecognitionattendance2025.domain.dto.request.TimeFilterRequest;
import com.example.befacerecognitionattendance2025.service.PayrollService;
import com.example.befacerecognitionattendance2025.service.impl.PayrollExportService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;

@RestApiV1
@RequiredArgsConstructor
public class PayrollController {
    private final PayrollService payrollService;
    private final PayrollExportService payrollExportService;

    @Operation(summary = "lấy bảng lương theo tháng hoặc năm của 1 phòng ban theo  ", description = "yêu cầu quyền manager")
    @PreAuthorize("hasAuthority('MANAGER')")
    @GetMapping(UrlConstant.Payroll.GET_BY_DEPARTMENT)
    public ResponseEntity<RestData<?>> getMonthlyPayroll (
            @PathVariable String departmentId,
            @Valid @ModelAttribute TimeFilterRequest time
    ) {
        return VsResponseUtil.success(payrollService.getPayrollByDepartmentId(departmentId,time));
    }

    @Operation(summary = "tạo bảng lương cho tất cả các nhân viên", description = "yêu cầu quyền manager")
    @PreAuthorize("hasAuthority('MANAGER')")
    @PostMapping(UrlConstant.Payroll.COMMON)
    public ResponseEntity<RestData<?>> createMonthlyPayroll (
            @Valid @ModelAttribute TimeFilterRequest time
    ) {
        return VsResponseUtil.success(HttpStatus.CREATED,payrollService.createPayroll(time));
    }

    @Operation(summary = "lấy bảng lương của bản thân theo tháng")
    @GetMapping(UrlConstant.Payroll.ME)
    public ResponseEntity<RestData<?>> getMyPayroll (
            @Valid @ModelAttribute TimeFilterRequest time
    ) {
        return VsResponseUtil.success(payrollService.getMyPayroll(time));
    }

    @Operation(summary = "cập nhật thưởng hoặc phạt cho nhân viên", description = "yêu cầu quyền manager")
    @PreAuthorize("hasAuthority('MANAGER')")
    @PutMapping(UrlConstant.Payroll.UPDATE_BONUS_DEDUCTION)
    public ResponseEntity<RestData<?>> updateBonusDeduction(
            @PathVariable String employeeId,
            @RequestBody @Valid PayrollEntryRequest request,
            @Valid @ModelAttribute TimeFilterRequest time
    ){
        return VsResponseUtil.success(payrollService.updateBonusDeduction(employeeId,request,time));
    }
    @Operation(summary = "Xuất file excel", description = "yêu cầu quyền manager")
    @GetMapping(UrlConstant.Payroll.EXPORT)
    @PreAuthorize("hasAuthority('MANAGER')")
    public ResponseEntity<InputStreamResource> exportNative(@RequestParam int month, @RequestParam int year) {

        // Controller ủy quyền toàn bộ cho Service
        ByteArrayInputStream in = payrollExportService.exportPayroll(month, year);

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=Bang_luong_T" + month + ".xlsx")
                .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
                .body(new InputStreamResource(in));
    }
}
