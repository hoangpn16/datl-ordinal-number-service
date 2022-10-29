package fet.datn.controller.customer;

import fet.datn.factory.ResponseFactory;
import fet.datn.interceptor.Payload;
import fet.datn.repositories.entities.OrdinalNumberEntity;
import fet.datn.repositories.entities.ScheduleEntity;
import fet.datn.request.ScheduleRequest;
import fet.datn.service.CustomerService;
import fet.datn.utils.Definition;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/user")
public class CustomerController {
    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    @Autowired
    private ResponseFactory factory;

    @Autowired
    private CustomerService service;

    @PostMapping(value = "/ordinal-number")
    public ResponseEntity genOrdinalNumber(@RequestAttribute(name = Definition.PAYLOAD, required = false) Payload payload) {

        logger.info("User id [{}] get ordinal number", payload.getUserId());
        OrdinalNumberEntity data = service.genOrdinalNumber(payload);
        return factory.success(data, OrdinalNumberEntity.class);
    }

    @GetMapping(value = "/ordinal-number")
    public ResponseEntity getNumberOfUser(@RequestAttribute(name = Definition.PAYLOAD, required = false) Payload payload) {
        logger.info("User id [{}] get number", payload.getUserId());
        OrdinalNumberEntity data = service.getNumberOfUser(payload);
        return factory.success(data, OrdinalNumberEntity.class);
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
