package fet.datn.service;

import fet.datn.repositories.entities.EmployeesEntity;
import fet.datn.repositories.entities.OtpEntity;
import fet.datn.repositories.entities.TokenEntity;

public interface TokenService {
    String generateOtpReferenceId();

    String generateCode(String validCharacters, int length);

    TokenEntity genTokenCustomer(OtpEntity otpEntity);

    TokenEntity genTokenAdmin(EmployeesEntity em);
}
