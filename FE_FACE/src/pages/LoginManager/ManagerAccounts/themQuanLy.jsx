import React, { useState } from "react";
import "./themQuanLy.css";

function ThemQuanLy() {
  const [formData, setFormData] = useState({
    fullName: "",
    username: "",
    email: "",
    phone: "",
    gender: "",
    password: "",
  });
  const [image, setImage] = useState(null);
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);

  // Xử lý thay đổi text input
  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData((prev) => ({ ...prev, [name]: value }));
  };

  // Xử lý chọn ảnh
  const handleImageChange = (e) => {
    setImage(e.target.files[0]);
  };

  // Hàm gửi API tạo quản lý
  const handleAddManager = async () => {
    setMessage("");
    setLoading(true);

    const token = sessionStorage.getItem("accessToken");
    if (!token) {
      setMessage("⚠️ Chưa đăng nhập hoặc token không tồn tại!");
      setLoading(false);
      return;
    }

    try {
      const body = new FormData();
      body.append(
        "data",
        JSON.stringify({
          fullName: formData.fullName,
          username: formData.username,
          email: formData.email,
          phone: formData.phone,
          gender: formData.gender,
          password: formData.password,
        })
      );
      if (image) body.append("image", image);

      const response = await fetch(
        "https://be-facerecognition-attendance-2025.onrender.com/api/v1/accounts/manager",
        {
          method: "POST",
          headers: {
            Authorization: `Bearer ${token}`,
          },
          body,
        }
      );

      const contentType = response.headers.get("content-type");

      if (!response.ok) {
        if (response.status === 401) {
          throw new Error("Phiên đăng nhập hết hạn hoặc không hợp lệ.");
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
      console.log("✅ Kết quả thêm quản lý:", data);

      if (data.status === "SUCCESS") {
        setMessage("✅ Thêm quản lý thành công!");
        setFormData({
          fullName: "",
          username: "",
          email: "",
          phone: "",
          gender: "",
          password: "",
        });
        setImage(null);
      } else {
        setMessage(`❌ ${data.message || "Thêm quản lý thất bại."}`);
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
          <h2>Thêm tài khoản Quản lý</h2>

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
              Tên đăng nhập:
              <input
                type="text"
                name="username"
                value={formData.username}
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
              Mật khẩu:
              <input
                type="password"
                name="password"
                value={formData.password}
                onChange={handleChange}
              />
            </label>
            <label>
              Ảnh đại diện:
              <input
                type="file"
                accept="image/*"
                onChange={handleImageChange}
              />
            </label>

            <button onClick={handleAddManager} disabled={loading}>
              {loading ? "Đang thêm..." : "Thêm quản lý"}
            </button>

            {message && <p className="message">{message}</p>}
          </div>
        </div>
      </div>
    </div>
  );
}

export default ThemQuanLy;
