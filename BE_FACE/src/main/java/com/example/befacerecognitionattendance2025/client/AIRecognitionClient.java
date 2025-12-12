package com.example.befacerecognitionattendance2025.client;

import com.example.befacerecognitionattendance2025.domain.dto.response.FaceSearchResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

@Component
@Slf4j
public class AIRecognitionClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;

    public AIRecognitionClient(@Value("${face-recognition.service.url}") String baseUrl) {
        this.baseUrl = baseUrl;
        // RestTemplate có sẵn trong Spring Web, không cần cài thêm WebFlux
        this.restTemplate = new RestTemplate();
    }

    /**
     * HÀM 1: Search Faces (Tìm kiếm khuôn mặt)
     */
    public String identifyEmployeeFromImage(MultipartFile image) {
        try {
            // 1. Tạo Headers (Quan trọng: Multipart)
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            // 2. Tạo Body
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            // 3. Xử lý File (Override getFilename là BẮT BUỘC để Python nhận diện)
            ByteArrayResource fileResource = new ByteArrayResource(image.getBytes()) {
                @Override
                public String getFilename() {
                    return image.getOriginalFilename() != null ? image.getOriginalFilename() : "search.jpg";
                }
            };
            body.add("file", fileResource);

            // 4. Gói Header + Body vào HttpEntity
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // 5. Gửi Request
            ResponseEntity<FaceSearchResponse> response = restTemplate.postForEntity(
                    baseUrl + "/search_faces/",
                    requestEntity,
                    FaceSearchResponse.class
            );

            // 6. Xử lý kết quả
            if (response.getBody() != null && "success".equals(response.getBody().getStatus())) {
                log.info("AI nhận diện thành công: ID={}", response.getBody().getEmployeeId());
                return response.getBody().getEmployeeId();
            }
            return null;

        } catch (Exception e) {
            log.error("Lỗi search face: {}", e.getMessage());
            return null;
        }
    }

    /**
     * HÀM 2: Register Face (Đăng ký khuôn mặt) - Đã fix lỗi 422
     */
    public boolean registerFace(String employeeId, MultipartFile image) {
        if (employeeId == null || employeeId.trim().isEmpty()) return false;

        try {
            // 1. Setup Headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);
            // RestTemplate sẽ TỰ ĐỘNG thêm boundary chuẩn, không lo bị lỗi boundary như WebClient

            // 2. Setup Body
            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();

            // a. Thêm Employee ID (Text)
            body.add("employee_id", employeeId);

            // b. Thêm File (Binary) - Bắt buộc override getFilename
            ByteArrayResource fileResource = new ByteArrayResource(image.getBytes()) {
                @Override
                public String getFilename() {
                    // Python FastAPI cần filename này để biết đây là file upload
                    return image.getOriginalFilename() != null ? image.getOriginalFilename() : "register.jpg";
                }
            };
            body.add("file", fileResource);

            // 3. Tạo Request Entity
            HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);

            // 4. Gửi Request POST
            log.info("Đang gửi request add_face đến AI: ID={}", employeeId);

            ResponseEntity<FaceSearchResponse> response = restTemplate.postForEntity(
                    baseUrl + "/add_face/",
                    requestEntity,
                    FaceSearchResponse.class
            );

            // 5. Check kết quả
            if (response.getBody() != null && "success".equals(response.getBody().getStatus())) {
                log.info("AI Service: Đăng ký thành công.");
                return true;
            } else {
                log.error("AI Service trả về lỗi: {}", response.getBody());
                return false;
            }

        } catch (HttpClientErrorException e) {
            // Bắt lỗi HTTP 4xx (400, 422...) và in ra body lỗi
            log.error("AI Service từ chối (HTTP {}). Body: {}", e.getStatusCode(), e.getResponseBodyAsString());
            throw new RuntimeException("AI Service từ chối: " + e.getResponseBodyAsString());
        } catch (Exception e) {
            log.error("Lỗi hệ thống khi gọi API add_face: ", e);
            throw new RuntimeException("Lỗi kết nối AI Service: " + e.getMessage());
        }
    }

    /**
     * HÀM 3: Reload Database (Làm mới Cache khuôn mặt)
     * Gọi endpoint này khi bạn vừa thêm dữ liệu vào MySQL và muốn AI cập nhật ngay lập tức.
     */
    public void reloadDatabase() {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<Void> requestEntity = new HttpEntity<>(headers);

            log.info("Đang yêu cầu AI reload lại dữ liệu từ MySQL...");
            ResponseEntity<FaceSearchResponse> response = restTemplate.postForEntity(
                    baseUrl + "/reload_db/",
                    requestEntity,
                    FaceSearchResponse.class
            );

            if (response.getBody() != null && "success".equals(response.getBody().getStatus())) {
                log.info("AI Reload DB thành công!");
            } else {
                log.warn("AI Reload DB thất bại: Unknown");
            }

        } catch (Exception e) {
            log.error("Lỗi khi gọi API reload_db: ", e);
        }
    }
}