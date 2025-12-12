package com.example.befacerecognitionattendance2025.domain.mapper;

import com.example.befacerecognitionattendance2025.domain.dto.request.PayrollEntryRequest;
import com.example.befacerecognitionattendance2025.domain.dto.response.PayrollResponse;
import com.example.befacerecognitionattendance2025.domain.dto.response.PayrollSummaryResponse;
import com.example.befacerecognitionattendance2025.domain.entity.Payroll;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface PayrollMapper {

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateBonusDeduction(PayrollEntryRequest request, @MappingTarget Payroll payroll);


    @Mapping(target = "employeeCode", source = "employee.employeeCode")
    @Mapping(target = "employeeName", source = "employee.fullName")
    @Mapping(target = "departmentName", source = "employee.department.name")
    PayrollSummaryResponse toSummaryResponse(Payroll payroll);
    List<PayrollSummaryResponse> toSummaryResponseList(List<Payroll> payrolls);

    @Mapping(target = "employeeCode", source = "employee.employeeCode")
    @Mapping(target = "employeeName", source = "employee.fullName")
    @Mapping(target = "departmentName", source = "employee.department.name")
    PayrollResponse toResponse(Payroll payroll);

    List<PayrollResponse> toResponseList(List<Payroll> payrolls);

}
