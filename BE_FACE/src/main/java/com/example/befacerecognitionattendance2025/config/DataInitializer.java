package com.example.befacerecognitionattendance2025.config;

import com.example.befacerecognitionattendance2025.constant.Gender;
import com.example.befacerecognitionattendance2025.constant.Role;
import com.example.befacerecognitionattendance2025.domain.entity.*;
import com.example.befacerecognitionattendance2025.repository.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;
@Configuration
@RequiredArgsConstructor
public class DataInitializer {

   private final EmployeeRepository employeeRepository;
   private final PasswordEncoder passwordEncoder;
   private final DepartmentRepository departmentRepository;
   private final AttendanceRepository attendanceRepository;

    @Bean
    public CommandLineRunner initData() {
        return args -> {
            // chỉ seed nếu chưa có employee nào
            if(employeeRepository.count() == 0) {
                seedData();
            }
        };
    }

    private void seedData() throws Exception {
        Random random = new Random();

        // 1. Departments
        Department hr = departmentRepository.findByName("Phòng Nhân Sự")
                .orElseGet(() -> departmentRepository.save(Department.builder()
                        .name("Phòng Nhân Sự")
                        .baseSalary(200.0)
                        .description("Phụ trách công tác nhân sự và tiền lương")
                        .build()));

        Department tech = departmentRepository.findByName("Phòng Kỹ Thuật")
                .orElseGet(() -> departmentRepository.save(Department.builder()
                        .name("Phòng Kỹ Thuật")
                        .baseSalary(150.0)
                        .description("Phát triển và bảo trì hệ thống")
                        .build()));

        Department sales = departmentRepository.findByName("Phòng Kinh Doanh")
                .orElseGet(() -> departmentRepository.save(Department.builder()
                        .name("Phòng Kinh Doanh")
                        .baseSalary(120.0)
                        .description("Bán hàng và chăm sóc khách hàng")
                        .build()));

        // 2. Employees
        int codeCounter = 1; // bắt đầu employeeCode từ 1

        String[][] users = {
                {"admin", "Admin123!", "Default Manager", "MALE", "MANAGER", "1990-01-01", "manager@example.com", "0123456789", "HR"},
                {"nhanvien1", "123456", "Nguyễn Văn A", "MALE", "STAFF", "1995-05-20", "a@example.com", "0901111111", "HR"},
                {"nhanvien2", "123456", "Trần Thị B", "FEMALE", "STAFF", "1993-07-15", "b@example.com", "0902222222", "TECH"},
                {"nhanvien3", "123456", "Lê Văn C", "MALE", "STAFF", "1992-03-10", "c@example.com", "0903333333", "SALES"}
        };

        for (String[] u : users) {
            String username = u[0];
            if (employeeRepository.findByUsername(username).isEmpty()) {
                Department dep;
                switch (u[8]) {
                    case "HR": dep = hr; break;
                    case "TECH": dep = tech; break;
                    case "SALES": dep = sales; break;
                    default: dep = hr;
                }
                Employee e = Employee.builder()
                        .employeeCode(codeCounter++)        // gán employeeCode tăng dần
                        .username(username)
                        .password(passwordEncoder.encode(u[1]))
                        .fullName(u[2])
                        .gender(Gender.valueOf(u[3]))
                        .role(Role.valueOf(u[4]))
                        .dateBirth(LocalDate.parse(u[5]))
                        .email(u[6])
                        .phoneNumber(u[7])
                        .department(dep)
                        .build();
                employeeRepository.save(e);
            }
        }

        List<Employee> employees = employeeRepository.findAll();


        // 4. Attendance (5 ngày gần đây)
        for (Employee e : employees) {
            for (int i = 1; i <= 5; i++) {
                LocalDate workDate = LocalDate.now().minusDays(i);
                    LocalDateTime checkIn = workDate.atTime(8, random.nextInt(30));
                    LocalDateTime checkOut = workDate.atTime(17, random.nextInt(30));
                    double totalHours = (checkOut.getHour() + checkOut.getMinute() / 60.0) -
                            (checkIn.getHour() + checkIn.getMinute() / 60.0);

                Attendance att = Attendance.builder()
                        .employee(e)
                        .workDate(workDate)
                        .checkInTime(checkIn)
                        .checkOutTime(checkOut)
                        .totalHours(totalHours)
                        .build();
                attendanceRepository.save(att);
            }
        }
    }
}