package fet.datn.repositories.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "SYSTEM_OTP_RESTRICTION")
@Getter @Setter
public class OtpRestrictionEntity {

    @Id
    @Column(name = "id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "user_id")
    private Integer userId;

    @Column(name = "mobile_number")
    private String mobileNumber;

    @Column(name = "type")
    private String type;

    @Column(name = "created_timestamp")
    private Timestamp createdTimestamp;
}
