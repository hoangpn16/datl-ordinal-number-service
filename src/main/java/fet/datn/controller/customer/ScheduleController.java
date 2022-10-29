package fet.datn.controller.customer;

import fet.datn.factory.ResponseFactory;
import fet.datn.interceptor.Payload;
import fet.datn.repositories.entities.ScheduleEntity;
import fet.datn.request.ScheduleRequest;
import fet.datn.service.ScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/user")
public class ScheduleController {
    private static final Logger logger = LoggerFactory.getLogger(ScheduleController.class);

    @Autowired
    private ResponseFactory factory;

    @Autowired
    private ScheduleService service;

    @GetMapping(value = "/schedule/all")
    public ResponseEntity getAllSchedule(@RequestAttribute Payload payload,
                                         @RequestParam(value = "orderBy", required = false, defaultValue = "id") String orderBy,
                                         @RequestParam(value = "direction", required = false, defaultValue = "DESC") String direction,
                                         @RequestParam(value = "pageNum", required = false, defaultValue = "0") Integer pageNum,
                                         @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
        logger.info("User id [{}] get all schedule", payload.getUserId());
        return service.getAllSchedule(payload, orderBy, direction, pageNum, pageSize);
    }

    @PostMapping(value = "/schedule/book")
    public ResponseEntity bookSchedule(@RequestAttribute Payload payload,
                                       @RequestBody ScheduleRequest requestBody) {
        logger.info("User id [{}] book schedule with body [{}]", payload.getUserId(), requestBody);
        ScheduleEntity data = service.bookSchedule(payload, requestBody);
        return factory.success(data, ScheduleEntity.class);
    }

    @PutMapping(value = "/schedule/cancel/{id}")
    public ResponseEntity cancelSchedule(@RequestAttribute Payload payload,
                                         @PathVariable("id") Long id) {
        logger.info("User id [{}] cancel schedule id [{}]", payload.getUserId(), id);
        ScheduleEntity data = service.cancelSchedule(payload, id);
        return factory.success(data, ScheduleEntity.class);
    }

    @DeleteMapping(value = "/schedule/delete/{id}")
    public ResponseEntity deleteSchedule(@RequestAttribute Payload payload,
                                         @PathVariable("id") Long id) {
        logger.info("User id [{}] delete schedule id [{}]", payload.getUserId(), id);
        service.deleteSchedule(payload, id);
        return factory.success();
    }

    @PutMapping(value = "/schedule/update/{id}")
    public ResponseEntity updateSchedule(@RequestAttribute Payload payload,
                                         @RequestBody ScheduleRequest requestBody, @PathVariable("id") Long id) {
        logger.info("User id [{}] update schedule id [{}] with body [{}]", payload.getUserId(), id, requestBody);
        ScheduleEntity data = service.updateSchedule(payload, id, requestBody);
        return factory.success(data, ScheduleEntity.class);
    }
}
