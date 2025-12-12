import React, { useState } from "react";
import "./layBangLuong.css";

function LayBangLuong() {
  const [day, setDay] = useState("");
  const [month, setMonth] = useState("");
  const [year, setYear] = useState("");
  const [message, setMessage] = useState("");
  const [loading, setLoading] = useState(false);
  const [salaryData, setSalaryData] = useState(null);

  const handleGetPayroll = async () => {
    setMessage("");
    setSalaryData(null);

    if (!day || !month || !year) {
      setMessage("⚠️ Vui lòng nhập đầy đủ ngày, tháng và năm!");
      return;
    }

    const token = sessionStorage.getItem("accessToken");
    if (!token) {
      setMessage("⚠️ Bạn chưa đăng nhập hoặc token không tồn tại!");
      return;
    }

    setLoading(true);
    try {
      // ✅ API yêu cầu query string (không body)
      const url = `https://be-facerecognition-attendance-2025.onrender.com/api/v1/payrolls/me?day=${parseInt(
        day
      )}&month=${parseInt(month)}&year=${parseInt(year)}`;

      const response = await fetch(url, {
        method: "GET",
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });

      const contentType = response.headers.get("content-type");

      if (!response.ok) {
        if (response.status === 401)
          throw new Error("Phiên đăng nhập hết hạn hoặc không hợp lệ.");
        throw new Error(`Lỗi máy chủ: ${response.status}`);
      }

      if (!contentType || !contentType.includes("application/json")) {
        const text = await response.text();
        console.warn("⚠️ Server trả về HTML:", text.slice(0, 200));
        throw new Error("Phản hồi không phải JSON hợp lệ.");
      }

      const data = await response.json();
      console.log("✅ Kết quả lấy bảng lương:", data);

      if (data.status === "SUCCESS") {
        setSalaryData(data.data);
        setMessage(`✅ ${data.message || "Lấy bảng lương thành công!"}`);
      } else {
        setMessage(`❌ ${data.message || "Không tìm thấy bảng lương."}`);
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
          <h2>Xem bảng lương của bạn</h2>

          <div className="form">
            <label>
              Ngày:
              <input
                type="number"
                min="1"
                max="31"
                value={day}
                onChange={(e) => setDay(e.target.value)}
              />
            </label>

            <label>
              Tháng:
              <input
                type="number"
                min="1"
                max="12"
                value={month}
                onChange={(e) => setMonth(e.target.value)}
              />
            </label>

            <label>
              Năm:
              <input
                type="number"
                min="2000"
                max="2100"
                value={year}
                onChange={(e) => setYear(e.target.value)}
              />
            </label>

            <button onClick={handleGetPayroll} disabled={loading}>
              {loading ? "Đang tải..." : "Xem bảng lương"}
            </button>

            {message && <p className="message">{message}</p>}
          </div>

          {/* Hiển thị kết quả */}
          {salaryData && (
            <div className="result">
              <h3>Thông tin bảng lương</h3>
              <pre>{JSON.stringify(salaryData, null, 2)}</pre>
            </div>
          )}
        </div>
      </div>
    </div>
  );
}

export default LayBangLuong;
