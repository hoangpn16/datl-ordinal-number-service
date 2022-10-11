package fet.datn.repositories.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "SYSTEM_OTP_VERIFICATION")
@Getter @Setter @NoArgsConstructor
public class OtpVerificationEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name ="otp_id")
    private Integer otpId;

    @Column(name = "verified_timestamp")
    private Timestamp verifiedTimestamp;

    @Column(name = "is_success")
    private Boolean isSuccess;
}
