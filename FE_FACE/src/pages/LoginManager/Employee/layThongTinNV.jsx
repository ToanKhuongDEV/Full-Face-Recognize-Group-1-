import React, { useEffect, useState } from "react";
import "./layThongTinNV.css";
import api from "../../../api/http"; // đảm bảo http.js có baseURL tới Render
import { useNavigate } from "react-router-dom";

function LayThongTinNV() {
  const [employees, setEmployees] = useState([]);
  const [error, setError] = useState("");
  const navigate = useNavigate();

  useEffect(() => {
    fetchEmployees();
  }, []);

  const fetchEmployees = async () => {
    try {
      const token = sessionStorage.getItem("accessToken");
      if (token) {
        api.defaults.headers.common.Authorization = `Bearer ${token}`;
      }

      const res = await api.get("/accounts");
      console.log("✅ Kết quả API nhân viên:", res.data);

      if (res.data.status === "SUCCESS") {
        setEmployees(res.data.data);
      } else {
        setError(res.data.message || "Không lấy được danh sách nhân viên");
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
          <h2 className="title">Danh sách nhân viên</h2>

          {error && <p className="error">{error}</p>}

          <div className="list">
            {employees.length > 0 ? (
              <table className="employee-table">
                <thead>
                  <tr>
                    <th>ID</th>
                    <th>Tên đăng nhập</th>
                    <th>Họ và tên</th>
                    <th>Giới tính</th>
                    <th>Email</th>
                    <th>SĐT</th>
                    <th>Phòng ban</th>
                    <th>Vai trò</th>
                  </tr>
                </thead>
                <tbody>
                  {employees.map((emp, index) => (
                    <tr key={index}>
                      <td>{emp.id}</td>
                      <td>{emp.username}</td>
                      <td>{emp.fullName || "—"}</td>
                      <td>{emp.gender || "—"}</td>
                      <td>{emp.email}</td>
                      <td>{emp.phoneNumber || "—"}</td>
                      <td>{emp.departmentId || "—"}</td>
                      <td>{emp.role || emp.authorities?.[0] || "—"}</td>
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

export default LayThongTinNV;
