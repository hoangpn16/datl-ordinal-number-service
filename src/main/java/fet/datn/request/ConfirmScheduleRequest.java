package fet.datn.request;

import lombok.Data;

@Data
public class ConfirmScheduleRequest {
    private String timeSchedule;
    private String employeeNote;
    private String location;
}
