package com.example.befacerecognitionattendance2025.constant;

public class ErrorMessage {

    public static String ERR_INVALID_IMAGE_FILE = "chỉ được upload ảnh";
    public static final String ERR_UNAUTHORIZED = "Xin lỗi, bạn cần cung cấp thông tin xác thực để thực hiện hành động này";
    public static final String ERR_FORBIDDEN = "Xin lỗi, bạn không có quyền để thực hiện hành động này";
    public static final String ERR = "Đã xảy ra lỗi không xác định";


    public static class Validation {
        public static final String ERR_NOT_BLANK = "Không thể trống";
        public static final String ERR_INVALID_EMAIL = "Sai định dạng email";
        public static final String ERR_INVALID_PASSWORD =
                "Password phải có ít nhất 8 ký tự, bao gồm ít nhất 1 chữ hoa, 1 chữ thường, 1 số và 1 ký tự đặc biệt";
        public static final String ERR_INVALID_USERNAME =
                "Username phải dài từ 5–30 ký tự và chỉ chứa chữ cái, số, dấu gạch dưới hoặc dấu chấm";
        public static final String ERR_INVALID_PHONE_NUMBER = "Số điện thoại không hợp lệ";
        public static final String ERR_INVALID_DATE = "Dữ liệu ngày/tháng/năm không hợp lệ";

    }

    public static class Auth {
        public static final String ERR_INCORRECT_CREDENTIALS = "Username hoặc mật khẩu không chính xác";
        public static final String ERR_INVALID_REFRESH_TOKEN = "Refresh token không hợp lệ hoặc đã hết hạn";
        public static final String ERR_INVALID_TOKEN = "Token không hợp lệ hoặc hết hạn";
    }

    public static class Employee{
        public static final String ERR_EMAIL_EXISTS = "Email đã tồn tại";
        public static final String ERR_USERNAME_EXISTS = "Username đã tồn tại";
        public static final String ERR_NOT_ENOUGH_AGE = "Nhân viên phải đủ 18 tuổi";
        public static final String ERR_NOT_FOUND = "Không tìm thấy nhân viên";
        public static final String ERR_INVALID_E = "Nhân viên đã trong 1 phòng ban khác: ";
    }

    public static class Department {
        public static final String ERR_INVALID_SALARY = "Base salary must be greater than 0";
        public static final String ERR_NAME_EXISTS = "Tên phòng ban đã tồn tại";
        public static final String ERR_NOT_FOUND = "Không tìm thấy phòng ban";
    }

    public static class Payroll{
        public static final String ERR_NOT_FOUND = "không tìm thấy bảng lương của nhân viên này trong năm , tháng này";
    }
}
