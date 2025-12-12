import React, { useState } from "react";
import "./login.css";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import { faUser, faLock } from "@fortawesome/free-solid-svg-icons";
import { faFacebook, faGoogle } from "@fortawesome/free-brands-svg-icons";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { jwtDecode } from "jwt-decode";

// 👉 URL API backend Spring Boot
const API_BASE_URL =
  "https://be-facerecognition-attendance-2025.onrender.com/api/v1/auth/login";

const Login = () => {
  const navigate = useNavigate();

  const [username, setUsername] = useState("");
  const [password, setPassword] = useState("");
  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  // 👉 Hàm xử lý đăng nhập
  const handleLogin = async () => {
    setError("");
    setLoading(true);
    try {
      const res = await axios.post(API_BASE_URL, { username, password });
      console.log("📦 Kết quả từ backend:", res.data);

      if (res.data.status === "SUCCESS") {
        // ✅ Token thực tế nằm trong accessToken
        const token = res.data.data.accessToken;
        sessionStorage.setItem("accessToken", token);

        let role = "USER";
        try {
          if (
            token &&
            typeof token === "string" &&
            token.split(".").length === 3
          ) {
            const decoded = jwtDecode(token);
            console.log("🔍 Token decoded:", decoded);

            // ✅ Lấy role từ trường auth trong JWT (backend của bạn)
            role = decoded.auth || "USER";
          } else {
            console.warn("⚠️ Token không hợp lệ hoặc không phải JWT:", token);
          }
        } catch (e) {
          console.warn("⚠️ Không decode được token:", e);
        }

        sessionStorage.setItem("role", role);
        alert("Đăng nhập thành công!");

        // ✅ Điều hướng theo vai trò
        if (role && role.toUpperCase().includes("ADMIN")) {
          navigate("/loginManager");
        } else if (role && role.toUpperCase().includes("MANAGER")) {
          navigate("/loginManager");
        } else {
          navigate("/");
        }
      } else {
        setError(res.data.message || "Sai tài khoản hoặc mật khẩu");
      }
    } catch (err) {
      console.error(err);
      setError("Lỗi kết nối hoặc sai thông tin đăng nhập");
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <div className="login">
        <div className="login-backgroud">
          <div className="title-login">
            <p>Login your account</p>
          </div>

          {/* Input username/password */}
          <div className="input">
            <div className="input-acc">
              <FontAwesomeIcon icon={faUser} className="icon" />
              <input
                type="text"
                placeholder="User name"
                value={username}
                onChange={(e) => setUsername(e.target.value)}
              />
            </div>
            <div className="input-pass">
              <FontAwesomeIcon icon={faLock} className="icon" />
              <input
                type="password"
                placeholder="Password"
                value={password}
                onChange={(e) => setPassword(e.target.value)}
              />
            </div>
          </div>

          {/* Thông báo lỗi */}
          {error && (
            <p style={{ color: "red", textAlign: "center", marginTop: "10px" }}>
              {error}
            </p>
          )}

          {/* Nút login & signup */}
          <div className="button">
            <div className="button-login">
              <button onClick={handleLogin} disabled={loading}>
                {loading ? "Loading..." : "Login"}
              </button>
            </div>
            <div className="button-signUp">
              <button onClick={() => navigate("/signup")}>Sign up</button>
            </div>
          </div>

          {/* Or + mạng xã hội */}
          <div className="or">
            <p>Or</p>
          </div>

          <div className="face-goo">
            <div className="face">
              <button>
                <a href="https://www.facebook.com/">
                  <FontAwesomeIcon icon={faFacebook} className="face" /> Sign in
                  with Facebook
                </a>
              </button>
            </div>
            <div className="google">
              <button>
                <a href="https://accounts.google.com/">
                  <FontAwesomeIcon icon={faGoogle} className="google" /> Sign in
                  with Google
                </a>
              </button>
            </div>
          </div>

          {/* Quay lại webcam */}
          <div className="back">
            <button onClick={() => navigate("/")}>Back to the Webcam</button>
          </div>
        </div>
      </div>
    </div>
  );
};

export default Login;
