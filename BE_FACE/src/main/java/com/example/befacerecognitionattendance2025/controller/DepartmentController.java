package com.example.befacerecognitionattendance2025.controller;

import com.example.befacerecognitionattendance2025.base.RestApiV1;
import com.example.befacerecognitionattendance2025.base.RestData;
import com.example.befacerecognitionattendance2025.base.VsResponseUtil;
import com.example.befacerecognitionattendance2025.constant.UrlConstant;
import com.example.befacerecognitionattendance2025.domain.dto.request.AddEmployeesRequest;
import com.example.befacerecognitionattendance2025.domain.dto.request.CreateDepartmentRequest;
import com.example.befacerecognitionattendance2025.domain.dto.request.UpdateDepartmentRequest;
import com.example.befacerecognitionattendance2025.domain.dto.response.DepartmentResponse;
import com.example.befacerecognitionattendance2025.service.DepartmentService;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestApiV1
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    @Operation(summary = "tạo phòng ban", description = "yêu cầu quyền manager")
    @PreAuthorize("hasAuthority('MANAGER')")
    @PostMapping(UrlConstant.Department.COMMON)
    public ResponseEntity<RestData<?>> createDepartment(
            @Valid @RequestBody CreateDepartmentRequest request) {
        DepartmentResponse response = departmentService.createDepartment(request);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "cập nhật phònd ban", description = "yêu cầu quyền manager")
    @PreAuthorize("hasAuthority('MANAGER')")
    @PutMapping(UrlConstant.Department.ID)
    public ResponseEntity<RestData<?>> updateDepartment(
            @PathVariable String id,
            @RequestBody UpdateDepartmentRequest request) {
        DepartmentResponse response = departmentService.updateDepartment(id, request);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "xóa phònd ban theo id", description = "yêu cầu quyền manager")
    @PreAuthorize("hasAuthority('MANAGER')")
    @DeleteMapping(UrlConstant.Department.ID)
    public ResponseEntity<RestData<?>> deleteDepartment(@PathVariable String id) {
        DepartmentResponse response = departmentService.deleteDepartment(id);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "lấy phònd ban theo id", description = "yêu cầu quyền manager")
    @PreAuthorize("hasAuthority('MANAGER')")
    @GetMapping(UrlConstant.Department.ID)
    public ResponseEntity<RestData<?>> getDepartmentById(@PathVariable String id) {
        DepartmentResponse response = departmentService.findDepartmentById(id);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "thêm nhân viên vào phòng ban", description = "yêu cầu quyền manager")
    @PreAuthorize("hasAuthority('MANAGER')")
    @PostMapping(UrlConstant.Department.ADD_EMPLOYEE)
    public ResponseEntity<RestData<?>> addEmployeeToDepartment(@PathVariable String id, @RequestBody @Valid AddEmployeesRequest request) {
        DepartmentResponse response = departmentService.addEmployeesToDepartment(id, request);
        return VsResponseUtil.success(response);
    }

    @Operation(summary = "lấy tất cả các phònd ban")
    @PreAuthorize("hasAuthority('MANAGER')")
    @GetMapping(UrlConstant.Department.COMMON)
    public ResponseEntity<RestData<?>> getAllDepartments() {
        return VsResponseUtil.success(departmentService.findAllDepartments());
    }
}
