package fet.datn.service.impl;

import fet.datn.exceptions.AppException;
import fet.datn.exceptions.ErrorCode;
import fet.datn.repositories.EmployeesRepository;
import fet.datn.repositories.entities.EmployeesEntity;
import fet.datn.request.UpdateInforRequest;

import fet.datn.service.EmployeeService;
import fet.datn.utils.AppUtils;
import fet.datn.utils.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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

}
