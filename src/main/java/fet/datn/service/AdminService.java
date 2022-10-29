package fet.datn.service;

import fet.datn.exceptions.AppException;
import fet.datn.exceptions.ErrorCode;
import fet.datn.interceptor.Payload;
import fet.datn.repositories.EmployeesRepository;
import fet.datn.repositories.entities.EmployeesEntity;
import fet.datn.repositories.entities.TokenEntity;
import fet.datn.request.LoginRequest;
import fet.datn.request.RegisterRequest;
import fet.datn.utils.DateTimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.security.crypto.bcrypt.BCrypt;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.sql.Timestamp;
import java.util.Date;
import java.util.UUID;

@Service
public class AdminService {
    private static final Logger logger = LoggerFactory.getLogger(AdminService.class);

    @Autowired
    private EmployeesRepository employeesDao;

    @Autowired
    private TokenService tokenService;

    public TokenEntity login(LoginRequest requestBody) {
        passwordVerify(requestBody.getPassword());

        EmployeesEntity em = employeesDao.findOneByUserName(requestBody.getUsername());
        if (em == null) {
            logger.error("Employee with [{}] username is not existed", requestBody.getUsername());
            throw new AppException(ErrorCode.ENTITY_NOT_EXISTS);
        }

        boolean check = BCrypt.checkpw(requestBody.getPassword(), em.getPassword());
        if (check) {
            return tokenService.genTokenAdmin(em);
        }
        throw new AppException(ErrorCode.INVALID_USER_PASS);
    }

    public EmployeesEntity register(RegisterRequest requestBody) {

        EmployeesEntity em = employeesDao.findOneByUserName(requestBody.getUsername());
        if (em != null) {
            logger.error("Employee with [{}] username is existed", requestBody.getUsername());
            throw new AppException(ErrorCode.ENTITY_EXISTED);
        }
        if (!requestBody.getPassword().equals(requestBody.getPasswordRepeat())) {
            logger.error("Password is not merge");
            throw new AppException(ErrorCode.PASSWORD_NOT_MERGE);
        }
        passwordVerify(requestBody.getPassword());

        em = new EmployeesEntity();
        em.setUserName(requestBody.getUsername());
        em.setPassword(new BCryptPasswordEncoder().encode(requestBody.getPassword()));
        em.setCreatedTime(DateTimeUtils.getDateTimeNow());
        em.setModifiedTime(DateTimeUtils.getDateTimeNow());
        return employeesDao.save(em);
    }


    private void passwordVerify(String password) {
        if (StringUtils.isBlank(password)) {
            logger.error("Missing password when create customer identity");
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }
        isValidPassword(password);
    }

    public static boolean isValidPassword(String password) {
        // Password Validation Rule
        // at least 1 letter
        // at least 1 number
        // at least 6 character length
        String pattern = "^[a-zA-Z0-9!@#$%^&*()-_=+\\[\\]\\{\\};:><]{6,75}$";

        if (password == null || !password.matches(pattern)) {
            logger.error("Password value [{}] is invalid pattern", password);
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }
        return true;
    }
}
