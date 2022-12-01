package fet.datn.service;

import fet.datn.interceptor.Payload;
import fet.datn.repositories.entities.EmployeesEntity;
import fet.datn.repositories.entities.TokenEntity;
import fet.datn.request.LoginRequest;
import fet.datn.request.RegisterRequest;

public interface AdminService {
    TokenEntity login(LoginRequest requestBody);

    EmployeesEntity register(RegisterRequest requestBody, Payload payload);

    EmployeesEntity getProfile(Payload payload);
}
