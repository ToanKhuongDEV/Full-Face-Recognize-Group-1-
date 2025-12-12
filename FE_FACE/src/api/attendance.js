import axiosClient from "./http";

const attendanceApi = {
  checkIn: (formData) => {
    // Gọi đến endpoint Check-in (không cần token vì đã cấu hình public)
    return axiosClient.post("/attendances/check", formData, {
      headers: {
        "Content-Type": "multipart/form-data",
      },
    });
  },
};

export default attendanceApi;