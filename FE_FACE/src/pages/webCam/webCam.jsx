import React, { useRef, useState, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import Webcam from "react-webcam";
import attendanceApi from "../../api/attendance"; // Đảm bảo đường dẫn import đúng
import "./webCam.css";

const WebCam = () => {
	const navigate = useNavigate();
	const webcamRef = useRef(null);
	const [isProcessing, setIsProcessing] = useState(false);

	// Hàm chuyển đổi ảnh Base64 sang File object
	const dataURLtoFile = (dataurl, filename) => {
		try {
			let arr = dataurl.split(","),
				mime = arr[0].match(/:(.*?);/)[1],
				bstr = atob(arr[1]),
				n = bstr.length,
				u8arr = new Uint8Array(n);
			while (n--) {
				u8arr[n] = bstr.charCodeAt(n);
			}
			return new File([u8arr], filename, { type: mime });
		} catch (e) {
			return null;
		}
	};

	const handleCheckIn = useCallback(async () => {
		if (!webcamRef.current) return;

		const imageSrc = webcamRef.current.getScreenshot();
		if (!imageSrc) return;

		setIsProcessing(true); // Bật trạng thái đang xử lý

		try {
			// 1. Chuẩn bị dữ liệu gửi đi
			const file = dataURLtoFile(imageSrc, "attendance.jpg");
			const formData = new FormData();
			// "image" phải khớp với @RequestParam("image") trong Java Controller
			formData.append("image", file);

			// 2. Gọi API
			const res = await attendanceApi.checkIn(formData);

			// 3. Xử lý kết quả trả về
			// Axios thường trả dữ liệu trong res.data
			const responseBody = res.data ? res.data : res;

			if (responseBody.status === "SUCCESS") {
				// --- TRƯỜNG HỢP THÀNH CÔNG ---
				// Lấy tên nhân viên từ object data
				const employeeName = responseBody.data.employeeName;
				alert(`Xin chào ${employeeName} 👋`);
			} else {
				// --- TRƯỜNG HỢP THẤT BẠI (Backend trả về status: ERROR) ---
				// Hiển thị nguyên văn message từ Backend
				alert(responseBody.message);
			}
		} catch (error) {
			console.error("Lỗi:", error);

			// Xử lý lỗi HTTP (400, 500...) nếu Backend trả về JSON lỗi
			const errorMsg = error.response?.data?.message || "Lỗi kết nối đến hệ thống!";
			alert(errorMsg);
		} finally {
			setIsProcessing(false); // Tắt trạng thái đang xử lý để chụp tiếp
		}
	}, [webcamRef]);

	return (
		<div className="webCam">
			<div className="cam">
				<h1>Timekeeping camera</h1>
				<div className="Webcam">
					<Webcam ref={webcamRef} audio={false} screenshotFormat="image/jpeg" width={350} />
				</div>

				{/* Nút Chụp Ảnh */}
				<div style={{ marginTop: "20px" }}>
					<button
						onClick={handleCheckIn}
						disabled={isProcessing}
						style={{
							padding: "10px 20px",
							fontSize: "18px",
							backgroundColor: isProcessing ? "#ccc" : "#4CAF50",
							color: "white",
							border: "none",
							borderRadius: "5px",
							cursor: isProcessing ? "not-allowed" : "pointer",
						}}
					>
						{isProcessing ? "Đang xử lý..." : "📸 Chấm công"}
					</button>
				</div>
			</div>

			<div className="bt-login">
				<div style={{ marginTop: "20px" }}>
					<button onClick={() => navigate("/login")}>Login with account Admin</button>
				</div>
			</div>
		</div>
	);
};

export default WebCam;
