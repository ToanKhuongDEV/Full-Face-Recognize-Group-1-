package com.example.befacerecognitionattendance2025.service.impl;

import com.example.befacerecognitionattendance2025.domain.dto.request.AddEmployeesRequest;
import com.example.befacerecognitionattendance2025.domain.entity.Department;
import com.example.befacerecognitionattendance2025.domain.dto.request.CreateDepartmentRequest;
import com.example.befacerecognitionattendance2025.domain.dto.request.UpdateDepartmentRequest;
import com.example.befacerecognitionattendance2025.domain.dto.response.DepartmentResponse;
import com.example.befacerecognitionattendance2025.domain.entity.Employee;
import com.example.befacerecognitionattendance2025.domain.mapper.DepartmentMapper;
import com.example.befacerecognitionattendance2025.exception.DuplicateResourceException;
import com.example.befacerecognitionattendance2025.exception.NotFoundException;
import com.example.befacerecognitionattendance2025.repository.DepartmentRepository;
import com.example.befacerecognitionattendance2025.repository.EmployeeRepository;
import com.example.befacerecognitionattendance2025.service.DepartmentService;
import com.example.befacerecognitionattendance2025.constant.ErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentMapper departmentMapper;
    private final EmployeeRepository employeeRepository;

    @Transactional
    @Override
    public DepartmentResponse createDepartment(CreateDepartmentRequest request) {
        if (departmentRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException(ErrorMessage.Department.ERR_NAME_EXISTS);
        }

        Department department = departmentMapper.toEntity(request);
        Department saved = departmentRepository.save(department);
        return departmentMapper.toResponse(saved);
    }

    @Transactional
    @Override
    public DepartmentResponse updateDepartment(String id, UpdateDepartmentRequest request) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Department.ERR_NOT_FOUND));

        departmentMapper.updateEntityFromRequest(request, department);
        Department updated = departmentRepository.save(department);
        return departmentMapper.toResponse(updated);
    }

    @Transactional
    @Override
    public DepartmentResponse deleteDepartment(String id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Department.ERR_NOT_FOUND));
        departmentRepository.delete(department);
        return departmentMapper.toResponse(department);
    }

    @Override
    public DepartmentResponse findDepartmentById(String id) {
        Department department = departmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Department.ERR_NOT_FOUND));
        return departmentMapper.toResponse(department);
    }

    @Transactional
    @Override
    public DepartmentResponse addEmployeesToDepartment(String departmentId, AddEmployeesRequest request) {

        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Department.ERR_NOT_FOUND));


        List<Employee> employees = employeeRepository.findAllById(request.getEmployeeIds());


        if (employees.size() != request.getEmployeeIds().size()) {
            List<String> notFoundIds = request.getEmployeeIds().stream()
                    .filter(id -> employees.stream().noneMatch(e -> e.getId().equals(id)))
                    .toList();
            throw new NotFoundException(ErrorMessage.Employee.ERR_NOT_FOUND + notFoundIds);
        }

        List<String> alreadyAssigned = employees.stream()
                .filter(e -> e.getDepartment() != null && !e.getDepartment().getId().equals(departmentId))
                .map(Employee::getId)
                .toList();
        if (!alreadyAssigned.isEmpty()) {
            throw new DuplicateResourceException(ErrorMessage.Employee.ERR_INVALID_E + alreadyAssigned);
        }

        employees.forEach(employee -> employee.setDepartment(department));
        department.getEmployees().addAll(employees);

        Department updated = departmentRepository.save(department);

        return departmentMapper.toResponse(updated);
    }

    @Override
    public List<DepartmentResponse> findAllDepartments() {
        return departmentMapper.toResponseList(departmentRepository.findAll());
    }

}