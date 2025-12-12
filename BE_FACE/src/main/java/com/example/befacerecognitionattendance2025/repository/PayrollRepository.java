package com.example.befacerecognitionattendance2025.repository;

import com.example.befacerecognitionattendance2025.domain.dto.response.PayrollSummaryResponse;
import com.example.befacerecognitionattendance2025.domain.entity.Payroll;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PayrollRepository extends JpaRepository<Payroll, String> {

    @Query("""
        SELECT p
        FROM Payroll p
        WHERE (:month is NULL or p.month = :month)
          AND p.year = :year
          AND p.employee.department.id = :departmentId
    """)
    List<Payroll> findPayrollByDepartmentAndMonth(
            @Param("month") Integer month,
            @Param("year") Integer year,
            @Param("departmentId") String departmentId
    );

    Optional<Payroll> findByEmployee_IdAndMonthAndYear(String employeeId, Integer month, Integer year);

    @Query("""
        SELECT p
        FROM Payroll p
        JOIN FETCH p.employee e
        JOIN FETCH e.department
        WHERE e.id = :employeeId
          AND p.month = :month
          AND p.year = :year
    """)
    Optional<Payroll> findByEmployeeAndMonthAndYearFetch(
            @Param("employeeId") String employeeId,
            @Param("month") Integer month,
            @Param("year") Integer year
    );
    @Query("""
        SELECT new com.example.befacerecognitionattendance2025.domain.dto.response.PayrollSummaryResponse(
            p.employee.employeeCode,
            p.employee.fullName,
            p.employee.department.name,
            p.finalSalary
        )
        FROM Payroll p
        WHERE p.month = :month AND p.year = :year
        ORDER BY p.employee.department.name ASC
    """)
    List<PayrollSummaryResponse> findPayrollForExcel(@Param("month") int month, @Param("year") int year);
}
