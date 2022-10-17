package fet.datn.controller.customer;

import fet.datn.config.OtpConfiguration;
import fet.datn.exceptions.AppException;
import fet.datn.exceptions.ErrorCode;
import fet.datn.factory.ResponseFactory;
import fet.datn.repositories.entities.OtpEntity;
import fet.datn.repositories.entities.TokenEntity;
import fet.datn.request.VerificationRequest;
import fet.datn.service.OtpService;
import fet.datn.service.TokenService;
import fet.datn.service.VerificationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import java.util.List;

@RestController
public class VerificationController {

    private static final Logger logger = LogManager.getLogger(VerificationController.class);

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private OtpService otpService;

    @Autowired
    private OtpConfiguration otpConfiguration;

    @Autowired
    private TokenService tokenService;

    @RequestMapping(value = "/otp/verify", method = RequestMethod.POST)
    public ResponseEntity verifyOtpCode(@RequestBody VerificationRequest verificationRequest) {
        logger.info("========== Start verifying OTP ==========");
        logger.info("Verifying OTP with [{}] otpReferenceId", verificationRequest.getOtpReferenceId());

        ResponseEntity responseEntity = buildOtpVerificationResponse(verificationRequest);

        logger.info("========== End verifying OTP ==========");
        return responseEntity;
    }

    private ResponseEntity buildOtpVerificationResponse(VerificationRequest verificationRequest) {
        String otpReferenceId = verificationRequest.getOtpReferenceId();
        String otpCode = verificationRequest.getOtpCode();

        if (otpReferenceId == null || otpCode == null) {
            logger.error("Otp_reference_id and otp_code must be specified");
            throw new AppException(ErrorCode.OTP_VERIFICATION_FAIL);
        }

        List<OtpEntity> otpEntities = otpService.getNonDeletedOtpEntity(otpReferenceId);

        if (otpEntities.isEmpty()) {
            logger.error("Cannot find OTP with [{}] otpReferenceId", otpReferenceId);
            throw new AppException(ErrorCode.OTP_REFERENCE_ID_NOT_FOUND);
        }

        if (otpEntities.size() > 1) {
            logger.error("Fatal error. There're more than 1 active OTP for one [{}] otpReferenceId", otpReferenceId);
            throw new AppException(ErrorCode.GENERAL_ERROR);
        }

        OtpEntity otpEntity = otpEntities.get(0);
        if (verificationService.isOtpVerified(otpEntity)) {
            logger.error("OTP with [{}] otpReferenceId has been verified already", otpReferenceId);
            throw new AppException(ErrorCode.OTP_USED);
        }

/*        Integer verificationRemaining = otpConfiguration.getVerificationLimit() - otpEntity.getOtpVerificationEntities().size();
        if (verificationRemaining <= 0) {
            logger.error("OTP with [{}] otpReferenceId has reached maximum failed verifications", otpReferenceId);
            throw new OtpException(ErrorCode.MAXIMUM_OTP_VERIFICATION_REACHED);
        }*/

        if (verificationService.isOtpExpired(otpEntity)) {
            logger.error("OTP with [{}] otpReferenceId has expired", otpReferenceId);
            verificationService.createOtpVerificationEntity(otpEntity, false);
            throw new AppException(ErrorCode.OTP_EXPIRED);
        }

        if (!verificationService.isOtpCodeMatch(otpEntity, otpCode)) {
            logger.error("OTP code does not match");
            verificationService.createOtpVerificationEntity(otpEntity, false);
            throw new AppException(ErrorCode.OTP_VERIFICATION_FAIL);
        }

        logger.info("Verify OTP with [{}] otpReferenceId successful", otpReferenceId);
        verificationService.createOtpVerificationEntity(otpEntity, true);
        TokenEntity token = tokenService.genTokenCustomer(otpEntity);
        return ResponseFactory.success(token, TokenEntity.class);
    }
}