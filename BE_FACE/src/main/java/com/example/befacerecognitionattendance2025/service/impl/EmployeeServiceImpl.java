package com.example.befacerecognitionattendance2025.service.impl;

import com.example.befacerecognitionattendance2025.client.AIRecognitionClient;
import com.example.befacerecognitionattendance2025.constant.ErrorMessage;
import com.example.befacerecognitionattendance2025.constant.Role;
import com.example.befacerecognitionattendance2025.domain.dto.request.ChangePasswordRequest;
import com.example.befacerecognitionattendance2025.domain.dto.request.CreateEmployeeRequest;
import com.example.befacerecognitionattendance2025.domain.dto.request.UpdateEmployeeRequest;
import com.example.befacerecognitionattendance2025.domain.dto.response.EmployeeResponse;
import com.example.befacerecognitionattendance2025.domain.entity.Department;
import com.example.befacerecognitionattendance2025.domain.entity.Employee;
import com.example.befacerecognitionattendance2025.domain.mapper.EmployeeMapper;
import com.example.befacerecognitionattendance2025.exception.DuplicateResourceException;
import com.example.befacerecognitionattendance2025.exception.InvalidException;
import com.example.befacerecognitionattendance2025.exception.NotFoundException;
import com.example.befacerecognitionattendance2025.exception.UnauthorizedException;
import com.example.befacerecognitionattendance2025.repository.DepartmentRepository;
import com.example.befacerecognitionattendance2025.repository.EmployeeRepository;
import com.example.befacerecognitionattendance2025.security.UserPrincipal;
import com.example.befacerecognitionattendance2025.service.AuthService;
import com.example.befacerecognitionattendance2025.service.EmployeeService;
import com.example.befacerecognitionattendance2025.util.UploadFileUtil;
import com.example.befacerecognitionattendance2025.util.ValidateUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final DepartmentRepository departmentRepository;
    private final EmployeeMapper employeeMapper;
    private final UploadFileUtil uploadFileUtil;
    private final PasswordEncoder passwordEncoder;
    private final AuthService authService;
    private final AIRecognitionClient aiRecognitionClient;

    @Override
    public EmployeeResponse createEmployee(CreateEmployeeRequest request, MultipartFile imageFile) {

        if (employeeRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException(ErrorMessage.Employee.ERR_EMAIL_EXISTS);
        }
        if (employeeRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new DuplicateResourceException(ErrorMessage.Employee.ERR_USERNAME_EXISTS);
        }

        ValidateUtil.validateAge(request.getDateBirth());
        ValidateUtil.validateCredentials(request.getUsername(), request.getPassword());
        Employee employee = employeeMapper.toEntity(request);
        Department department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Department.ERR_NOT_FOUND));
        employee.setDepartment(department);
        employee.setRole(Role.STAFF);
        employee.setPassword(passwordEncoder.encode(request.getPassword()));
        Integer maxCode = employeeRepository.findMaxEmployeeCode();
        employee.setEmployeeCode(maxCode + 1);

        Employee savedEmployee = employeeRepository.saveAndFlush(employee);

        // CHỈ CÒN LOGIC UPLOAD ẢNH LÊN CLOUDINARY (ĐỂ HIỂN THỊ AVATAR)
        if (imageFile != null && !imageFile.isEmpty()) {
            UploadFileUtil.validateIsImage(imageFile);
            try {
                String imageUrl = uploadFileUtil.uploadImage(imageFile);
                savedEmployee.setAvatar(imageUrl);
                // Lưu lại URL avatar vào DB
                employeeRepository.save(savedEmployee);
            } catch (Exception e) {
                log.error("Failed to upload avatar to Cloudinary: {}", e.getMessage());
            }
            // Đã xóa phần gọi aiRecognitionClient.registerFace ở đây
        }

        return employeeMapper.toResponse(savedEmployee);
    }


    @Override
    @Transactional
    public EmployeeResponse changePassword(ChangePasswordRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        UserPrincipal userPrincipal = (UserPrincipal) authentication.getPrincipal();

        var employee = employeeRepository.findById(userPrincipal.getId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Employee.ERR_NOT_FOUND));

        if (!passwordEncoder.matches(request.getOldPassword(), employee.getPassword())) {
            throw new UnauthorizedException(ErrorMessage.Auth.ERR_INCORRECT_CREDENTIALS);
        }

        if (!ValidateUtil.validatePassword(request.getNewPassword())) {
            throw new InvalidException(ErrorMessage.Validation.ERR_INVALID_PASSWORD);
        }
        employee.setPassword(passwordEncoder.encode(request.getNewPassword()));
        employeeRepository.save(employee);

        return employeeMapper.toResponse(employee);
    }

    @Override
    public EmployeeResponse createManager(CreateEmployeeRequest request, MultipartFile imageFile) {
        if (employeeRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new DuplicateResourceException(ErrorMessage.Employee.ERR_EMAIL_EXISTS);
        }
        if (employeeRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new DuplicateResourceException(ErrorMessage.Employee.ERR_USERNAME_EXISTS);
        }

        ValidateUtil.validateAge(request.getDateBirth());
        ValidateUtil.validateCredentials(request.getUsername(), request.getPassword());

        Employee manager = employeeMapper.toEntity(request);
        Department  department = departmentRepository.findById(request.getDepartmentId())
                .orElseThrow( () -> new NotFoundException(ErrorMessage.Department.ERR_NOT_FOUND));
        manager.setDepartment(department);
        manager.setRole(Role.MANAGER);

        manager.setPassword(passwordEncoder.encode(request.getPassword()));
        Integer maxCode = employeeRepository.findMaxEmployeeCode();
        manager.setEmployeeCode(maxCode + 1);

        Employee savedManager = employeeRepository.saveAndFlush(manager);

        if (imageFile != null && !imageFile.isEmpty()) {
            UploadFileUtil.validateIsImage(imageFile);
            try {
                String imageUrl = uploadFileUtil.uploadImage(imageFile);
                savedManager.setAvatar(imageUrl);
                employeeRepository.save(savedManager);
            } catch (Exception e) {
                log.error("Failed to upload avatar to Cloudinary: {}", e.getMessage());
            }
        }

        return employeeMapper.toResponse(savedManager);
    }


    @Override
    @Transactional
    public EmployeeResponse deleteEmployee(String id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Employee.ERR_NOT_FOUND));

        employeeRepository.delete(employee);
        return employeeMapper.toResponse(employee);
    }

    @Override
    @Transactional
    public EmployeeResponse updateEmployee(String id, UpdateEmployeeRequest request, MultipartFile file) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Employee.ERR_NOT_FOUND));

        if (request.getEmail() != null && !request.getEmail().equals(employee.getEmail())) {
            if (employeeRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new DuplicateResourceException(ErrorMessage.Employee.ERR_EMAIL_EXISTS);
            }
        }

        employeeMapper.updateEmployee(request,employee);
        if(request.getDepartmentId() != null){
            Department  department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow( () -> new NotFoundException(ErrorMessage.Department.ERR_NOT_FOUND));
            employee.setDepartment(department);
        }

        // CHỈ UPLOAD AVATAR, KHÔNG GỌI AI
        if (file != null && !file.isEmpty()) {
            UploadFileUtil.validateIsImage(file);
            try {
                String imageUrl = uploadFileUtil.uploadImage(file);
                employee.setAvatar(imageUrl);
            } catch (Exception e) {
                log.error("Cloudinary upload failed", e);
            }
            // Đã xóa khối try-catch gọi registerFace ở đây
        }

        employeeRepository.save(employee);
        return employeeMapper.toResponse(employee);
    }

    @Override
    @Transactional
    public EmployeeResponse updateMyProfile(UpdateEmployeeRequest request, MultipartFile file) {
        Employee employee = employeeRepository.findById(authService.getCurrentUserId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Employee.ERR_NOT_FOUND));

        if (request.getEmail() != null && !request.getEmail().equals(employee.getEmail())) {
            if (employeeRepository.findByEmail(request.getEmail()).isPresent()) {
                throw new DuplicateResourceException(ErrorMessage.Employee.ERR_EMAIL_EXISTS);
            }
        }

        employeeMapper.updateEmployee(request, employee);

        if (request.getDepartmentId() != null) {
            Department department = departmentRepository.findById(request.getDepartmentId())
                    .orElseThrow(() -> new NotFoundException(ErrorMessage.Department.ERR_NOT_FOUND));
            employee.setDepartment(department);
        }

        // CHỈ UPLOAD AVATAR, KHÔNG GỌI AI
        if (file != null && !file.isEmpty()) {
            UploadFileUtil.validateIsImage(file);
            try {
                String imageUrl = uploadFileUtil.uploadImage(file);
                employee.setAvatar(imageUrl);
            } catch (Exception e) {
                log.error("Cloudinary upload failed", e);
            }
            // Đã xóa khối try-catch gọi registerFace ở đây
        }

        employeeRepository.save(employee);
        return employeeMapper.toResponse(employee);
    }

    @Override
    public List<EmployeeResponse> getAllEmployee() {
        return employeeMapper.toResponseList(employeeRepository.findAll());
    }

    @Override
    public EmployeeResponse getEmployeeById(String id) {
        Employee employee = employeeRepository.findById(id)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Employee.ERR_NOT_FOUND));
        return employeeMapper.toResponse(employee);
    }

    @Override
    public EmployeeResponse getMe() {
        Employee employee = employeeRepository.findById(authService.getCurrentUserId())
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Employee.ERR_NOT_FOUND));
        return employeeMapper.toResponse(employee);
    }

    @Override
    @Transactional
    public void addFaceData(String employeeId, MultipartFile imageFile) {

        Employee employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new NotFoundException(ErrorMessage.Employee.ERR_NOT_FOUND));

        if (imageFile == null || imageFile.isEmpty()) {
            throw new InvalidException("File ảnh không được để trống");
        }
        UploadFileUtil.validateIsImage(imageFile);

        boolean aiSuccess;
        try {
            aiSuccess = aiRecognitionClient.registerFace(employee.getId(), imageFile);
        } catch (Exception e) {
            log.error("Lỗi kết nối đến AI Service cho nhân viên {}", employee.getEmployeeCode(), e);
            throw new RuntimeException("Lỗi kết nối đến hệ thống nhận diện khuôn mặt");
        }

        if (aiSuccess) {
            // QUAN TRỌNG: Thêm thành công thì reload DB ngay để AI nhận diện được luôn
            aiRecognitionClient.reloadDatabase();

            log.info("Đã thêm dữ liệu khuôn mặt mới cho nhân viên: {}", employee.getEmployeeCode());
            // Có thể cập nhật flag hasFaceData nếu trong DB có cột đó
            // employee.setHasFaceData(true);
            // employeeRepository.save(employee);
        } else {
            throw new InvalidException("Không thể trích xuất khuôn mặt từ ảnh này. Vui lòng chọn ảnh rõ nét hơn.");
        }
    }
}