## Tổng quan
- `dataset/` : mỗi subfolder là 1 người, chứa ảnh mẫu.
- `encode_faces.py` : script để tạo encodings từ ảnh (hoặc push encodings vào MongoDB khi dùng `--use-mongo`).
- `api.py` : FastAPI service để nhận diện (endpoint `/recognize`), thêm người (`/persons`) và reindex (`/reindex`).
- `db.py` : helper kết nối MongoDB và đọc/ghi encodings.
- `search_index.py` : ANN index (FAISS/Annoy) với fallback brute-force.

## Yêu cầu (Dependencies)

Đặt working directory vào `app/` và cài:

```bash
pip install -r requirements.txt
```

Gợi ý cài thêm (tùy lựa chọn, cải thiện hiệu năng):

- FAISS (khuyến nghị cho dataset lớn):
    - Conda: `conda install -c pytorch faiss-cpu` hoặc `pip install faiss-cpu` (tùy platform)
- Annoy (lightweight alternative): `pip install annoy`

Lưu ý: `face_recognition` yêu cầu `dlib` — nếu gặp lỗi khi pip install, dùng hướng dẫn platform-specific (ví dụ cài build tools, cmake, hoặc dùng conda).

## Cấu hình MongoDB

Tạo file `.env` trong `app/` (ví dụ đã có sẵn) với:

```
MONGO_URI=mongodb://localhost:27017
MONGO_DB=face_recognition
MONGO_COLL=encodings
```

`db.py` đọc các biến này để kết nối.

## Encode và lưu vectors vào MongoDB

Ví dụ: encode dataset và push vectors vào MongoDB:

```bash
python encode_faces.py --dataset dataset --use-mongo --mongo-uri mongodb://localhost:27017 --mongo-db face_recognition --mongo-coll encodings
```

Nếu muốn xuất luôn `encodings.pickle` (tương thích script cũ):

```bash
python encode_faces.py --dataset dataset --use-mongo --encodings encodings.pickle
```

## Chạy API nhận diện

Chạy server:

```bash
uvicorn api:app --host 0.0.0.0 --port 8000
```

Endpoints quan trọng:

- `POST /recognize` : upload ảnh để nhận diện.
    - Form: `file` (image). Query params: `detection_method` (`cnn` hoặc `hog`), `tolerance` (float, mặc định `0.6`).
    - Response: `{"faces": [{"box": [top,right,bottom,left], "name": "...", "distance": <float|null>}...]}`

- `POST /persons` : thêm 1 người mới vào DB.
    - Form fields: `name` (string), `files` (một hoặc nhiều file ảnh). Ví dụ:

```bash
curl -X POST "http://localhost:8000/persons" \
    -F "name=cuong" \
    -F "files=@/path/to/img1.jpg" \
    -F "files=@/path/to/img2.jpg"
```

    - Endpoint sẽ trích xuất face encodings từ ảnh, lưu lên MongoDB (`{name: ..., encodings: [...]}`) và rebuild index.

- `POST /reindex` : buộc rebuild ANN index từ MongoDB (useful after bulk inserts).


## Lưu ý khi thu thập dữ liệu

- Mỗi ảnh trong `dataset/<person>/` nên chứa 1 khuôn mặt, chất lượng tốt, nhiều điều kiện (góc, ánh sáng).
- Nếu nhiều người trùng tên, hãy thêm `ID` vào tên thư mục (ví dụ: `cuong_001`).
