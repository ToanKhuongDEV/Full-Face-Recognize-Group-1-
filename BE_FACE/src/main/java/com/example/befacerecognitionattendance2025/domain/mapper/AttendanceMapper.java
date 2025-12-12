package com.example.befacerecognitionattendance2025.domain.mapper;

import com.example.befacerecognitionattendance2025.domain.dto.response.AttendanceSummaryDTO;
import com.example.befacerecognitionattendance2025.domain.entity.Attendance;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface AttendanceMapper {

    @Mapping(source = "employee.id", target = "employeeId")
    @Mapping(source = "employee.fullName", target = "employeeName")
    AttendanceSummaryDTO toSummaryDTO(Attendance attendance);

    List<AttendanceSummaryDTO> toSummaryDTOList(List<Attendance> attendances);

}
