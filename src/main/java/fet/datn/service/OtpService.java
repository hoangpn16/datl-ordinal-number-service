package fet.datn.service;

import fet.datn.config.OtpConfiguration;
import fet.datn.exceptions.AppException;
import fet.datn.exceptions.ErrorCode;
import fet.datn.repositories.CustomerDao;
import fet.datn.repositories.OtpRepository;
import fet.datn.repositories.entities.CustomerEntity;
import fet.datn.repositories.entities.OtpEntity;
import fet.datn.repositories.entities.OtpRestrictionEntity;
import fet.datn.request.OtpGenerationRequest;
import fet.datn.utils.DateTimeUtils;
import fet.datn.utils.OtpTypeEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

@Service
public class OtpService {

    private static final Logger logger = LogManager.getLogger(OtpService.class);

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private TokenService tokenService;

    @Autowired
    private OtpConfiguration otpConfiguration;

    @Autowired
    private OtpRestrictionService otpRestrictionService;

    @Autowired
    CustomerDao customerDao;

    public OtpEntity generateOtp(OtpGenerationRequest request) {
        CustomerEntity customer = customerDao.findOneByPhone(request.getMobileNumber());
        if (customer == null) {
            customer = new CustomerEntity();
            customer.setPhone(request.getMobileNumber());
            customer.setCreatedTime(DateTimeUtils.getDateTimeNow());
            customer.setModifiedTime(DateTimeUtils.getDateTimeNow());

            customerDao.save(customer);
        }

        // Check mobile_number is block or not. now > created_timestamp + Z * 1000L --> ok.
        OtpRestrictionEntity blocked = otpRestrictionService.findOtpRestrictionByMobile(request.getMobileNumber(), request.getType());
        if (blocked != null) {
            if (request.getType().equals(OtpTypeEnum.LOGIN.getValue()) || request.getType().equals(OtpTypeEnum.FORGOT_PASSWORD.getValue())) {
                logger.error("Found temporary blocked mobile number [{}]", request.getMobileNumber());
                throw new AppException(ErrorCode.MOBILE_BLOCKED);
            }
        }

        Long timesCreateOTP = null;

        // Check sdt da duoc dang ki hay chua [Registration]. Neu da dang ki -> failcase
        if (request.getType().equals(OtpTypeEnum.LOGIN.getValue())) {
            timesCreateOTP = verifyMobileNumberRegistrationAndGetTimesGeneration(request);
        }


        // times > X ==> failcase. Block user_id or mobilenumber for business [type]
        if (timesCreateOTP >= otpConfiguration.getGenerationTimesLimit()) {
            otpRestrictionService.createOtpRestriction(request);
            throw new AppException(ErrorCode.MAXIMUM_OTP_GENERATION_REACHED);
        }

        OtpEntity otpEntity = buildOtpEntity(
                request.getMobileNumber(),
                tokenService.generateOtpReferenceId(),
                request.getType());


        logger.info("Generating new otp");
        otpRepository.save(otpEntity);
        logger.info("Generated otp with [{}] otpReferenceId successfully", otpEntity.getOtpReferenceId());

        return otpEntity;
    }

    public OtpEntity regenerateOtp(OtpEntity otpEntity) {
        OtpEntity regenerateOtpEntity = buildOtpEntity(
                otpEntity.getMobileNumber(),
                otpEntity.getOtpReferenceId(),
                otpEntity.getType());

        otpEntity.setIsDeleted(true);
        otpEntity.setLastUpdatedTimestamp(regenerateOtpEntity.getLastUpdatedTimestamp());
        logger.info("Regenerating new otp for [{}] otpReferenceId", otpEntity.getOtpReferenceId());
        otpRepository.saveAll(Arrays.asList(otpEntity, regenerateOtpEntity));
        logger.info("Regenerated new otp for [{}] otpReferenceId successfully", otpEntity.getOtpReferenceId());

        return regenerateOtpEntity;
    }

    public void deleteOtp(String otpReferenceId) {
        List<OtpEntity> otpEntities = getNonDeletedOtpEntity(otpReferenceId);

        if (otpEntities.isEmpty()) {
            logger.error("Cannot find OTP with [{}] otpReferenceId", otpReferenceId);
            throw new AppException(ErrorCode.OTP_REFERENCE_ID_NOT_FOUND);
        }

        if (otpEntities.size() > 1) {
            logger.error("Fatal error. There're more than 1 active OTP for one [{}] otpReferenceId", otpReferenceId);
            throw new AppException(ErrorCode.GENERAL_ERROR);
        }
        OtpEntity otpEntity = otpEntities.get(0);
        logger.info("Set deletion status of [{}] otpReferenceId to TRUE when it is used", otpEntity.getOtpReferenceId());
        otpEntity.setIsDeleted(true);
        otpEntity.setLastUpdatedTimestamp(new Timestamp(System.currentTimeMillis()));
        otpRepository.save(otpEntity);
        logger.info("OTP with [{}] otpReferenceId is deleted", otpEntity.getOtpReferenceId());
    }


    public List<OtpEntity> getOtpDetailsByOtpReferenceId(String otpReferenceId) {
        logger.info("Find otps by otpReferenceId [{}]", otpReferenceId);
        List<OtpEntity> otpEntities = otpRepository.findAllByOtpReferenceId(otpReferenceId);
        logger.info("Found [{}] otp(s)", otpEntities.size());
        return otpEntities;
    }

    public List<OtpEntity> getNonDeletedOtpEntity(String otpReferenceId) {
        logger.info("Finding not deleted OTP with [{}] otpReferenceId", otpReferenceId);
        List<OtpEntity> otpEntities = otpRepository.findAllByOtpReferenceIdAndIsDeletedIsFalse(otpReferenceId);
        logger.info("Found [{}] otp", otpEntities.size());
        return otpEntities;
    }

    private OtpEntity buildOtpEntity(String mobileNumber, String otpReferenceId, String otpType) {
        OtpEntity otpEntity = new OtpEntity();

        otpEntity.setUserId(customerDao.findOneByPhone(mobileNumber).getUserId());
        otpEntity.setMobileNumber(mobileNumber);
        otpEntity.setOtpReferenceId(otpReferenceId);
        otpEntity.setType(otpType);
        String otpCode = generateOtpCode();
        otpEntity.setRawOtpCode(otpCode);
        otpEntity.setOtpCode(otpCode);
        otpEntity.setIsDeleted(false);
        otpEntity.setIsSuccessVerified(false);
        long now = System.currentTimeMillis();
        Timestamp timestamp = new Timestamp(now);
        otpEntity.setCreatedTimestamp(timestamp);
        otpEntity.setLastUpdatedTimestamp(timestamp);
        otpEntity.setExpiredTimestamp(new Timestamp(now + otpConfiguration.getOtpLifeTimeInSecond() * 1000));
        return otpEntity;
    }

    private String generateOtpCode() {
        String otpCode;
        List<String> blacklist = otpConfiguration.getOtpBlacklist();

        do {
            otpCode = tokenService.generateCode(otpConfiguration.getOtpValidCharacter(), otpConfiguration.getOtpLength());
        } while (blacklist.contains(otpCode));

        return otpCode;
    }

    private Long verifyMobileNumberRegistrationAndGetTimesGeneration(OtpGenerationRequest request) {

        Long timesCreateOTP = otpRepository.countGeneratedOtpsByMobileNumber(
                request.getMobileNumber(),
                request.getType(),
                new Timestamp(System.currentTimeMillis() - otpConfiguration.getGenerationRegisterTimeBoxInMinute() * 60 * 1000L));

        logger.info("Count number of otp generation for mobile [{}], type [{}] from [{}] minutes past to present. Result: [{}]", request.getMobileNumber(),
                request.getType(), otpConfiguration.getGenerationRegisterTimeBoxInMinute(), timesCreateOTP);
        return timesCreateOTP;
    }


}
