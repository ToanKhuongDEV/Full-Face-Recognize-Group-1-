package com.example.befacerecognitionattendance2025.domain.mapper;

import com.example.befacerecognitionattendance2025.domain.dto.request.CreateEmployeeRequest;
import com.example.befacerecognitionattendance2025.domain.dto.request.UpdateEmployeeRequest;
import com.example.befacerecognitionattendance2025.domain.dto.response.EmployeeResponse;
import com.example.befacerecognitionattendance2025.domain.entity.Employee;
import org.mapstruct.*;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface EmployeeMapper {

    @Mapping(source = "department.name", target = "departmentName")
    EmployeeResponse toResponse(Employee employee);

    List<EmployeeResponse> toResponseList(List<Employee> listEmployees );

    Employee toEntity(CreateEmployeeRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateEmployee (UpdateEmployeeRequest request, @MappingTarget Employee employee);

}
