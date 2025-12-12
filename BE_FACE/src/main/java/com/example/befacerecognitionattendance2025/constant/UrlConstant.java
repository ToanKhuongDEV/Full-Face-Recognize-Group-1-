package com.example.befacerecognitionattendance2025.constant;

public class UrlConstant {


    public static class Auth {
        private static final String PRE_FIX = "/auth";
        public static final String LOGIN = PRE_FIX + "/login";
        public static final String REFRESH = PRE_FIX + "/refresh";
    }
    public static class Employee {
        private static final String PRE_FIX = "/accounts";
        public static final String COMMON = PRE_FIX;
        public static final String ME = PRE_FIX + "/me";
        public static final String ID = PRE_FIX + "/{id}";
        public static final String CHANGE_PASSWORD = PRE_FIX + "/change-password";
        public static final String CREATE_MANAGER = PRE_FIX + "/manager";
        public static final String TRAIN_FACE = ID + "/face-data";
    }
    public static class Department {
        private static final String PRE_FIX = "/departments";
        public static final String COMMON = PRE_FIX;
        public static final String ID = PRE_FIX + "/{id}";
        public static final String ADD_EMPLOYEE = ID + "/department";
    }

    public static class Attendance {
        private static final String PRE_FIX = "/attendances";
        public static final String TOTAL_WORK_HOUR = PRE_FIX +"/employee" + "/{employeeId}";
        public static final String TOTAL_WORK_ME = PRE_FIX +"/me";
        public static final String CHECK = PRE_FIX + "/check";
    }

    public static class Payroll {
        private static final String PRE_FIX = "/payrolls";
        public static final String COMMON = PRE_FIX;
        public static final String EXPORT = PRE_FIX + "/export-salary";
        public static final String ME = PRE_FIX + "/me";
        public static final String GET_BY_DEPARTMENT = PRE_FIX + "/department" + "/{departmentId}";
        public static final String UPDATE_BONUS_DEDUCTION = PRE_FIX + "/employee" + "/{employeeId}";
    }
}
