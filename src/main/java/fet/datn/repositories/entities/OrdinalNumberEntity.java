package fet.datn.repositories.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Data
@Table(name = "ORDINAL_NUMBERS")
public class OrdinalNumberEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "user_id")
    private Long userId;

    @Column(name = "ordinal_number")
    private Integer ordinalNumber;

    @Column(name = "status")
    private Integer status;

    @Column(name = "created_time")
    private String createdTime;

    @Column(name = "employee_id")
    private Long employeeId;

    @Column(name = "time_handled")
    private String timeHandled;

    @Column(name = "time_completed")
    private String timeCompleted;

    @Transient
    private Integer numberWaiting = 0;
}
