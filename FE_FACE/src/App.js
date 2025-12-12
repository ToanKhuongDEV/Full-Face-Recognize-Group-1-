import logo from "./logo.svg";
import "./App.css";
import React from "react";
import { BrowserRouter as Router, Routes, Route } from "react-router-dom";

// --- IMPORT CÁC TRANG ---
import Login from "./pages/Login/login";
import SignUp from "./pages/signUp/signUp";
import WebCam from "./pages/webCam/webCam";
import LoginManager from "./pages/LoginManager/loginManager.jsx";

// Import trang Đăng ký khuôn mặt mới tạo
import RegisterFace from "./pages/webCam/RegisterFace";

import CapNhatThongTinNV from "./pages/LoginManager/Employee/capNhatThongTinNV.jsx";
import LayThongTinNV from "./pages/LoginManager/Employee/layThongTinNV.jsx";
import ThemQuanLy from "./pages/LoginManager/ManagerAccounts/themQuanLy.jsx";
import ThongTinNhanVien from "./pages/LoginManager/ManagerAccounts/thongTinNhanVien.jsx";
import ThongTinPhongBan from "./pages/LoginManager/ManagerAccounts/thongTinPhongBan.jsx";
import XemThongTinBanThan from "./pages/LoginManager/Profile/xemThongTinBanThan.jsx";
import LayBangLuong from "./pages/LoginManager/Salary/layBangLuong.jsx";
import TaoBangLuong from "./pages/LoginManager/Salary/taoBangLuong.jsx";
import CapNhatThuongPhat from "./pages/LoginManager/Salary/capNhatThuongPhat.jsx";
import DoiMatKhau from "./pages/LoginManager/Profile/doiMatKhau.jsx";
import CapNhatThongTin from "./pages/LoginManager/Profile/capNhatThongTin.jsx";

function App() {
	return (
		<div className="App">
			<Router>
				<Routes>
					{/* Trang chủ (Điểm danh) */}
					<Route path="/" element={<WebCam />} />

					{/* --- ROUTE MỚI: ĐĂNG KÝ KHUÔN MẶT --- */}
					<Route path="/register-face" element={<RegisterFace />} />

					<Route path="/login" element={<Login />} />
					<Route path="/signup" element={<SignUp />} />
					<Route path="/loginManager" element={<LoginManager />} />

					{/* Các Route quản lý */}
					<Route path="/loginManager/capNhatThongTinNV" element={<CapNhatThongTinNV />} />
					<Route path="/loginManager/layThongTinNV" element={<LayThongTinNV />} />
					<Route path="/loginManager/themQuanLy" element={<ThemQuanLy />} />
					<Route path="/loginManager/thongTinPhongBan" element={<ThongTinPhongBan />} />
					<Route path="/loginManager/xemThongTinBanThan" element={<XemThongTinBanThan />} />
					<Route path="/loginManager/layBangLuong" element={<LayBangLuong />} />
					<Route path="/loginManager/taoBangLuong" element={<TaoBangLuong />} />
					<Route path="/loginManager/capNhatThuongPhat" element={<CapNhatThuongPhat />} />
					<Route path="/loginManager/doiMatKhau" element={<DoiMatKhau />} />
					<Route path="/loginManager/capNhatThongTin" element={<CapNhatThongTin />} />
				</Routes>
			</Router>
		</div>
	);
}

export default App;
