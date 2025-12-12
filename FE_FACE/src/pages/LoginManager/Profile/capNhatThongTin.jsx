import React, { useState } from "react";
import "./capNhatThongTin.css";

function CapNhatThongTin() {
  const [formData, setFormData] = useState({
    fullName: "",
    email: "",
    phone: "",
    gender: "",
  });
  const [image, setImage] = useState(null);
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState("");

  // Xử lý khi nhập text
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  // Xử lý khi chọn ảnh
  const handleImageChange = (e) => {
    setImage(e.target.files[0]);
  };

  // Hàm gọi API PATCH
  const handleUpdate = async () => {
    setLoading(true);
    setMessage("");

    const token = sessionStorage.getItem("accessToken");
    if (!token) {
      setMessage("⚠️ Chưa đăng nhập hoặc token không tồn tại.");
      setLoading(false);
      return;
    }

    try {
      const body = new FormData();
      // data phải là JSON string vì backend nhận object
      body.append(
        "data",
        JSON.stringify({
          fullName: formData.fullName,
          email: formData.email,
          phone: formData.phone,
          gender: formData.gender,
        })
      );
      if (image) body.append("image", image);

      const response = await fetch(
        "https://be-facerecognition-attendance-2025.onrender.com/api/v1/accounts/me",
        {
          method: "PATCH",
          headers: {
            Authorization: `Bearer ${token}`,
          },
          body,
        }
      );

      const contentType = response.headers.get("content-type");
      if (!response.ok) {
        if (response.status === 401)
          throw new Error("Phiên đăng nhập hết hạn hoặc không hợp lệ.");
        throw new Error(`Lỗi máy chủ: ${response.status}`);
      }

      if (!contentType || !contentType.includes("application/json")) {
        const text = await response.text();
        console.warn("⚠️ Server trả về HTML:", text.slice(0, 200));
        throw new Error("Phản hồi không phải JSON hợp lệ");
      }

      const data = await response.json();
      console.log("✅ Kết quả cập nhật:", data);
      setMessage("✅ Cập nhật thông tin thành công!");
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
          <h2>Cập nhật thông tin cá nhân</h2>

          <div className="form">
            <label>
              Họ và tên:
              <input
                type="text"
                name="fullName"
                value={formData.fullName}
                onChange={handleChange}
              />
            </label>
            <label>
              Email:
              <input
                type="email"
                name="email"
                value={formData.email}
                onChange={handleChange}
              />
            </label>
            <label>
              Số điện thoại:
              <input
                type="text"
                name="phone"
                value={formData.phone}
                onChange={handleChange}
              />
            </label>
            <label>
              Giới tính:
              <select
                name="gender"
                value={formData.gender}
                onChange={handleChange}
              >
                <option value="">-- Chọn giới tính --</option>
                <option value="MALE">Nam</option>
                <option value="FEMALE">Nữ</option>
                <option value="OTHER">Khác</option>
              </select>
            </label>
            <label>
              Ảnh đại diện:
              <input
                type="file"
                accept="image/*"
                onChange={handleImageChange}
              />
            </label>

            <button onClick={handleUpdate} disabled={loading}>
              {loading ? "Đang cập nhật..." : "Cập nhật thông tin"}
            </button>

            {message && <p className="message">{message}</p>}
          </div>
        </div>
      </div>
    </div>
  );
}

export default CapNhatThongTin;
