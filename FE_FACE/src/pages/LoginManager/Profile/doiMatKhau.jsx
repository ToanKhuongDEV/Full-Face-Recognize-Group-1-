import React, { useState } from "react";
import "./doiMatKhau.css";

function DoiMatKhau() {
  const [oldPassword, setOldPassword] = useState("");
  const [newPassword, setNewPassword] = useState("");
  const [confirmPassword, setConfirmPassword] = useState("");
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  const handleChangePassword = async () => {
    setMessage("");
    if (!oldPassword || !newPassword) {
      setMessage("⚠️ Vui lòng nhập đầy đủ mật khẩu cũ và mật khẩu mới!");
      return;
    }
    if (newPassword !== confirmPassword) {
      setMessage("⚠️ Mật khẩu xác nhận không khớp!");
      return;
    }

    const token = sessionStorage.getItem("accessToken");
    if (!token) {
      setMessage("⚠️ Bạn chưa đăng nhập hoặc token không tồn tại!");
      return;
    }

    setLoading(true);
    try {
      const response = await fetch(
        "https://be-facerecognition-attendance-2025.onrender.com/api/v1/accounts/change-password",
        {
          method: "POST",
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${token}`,
          },
          body: JSON.stringify({
            oldPassword: oldPassword,
            newPassword: newPassword,
          }),
        }
      );

      const contentType = response.headers.get("content-type");

      if (!response.ok) {
        if (response.status === 401) {
          throw new Error("Phiên đăng nhập đã hết hạn hoặc không hợp lệ.");
        } else {
          throw new Error(`Lỗi máy chủ: ${response.status}`);
        }
      }

      if (!contentType || !contentType.includes("application/json")) {
        const text = await response.text();
        console.warn("⚠️ Server trả về HTML:", text.slice(0, 200));
        throw new Error("Phản hồi không phải JSON hợp lệ.");
      }

      const data = await response.json();
      console.log("✅ Kết quả đổi mật khẩu:", data);

      if (data.status === "SUCCESS") {
        setMessage("✅ Đổi mật khẩu thành công!");
        setOldPassword("");
        setNewPassword("");
        setConfirmPassword("");
      } else {
        setMessage(`❌ ${data.message || "Đổi mật khẩu thất bại."}`);
      }
    } catch (err) {
      console.error("❌ Lỗi:", err);
      setMessage(`❌ ${err.message}`);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div className="manager-child">
      <div className="background">
        <div className="box">
          <h2>Đổi mật khẩu</h2>

          <div className="form">
            <label>
              Mật khẩu cũ:
              <input
                type="password"
                value={oldPassword}
                onChange={(e) => setOldPassword(e.target.value)}
              />
            </label>

            <label>
              Mật khẩu mới:
              <input
                type="password"
                value={newPassword}
                onChange={(e) => setNewPassword(e.target.value)}
              />
            </label>

            <label>
              Xác nhận mật khẩu mới:
              <input
                type="password"
                value={confirmPassword}
                onChange={(e) => setConfirmPassword(e.target.value)}
              />
            </label>

            <button onClick={handleChangePassword} disabled={loading}>
              {loading ? "Đang đổi mật khẩu..." : "Đổi mật khẩu"}
            </button>

            {message && <p className="message">{message}</p>}
          </div>
        </div>
      </div>
    </div>
  );
}

export default DoiMatKhau;
