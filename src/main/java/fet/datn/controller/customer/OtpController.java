package fet.datn.controller.customer;

import fet.datn.config.OtpConfiguration;
import fet.datn.exceptions.AppException;
import fet.datn.exceptions.ErrorCode;
import fet.datn.factory.ResponseFactory;
import fet.datn.repositories.entities.OtpEntity;
import fet.datn.request.OtpGenerationRequest;
import fet.datn.request.OtpRegenerationRequest;
import fet.datn.response.OtpResponse;
import fet.datn.service.OtpService;
import fet.datn.service.SmsSenderService;
import fet.datn.service.VerificationService;
import fet.datn.utils.OtpTypeEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;


import java.sql.Timestamp;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@RestController
public class OtpController {

    private static final Logger logger = LogManager.getLogger(OtpController.class);

    @Autowired
    private OtpService otpService;

    @Autowired
    private OtpConfiguration otpConfiguration;

    @Autowired
    private VerificationService verificationService;

    @Autowired
    private SmsSenderService smsSenderService;

    @RequestMapping(value = "/otp/generate", method = RequestMethod.POST)
    public ResponseEntity generateOtp(@RequestBody OtpGenerationRequest request) throws Exception {
        logger.info("========== Start generate otp ==========");
        ResponseEntity responseEntity = buildOtpGenerateVerificationResponse(request);
        if (responseEntity == null) {
            //TODO: sending otp via notification => must change to SMS when run production
            OtpEntity otpEntity = otpService.generateOtp(request);
            OtpResponse response = new OtpResponse();
            response.setOtpReferenceId(otpEntity.getOtpReferenceId());
            response.setCreatedTimestamp(otpEntity.getCreatedTimestamp());
            response.setExpiredTimestamp(otpEntity.getExpiredTimestamp());
            responseEntity = ResponseFactory.success(response, OtpResponse.class);
            smsSenderService.sendSmsMessage(otpEntity);
        }
        logger.info("========== End generate otp ==========");
        return responseEntity;
    }

    @RequestMapping(value = "/otp/regenerate", method = RequestMethod.POST)
    public ResponseEntity regenerateOtp(@RequestBody OtpRegenerationRequest request) {
        logger.info("========== Start regenerate otp ==========");
        ResponseEntity responseEntity = buildRegenerateOtpResponse(request.getOtpReferenceId());
        logger.info("========== End regenerate otp ==========");
        return responseEntity;
    }


    private ResponseEntity buildRegenerateOtpResponse(String otpReferenceId) {
        if (otpReferenceId == null) {
            logger.error("The required field [otp_reference_id] was not sent");
            throw new AppException(ErrorCode.OTP_REFERENCE_ID_IS_REQUIRED);
        }

        List<OtpEntity> otpEntities = otpService.getOtpDetailsByOtpReferenceId(otpReferenceId);

        if (otpEntities.isEmpty()) {
            logger.error("The [{}] otpReferenceId is not existed", otpReferenceId);
            throw new AppException(ErrorCode.OTP_REFERENCE_ID_NOT_FOUND);
        }

        int totalEntitiesInTimeBox = countOtpEntitiesInTimeBox(otpEntities);
        int regenerationInTimeBoxRemain = otpConfiguration.getRegenerationLimit() - totalEntitiesInTimeBox;
        if (regenerationInTimeBoxRemain < 0) {
            logger.error("Regenerating OTP exceeds the limitation for [{}] otpReferenceId", otpReferenceId);
            throw new AppException(ErrorCode.MAXIMUM_OTP_GENERATION_REACHED);
        }

        OtpEntity otpEntity = getActiveOtpEntity(otpEntities);
        long currentTimeInMillis = System.currentTimeMillis();
        if (otpEntity.getCreatedTimestamp().getTime() + otpConfiguration.getRegenerationSuspendTimeInSecond() * 1000L > currentTimeInMillis) {
            logger.error("Can't regenerate an OTP right after creation");
            throw new AppException(ErrorCode.CANT_INSTANT_REGENERATE_OTP_AFTER_CREATE);
        }

        if (verificationService.isOtpVerified(otpEntity)) {
            logger.error("OTP was already used. Cannot regenerate OTP");
            throw new AppException(ErrorCode.OTP_USED);
        }

        /*if (verificationService.isOtpExpired(otpEntity)) {
            logger.error("OTP with [{}] otpReferenceId has expired", otpReferenceId);
            throw new AuthenException(AuthenErrorCode.OTP_EXPIRED);
        }*/

        OtpEntity regenerateOtpEntity = otpService.regenerateOtp(otpEntity);
        OtpResponse response = new OtpResponse();
        response.setOtpReferenceId(regenerateOtpEntity.getOtpReferenceId());
        response.setCreatedTimestamp(regenerateOtpEntity.getCreatedTimestamp());
        response.setExpiredTimestamp(regenerateOtpEntity.getExpiredTimestamp());
        ResponseEntity responseEntity = ResponseFactory.success(response, OtpResponse.class);
        smsSenderService.sendSmsMessage(regenerateOtpEntity);
        return responseEntity;
    }


    private ResponseEntity buildOtpGenerateVerificationResponse(OtpGenerationRequest request) {
        String otpType = request.getType();
        if (otpType != null) {
            boolean match = Arrays.stream(OtpTypeEnum.values())
                    .anyMatch(e -> e.getValue().equals(otpType));

            if (!match) {
                logger.error("The [{}] otp type is unsupported. System cannot generate OTP", otpType);
                throw new AppException(ErrorCode.INVALID_OTP_TYPE);
            }

            if (OtpTypeEnum.LOGIN.getValue().equals(otpType) && request.getMobileNumber() == null) {
                logger.error("The mobileNumber is not specified when otp_type is LOGIN");
                throw new AppException(ErrorCode.INVALID_OTP_REGISTER_REQUEST);
            }
        } else {
            logger.error("The otp type isn't specified. System cannot generate OTP");
            throw new AppException(ErrorCode.INVALID_OTP_TYPE);
        }


        if (!isValidMobileNumber(request.getMobileNumber())) {
            logger.error("The [{}] mobile number is malformed. System cannot generate OTP", request.getMobileNumber());
            throw new AppException(ErrorCode.INVALID_MOBILE_NUMBER_FORMAT);
        }
        return null;
    }

    private int countOtpEntitiesInTimeBox(List<OtpEntity> otpEntities) {
        long mileStoneTimeInMillis = System.currentTimeMillis() - otpConfiguration.getRegenerationTimeBoxInMinute() * 60 * 1000L;
        Timestamp mileStoneTimestamp = new Timestamp(mileStoneTimeInMillis);
        return (int) otpEntities.stream()
                .filter(entity -> entity.getCreatedTimestamp().after(mileStoneTimestamp))
                .count();
    }

    private OtpEntity getActiveOtpEntity(List<OtpEntity> otpEntities) {
        return otpEntities.stream()
                .filter(otpEntity -> !otpEntity.getIsDeleted())
                .findFirst()
                .orElse(null);
    }

    private boolean isValidMobileNumber(String mobileNumber) {
        if (mobileNumber == null)
            return true;
        Pattern pattern = Pattern.compile(otpConfiguration.getMobileNumberFormat());
        Matcher matcher = pattern.matcher(mobileNumber);
        return matcher.find();
    }
}
