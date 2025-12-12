package com.example.befacerecognitionattendance2025.service;


import com.example.befacerecognitionattendance2025.domain.dto.request.PayrollEntryRequest;
import com.example.befacerecognitionattendance2025.domain.dto.request.TimeFilterRequest;
import com.example.befacerecognitionattendance2025.domain.dto.response.PayrollResponse;
import com.example.befacerecognitionattendance2025.domain.dto.response.PayrollSummaryResponse;

import java.util.List;

public interface PayrollService {

    List<PayrollSummaryResponse> getPayrollByDepartmentId(String departmentId, TimeFilterRequest time);
    List<PayrollSummaryResponse> createPayroll(TimeFilterRequest time);
    PayrollResponse getMyPayroll(TimeFilterRequest time);
    PayrollResponse updateBonusDeduction(String employeeId, PayrollEntryRequest request, TimeFilterRequest time);

}
