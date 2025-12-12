package com.example.befacerecognitionattendance2025.util;

import com.example.befacerecognitionattendance2025.constant.ErrorMessage;
import com.example.befacerecognitionattendance2025.domain.dto.request.TimeFilterRequest;
import com.example.befacerecognitionattendance2025.exception.InvalidException;

import java.time.LocalDate;

public class ValidateUtil {

    private static final String USERNAME_REGEX = "^[A-Za-z0-9_.]{5,30}$";

    // Regex: ≥8 ký tự, có ít nhất 1 chữ hoa, 1 chữ thường, 1 số, 1 ký tự đặc biệt bất kỳ
    private static final String PASSWORD_REGEX = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[^A-Za-z\\d]).{8,}$";

    /**
     * Kiểm tra hợp lệ username và password.
     */
    public static void validateCredentials(String username, String password) {
        if (username == null || !username.matches(USERNAME_REGEX)) {
            throw new InvalidException(ErrorMessage.Validation.ERR_INVALID_USERNAME);
        }

        if (password == null || !password.matches(PASSWORD_REGEX)) {
            throw new InvalidException(ErrorMessage.Validation.ERR_INVALID_PASSWORD);
        }
    }

    /**
     * Kiểm tra tuổi ≥18.
     */
    public static void validateAge(LocalDate dateOfBirth) {
        LocalDate today = LocalDate.now();
        LocalDate minAllowedDate = today.minusYears(18);

        if (dateOfBirth == null || dateOfBirth.isAfter(minAllowedDate)) {
            throw new InvalidException(ErrorMessage.Employee.ERR_NOT_ENOUGH_AGE);
        }
    }

    /**
     * Trả về true nếu password hợp lệ (để test nhanh mà không ném exception).
     */
    public static boolean validatePassword(String password) {
        return password != null && password.matches(PASSWORD_REGEX);
    }

    public static void validateDate(TimeFilterRequest time) {
        if (time.getDay() != null && (time.getDay() < 1 || time.getDay() > 31)) {
            throw new InvalidException(ErrorMessage.Validation.ERR_INVALID_DATE);
        }
        if (time.getMonth() != null && (time.getMonth() < 1 || time.getMonth() > 12)) {
            throw new InvalidException(ErrorMessage.Validation.ERR_INVALID_DATE);
        }
        if (time.getYear() != null && (time.getYear() < 1 || time.getYear() >LocalDate.now().getYear() )) {
            throw new InvalidException(ErrorMessage.Validation.ERR_INVALID_DATE);

        }
    }

}
