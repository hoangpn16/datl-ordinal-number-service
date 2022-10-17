package fet.datn.repositories.entities;

import lombok.Data;

import javax.persistence.*;
import java.util.Date;

@Data
@Entity
@Table(name = "SYSTEM_TOKEN")
public class TokenEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "token")
    private String token;

    @Column(name = "created_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdTime;

    @Column(name = "expired_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date expiredTime;

    @Column(name = "user_id")
    private Long userId;
}
