package com.example.befacerecognitionattendance2025.service;

import com.example.befacerecognitionattendance2025.domain.dto.request.TimeFilterRequest;
import com.example.befacerecognitionattendance2025.domain.dto.response.AttendanceSummaryDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface AttendanceService {

    /**
     * Lấy lịch sử làm việc của 1 nhân viên theo bộ lọc động (ngày / tháng / năm).
     * @param filterRequest chứa các trường lọc (day, month, year)
     * @return danh sách bản ghi tổng hợp giờ làm việc
     */
    List<AttendanceSummaryDTO> getWorkingHoursByFilter(String employeeId, TimeFilterRequest filterRequest);

    /**
     * Ghi nhận giờ vào hoặc ra cho nhân viên dựa trên nhận diện khuôn mặt.
     * tự động quyết định là check-in hay check-out tùy theo trạng thái trong ngày.
     * @param faceImage ảnh khuôn mặt được gửi từ frontend
     * @return bản ghi tổng hợp giờ làm việc
     */
    AttendanceSummaryDTO recordFaceAttendance(MultipartFile faceImage);

    List<AttendanceSummaryDTO> getMyWorkingHoursByFilter(TimeFilterRequest filterRequest);
    Double getTotalWorkingHoursDynamic(String employeeId, TimeFilterRequest filterRequest);

}
