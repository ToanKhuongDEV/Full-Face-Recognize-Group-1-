package com.example.befacerecognitionattendance2025.controller;

import com.example.befacerecognitionattendance2025.base.RestApiV1;
import com.example.befacerecognitionattendance2025.base.RestData;
import com.example.befacerecognitionattendance2025.base.VsResponseUtil;
import com.example.befacerecognitionattendance2025.constant.UrlConstant;
import com.example.befacerecognitionattendance2025.domain.dto.request.ChangePasswordRequest;
import com.example.befacerecognitionattendance2025.domain.dto.request.CreateEmployeeRequest;
import com.example.befacerecognitionattendance2025.domain.dto.request.UpdateEmployeeRequest;
import com.example.befacerecognitionattendance2025.service.EmployeeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestApiV1
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService service;

    @Operation(summary = "tạo nhân viên", description = "yêu cầu quyền manager")
    @PreAuthorize("hasAuthority('MANAGER')")
    @PostMapping(
            value = UrlConstant.Employee.COMMON,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<RestData<?>> createEmployee(
            @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            @Valid @RequestPart("data") CreateEmployeeRequest request,
            @RequestPart(value = "image", required = false) MultipartFile imageFile) {
        return VsResponseUtil.success(service.createEmployee(request, imageFile));
    }

    @Operation(summary = "tạo quản lý", description = "yêu cầu quyền manager")
    @PreAuthorize("hasAuthority('MANAGER')")
    @PostMapping(
            value = UrlConstant.Employee.CREATE_MANAGER,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<RestData<?>> createManager(
            @Parameter(content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE))
            @Valid @RequestPart("data") CreateEmployeeRequest request,
            @RequestPart(value = "image", required = false) MultipartFile imageFile
    ) {
        return VsResponseUtil.success(service.createManager(request, imageFile));
    }

    @Operation(summary = "đổi mật khẩu của bản thân")
    @PostMapping(UrlConstant.Employee.CHANGE_PASSWORD)
    public ResponseEntity<RestData<?>> changePassword(@Valid @RequestBody ChangePasswordRequest request ) {
        return VsResponseUtil.success(service.changePassword(request));
    }

    @Operation(summary = "cập nhật nhân viên theo id", description = "yêu cầu quyền manager")
    @PreAuthorize("hasAuthority('MANAGER')")
    @PatchMapping(
            value = UrlConstant.Employee.ID,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<RestData<?>> updateEmployee(
            @PathVariable("id") String id,
            @Valid @RequestPart("data") UpdateEmployeeRequest request,
            @RequestPart(value = "image", required = false) MultipartFile imageFile
    ) {
        return VsResponseUtil.success(service.updateEmployee(id, request, imageFile));
    }

    @Operation(summary = "cập nhật thông tin của bản thân")
    @PatchMapping(
            value = UrlConstant.Employee.ME,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<RestData<?>> updateEmployee(
            @Valid @RequestPart("data") UpdateEmployeeRequest request,
            @RequestPart(value = "image", required = false) MultipartFile imageFile
    ) {
        return VsResponseUtil.success(service.updateMyProfile(request, imageFile));
    }
    @Operation(summary = "lấy thông tin của bản thân")
    @GetMapping(UrlConstant.Employee.ME)
    public ResponseEntity<RestData<?>> getMyProfile(){
        return VsResponseUtil.success(service.getMe());
    }

    @Operation(summary = "xóa nhân viên theo id", description = "yêu cầu quyền manager")
    @PreAuthorize("hasAuthority('MANAGER')")
    @DeleteMapping(UrlConstant.Employee.ID)
    public ResponseEntity<RestData<?>> deleteEmployee(@PathVariable("id") String id) {
        return VsResponseUtil.success(service.deleteEmployee(id));
    }

    @Operation(summary = "lấy tất cả nhân viên", description = "yêu cầu quyền manager")
    @PreAuthorize("hasAuthority('MANAGER')")
    @GetMapping(UrlConstant.Employee.COMMON)
    public ResponseEntity<RestData<?>> getAllEmployees() {
        return VsResponseUtil.success(service.getAllEmployee());
    }

    @Operation(summary = "lấy thông tin nhân viên theo id", description = "yêu cầu quyền manager")
    @PreAuthorize("hasAuthority('MANAGER')")
    @GetMapping(UrlConstant.Employee.ID)
    public ResponseEntity<RestData<?>> getEmployeeById(@PathVariable("id") String id) {
        return VsResponseUtil.success(service.getEmployeeById(id));
    }

    @Operation(summary = "Nạp thêm dữ liệu khuôn mặt (Training bổ sung)",
            description = "Gửi thêm ảnh để AI học các góc mặt khác nhau. Yêu cầu quyền MANAGER.")
    @PreAuthorize("hasAuthority('MANAGER')")
    @PostMapping(
            value = UrlConstant.Employee.TRAIN_FACE,
            consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    )
    public ResponseEntity<RestData<?>> addFaceData(
            @PathVariable("id") String id,
            @RequestPart(value = "image", required = true) MultipartFile imageFile
    ) {
        service.addFaceData(id, imageFile);

        return VsResponseUtil.success("Thêm dữ liệu khuôn mặt thành công");
    }
}
