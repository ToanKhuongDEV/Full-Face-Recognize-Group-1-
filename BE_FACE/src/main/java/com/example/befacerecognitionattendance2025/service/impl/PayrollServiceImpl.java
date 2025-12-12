package com.example.befacerecognitionattendance2025.service.impl;

import com.example.befacerecognitionattendance2025.constant.ErrorMessage;
import com.example.befacerecognitionattendance2025.domain.dto.request.PayrollEntryRequest;
import com.example.befacerecognitionattendance2025.domain.dto.request.TimeFilterRequest;
import com.example.befacerecognitionattendance2025.domain.dto.response.PayrollResponse;
import com.example.befacerecognitionattendance2025.domain.dto.response.PayrollSummaryResponse;
import com.example.befacerecognitionattendance2025.domain.entity.Employee;
import com.example.befacerecognitionattendance2025.domain.entity.Payroll;
import com.example.befacerecognitionattendance2025.domain.mapper.PayrollMapper;
import com.example.befacerecognitionattendance2025.exception.NotFoundException;
import com.example.befacerecognitionattendance2025.repository.EmployeeRepository;
import com.example.befacerecognitionattendance2025.repository.PayrollRepository;
import com.example.befacerecognitionattendance2025.service.AttendanceService;
import com.example.befacerecognitionattendance2025.service.AuthService;
import com.example.befacerecognitionattendance2025.service.PayrollService;
import com.example.befacerecognitionattendance2025.util.ValidateUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class PayrollServiceImpl implements PayrollService {

    private final PayrollRepository payrollRepository;
    private final PayrollMapper payrollMapper;
    private final EmployeeRepository employeeRepository;
    private final AttendanceService attendanceService;
    private final AuthService authService;


    @Override
    @Transactional(readOnly = true)
    public List<PayrollSummaryResponse> getPayrollByDepartmentId(String departmentId, TimeFilterRequest time) {
        ValidateUtil.validateDate(time);
        List<Payroll> responses = payrollRepository.findPayrollByDepartmentAndMonth(time.getMonth(), time.getYear(), departmentId);
        return payrollMapper.toSummaryResponseList(responses);
    }


    @Override
    @Transactional
    public List<PayrollSummaryResponse> createPayroll(TimeFilterRequest time) {
        ValidateUtil.validateDate(time);
        Integer month = time.getMonth();
        Integer year = time.getYear();

        List<Employee> employees = employeeRepository.findAll();
        List<Payroll> payrolls = new ArrayList<>();

        for (Employee employee : employees) {
            Optional<Payroll> existingPayrollOpt =
                    payrollRepository.findByEmployee_IdAndMonthAndYear(employee.getId(), month, year);

            Double totalHours = attendanceService.getTotalWorkingHoursDynamic(employee.getId(), time);
            if (totalHours == null) totalHours = 0.0;

            Double baseSalary = employee.getDepartment().getBaseSalary();
            Double finalSalary = baseSalary * totalHours;

            Payroll payroll;

            if (existingPayrollOpt.isPresent()) {
                payroll = existingPayrollOpt.get();
                payroll.setTotalHours(totalHours);
                payroll.setBaseSalary(baseSalary);
                payroll.setFinalSalary(baseSalary * totalHours + payroll.getBonus() - payroll.getDeduction());
            } else {
                payroll = Payroll.builder()
                        .employee(employee)
                        .month(month)
                        .year(year)
                        .totalHours(totalHours)
                        .bonus(0.0)
                        .deduction(0.0)
                        .baseSalary(baseSalary)
                        .finalSalary(finalSalary)
                        .build();
            }

            payrolls.add(payroll);
        }

        payrollRepository.saveAll(payrolls);
        return payrollMapper.toSummaryResponseList(payrolls);
    }

    @Override
    @Transactional(readOnly = true)
    public PayrollResponse getMyPayroll(TimeFilterRequest time) {
        ValidateUtil.validateDate(time);
        String employeeId = authService.getCurrentUserId();
        Payroll payroll =  payrollRepository.findByEmployeeAndMonthAndYearFetch(employeeId, time.getMonth(), time.getYear())
                .orElseThrow(()-> new NotFoundException(ErrorMessage.Payroll.ERR_NOT_FOUND));
        return payrollMapper.toResponse(payroll);
    }

    @Override
    @Transactional
    public PayrollResponse updateBonusDeduction(String employeeId, PayrollEntryRequest request, TimeFilterRequest time) {
        ValidateUtil.validateDate(time);

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Employee.ERR_NOT_FOUND));

        Payroll payroll = payrollRepository
                .findByEmployeeAndMonthAndYearFetch(employeeId, time.getMonth(), time.getYear())
                .orElseGet(() -> {
                    // Nếu chưa có payroll thì tạo mới
                    return Payroll.builder()
                            .employee(employee)
                            .month(time.getMonth())
                            .year(time.getYear())
                            .totalHours(0.0)
                            .baseSalary(employee.getDepartment().getBaseSalary())
                            .finalSalary(0.0)
                            .bonus(0.0)
                            .deduction(0.0)
                            .build();
                });

        // Cập nhật bonus và deduction
        payrollMapper.updateBonusDeduction(request, payroll);
        payroll.setFinalSalary(payroll.getBaseSalary() * payroll.getTotalHours() + payroll.getBonus() - payroll.getDeduction());

        payrollRepository.save(payroll);
        return payrollMapper.toResponse(payroll);
    }
}