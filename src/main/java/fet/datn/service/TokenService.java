package fet.datn.service;

import fet.datn.repositories.TokenDao;
import fet.datn.repositories.entities.EmployeesEntity;
import fet.datn.repositories.entities.OtpEntity;
import fet.datn.repositories.entities.TokenEntity;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.Date;
import java.util.Random;
import java.util.UUID;


@Service
public class TokenService {
    @Autowired
    private TokenDao tokenDao;

    @Value("${token.expired.time.in.day}")
    private Integer delta;

    @Value("${token.admin.expired.time.in.day}")
    private Integer deltaTimeAdmin;

    public String generateOtpReferenceId() {
        return UUID.randomUUID().toString();
    }

    public String generateCode(String validCharacters, int length) {
        StringBuilder builder = new StringBuilder();
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            int index = random.nextInt(validCharacters.length());
            builder.append(validCharacters.charAt(index));
        }
        return builder.toString();
    }

    public TokenEntity genTokenCustomer(OtpEntity otpEntity) {
        TokenEntity tokenEntity = new TokenEntity();
        tokenEntity.setToken("Customer-" + UUID.randomUUID().toString());
        tokenEntity.setUserId(otpEntity.getUserId());
        tokenEntity.setCreatedTime(new Date());
        Long now = System.currentTimeMillis();
        long deltaTime = 86400000l * delta;
        Timestamp expiredTime = new Timestamp(now + deltaTime);
        tokenEntity.setExpiredTime(expiredTime);

        return tokenDao.save(tokenEntity);
    }

    public TokenEntity genTokenAdmin(EmployeesEntity em) {
        TokenEntity tokenEntity = new TokenEntity();
        String token = "Admin-" + UUID.randomUUID().toString();
        Long now = System.currentTimeMillis();
        long deltaTime = 86400000l * deltaTimeAdmin;
        Timestamp expiredTime = new Timestamp(now + deltaTime);

        tokenEntity.setToken(token);
        tokenEntity.setUserId(em.getUserId());
        tokenEntity.setCreatedTime(new Date());
        tokenEntity.setExpiredTime(expiredTime);

        return tokenDao.save(tokenEntity);
    }

}
