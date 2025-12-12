import face_recognition
import numpy as np
from src.db import db

def search_face(input_encoding: np.ndarray, tolerance: float = 0.45):
    """
    So sánh vector khuôn mặt đầu vào với dữ liệu đã load từ DB.
    
    Args:
        input_encoding: Vector 128 chiều của khuôn mặt cần tìm.
        tolerance: Độ sai số (Càng thấp càng chính xác nhưng dễ miss, càng cao càng dễ nhận nhầm).
                0.45 là mức khuyến nghị cho hệ thống chấm công chặt chẽ.
    
    Returns:
        dict: {
            "status": "success" | "unknown",
            "name": str,
            "id": str (UUID),
            "distance": float
        }
    """
    # 1. Load dữ liệu từ RAM (đã cache trong db.py)
    known_encodings, known_names, known_ids = db.load_known_faces()

    if not known_encodings:
        return {"status": "unknown", "message": "Database is empty"}

    # 2. Tính khoảng cách (Euclidean distance) tới TẤT CẢ khuôn mặt trong DB
    # face_recognition.face_distance trả về mảng khoảng cách
    face_distances = face_recognition.face_distance(known_encodings, input_encoding)

    # 3. Tìm khuôn mặt có khoảng cách nhỏ nhất (giống nhất)
    best_match_index = np.argmin(face_distances)
    min_distance = face_distances[best_match_index]

    # 4. Kiểm tra ngưỡng chấp nhận (Tolerance)
    if min_distance <= tolerance:
        return {
            "status": "success",
            "name": known_names[best_match_index],
            "id": known_ids[best_match_index], # Đây là employee_id (UUID)
            "distance": float(min_distance)
        }
    else:
        return {
            "status": "unknown",
            "distance": float(min_distance),
            "message": "Face not matched within tolerance"
        }