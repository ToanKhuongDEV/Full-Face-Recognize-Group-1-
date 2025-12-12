package com.example.befacerecognitionattendance2025.domain.mapper;

import com.example.befacerecognitionattendance2025.domain.dto.request.CreateDepartmentRequest;
import com.example.befacerecognitionattendance2025.domain.dto.request.UpdateDepartmentRequest;
import com.example.befacerecognitionattendance2025.domain.dto.response.DepartmentResponse;
import com.example.befacerecognitionattendance2025.domain.entity.Department;
import com.example.befacerecognitionattendance2025.domain.entity.Employee;
import org.mapstruct.*;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface DepartmentMapper {

    Department toEntity(CreateDepartmentRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEntityFromRequest(UpdateDepartmentRequest request, @MappingTarget Department department);

    @Mapping(target = "employeeNames", expression = "java(getEmployeeNames(department))")
    DepartmentResponse toResponse(Department department);

    List<DepartmentResponse> toResponseList(List<Department> departments);

    default List<String> getEmployeeNames(Department department) {
        if (department.getEmployees() == null) return null;
        return department.getEmployees().stream()
                .map(Employee::getFullName)
                .collect(Collectors.toList());
    }
}
