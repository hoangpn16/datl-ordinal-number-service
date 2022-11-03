package fet.datn.controller.admin;

import fet.datn.exceptions.AppException;
import fet.datn.exceptions.ErrorCode;
import fet.datn.factory.ResponseFactory;
import fet.datn.interceptor.Payload;
import fet.datn.repositories.EmployeesRepository;
import fet.datn.repositories.entities.EmployeesEntity;
import fet.datn.repositories.entities.TokenEntity;
import fet.datn.request.LoginRequest;
import fet.datn.request.RegisterRequest;
import fet.datn.request.UpdateInforRequest;
import fet.datn.request.UploadImage;
import fet.datn.service.AdminService;
import fet.datn.service.EmployeeService;
import fet.datn.service.FileStorageService;
import fet.datn.service.impl.AdminServiceImpl;
import fet.datn.service.impl.EmployeeServiceImpl;
import fet.datn.service.impl.FileStorageServiceImpl;
import fet.datn.utils.Definition;
import io.swagger.annotations.ApiOperation;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;

@RestController
@RequestMapping(value = "/admin")
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private AdminService service;

    @Autowired
    private EmployeeService employeeServiceImpl;

    @Autowired
    private FileStorageService fileStorageService;

    @Autowired
    private EmployeesRepository employeesRepository;

    @PostMapping(value = "/login")
    @ApiOperation(value = "API đăng nhập cho CMS")
    public ResponseEntity login(@RequestBody LoginRequest requestBody) {
        logger.info("Username [{}] login", requestBody.getUsername());
        TokenEntity data = service.login(requestBody);
        return ResponseFactory.success(data, TokenEntity.class);
    }

    @PostMapping(value = "/register")
    @ApiOperation(value = "API admin tạo tài khoản cho nhân viên")
    public ResponseEntity register(@RequestAttribute(name = Definition.PAYLOAD, required = false) Payload payload, @RequestBody RegisterRequest requestBody) {
        if (payload == null) {
            logger.info("Mã truy cập không hợp lệ");
            throw new AppException(ErrorCode.TOKEN_NOT_FOUND);
        }
        logger.info("Register request with body [{}]", requestBody.toString());
        EmployeesEntity data = service.register(requestBody);
        return ResponseFactory.success(data, EmployeesEntity.class);
    }

    @PutMapping(value = "/update")
    @ApiOperation(value = "API cập nhật thông tin cá nhân cho các tài khoản CMS")
    public ResponseEntity updateInfo(@RequestAttribute(name = Definition.PAYLOAD, required = false) Payload payload,
                                     @RequestBody UpdateInforRequest requestBody) {
        if (payload == null) {
            logger.info("Mã truy cập không hợp lệ");
            throw new AppException(ErrorCode.TOKEN_NOT_FOUND);
        }
        logger.info("Account id [{}] update infor with body [{}]", payload.getUserId(), requestBody);
        EmployeesEntity data = employeeServiceImpl.updateEmployeeInfor(payload.getUserId(), requestBody);
        return ResponseFactory.success(data, EmployeesEntity.class);
    }

    @PutMapping("/avatar")
    @ApiOperation(value = "API cập nhật avatar")
    public ResponseEntity uploadAvatar(@RequestBody UploadImage body, @RequestAttribute(name = Definition.PAYLOAD, required = false) Payload payload) {
        if (payload == null) {
            logger.info("Mã truy cập không hợp lệ");
            throw new AppException(ErrorCode.TOKEN_NOT_FOUND);
        }
        logger.info("Update avatar shopId [{}], imageName [{}]", payload.getUserId(), body.getImageName());
        String fileName = fileStorageService.saveImage(payload.getUserId(), body);
        logger.info("Update avatar shopId [{}], filename [{}]", payload.getUserId(), fileName);
        EmployeesEntity entity = employeesRepository.findOneByUserId(payload.getUserId());

        //Delete old avatar on server
        if (!Strings.isBlank(entity.getAvatar())) {
            fileStorageService.deleteFileByPath(payload.getUserId(), entity.getAvatar());
        }
        entity.setAvatar(fileName);
        entity = employeesRepository.save(entity);
        return ResponseFactory.success(entity, EmployeesEntity.class);
    }

    @GetMapping("/avatar")
    @ApiOperation(value = "API load avatar")
    public ResponseEntity viewAvatar(@RequestAttribute(name = Definition.PAYLOAD, required = false) Payload payload, HttpServletRequest request) {
        if (payload == null) {
            logger.info("Mã truy cập không hợp lệ");
            throw new AppException(ErrorCode.TOKEN_NOT_FOUND);
        }
        EmployeesEntity entity = employeesRepository.findOneByUserId(payload.getUserId());
        Resource resource = null;
        try {
            resource = fileStorageService.loadImage(payload.getUserId(), entity.getAvatar());
        } catch (Exception e) {
            e.printStackTrace();
            logger.info("Khong tim thay avatar cua userId {} ", payload.getUserId());
            throw new AppException(ErrorCode.ENTITY_NOT_EXISTS);
        }

        String contentType = null;
        try {
            contentType = request.getServletContext().getMimeType(resource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("Could not determine file type.");
        }

        if (contentType == null) {
            contentType = "application/octet-stream";
        }

        return ResponseEntity.ok().contentType(MediaType.parseMediaType(contentType))
//					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
                .body(resource);

    }
}
