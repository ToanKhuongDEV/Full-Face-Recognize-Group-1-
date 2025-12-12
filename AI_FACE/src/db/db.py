from dotenv import load_dotenv
import os
import json
import numpy as np
from typing import List, Tuple, Optional

from sqlalchemy import (create_engine, MetaData, Table,
    Column,
    Integer,
    String,
    JSON,
    ForeignKey,
    DateTime,
    select,
    func,
    text
)
from sqlalchemy.exc import SQLAlchemyError
from uuid import uuid4
from datetime import datetime

# Load biến môi trường
load_dotenv()

# --- CẤU HÌNH KẾT NỐI DB (Đã tối ưu cho .env của bạn) ---
DATABASE_URI = os.getenv("DATABASE_URI")

# Nếu chưa có biến gộp, tự động ghép từ các biến lẻ
if not DATABASE_URI:
    host = os.getenv("DB_HOST", "localhost")
    port = os.getenv("DB_PORT", "3306")
    user = os.getenv("DB_USER", "root")
    password = os.getenv("DB_PASSWORD", "")
    dbname = os.getenv("DB_NAME", "face_attendance")
    DATABASE_URI = f"mysql+pymysql://{user}:{password}@{host}:{port}/{dbname}?charset=utf8mb4"

# Cấu hình Engine
engine_kwargs = {
    "future": True,
    "pool_pre_ping": True, # Tự động kết nối lại nếu rớt mạng
    "echo": False,
}

# Cấu hình SSL (Chỉ bật nếu DB_SSL_MODE khác DISABLED)
DB_SSL_MODE = os.getenv("DB_SSL_MODE", "DISABLED")
if DB_SSL_MODE != "DISABLED":
    engine_kwargs["connect_args"] = {
        "ssl_verify_cert": True,
        "ssl_verify_identity": True,
    }

print(f"🔌 Kết nối DB: {DATABASE_URI.split('@')[-1]}") # Log tên DB (che mật khẩu)

try:
    engine = create_engine(DATABASE_URI, **engine_kwargs)
    metadata = MetaData()
except Exception as e:
    print(f"❌ Lỗi khởi tạo Engine: {e}")
    raise

# Cache trong RAM
_ENC_CACHE = None

# --- ĐỊNH NGHĨA BẢNG ---

# Bảng nhân viên
employees_table = Table(
    "employees",
    metadata,
    Column("id", String(255), primary_key=True),
    Column("full_name", String(255), nullable=False),
    Column("employee_code", Integer, nullable=False),
    Column("avatar", String(255), nullable=True),
)

# Bảng dữ liệu khuôn mặt
face_data_table = Table(
    "face_data",
    metadata,
    Column("id", String(255), primary_key=True),
    Column("created_at", DateTime(timezone=True), server_default=func.now()),
    Column("encoding", JSON, nullable=False),
    Column("employee_id", String(255), ForeignKey("employees.id"), nullable=False),
)

def get_connection():
    return engine.connect()

def init_db():
    metadata.create_all(engine)

# --- CÁC HÀM TƯƠNG TÁC ---

def load_known_faces() -> Tuple[List[np.ndarray], List[str], List[str]]:
    """
    Load toàn bộ dữ liệu khuôn mặt và tên nhân viên lên RAM.
    """
    global _ENC_CACHE
    if _ENC_CACHE is not None:
        return _ENC_CACHE

    encodings = []
    names = []
    ids = []

    print("🔄 Đang tải dữ liệu khuôn mặt từ Database...")
    with get_connection() as conn:
        stmt = select(
            face_data_table.c.encoding,
            employees_table.c.full_name,
            employees_table.c.id
        ).select_from(
            face_data_table.join(employees_table, face_data_table.c.employee_id == employees_table.c.id)
        )
        
        try:
            results = conn.execute(stmt).fetchall()
            if not results:
                print("⚠️ Database chưa có dữ liệu khuôn mặt.")
                return [], [], []

            for row in results:
                vec_data = row.encoding
                try:
                    # Chuyển đổi JSON list sang numpy array
                    if isinstance(vec_data, str):
                        vec_data = json.loads(vec_data)
                    
                    arr = np.array(vec_data, dtype=np.float64)
                    
                    encodings.append(arr)
                    names.append(row.full_name)
                    ids.append(row.id)
                except Exception as e:
                    print(f"❌ Lỗi parse vector ID {row.id}: {e}")
                    continue
            
            print(f"✅ Đã tải thành công {len(encodings)} khuôn mặt.")

        except SQLAlchemyError as e:
            print(f"❌ Lỗi Database khi tải: {e}")
            return [], [], []

    _ENC_CACHE = (encodings, names, ids)
    return encodings, names, ids

def add_face_encoding(employee_id: str, encoding: List[float]) -> bool:
    """Thêm vector khuôn mặt mới."""
    new_id = str(uuid4())
    
    if isinstance(encoding, np.ndarray):
        encoding_list = encoding.tolist()
    else:
        encoding_list = encoding

    stmt = face_data_table.insert().values(
        id=new_id,
        created_at=datetime.now(), # Đã sửa utcnow -> now
        encoding=encoding_list,
        employee_id=employee_id
    )

    with get_connection() as conn:
        try:
            conn.execute(stmt)
            conn.commit()
            
            global _ENC_CACHE
            _ENC_CACHE = None
            print(f"✅ Đã lưu vector mới cho nhân viên ID: {employee_id}")
            return True
        except SQLAlchemyError as e:
            print(f"❌ Lỗi lưu dữ liệu: {e}")
            conn.rollback()
            return False

def get_employee_by_id(employee_id: str):
    """Lấy thông tin nhân viên."""
    with get_connection() as conn:
        stmt = select(employees_table).where(employees_table.c.id == employee_id)
        row = conn.execute(stmt).first()
        return row

def delete_face_data(employee_id: str) -> int:
    """Xóa dữ liệu khuôn mặt của nhân viên."""
    with get_connection() as conn:
        try:
            stmt = face_data_table.delete().where(face_data_table.c.employee_id == employee_id)
            result = conn.execute(stmt)
            conn.commit()
            
            global _ENC_CACHE
            _ENC_CACHE = None
            return result.rowcount
        except SQLAlchemyError as e:
            conn.rollback()
            raise e

def refresh_cache():
    global _ENC_CACHE
    _ENC_CACHE = None
    load_known_faces()