import React, { useRef, useState, useCallback } from "react";
import { useNavigate, useLocation } from "react-router-dom";
import Webcam from "react-webcam";
import { registerFaceData } from "../../api/employee"; 
import "./webCam.css"; 

const RegisterFace = () => {
  // --- 1. KHAI BÁO TẤT CẢ HOOKS Ở ĐẦU ---
  const navigate = useNavigate();
  const location = useLocation();
  const webcamRef = useRef(null);
  const [isProcessing, setIsProcessing] = useState(false);

  // Lấy dữ liệu từ state (có thể undefined)
  const { employeeId, employeeName } = location.state || {};

  // Hàm helper (không phải hook, để đâu cũng được, nhưng để đây cho gọn)
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

  // --- 2. KHAI BÁO USECALLBACK (HOOK) ---
  // Phải khai báo hook này TRƯỚC khi return
  const handleRegister = useCallback(async () => {
    if (!webcamRef.current) return;

    const imageSrc = webcamRef.current.getScreenshot();
    if (!imageSrc) return;

    setIsProcessing(true);

    try {
      const file = dataURLtoFile(imageSrc, "register.jpg");
      
      // Gọi API
      const res = await registerFaceData(employeeId, file);

      if (res && res.status === "SUCCESS") {
        alert(`✅ Đăng ký thành công cho nhân viên: ${employeeName}`);
        navigate("/loginManager"); 
      } else {
        alert(`❌ Thất bại: ${res.message || "Lỗi không xác định"}`);
      }

    } catch (error) {
      console.error("Lỗi:", error);
      const errorMsg = error.response?.data?.message || "Lỗi kết nối đến hệ thống!";
      alert(`❌ Lỗi: ${errorMsg}`);
    } finally {
      setIsProcessing(false);
    }
  }, [webcamRef, employeeId, employeeName, navigate]);

  // --- 3. BÂY GIỜ MỚI ĐƯỢC KIỂM TRA ĐIỀU KIỆN ĐỂ RETURN SỚM ---
  // Nếu không có ID (truy cập trái phép), quay về trang quản lý
  if (!employeeId) {
    return (
      <div className="webCam">
        <div className="cam">
          <h2 style={{color: "red"}}>⚠️ Lỗi: Không tìm thấy thông tin nhân viên!</h2>
          <button onClick={() => navigate("/loginManager")}>Quay lại danh sách</button>
        </div>
      </div>
    );
  }

  // --- 4. RETURN GIAO DIỆN CHÍNH ---
  return (
    <div className="webCam">
      <div className="cam">
        <h1>Đăng ký khuôn mặt</h1>
        <p style={{marginBottom: "10px", color: "#555"}}>
            Nhân viên: <strong>{employeeName}</strong> (ID: {employeeId})
        </p>
        
        <div className="Webcam">
          <Webcam 
            ref={webcamRef} 
            audio={false} 
            screenshotFormat="image/jpeg" 
            width={350} 
          />
        </div>

        <div style={{ marginTop: "20px", display: "flex", gap: "10px", justifyContent: "center" }}>
          <button
            onClick={() => navigate("/loginManager")}
            className="btn"
            style={{ backgroundColor: "#6c757d", color: "white", padding: "10px 20px", border: "none", borderRadius: "5px" }}
          >
            Quay lại
          </button>

          <button
            onClick={handleRegister}
            disabled={isProcessing}
            style={{
              padding: "10px 20px",
              fontSize: "16px",
              backgroundColor: isProcessing ? "#ccc" : "#ff9800",
              color: "white",
              border: "none",
              borderRadius: "5px",
              cursor: isProcessing ? "not-allowed" : "pointer",
              fontWeight: "bold"
            }}
          >
            {isProcessing ? "⏳ Đang xử lý..." : "📸 Chụp & Đăng ký"}
          </button>
        </div>
      </div>
    </div>
  );
};

export default RegisterFace;