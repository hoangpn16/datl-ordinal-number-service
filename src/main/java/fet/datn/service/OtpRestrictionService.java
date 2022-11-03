package fet.datn.service;

import fet.datn.repositories.entities.OtpRestrictionEntity;
import fet.datn.request.OtpGenerationRequest;

public interface OtpRestrictionService {
    void createOtpRestriction(OtpGenerationRequest request);

    OtpRestrictionEntity findOtpRestrictionByMobile(String mobile, String type);
}
