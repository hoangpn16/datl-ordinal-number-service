package fet.datn.repositories.entities;

import lombok.Data;


import javax.persistence.*;

@Data
@Entity
@Table(name = "SYSTEM_CUSTOMER")
public class CustomerEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "phone")
    private String phone;

    @Column(name = "created_time")
    private String createdTime;

    @Column(name = "modified_time")
    private String modifiedTime;
}
