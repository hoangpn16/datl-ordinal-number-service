package fet.datn.service.impl;

import fet.datn.exceptions.AppException;
import fet.datn.exceptions.ErrorCode;
import fet.datn.interceptor.Payload;
import fet.datn.repositories.EmployeesRepository;
import fet.datn.repositories.entities.EmployeesEntity;
import fet.datn.request.ChangePassword;
import fet.datn.request.UpdateInforRequest;

import fet.datn.service.EmployeeService;
import fet.datn.utils.AppUtils;
import fet.datn.utils.DateTimeUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class EmployeeServiceImpl implements EmployeeService {
    private static final Logger logger = LoggerFactory.getLogger(EmployeeServiceImpl.class);

    @Autowired
    private EmployeesRepository employeesRepository;

    @Override
    public EmployeesEntity updateEmployeeInfor(Long userId, UpdateInforRequest requestBody) {
        EmployeesEntity entity = employeesRepository.findOneByUserId(userId);
        if (entity == null) {
            logger.info("Not found userId [{}]", userId);
            throw new AppException(ErrorCode.ENTITY_NOT_EXISTS);
        }
        AppUtils.copyPropertiesIgnoreNull(requestBody, entity);
        entity.setModifiedTime(DateTimeUtils.getDateTimeNow());
        return employeesRepository.save(entity);
    }

    @Override
    public EmployeesEntity changePassword(Payload payload, ChangePassword requestBody) {
        EmployeesEntity entity = employeesRepository.findOneByUserName(requestBody.getUsername());
        if (entity == null) {
            throw new AppException(ErrorCode.ENTITY_NOT_EXISTS);
        }
        if(!payload.getToken().contains("Admin")){
            if(!payload.getUserId().equals(entity.getUserId())){
                throw new AppException(ErrorCode.NOT_PERMISSION);
            }
        }
        if (!requestBody.getPassword().equals(requestBody.getPasswordAgain())) {
            throw new AppException(ErrorCode.PASSAGAIN_INVALID);
        }
        passwordVerify(requestBody.getPassword());
        entity.setPassword(new BCryptPasswordEncoder().encode(requestBody.getPassword()));
        return employeesRepository.save(entity);
    }

    private void passwordVerify(String password) {
        if (StringUtils.isBlank(password)) {
            logger.error("Missing password when create customer identity");
            throw new AppException(ErrorCode.INVALID_PASSWORD);
        }
        isValidPassword(password);
    }

    private static boolean isValidPassword(String password) {
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
