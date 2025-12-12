package com.example.befacerecognitionattendance2025.repository;

import com.example.befacerecognitionattendance2025.domain.entity.Employee;
import com.example.befacerecognitionattendance2025.domain.entity.FaceData;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FaceDataRepository extends JpaRepository<FaceData, String> {
    long countByEmployee(Employee employee);
}
