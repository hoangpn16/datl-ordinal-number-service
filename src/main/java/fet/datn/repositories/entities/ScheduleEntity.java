package fet.datn.repositories.entities;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table(name = "MANAGE_SCHEDULES")
@Data
public class ScheduleEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @Column(name = "customer_id")
    private Long customerId;

    @Column(name = "title")
    private String title;

    @Column(name = "customer_note")
    private String customerNote;

    @Column(name = "time_schedule")
    private String timeSchedule;

    @Column(name = "employyee_confirm_id")
    private Long employeeeeConfirmId;

    @Column(name = "employee_note")
    private String employeeeeNote;

    @Column(name = "time_confirm")
    private String timeConfirm;

    @Column(name = "status")
    private Integer status;

    @Column(name = "time_created")
    private String timeCreated;
}
