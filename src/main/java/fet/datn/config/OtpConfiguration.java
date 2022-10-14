package fet.datn.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import java.util.Arrays;
import java.util.List;

@Configuration
public class OtpConfiguration {

    @Value("${otp.valid.character}")
    private String otpValidCharacter;

    @Value("${otp.length}")
    private Integer otpLength;

    @Value("${otp.life-time-in-second}")
    private Integer otpLifeTimeInSecond;

    @Value("${otp.mobile-number.format}")
    private String mobileNumberFormat;

    @Value("${otp.verification.limit}")
    private Integer verificationLimit;

    @Value("${otp.regeneration.limit}")
    private Integer regenerationLimit;

    @Value("${otp.regeneration.time-box-in-minute}")
    private Integer regenerationTimeBoxInMinute;

    @Value("${otp.regeneration.suspend-time-in-second}")
    private Integer regenerationSuspendTimeInSecond;

    @Value("${otp.generation.register.time-box-in-minute}")
    private Integer generationRegisterTimeBoxInMinute;

    @Value("${otp.generation.forgot-pass.time-box-in-minute}")
    private Integer generationForgotPassTimeBoxInMinute;

    @Value("${otp.generation.times.limit}")
    private Integer generationTimesLimit;

    @Value("${otp.generation.blocked.time-in-minute}")
    private Integer generationBlockedTimeInMinute;

    private List<String> otpBlacklist;

    public OtpConfiguration(@Value("${otp.blacklist}") String otpBlacklistValue) {
        otpBlacklist = Arrays.asList(otpBlacklistValue.split(","));
    }

    public String getOtpValidCharacter() {
        return otpValidCharacter;
    }

    public List<String> getOtpBlacklist() {
        return otpBlacklist;
    }

    public Integer getOtpLength() {
        return otpLength;
    }

    public Integer getOtpLifeTimeInSecond() {
        return otpLifeTimeInSecond;
    }

    public String getMobileNumberFormat() {
        return mobileNumberFormat;
    }

    public Integer getVerificationLimit() {
        return verificationLimit;
    }

    public Integer getRegenerationLimit() {
        return regenerationLimit;
    }

    public Integer getRegenerationTimeBoxInMinute() {
        return regenerationTimeBoxInMinute;
    }

    public Integer getRegenerationSuspendTimeInSecond() {
        return regenerationSuspendTimeInSecond;
    }

    public Integer getGenerationRegisterTimeBoxInMinute() {
        return generationRegisterTimeBoxInMinute;
    }

    public Integer getGenerationForgotPassTimeBoxInMinute() {
        return generationForgotPassTimeBoxInMinute;
    }

    public Integer getGenerationTimesLimit() {
        return generationTimesLimit;
    }

    public Integer getGenerationBlockedTimeInMinute() {
        return generationBlockedTimeInMinute;
    }
}
