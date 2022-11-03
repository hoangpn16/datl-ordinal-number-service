package fet.datn.controller.customer;

import fet.datn.exceptions.AppException;
import fet.datn.exceptions.ErrorCode;
import fet.datn.factory.ResponseFactory;
import fet.datn.interceptor.Payload;
import fet.datn.repositories.entities.ScheduleEntity;
import fet.datn.request.ScheduleRequest;
import fet.datn.service.ScheduleService;
import fet.datn.service.impl.ScheduleServiceImpl;
import fet.datn.utils.Definition;
import io.swagger.annotations.ApiOperation;
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
    private ScheduleService service;


    @GetMapping(value = "/schedule/all")
    @ApiOperation(value = "API client lấy tất cả lịch sử lịch hẹn")
    public ResponseEntity getAllSchedule(@RequestAttribute(name = Definition.PAYLOAD, required = false) Payload payload,
                                         @RequestParam(value = "orderBy", required = false, defaultValue = "id") String orderBy,
                                         @RequestParam(value = "direction", required = false, defaultValue = "DESC") String direction,
                                         @RequestParam(value = "pageNum", required = false, defaultValue = "0") Integer pageNum,
                                         @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
        if (payload == null) {
            logger.info("Mã truy cập không hợp lệ");
            throw new AppException(ErrorCode.TOKEN_NOT_FOUND);
        }
        logger.info("User id [{}] get all schedule", payload.getUserId());
        return service.getAllSchedule(payload, orderBy, direction, pageNum, pageSize);
    }

    @PostMapping(value = "/schedule/book")
    @ApiOperation(value = "API client book lịch hẹn ")
    public ResponseEntity bookSchedule(@RequestAttribute(name = Definition.PAYLOAD, required = false) Payload payload,
                                       @RequestBody ScheduleRequest requestBody) {
        if (payload == null) {
            logger.info("Mã truy cập không hợp lệ");
            throw new AppException(ErrorCode.TOKEN_NOT_FOUND);
        }
        logger.info("User id [{}] book schedule with body [{}]", payload.getUserId(), requestBody);
        ScheduleEntity data = service.bookSchedule(payload, requestBody);
        return ResponseFactory.success(data, ScheduleEntity.class);
    }

    @PutMapping(value = "/schedule/cancel/{id}")
    @ApiOperation(value = "API client từ chối lịch hẹn")
    public ResponseEntity cancelSchedule(@RequestAttribute(name = Definition.PAYLOAD, required = false) Payload payload,
                                         @PathVariable("id") Long id) {
        if (payload == null) {
            logger.info("Mã truy cập không hợp lệ");
            throw new AppException(ErrorCode.TOKEN_NOT_FOUND);
        }
        logger.info("User id [{}] cancel schedule id [{}]", payload.getUserId(), id);
        ScheduleEntity data = service.cancelSchedule(payload, id);
        return ResponseFactory.success(data, ScheduleEntity.class);
    }

    @DeleteMapping(value = "/schedule/delete/{id}")
    @ApiOperation(value = "API client xóa lịch hẹn")
    public ResponseEntity deleteSchedule(@RequestAttribute(name = Definition.PAYLOAD, required = false) Payload payload,
                                         @PathVariable("id") Long id) {
        if (payload == null) {
            logger.info("Mã truy cập không hợp lệ");
            throw new AppException(ErrorCode.TOKEN_NOT_FOUND);
        }
        logger.info("User id [{}] delete schedule id [{}]", payload.getUserId(), id);
        service.deleteSchedule(payload, id);
        return ResponseFactory.success();
    }

    @PutMapping(value = "/schedule/update/{id}")
    @ApiOperation(value = "API client cập nhật thông tin lịch hẹn")
    public ResponseEntity updateSchedule(@RequestAttribute(name = Definition.PAYLOAD, required = false) Payload payload,
                                         @RequestBody ScheduleRequest requestBody, @PathVariable("id") Long id) {
        if (payload == null) {
            logger.info("Mã truy cập không hợp lệ");
            throw new AppException(ErrorCode.TOKEN_NOT_FOUND);
        }
        logger.info("User id [{}] update schedule id [{}] with body [{}]", payload.getUserId(), id, requestBody);
        ScheduleEntity data = service.updateSchedule(payload, id, requestBody);
        return ResponseFactory.success(data, ScheduleEntity.class);
    }
}
