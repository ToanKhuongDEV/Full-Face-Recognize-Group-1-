package com.example.befacerecognitionattendance2025.util;



import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.example.befacerecognitionattendance2025.constant.ErrorMessage;
import com.example.befacerecognitionattendance2025.exception.UploadFileException;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Component
public class UploadFileUtil {

    private final Cloudinary cloudinary;

    public String uploadImage(MultipartFile file) {
        try {
            Map<?, ?> result = cloudinary.uploader().upload(
                    file.getBytes(),
                    ObjectUtils.asMap("resource_type", "image")
            );
            log.info("Cloudinary config: {}", cloudinary.config);
            return result.get("secure_url").toString();
        } catch (IOException e) {
            throw new UploadFileException("Upload image failed!");
        }
    }

    public void destroyImage(String imageUrl) {
        String publicId = extractPublicId(imageUrl);
        try {
            Map<?, ?> result = cloudinary.uploader().destroy(publicId, ObjectUtils.emptyMap());
            log.info("Destroyed image with public_id = {}, result = {}", publicId, result);
        } catch (IOException e) {
            throw new UploadFileException("Remove image failed!");
        }
    }


    private String extractPublicId(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            String path = url.getPath(); // /your-cloud/image/upload/.../filename.jpg
            String[] segments = path.split("/");
            String filenameWithExt = segments[segments.length - 1];

            // Tách phần tên không có đuôi mở rộng (nếu có)
            String filename = filenameWithExt.contains(".")
                    ? filenameWithExt.substring(0, filenameWithExt.lastIndexOf('.'))
                    : filenameWithExt;

            // Bắt đầu ghép từ phần sau "upload/"
            StringBuilder publicId = new StringBuilder();
            boolean foundUpload = false;
            for (int i = 1; i < segments.length - 1; i++) {
                if (foundUpload) {
                    publicId.append(segments[i]).append("/");
                }
                if (segments[i].equals("upload")) {
                    foundUpload = true;
                }
            }
            publicId.append(filename);
            return publicId.toString();
        } catch (MalformedURLException e) {
            throw new UploadFileException("Invalid Cloudinary image URL");
        }
    }

    public static void validateIsImage(MultipartFile file) {
        String contentType = file.getContentType();
        log.info("Đang validate file: {}, Content-Type: {}", file.getOriginalFilename(), contentType);
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new UploadFileException(ErrorMessage.ERR_INVALID_IMAGE_FILE);
        }
    }
}
