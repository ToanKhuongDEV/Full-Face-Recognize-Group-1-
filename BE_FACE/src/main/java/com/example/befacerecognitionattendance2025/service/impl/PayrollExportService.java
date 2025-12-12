package com.example.befacerecognitionattendance2025.service.impl;

import com.example.befacerecognitionattendance2025.domain.dto.response.PayrollSummaryResponse;
import com.example.befacerecognitionattendance2025.repository.PayrollRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PayrollExportService {

    private final PayrollRepository payrollRepository;

    // --- Phương thức chính: Xuất file ---
    public ByteArrayInputStream exportPayroll(int month, int year) {

        // BƯỚC 1: Lấy dữ liệu từ Database (Sử dụng DTO Record)
        // Đảm bảo bạn đã có hàm findPayrollForExcel trả về List<PayrollSummaryResponse> trong Repository
        List<PayrollSummaryResponse> data = payrollRepository.findPayrollForExcel(month, year);

        // Debug log để kiểm tra (có thể xóa sau này)
        System.out.println(">>> Exporting Payroll T" + month + "/" + year + " - Found: " + data.size() + " records.");

        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            if (data.isEmpty()) {
                workbook.createSheet("Không có dữ liệu");
            } else {
                // BƯỚC 2: Nhóm dữ liệu theo Tên Phòng Ban
                // Lưu ý: Dùng method reference p.departmentName() vì đây là Java Record
                Map<String, List<PayrollSummaryResponse>> groupedByDept = data.stream()
                        .collect(Collectors.groupingBy(PayrollSummaryResponse::departmentName));

                // Tạo Style định dạng tiền tệ một lần để tái sử dụng
                CellStyle currencyStyle = createCurrencyStyle(workbook);
                CellStyle headerStyle = createHeaderStyle(workbook);

                // BƯỚC 3: Tạo từng Sheet cho mỗi phòng ban
                for (Map.Entry<String, List<PayrollSummaryResponse>> entry : groupedByDept.entrySet()) {
                    // Xử lý tên sheet để tránh lỗi ký tự đặc biệt
                    String safeDeptName = sanitizeSheetName(entry.getKey());
                    createSheetForDept(workbook, safeDeptName, entry.getValue(), headerStyle, currencyStyle);
                }
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());

        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi tạo file Excel: " + e.getMessage());
        }
    }

    // --- Hàm phụ: Tạo Sheet và điền dữ liệu ---
    private void createSheetForDept(Workbook workbook, String deptName, List<PayrollSummaryResponse> staffList,
                                    CellStyle headerStyle, CellStyle currencyStyle) {
        Sheet sheet = workbook.createSheet(deptName);

        // 1. Tạo Header
        Row header = sheet.createRow(0);
        String[] columns = {"Mã NV", "Họ Tên", "Lương Thực Lĩnh"};

        for (int i = 0; i < columns.length; i++) {
            Cell cell = header.createCell(i);
            cell.setCellValue(columns[i]);
            cell.setCellStyle(headerStyle);
        }

        // 2. Điền dữ liệu
        int rowIdx = 1;
        for (PayrollSummaryResponse p : staffList) {
            Row row = sheet.createRow(rowIdx++);

            // Cột 0: Mã NV (Record accessor: p.employeeCode())
            if (p.employeeCode() != null) {
                row.createCell(0).setCellValue(p.employeeCode());
            } else {
                row.createCell(0).setCellValue("");
            }

            // Cột 1: Họ Tên (Record accessor: p.employeeName())
            row.createCell(1).setCellValue(p.employeeName());

            // Cột 2: Lương (Record accessor: p.finalSalary())
            Cell salaryCell = row.createCell(2);
            if (p.finalSalary() != null) {
                salaryCell.setCellValue(p.finalSalary());
            } else {
                salaryCell.setCellValue(0);
            }
            salaryCell.setCellStyle(currencyStyle);
        }

        // 3. Tự động chỉnh độ rộng cột
        for (int i = 0; i < columns.length; i++) {
            sheet.autoSizeColumn(i);
        }
    }

    // --- Các hàm tiện ích (Helper) ---

    // Xử lý tên Sheet: Loại bỏ ký tự cấm và giới hạn 31 ký tự
    private String sanitizeSheetName(String name) {
        if (name == null || name.trim().isEmpty()) {
            return "Unknown_Dept";
        }
        String safeName = name.replaceAll("[\\\\/*?\\[\\]:]", " ").trim();
        // Excel giới hạn tên sheet tối đa 31 ký tự
        return safeName.length() > 30 ? safeName.substring(0, 30) : safeName;
    }

    // Tạo Style cho số tiền (VD: 10,000,000)
    private CellStyle createCurrencyStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        DataFormat format = workbook.createDataFormat();
        style.setDataFormat(format.getFormat("#,##0"));
        return style;
    }

    // Tạo Style cho Header (In đậm)
    private CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = workbook.createFont();
        font.setBold(true);
        style.setFont(font);
        return style;
    }
}