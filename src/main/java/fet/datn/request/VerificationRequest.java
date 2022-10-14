package fet.datn.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class VerificationRequest {

    @JsonProperty("otp_reference_id")
    private String otpReferenceId;

    @JsonProperty("otp_code")
    private String otpCode;
}

