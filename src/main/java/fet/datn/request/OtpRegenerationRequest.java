package fet.datn.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class OtpRegenerationRequest {
    @JsonProperty("otp_reference_id")
    private String otpReferenceId;
}
