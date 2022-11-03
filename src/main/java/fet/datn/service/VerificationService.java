package fet.datn.service;

import fet.datn.repositories.entities.OtpEntity;

import javax.transaction.Transactional;

public interface VerificationService {
    @Transactional
    void createOtpVerificationEntity(OtpEntity otpEntity, boolean isSuccessful);

    boolean isOtpCodeMatch(OtpEntity otpEntity, String otpCode);

    boolean isOtpExpired(OtpEntity otpEntity);

    boolean isOtpVerified(OtpEntity otpEntity);
}
