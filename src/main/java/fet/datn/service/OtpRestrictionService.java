package fet.datn.service;


import fet.datn.config.OtpConfiguration;
import fet.datn.repositories.OtpRestrictionRepository;
import fet.datn.repositories.entities.OtpRestrictionEntity;
import fet.datn.request.OtpGenerationRequest;
import fet.datn.utils.OtpTypeEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


import java.sql.Timestamp;
import java.util.List;

@Service
public class OtpRestrictionService {

    private Logger logger = LogManager.getLogger(OtpRestrictionService.class);

    @Autowired
    private OtpRestrictionRepository otpRestrictionRepository;

    @Autowired
    private OtpConfiguration otpConfiguration;

    public void createOtpRestriction(OtpGenerationRequest request){
        OtpRestrictionEntity otpRestrictionEntity = buildOtpRestrictionEntity(request);
        if(request.getType().equals(OtpTypeEnum.LOGIN.getValue())){
            logger.info("Creating otp_restriction with mobile number [{}], type [{}] because too many generation in [{}] minutes", request.getMobileNumber(), request.getType(), otpConfiguration.getGenerationRegisterTimeBoxInMinute());
        }
        otpRestrictionRepository.save(otpRestrictionEntity);
    }

    private OtpRestrictionEntity buildOtpRestrictionEntity(OtpGenerationRequest request){
        OtpRestrictionEntity otpRestrictionEntity = new OtpRestrictionEntity();
        otpRestrictionEntity.setMobileNumber(request.getMobileNumber());
        otpRestrictionEntity.setType(request.getType());
        otpRestrictionEntity.setCreatedTimestamp(new Timestamp(System.currentTimeMillis()));
        return otpRestrictionEntity;
    }

    public OtpRestrictionEntity findOtpRestrictionByMobile(String mobile, String type){
        OtpRestrictionEntity otpRestrictionEntity = null;
        if(type.equals(OtpTypeEnum.LOGIN.getValue()) || type.equals(OtpTypeEnum.FORGOT_PASSWORD.getValue())){
            List<OtpRestrictionEntity> result = otpRestrictionRepository.findOtpRestrictionByMobileAndCreateTimeLessThan(mobile,
                    new Timestamp(System.currentTimeMillis() - otpConfiguration.getGenerationBlockedTimeInMinute() * 60 * 1000L));
            if(!result.isEmpty())
                otpRestrictionEntity = result.get(0);
        }
        return otpRestrictionEntity;
    }
}
