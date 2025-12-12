from fastapi import FastAPI, UploadFile, File, Form, HTTPException
from fastapi.middleware.cors import CORSMiddleware
import face_recognition
import numpy as np
import cv2
import shutil
import os

# Import modules nội bộ
from src.search import search_index
from src.db import db

app = FastAPI(title="Face Attendance API")

# Cấu hình CORS để Frontend (React/Vue) gọi được
app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)

# Khởi động: Load dữ liệu từ MySQL lên RAM trước
@app.on_event("startup")
async def startup_event():
    db.load_known_faces()

@app.get("/")
def read_root():
    return {"message": "Face Attendance API is running with MySQL"}

# --- ENDPOINT 1: TÌM KIẾM KHUÔN MẶT ---
@app.post("/search_faces/")
async def search_faces_endpoint(file: UploadFile = File(...)):

    if file.content_type not in {"image/png", "image/jpeg", "image/jpg"}:
        raise HTTPException(
            status_code=400,
            detail="Invalid image format. Only PNG, JPG, JPEG are allowed."
        )

    try:
        # 1. Đọc file ảnh từ request
        contents = await file.read()
        nparr = np.frombuffer(contents, np.uint8)
        img = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
        
        # 2. Chuyển sang RGB (face_recognition cần RGB)
        rgb_img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)

        # 3. Phát hiện và mã hóa khuôn mặt
        # (Lấy khuôn mặt đầu tiên tìm thấy trong ảnh)
        face_locations = face_recognition.face_locations(rgb_img)
        if not face_locations:
            return {"status": "fail", "message": "No face detected in image"}

        # Mã hóa khuôn mặt đầu tiên
        unknown_encoding = face_recognition.face_encodings(rgb_img, face_locations)[0]

        # 4. Gọi module search để so sánh
        result = search_index.search_face(unknown_encoding)

        return result

    except Exception as e:
        return {"status": "error", "message": str(e)}


# --- ENDPOINT 2: THÊM DỮ LIỆU KHUÔN MẶT ---
@app.post("/add_face/")
async def add_face_endpoint(employee_id: str = Form(...), file: UploadFile = File(...)):
    
    if file.content_type not in {"image/png", "image/jpeg", "image/jpg"}:
        raise HTTPException(
            status_code=400,
            detail="Invalid image format. Only PNG, JPG, JPEG are allowed."
        )

    try:
        # 1. Đọc ảnh
        contents = await file.read()
        nparr = np.frombuffer(contents, np.uint8)
        img = cv2.imdecode(nparr, cv2.IMREAD_COLOR)
        rgb_img = cv2.cvtColor(img, cv2.COLOR_BGR2RGB)

        # 2. Mã hóa
        face_locations = face_recognition.face_locations(rgb_img)
        if not face_locations:
            return {"status": "fail", "message": "No face detected"}
        
        new_encoding = face_recognition.face_encodings(rgb_img, face_locations)[0]

        # 3. Lưu vào Database
        success = db.add_face_encoding(employee_id, new_encoding)

        if success:
            return {
                "status": "success", 
                "message": f"Added face data for employee_id {employee_id}"
            }
        else:
            return {
                "status": "fail", 
                "message": "Database error or Employee ID not found (foreign key constraint)"
            }

    except Exception as e:
        return {"status": "error", "message": str(e)}

# --- ENDPOINT 3: RELOAD CACHE (Tiện ích) ---
@app.post("/reload_db/")
async def reload_db():
    """Gọi endpoint này khi bạn sửa dữ liệu thủ công trong DB và muốn API cập nhật ngay"""
    db.refresh_cache()
    return {"status": "success", "message": "Cache refreshed form Database"}