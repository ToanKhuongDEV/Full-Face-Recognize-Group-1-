import cv2

def detect_faces_hog_opencv(image_path):
    try:
        # Tải ảnh
        image = cv2.imread(image_path)
        if image is None:
            print(f"Lỗi: Không thể tải ảnh từ đường dẫn {image_path}")
            return

        gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

        try:
            import dlib
            print("Sử dụng dlib cho Nhận diện Khuôn mặt HOG/SVM...")

            detector = dlib.get_frontal_face_detector()

            faces = detector(gray, 1)

            print(f"Tìm thấy {len(faces)} khuôn mặt.")

            # Vẽ hộp giới hạn lên ảnh
            for face in faces:
                x1 = face.left()
                y1 = face.top()
                x2 = face.right()
                y2 = face.bottom()
                cv2.rectangle(image, (x1, y1), (x2, y2), (255, 0, 0), 2)

        except ImportError:
            print("Dlib không được cài đặt. Thử sử dụng Haar Cascade (cách cổ điển của OpenCV).")
            # --- Phương án dự phòng: Sử dụng Haar Cascade (Tách rời khỏi HOG) ---
            face_cascade = cv2.CascadeClassifier(cv2.data.haarcascades + 'haarcascade_frontalface_default.xml')

            # Phát hiện khuôn mặt
            faces = face_cascade.detectMultiScale(
                gray,
                scaleFactor=1.1,
                minNeighbors=5,
                minSize=(30, 30)
            )

            print(f"Tìm thấy {len(faces)} khuôn mặt.")

            # Vẽ hộp giới hạn lên ảnh
            for (x, y, w, h) in faces:
                cv2.rectangle(image, (x, y), (x + w, y + h), (0, 255, 0), 2)

        # Hiển thị kết quả
        cv2.imshow("Face Detection (HOG/SVM - Dlib/Haar)", image)
        cv2.waitKey(0)
        cv2.destroyAllWindows()

    except Exception as e:
        print(f"Đã xảy ra lỗi: {e}")
