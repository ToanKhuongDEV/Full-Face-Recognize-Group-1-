package com.example.befacerecognitionattendance2025.service;

import com.example.befacerecognitionattendance2025.domain.dto.request.AddEmployeesRequest;
import com.example.befacerecognitionattendance2025.domain.dto.request.CreateDepartmentRequest;
import com.example.befacerecognitionattendance2025.domain.dto.request.UpdateDepartmentRequest;
import com.example.befacerecognitionattendance2025.domain.dto.response.DepartmentResponse;

import java.util.List;

public interface DepartmentService {

    DepartmentResponse createDepartment(CreateDepartmentRequest request);

    DepartmentResponse updateDepartment(String id, UpdateDepartmentRequest request);

    DepartmentResponse deleteDepartment(String id);

    DepartmentResponse findDepartmentById(String id);

    DepartmentResponse addEmployeesToDepartment(String departmentId, AddEmployeesRequest request);

    List<DepartmentResponse> findAllDepartments();
}
