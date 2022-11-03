package fet.datn.service;

import fet.datn.repositories.entities.OtpEntity;
import fet.datn.request.OtpGenerationRequest;

import java.util.List;

public interface OtpService {
    OtpEntity generateOtp(OtpGenerationRequest request);

    OtpEntity regenerateOtp(OtpEntity otpEntity);

    void deleteOtp(String otpReferenceId);

    List<OtpEntity> getOtpDetailsByOtpReferenceId(String otpReferenceId);

    List<OtpEntity> getNonDeletedOtpEntity(String otpReferenceId);
}
