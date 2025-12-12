import React, { useEffect, useState } from "react";
import "./thongTinPhongBan.css";
import api from "../../../api/http"; // Đảm bảo bạn có file http.js chứa baseURL tới Render
import { useNavigate } from "react-router-dom";

function ThongTinPhongBan() {
  const [departments, setDepartments] = useState([]);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    fetchDepartments();
  }, []);

  const fetchDepartments = async () => {
    try {
      const token = sessionStorage.getItem("accessToken");
      if (token) {
        api.defaults.headers.common.Authorization = `Bearer ${token}`;
      }

      const res = await api.get("/departments");
      console.log("✅ Kết quả từ API:", res.data);

      if (res.data.status === "SUCCESS") {
        setDepartments(res.data.data);
      } else {
        setError(res.data.message || "Không lấy được danh sách phòng ban");
      }
    } catch (err) {
      console.error("❌ Lỗi khi gọi API:", err.response || err);
      setError("Lỗi khi gọi API hoặc chưa đăng nhập!");
    }
  };

  return (
    <div className="manager-child">
      <div className="backgroud">
        <div className="box">
          <h2 className="title">Danh sách phòng ban</h2>

          {error && <p className="error">{error}</p>}

          <div className="list">
            {departments.length > 0 ? (
              <table className="dept-table">
                <thead>
                  <tr>
                    <th>Mã phòng ban</th>
                    <th>Tên phòng ban</th>
                    <th>Mô tả</th>
                  </tr>
                </thead>
                <tbody>
                  {departments.map((dept, index) => (
                    <tr key={index}>
                      <td>{dept.id}</td>
                      <td>{dept.name}</td>
                      <td>{dept.description || "—"}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            ) : (
              !error && (
                <p style={{ textAlign: "center" }}>Đang tải dữ liệu...</p>
              )
            )}
          </div>

          <div className="button">
            <button onClick={() => navigate("/loginManager")}>Quay lại</button>
          </div>
        </div>
      </div>
    </div>
  );
}

export default ThongTinPhongBan;
