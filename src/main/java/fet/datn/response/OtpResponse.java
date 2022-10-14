package fet.datn.response;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;

import java.sql.Timestamp;

@Getter @Setter
public class OtpResponse {

    @JsonProperty("otp_reference_id")
    private String otpReferenceId;

    @JsonProperty("created_timestamp")
    private Timestamp createdTimestamp;

    @JsonProperty("expired_timestamp")
    private Timestamp expiredTimestamp;
}


