package fet.datn.controller.admin;

import fet.datn.factory.ResponseFactory;
import fet.datn.interceptor.Payload;
import fet.datn.repositories.entities.ScheduleEntity;
import fet.datn.request.ConfirmScheduleRequest;
import fet.datn.service.ScheduleService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/admin/schedule")
public class AdminScheduleController {
    private static final Logger logger = LoggerFactory.getLogger(AdminScheduleController.class);

    @Autowired
    private ResponseFactory factory;

    @Autowired
    private ScheduleService service;

    @GetMapping
    public ResponseEntity getAllScheduleByStatus(@RequestParam(value = "status", required = false) Integer status,
                                                 @RequestParam(value = "orderBy", required = false, defaultValue = "id") String orderBy,
                                                 @RequestParam(value = "direction", required = false, defaultValue = "DESC") String direction,
                                                 @RequestParam(value = "pageNum", required = false, defaultValue = "0") Integer pageNum,
                                                 @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
        logger.info("Get all schedule by status [{}],orderBy [{}] direction [{}] pageNum [{}] pageSize [{}]", status, orderBy, direction, pageNum, pageSize);
        return service.findAllSchedule(status, orderBy, direction, pageNum, pageSize);
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity confirmSchedule(@RequestAttribute Payload payload,
                                          @PathVariable("id") Long id,
                                          @RequestBody ConfirmScheduleRequest requestBody) {
        logger.info("Employee id [{}] confirm scheudle id [{}] with body", payload.getUserId(), id, requestBody.toString());
        ScheduleEntity data = service.confirmSchedule(payload, id, requestBody);
        return factory.success(data, ScheduleEntity.class);
    }

    @PutMapping(value = "/status/{id}")
    public ResponseEntity updateStatusSchedule(@PathVariable("id") Long id,
                                               @RequestParam("status") Integer status,
                                               @RequestAttribute Payload payload) {
        logger.info("Employee id [{}] update status for schedule id [{}] to [{}]", payload.getUserId(), id, status);
        ScheduleEntity data = service.updateStatusSchedule(payload, id, status);
        return factory.success(data, ScheduleEntity.class);

    }

}
