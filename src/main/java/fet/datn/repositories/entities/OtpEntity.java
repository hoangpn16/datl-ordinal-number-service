package fet.datn.repositories.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "SYSTEM_OTP")
@Getter @Setter @NoArgsConstructor
public class OtpEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "otp_code")
    private String otpCode;

    @Column(name = "otp_reference_id")
    private String otpReferenceId;

    @Column(name = "mobile_number")
    private String mobileNumber;

    @Column(name = "type")
    private String type;

    @Column(name = "created_timestamp")
    private Timestamp createdTimestamp;

    @Column(name = "last_updated_timestamp")
    private Timestamp lastUpdatedTimestamp;

    @Column(name = "expired_timestamp")
    private Timestamp expiredTimestamp;

    @Column(name = "is_deleted")
    private Boolean isDeleted;

    @Column(name = "is_success_verified")
    private Boolean isSuccessVerified;

    @Transient
    private String rawOtpCode;
}
