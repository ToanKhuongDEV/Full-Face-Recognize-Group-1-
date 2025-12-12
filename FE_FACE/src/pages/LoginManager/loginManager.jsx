import React from "react";
import "./loginManager.css";
import { useNavigate } from "react-router-dom";

function LoginManager() {
  const navigate = useNavigate();

  // 👉 Hàm xử lý logout
  const handleLogout = () => {
    sessionStorage.removeItem("accessToken");
    sessionStorage.removeItem("role");
    navigate("/login"); // quay về trang đăng nhập
  };

  return (
    <div className="loginManager">
      <div className="backGroud">
        <div className="menu">
          <div className="box-manager">
            {/* Header */}
            <div className="header">
              <h1>Nhóm 1</h1>
              <h2>Welcome Manager</h2>
            </div>

            {/* Title */}
            <div className="title">
              <h1>Dashboard</h1>
            </div>

            {/* Nội dung menu */}
            <div className="content">
              <div className="menu-item">
                {/* Employee */}
                <div className="Employee">
                  <h4>Employee</h4>
                  <p onClick={() => navigate("/loginManager/layThongTinNV")}>
                    Lấy thông tin nhân viên
                  </p>
                  <p
                    onClick={() => navigate("/loginManager/capNhatThongTinNV")}
                  >
                    Cập nhật thông tin nhân viên
                  </p>
                </div>

                {/* ManagerAccounts */}
                <div className="ManagerAccounts">
                  <h4>ManagerAccounts</h4>
                  <p onClick={() => navigate("/loginManager/themQuanLy")}>
                    Thêm quản lý
                  </p>
                  <p onClick={() => navigate("/loginManager/thongTinPhongBan")}>
                    Thông tin phòng ban
                  </p>
                </div>

                {/* Profile */}
                <div className="Profile">
                  <h4>Profile</h4>
                  <p
                    onClick={() => navigate("/loginManager/xemThongTinBanThan")}
                  >
                    Xem thông tin bản thân
                  </p>
                  <p onClick={() => navigate("/loginManager/capNhatThongTin")}>
                    Cập nhật thông tin
                  </p>
                  <p onClick={() => navigate("/loginManager/doiMatKhau")}>
                    Đổi mật khẩu
                  </p>
                </div>

                {/* Salary */}
                <div className="Salary">
                  <h4>Salary</h4>
                  <p
                    onClick={() => navigate("/loginManager/capNhatThuongPhat")}
                  >
                    Cập nhật thưởng phạt
                  </p>
                  <p onClick={() => navigate("/loginManager/taoBangLuong")}>
                    Tạo bảng lương
                  </p>
                  <p onClick={() => navigate("/loginManager/layBangLuong")}>
                    Lấy bảng lương theo tháng, năm
                  </p>
                </div>
              </div>

              {/* Logout */}
              <div className="logout">
                <button onClick={handleLogout}>Logout</button>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default LoginManager;
