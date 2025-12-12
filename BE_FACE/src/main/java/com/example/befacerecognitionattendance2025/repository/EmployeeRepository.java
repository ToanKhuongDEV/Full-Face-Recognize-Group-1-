package com.example.befacerecognitionattendance2025.repository;

import com.example.befacerecognitionattendance2025.domain.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee,String> {

    Optional<Employee> findByUsername(String username);
    Optional<Employee> findByEmail(String email);

    @Modifying
    @Query("UPDATE Employee e SET e.lastLogin = :lastLogin WHERE e.id = :employeeId")
    void updateLastLogin(@Param("employeeId") String employeeId, @Param("lastLogin") LocalDateTime lastLogin);

    @Query("SELECT COALESCE(MAX(e.employeeCode), 0) FROM Employee e")
    Integer findMaxEmployeeCode();
}
