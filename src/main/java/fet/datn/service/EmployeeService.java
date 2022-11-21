package fet.datn.service;

import fet.datn.interceptor.Payload;
import fet.datn.repositories.entities.EmployeesEntity;
import fet.datn.request.ChangePassword;
import fet.datn.request.UpdateInforRequest;

public interface EmployeeService {
    EmployeesEntity updateEmployeeInfor(Long userId, UpdateInforRequest requestBody);

    EmployeesEntity changePassword(Payload payload, ChangePassword requestBody);
}
