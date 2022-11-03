package fet.datn.service.impl;

import fet.datn.repositories.OtpRepository;
import fet.datn.repositories.OtpVerificationRepository;
import fet.datn.repositories.entities.OtpEntity;
import fet.datn.repositories.entities.OtpVerificationEntity;
import fet.datn.service.VerificationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import javax.transaction.Transactional;
import java.sql.Timestamp;

@Service
public class VerificationServiceImpl implements VerificationService {

    private static final Logger logger = LogManager.getLogger(VerificationServiceImpl.class);

    @Autowired
    private OtpVerificationRepository otpVerificationRepository;

    @Autowired
    private OtpRepository otpRepository;

    @Override
    @Transactional
    public void createOtpVerificationEntity(OtpEntity otpEntity, boolean isSuccessful) {
        Integer otpId = otpEntity.getId();
        logger.info("Creating OTP {} verification for [{}] otpId", isSuccessful ? "success" : "fail", otpId);
        OtpVerificationEntity otpVerificationEntity = this.buildOtpVerificationEntity(otpId, isSuccessful);
        otpVerificationRepository.save(otpVerificationEntity);
        logger.info("Created OTP verification for [{}] otpId", otpId);

        if (isSuccessful) {
            otpEntity.setIsSuccessVerified(true);
            logger.info("Mark the [{}] otpId is successfully verified", otpEntity.getId());
            otpRepository.save(otpEntity);
        }
    }

    @Override
    public boolean isOtpCodeMatch(OtpEntity otpEntity, String otpCode) {
        return otpCode.equals(otpEntity.getOtpCode());
    }

    @Override
    public boolean isOtpExpired(OtpEntity otpEntity) {
        Timestamp now = new Timestamp(System.currentTimeMillis());
        return otpEntity.getExpiredTimestamp().before(now);
    }

    @Override
    public boolean isOtpVerified(OtpEntity otpEntity) {
        return otpEntity.getIsSuccessVerified();
    }

    private OtpVerificationEntity buildOtpVerificationEntity(Integer otpId, boolean isSuccessful) {
        OtpVerificationEntity otpVerificationEntity = new OtpVerificationEntity();
        otpVerificationEntity.setOtpId(otpId);
        otpVerificationEntity.setIsSuccess(isSuccessful);
        otpVerificationEntity.setVerifiedTimestamp(new Timestamp(System.currentTimeMillis()));

        return otpVerificationEntity;
    }
}
