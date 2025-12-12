from imutils import paths
import argparse
import pickle
import cv2
import os
import face_recognition
from src.db import db  


ap =argparse.ArgumentParser()
ap.add_argument("-i", "--dataset", required=True, help="path to the directory of faces and images")
# các encodings và names được lưu vào file (pickle) — optional when using SQL DB
ap.add_argument("-e", "--encodings", required=False, help="path to the serialized file of facial encodings (pickle)")
# trước khi encode face thì phải detect nó (đây là bước luôn phải làm trong face recognition) - chọn method để detect faces
ap.add_argument("-d", "--detection_method", type=str, default="cnn", help="face detector to use: cnn or hog")

# DB options: use SQL (MySQL) via SQLAlchemy
ap.add_argument("--use-sql", action="store_true", dest="use_sql", help="store/retrieve encodings in SQL DB (MySQL)")
ap.add_argument("--db-uri", type=str, help="SQLAlchemy database URI (e.g. mysql+pymysql://user:pass@host/db)")

# face_data table options
ap.add_argument("--employee-id", type=str, help="employee ID to store in face_data table (optional)")
ap.add_argument("--save-to-face-data", action="store_true", dest="save_to_face_data", help="save encodings to face_data table in addition to users/encodings")

args = vars(ap.parse_args())

# support SQL mode flag
use_sql = args.get("use_sql")

# lấy paths của images trong dataset
print("[INFO] quantifying faces...")
imagePaths = list(paths.list_images(args["dataset"]))

# khởi tạo list chứa known encodings và known names (để các test images so sánh)
# chứa encodings và tên của các images trong dataset
knownEncodings = []
knownNames = []

# nếu không dùng SQL (MySQL) thì scan dataset và build pickle lists
if not use_sql:
    # duyệt qua các image paths
    for (i, imagePath) in enumerate(imagePaths):
        # lấy tên người từ imagepath
        print("[INFO] processing image {}/{}".format(i+1, len(imagePaths)))
        name = imagePath.split(os.path.sep)[-2]

        # load image bằng OpenCV và chuyển từ BGR to RGB (dlib cần)
        image = cv2.imread(imagePath)
        rgb = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)

        # Đối với từng image phải thực hiện detect face, trích xuất face ROI và chuyển về encoding
        # trả về array of bboxes of faces, dùng dlib như bài face detection đó
        # model="cnn" chính xác hơn nhưng chậm hơn, "hog" nhanh hơn nhưng kém chính xác hơn
        boxes = face_recognition.face_locations(rgb, model=args["detection_method"])    

        # tính the facial embedding for the face
        # sẽ tính encodings cho mỗi face phát hiện được trong ảnh (có thể có nhiều faces)
        # Để lý tưởng trong ảnh nên chỉ có một mặt người của mình thôi
        encodings = face_recognition.face_encodings(rgb, boxes)  

        # duyệt qua các encodings
        # Trong ảnh có thể có nhiều faces, mà ở đây chỉ có 1 tên
        # Nên chắc chắn trong dataset ban đầu ảnh chỉ có một mặt người thôi nhé
        # Lý tưởng nhất mỗi ảnh có 1 face và có 1 encoding thôi
        for encoding in encodings:
            # lưu encoding và name vào lists bên trên
            knownEncodings.append(encoding)
            knownNames.append(name)

# ---- New: support incremental updates using SQL DB or an existing pickle file ----
if use_sql:
    # allow overriding DATABASE_URI via CLI
    if args.get("db_uri"):
        os.environ["DATABASE_URI"] = args.get("db_uri")

    # initialize SQL tables
    db.init_db()

    # Process dataset images, but only add encodings for names not already in DB
    print("[INFO] scanning dataset for new people to encode (SQL)...")
    imagePaths = list(paths.list_images(args["dataset"]))

    # reduce DB queries: compute unique names once and query DB in batch
    unique_names = set([p.split(os.path.sep)[-2] for p in imagePaths])
    existing_users = db.get_existing_users(list(unique_names))

    save_to_face_data = args.get("save_to_face_data")
    employee_id = args.get("employee_id")

    for (i, imagePath) in enumerate(imagePaths):
        name = imagePath.split(os.path.sep)[-2]
        
        # Skip existing users ONLY if not saving to face_data
        # If --save-to-face-data is set, we still need to process images for face_data table
        if name in existing_users and not save_to_face_data:
            continue

        image = cv2.imread(imagePath)
        rgb = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
        boxes = face_recognition.face_locations(rgb, model=args["detection_method"])    
        encs = face_recognition.face_encodings(rgb, boxes)

        enc_list = [e.tolist() for e in encs]
        if len(enc_list) == 0:
            continue

        # create user and add encodings to users/encodings tables (only if new user)
        if name not in existing_users:
            uid = db.get_or_create_user(name)
            db.add_encodings_for_user(uid, enc_list)
            print(f"[INFO] added {len(enc_list)} encodings for new user '{name}' to users/encodings tables")
        
        # also save to face_data table if requested (always execute if flag is set)
        if save_to_face_data and employee_id:
            db.add_face_data_for_employee(employee_id, enc_list)
            print(f"[INFO] saved {len(enc_list)} encodings for employee {employee_id} to face_data table")
        elif save_to_face_data and not employee_id:
            # if --save-to-face-data without --employee-id, use person name as employee_id
            db.add_face_data_for_employee(name, enc_list)
            print(f"[INFO] saved {len(enc_list)} encodings for person {name} to face_data table")

    print("[INFO] finished writing new encodings to SQL DB")
    # optionally export pickle for backward compatibility
    if args.get("encodings"):
        print("[INFO] exporting combined encodings from SQL DB to pickle {}".format(args.get("encodings")))
        all_encs, all_names = db.load_encodings_from_db()
        combo_encs = [e.tolist() for e in all_encs]
        data = {"encodings": combo_encs, "names": all_names}
        with open(args.get("encodings"), "wb") as f:
            f.write(pickle.dumps(data))

    raise SystemExit(0)

else:
    # not using SQL (create a pickle file locally):
    # If user provided a pickle path but the file does NOT exist -> write full encodings gathered above
    if args.get("encodings") and not os.path.exists(args.get("encodings")):
        print("[INFO] serializing encodings to new pickle {}".format(args.get("encodings")))
        data = {"encodings": knownEncodings, "names": knownNames}
        with open(args.get("encodings"), "wb") as f:
            f.write(pickle.dumps(data))
        print("[INFO] finished writing {} encodings".format(len(knownEncodings)))
        raise SystemExit(0)

    # If a pickle file exists, only add new people not already encoded in that file
    if args.get("encodings") and os.path.exists(args.get("encodings")):
        try:
            print("[INFO] loading existing encodings from pickle {}".format(args.get("encodings")))
            existing = pickle.loads(open(args.get("encodings"), "rb").read())
            existing_names = set(existing.get("names", []))
        except Exception:
            existing = {"encodings": [], "names": []}
            existing_names = set()

        # Re-scan dataset but only process names not in existing_names
        new_encs = []
        new_names = []
        imagePaths = list(paths.list_images(args["dataset"]))
        for (i, imagePath) in enumerate(imagePaths):
            name = imagePath.split(os.path.sep)[-2]
            if name in existing_names:
                continue

            print("[INFO] processing image {}/{} (new: {})".format(i+1, len(imagePaths), name))
            image = cv2.imread(imagePath)
            rgb = cv2.cvtColor(image, cv2.COLOR_BGR2RGB)
            boxes = face_recognition.face_locations(rgb, model=args["detection_method"])    
            encs = face_recognition.face_encodings(rgb, boxes)
            for enc in encs:
                new_encs.append(enc)
                new_names.append(name)

        # combine existing + new and write back to pickle
        combined_encs = existing.get("encodings", []) + new_encs
        combined_names = existing.get("names", []) + new_names
        data = {"encodings": combined_encs, "names": combined_names}

        if args.get("encodings"):
            print("[INFO] serializing combined encodings to pickle {}".format(args.get("encodings")))
            with open(args.get("encodings"), "wb") as f:
                f.write(pickle.dumps(data))

        print("[INFO] finished updating pickle with new people.")
        raise SystemExit(0)

# dump (lưu) the facial encodings + names vào ổ cứng
print("[INFO] serializing encodings...")
data = {"encodings": knownEncodings, "names": knownNames}

with open(args["encodings"], "wb") as f:
    f.write(pickle.dumps(data))


    







