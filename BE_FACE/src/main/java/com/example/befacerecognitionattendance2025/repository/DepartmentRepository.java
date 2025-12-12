package com.example.befacerecognitionattendance2025.repository;

import com.example.befacerecognitionattendance2025.domain.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, String> {

    Optional<Department> findByName(String name);

    boolean existsByName(String name);
}
