import api from "./http";

// ✅ Lấy tất cả nhân viên
export async function getAllEmployees() {
	const token = sessionStorage.getItem("accessToken");
	if (token) {
		api.defaults.headers.common.Authorization = `Bearer ${token}`;
	}

	const res = await api.get("/accounts");
	return res.data; // backend trả về { status, message, data }
}

// ✅ Thêm nhân viên (multipart/form-data)
export async function createEmployee(data, imageFile) {
	const token = sessionStorage.getItem("accessToken");
	if (token) {
		api.defaults.headers.common.Authorization = `Bearer ${token}`;
	}

	const formData = new FormData();

	// Gửi đúng định dạng @RequestPart("data") — JSON string
	formData.append("data", new Blob([JSON.stringify(data)], { type: "application/json" }));

	if (imageFile) {
		formData.append("image", imageFile);
	}

	try {
		const res = await api.post("/accounts", formData, {
			headers: { "Content-Type": "multipart/form-data" },
		});
		return res.data;
	} catch (err) {
		console.error("❌ Server trả lỗi khi tạo nhân viên:", err.response?.data || err);
		throw err;
	}
}

// ✅ Cập nhật nhân viên (multipart/form-data)
export async function updateEmployee(id, data, imageFile) {
	const token = sessionStorage.getItem("accessToken");
	if (token) {
		api.defaults.headers.common.Authorization = `Bearer ${token}`;
	}

	const formData = new FormData();
	formData.append("data", new Blob([JSON.stringify(data)], { type: "application/json" }));

	if (imageFile) {
		formData.append("image", imageFile);
	}

	try {
		const res = await api.put(`/accounts/${id}`, formData, {
			headers: { "Content-Type": "multipart/form-data" },
		});
		return res.data;
	} catch (err) {
		console.error("❌ Server trả lỗi khi cập nhật nhân viên:", err.response?.data || err);
		throw err;
	}
}

// ✅ Xóa nhân viên
export async function deleteEmployee(id) {
	const token = sessionStorage.getItem("accessToken");
	if (token) {
		api.defaults.headers.common.Authorization = `Bearer ${token}`;
	}

	try {
		const res = await api.delete(`/accounts/${id}`);
		return res.data;
	} catch (err) {
		console.error("❌ Lỗi khi xóa nhân viên:", err.response?.data || err);
		throw err;
	}
}

// Đăng ký khuôn mặt
export async function registerFaceData(employeeId, imageFile) {
	const token = sessionStorage.getItem("accessToken");
	if (token) {
		api.defaults.headers.common.Authorization = `Bearer ${token}`;
	}

	const formData = new FormData();
	// Backend yêu cầu @RequestPart("image")
	formData.append("image", imageFile);

	try {
		// Endpoint: /accounts/{id}/face-data
		const res = await api.post(`/accounts/${employeeId}/face-data`, formData, {
			headers: { "Content-Type": "multipart/form-data" },
		});
		return res.data;
	} catch (err) {
		console.error("❌ Lỗi khi đăng ký khuôn mặt:", err.response?.data || err);
		throw err;
	}
}
