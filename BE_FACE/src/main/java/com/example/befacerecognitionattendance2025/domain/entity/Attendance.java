package com.example.befacerecognitionattendance2025.domain.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "attendance")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Attendance {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "employee_id", nullable = false)
    private Employee employee;

    @CreationTimestamp
    @Column(name = "work_date")
    private LocalDate workDate;

    @Column(name = "check_in")
    private LocalDateTime checkInTime;

    @Column(name = "check_out")
    private LocalDateTime checkOutTime;

    @Column(name = "total_hours")
    private Double totalHours;

}
