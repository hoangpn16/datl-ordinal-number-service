package fet.datn.controller.admin;

import fet.datn.factory.ResponseFactory;
import fet.datn.interceptor.Payload;
import fet.datn.repositories.entities.EmployeesEntity;
import fet.datn.repositories.entities.TokenEntity;
import fet.datn.request.LoginRequest;
import fet.datn.request.RegisterRequest;
import fet.datn.request.UpdateInforRequest;
import fet.datn.service.AdminService;
import fet.datn.service.EmployeeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/admin")
public class AdminController {
    private static final Logger logger = LoggerFactory.getLogger(AdminController.class);

    @Autowired
    private AdminService service;

    @Autowired
    private EmployeeService employeeService;


    @PostMapping(value = "/login")
    public ResponseEntity login(@RequestBody LoginRequest requestBody) {
        logger.info("Username [{}] login", requestBody.getUsername());
        TokenEntity data = service.login(requestBody);
        return ResponseFactory.success(data, TokenEntity.class);
    }

    @PostMapping(value = "/register")
    public ResponseEntity register(@RequestBody RegisterRequest requestBody) {
        logger.info("Register request with body [{}]", requestBody.toString());
        EmployeesEntity data = service.register(requestBody);
        return ResponseFactory.success(data, EmployeesEntity.class);
    }

    @PutMapping(value = "/update")
    public ResponseEntity updateInfo(@RequestAttribute Payload payload,
                                     @RequestBody UpdateInforRequest requestBody) {
        logger.info("Account id [{}] update infor with body [{}]", payload.getUserId(), requestBody);
        EmployeesEntity data = employeeService.updateEmployeeInfor(payload.getUserId(), requestBody);
        return ResponseFactory.success(data, EmployeesEntity.class);
    }
}
