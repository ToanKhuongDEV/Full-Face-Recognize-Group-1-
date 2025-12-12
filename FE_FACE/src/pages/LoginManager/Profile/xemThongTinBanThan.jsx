import React, { useEffect, useState } from "react";
import "./xemThongTinBanThan.css";

function XemThongTinBanThan() {
  const [userData, setUserData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState(null);

  useEffect(() => {
    const token = sessionStorage.getItem("accessToken");
    console.log("🔑 Token hiện tại:", token);

    if (!token) {
      setError("Chưa đăng nhập hoặc token không tồn tại");
      setLoading(false);
      return;
    }

    fetch(
      "https://be-facerecognition-attendance-2025.onrender.com/api/v1/accounts/me",
      {
        method: "GET",
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${token}`,
        },
      }
    )
      .then(async (response) => {
        const contentType = response.headers.get("content-type");

        // Nếu API trả lỗi
        if (!response.ok) {
          if (response.status === 401) {
            throw new Error("Phiên đăng nhập đã hết hạn hoặc không hợp lệ.");
          } else {
            throw new Error(`Lỗi máy chủ: ${response.status}`);
          }
        }

        // Nếu backend trả về HTML (ví dụ trang lỗi)
        if (!contentType || !contentType.includes("application/json")) {
          const text = await response.text();
          console.warn("⚠️ Server trả về HTML:", text.slice(0, 200));
          throw new Error("Phản hồi không phải JSON hợp lệ");
        }

        return response.json();
      })
      .then((data) => {
        console.log("✅ Dữ liệu API:", data);
        setUserData(data.data || data); // fallback nếu backend không bọc trong `data`
      })
      .catch((err) => {
        console.error("❌ Lỗi khi gọi API:", err.message);
        setError(err.message);
      })
      .finally(() => setLoading(false));
  }, []);

  if (loading)
    return <div className="loading">Đang tải thông tin cá nhân...</div>;
  if (error) return <div className="error">Lỗi: {error}</div>;

  return (
    <div className="manager-child">
      <div className="background">
        <div className="box">
          <h2>Thông tin cá nhân</h2>
          {userData ? (
            <div className="info-content">
              <div className="info-item">
                <span>Họ tên:</span> {userData.fullName || "Chưa có dữ liệu"}
              </div>
              <div className="info-item">
                <span>Tên đăng nhập:</span>{" "}
                {userData.username || "Chưa có dữ liệu"}
              </div>
              <div className="info-item">
                <span>Email:</span> {userData.email || "Chưa có dữ liệu"}
              </div>
              <div className="info-item">
                <span>Số điện thoại:</span>{" "}
                {userData.phone || "Chưa có dữ liệu"}
              </div>
              <div className="info-item">
                <span>Giới tính:</span> {userData.gender || "Chưa có dữ liệu"}
              </div>
              <div className="info-item">
                <span>Vai trò:</span> {userData.role || "Chưa có dữ liệu"}
              </div>
              <div className="info-item">
                <span>Ngày tạo tài khoản:</span>{" "}
                {userData.createdAt || "Chưa có dữ liệu"}
              </div>
            </div>
          ) : (
            <div>Không tìm thấy thông tin người dùng.</div>
          )}
        </div>
      </div>
    </div>
  );
}

export default XemThongTinBanThan;
