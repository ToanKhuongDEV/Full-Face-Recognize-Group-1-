package com.example.befacerecognitionattendance2025.service.impl;

import com.example.befacerecognitionattendance2025.client.AIRecognitionClient;
import com.example.befacerecognitionattendance2025.constant.ErrorMessage;
import com.example.befacerecognitionattendance2025.domain.dto.request.TimeFilterRequest;
import com.example.befacerecognitionattendance2025.domain.dto.response.AttendanceSummaryDTO;
import com.example.befacerecognitionattendance2025.domain.entity.Attendance;
import com.example.befacerecognitionattendance2025.domain.entity.Employee;
import com.example.befacerecognitionattendance2025.domain.mapper.AttendanceMapper;
import com.example.befacerecognitionattendance2025.exception.NotFoundException;
import com.example.befacerecognitionattendance2025.repository.AttendanceRepository;
import com.example.befacerecognitionattendance2025.repository.EmployeeRepository;
import com.example.befacerecognitionattendance2025.service.AttendanceService;
import com.example.befacerecognitionattendance2025.service.AuthService;
import com.example.befacerecognitionattendance2025.util.ValidateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class AttendanceServiceImpl implements AttendanceService {

    private final AttendanceRepository attendanceRepository;
    private final AttendanceMapper attendanceMapper;
    private final AIRecognitionClient aiRecognitionClient;
    private final EmployeeRepository employeeRepository;
    private final AuthService authService;

    @Override
    public Double getTotalWorkingHoursDynamic(String employeeId, TimeFilterRequest time) {

        if(employeeRepository.findById(employeeId).isEmpty()){
            throw new NotFoundException(ErrorMessage.Employee.ERR_NOT_FOUND);
        }
        ValidateUtil.validateDate(time);
        return attendanceRepository.getTotalWorkingHoursDynamic(employeeId, time.getDay(), time.getMonth(), time.getYear());
    }

    @Override
    @Transactional(readOnly = true)
    public List<AttendanceSummaryDTO> getWorkingHoursByFilter(String employeeId, TimeFilterRequest request) {
        if(employeeRepository.findById(employeeId).isEmpty()) {
            throw new NotFoundException(ErrorMessage.Employee.ERR_NOT_FOUND);
        }
        ValidateUtil.validateDate(request);
        List<Attendance> attendances = attendanceRepository.findAttendanceDynamic(employeeId, request.getDay(), request.getMonth(), request.getYear());
        return attendanceMapper.toSummaryDTOList(attendances);
    }
    @Override
    @Transactional
    public AttendanceSummaryDTO recordFaceAttendance(MultipartFile faceImage) {
        String employeeId = aiRecognitionClient.identifyEmployeeFromImage(faceImage);
        if (employeeId == null || employeeId.isEmpty()) {
            throw new NotFoundException("Không nhận diện được khuôn mặt. Vui lòng thử lại hoặc di chuyển gần camera hơn.");
        }

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Employee.ERR_NOT_FOUND));

        LocalDateTime now = LocalDateTime.now();

        Optional<Attendance> unfinished = attendanceRepository.findLatestUnfinished(employeeId);

        Attendance attendance;
        if (unfinished.isPresent()) {
            // 3. Nếu có bản ghi chưa checkout => check-out
            attendance = unfinished.get();

            if (Duration.between(attendance.getCheckInTime(), now).toMinutes() < 1) {
                return attendanceMapper.toSummaryDTO(attendance);
            }

            attendance.setCheckOutTime(now);
            Duration duration = Duration.between(attendance.getCheckInTime(), now);
            double hours = duration.toMinutes() / 60.0;
            attendance.setTotalHours(Math.round(hours * 100.0) / 100.0);
        } else {
            // 4. Nếu chưa có bản ghi nào => check-in mới
            attendance = Attendance.builder()
                    .employee(employee)
                    .checkInTime(now)
                    .workDate(now.toLocalDate())
                    .build();
        }

        Attendance saved = attendanceRepository.save(attendance);
        return attendanceMapper.toSummaryDTO(saved);
    }

    @Override
    public List<AttendanceSummaryDTO> getMyWorkingHoursByFilter(TimeFilterRequest request) {
        ValidateUtil.validateDate(request);
        String employeeId = authService.getCurrentUserId();
        if(employeeRepository.findById(employeeId).isEmpty()) {
            throw new NotFoundException(ErrorMessage.Employee.ERR_NOT_FOUND);
        }
        List<Attendance> attendances = attendanceRepository.findAttendanceDynamic(employeeId, request.getDay(), request.getMonth(), request.getYear());
        return attendanceMapper.toSummaryDTOList(attendances);
    }

}
