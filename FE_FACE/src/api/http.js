import axios from "axios";

// ✅ Dùng baseURL từ Render
const api = axios.create({
	baseURL: "http://localhost:8080/api/v1",
	headers: { "Content-Type": "application/json" },
});

// ✅ Hàm set token vào header
export function setAccessToken(token) {
	if (token) {
		api.defaults.headers.common.Authorization = `Bearer ${token}`;
	} else {
		delete api.defaults.headers.common.Authorization;
	}
}

// ✅ Interceptor: thêm token vào request
api.interceptors.request.use(
	(config) => {
		const token = sessionStorage.getItem("accessToken");
		if (token) {
			config.headers.Authorization = `Bearer ${token}`;
		}
		return config;
	},
	(error) => Promise.reject(error),
);

// ✅ Interceptor: xử lý token hết hạn (401)
api.interceptors.response.use(
	(response) => response,
	async (error) => {
		const originalRequest = error.config;
		if (error.response?.status === 401 && !originalRequest._retry) {
			originalRequest._retry = true;
			const refreshToken = sessionStorage.getItem("refreshToken");
			if (refreshToken) {
				try {
					const refreshRes = await axios.post("https://be-facerecognition-attendance-2025.onrender.com/api/v1/auth/refresh", { refreshToken });

					if (refreshRes.data.status === "SUCCESS") {
						const newAccessToken = refreshRes.data.data;
						sessionStorage.setItem("accessToken", newAccessToken);
						setAccessToken(newAccessToken);
						originalRequest.headers.Authorization = `Bearer ${newAccessToken}`;
						return api(originalRequest);
					}
				} catch (refreshErr) {
					console.error("⚠️ Refresh token thất bại:", refreshErr);
					sessionStorage.clear();
					window.location.href = "/login";
				}
			} else {
				sessionStorage.clear();
				window.location.href = "/login";
			}
		}
		return Promise.reject(error);
	},
);

export default api;
