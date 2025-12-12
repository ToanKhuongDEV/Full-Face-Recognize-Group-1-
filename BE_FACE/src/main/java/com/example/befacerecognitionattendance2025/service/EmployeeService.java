package com.example.befacerecognitionattendance2025.service;

import com.example.befacerecognitionattendance2025.domain.dto.request.ChangePasswordRequest;
import com.example.befacerecognitionattendance2025.domain.dto.request.CreateEmployeeRequest;
import com.example.befacerecognitionattendance2025.domain.dto.request.UpdateEmployeeRequest;
import com.example.befacerecognitionattendance2025.domain.dto.response.EmployeeResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface EmployeeService {

    EmployeeResponse createEmployee (CreateEmployeeRequest request, MultipartFile file);
    EmployeeResponse createManager (CreateEmployeeRequest request, MultipartFile file);
    EmployeeResponse deleteEmployee ( String id);
    EmployeeResponse updateEmployee ( String id, UpdateEmployeeRequest request, MultipartFile file);
    EmployeeResponse updateMyProfile (UpdateEmployeeRequest request, MultipartFile file);
    EmployeeResponse changePassword(ChangePasswordRequest request) ;

    List<EmployeeResponse> getAllEmployee();

    EmployeeResponse getEmployeeById(String id);
    EmployeeResponse getMe();
    void  addFaceData(String employeeId, MultipartFile imageFile);
}
