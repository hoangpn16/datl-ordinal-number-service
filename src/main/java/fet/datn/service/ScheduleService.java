package fet.datn.service;

import fet.datn.interceptor.Payload;
import fet.datn.repositories.entities.ScheduleEntity;
import fet.datn.request.ConfirmScheduleRequest;
import fet.datn.request.ScheduleRequest;
import org.springframework.http.ResponseEntity;

public interface ScheduleService {
    ResponseEntity getAllSchedule(Payload payload, String orderBy, String direction, Integer pageNum, Integer pageSize);

    ResponseEntity getReportSchedule(Payload payload, String start, String end, String orderBy, String direction, Integer pageNum, Integer pageSize);

    ScheduleEntity bookSchedule(Payload payload, ScheduleRequest requestBody);

    ScheduleEntity updateSchedule(Payload payload, Long id, ScheduleRequest request);

    ScheduleEntity cancelSchedule(Payload payload, Long id);

    void deleteSchedule(Payload payload, Long id);

    //API for admin
    ScheduleEntity confirmSchedule(Payload payload, Long id, ConfirmScheduleRequest requestBody);

    ScheduleEntity updateStatusSchedule(Payload payload, Long id, Integer status);

    ResponseEntity findAllSchedule(Integer status, String orderBy, String direction, Integer pageNum, Integer pageSize);
}
