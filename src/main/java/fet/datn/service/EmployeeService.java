package fet.datn.service;

import fet.datn.repositories.entities.EmployeesEntity;
import fet.datn.request.UpdateInforRequest;

public interface EmployeeService {
    EmployeesEntity updateEmployeeInfor(Long userId, UpdateInforRequest requestBody);
}
