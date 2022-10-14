package fet.datn.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class OtpGenerationRequest {

    @JsonProperty("mobile_number")
    private String mobileNumber;

    @JsonProperty("type")
    private String type;
}
