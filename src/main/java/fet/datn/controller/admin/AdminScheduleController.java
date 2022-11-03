package fet.datn.controller.admin;

import fet.datn.exceptions.AppException;
import fet.datn.exceptions.ErrorCode;
import fet.datn.factory.ResponseFactory;
import fet.datn.interceptor.Payload;
import fet.datn.repositories.entities.ScheduleEntity;
import fet.datn.request.ConfirmScheduleRequest;
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
@RequestMapping(value = "/admin/schedule")
public class AdminScheduleController {
    private static final Logger logger = LoggerFactory.getLogger(AdminScheduleController.class);

    @Autowired
    private ScheduleService service;

    @GetMapping
    @ApiOperation(value = "API CMS lấy tất cả lịch hẹn theo trạng thái")
    public ResponseEntity getAllScheduleByStatus(@RequestAttribute(name = Definition.PAYLOAD, required = false) Payload payload,
                                                 @RequestParam(value = "status", required = false) Integer status,
                                                 @RequestParam(value = "orderBy", required = false, defaultValue = "id") String orderBy,
                                                 @RequestParam(value = "direction", required = false, defaultValue = "DESC") String direction,
                                                 @RequestParam(value = "pageNum", required = false, defaultValue = "0") Integer pageNum,
                                                 @RequestParam(value = "pageSize", required = false, defaultValue = "20") Integer pageSize) {
        if (payload == null) {
            logger.info("Mã truy cập không hợp lệ");
            throw new AppException(ErrorCode.TOKEN_NOT_FOUND);
        }
        logger.info("Get all schedule by status [{}],orderBy [{}] direction [{}] pageNum [{}] pageSize [{}]", status, orderBy, direction, pageNum, pageSize);
        return service.findAllSchedule(status, orderBy, direction, pageNum, pageSize);
    }

    @PutMapping(value = "/{id}")
    @ApiOperation(value = "API CMS phê duyệt lịch hẹn")
    public ResponseEntity confirmSchedule(@RequestAttribute(name = Definition.PAYLOAD, required = false) Payload payload,
                                          @PathVariable("id") Long id,
                                          @RequestBody ConfirmScheduleRequest requestBody) {
        if (payload == null) {
            logger.info("Mã truy cập không hợp lệ");
            throw new AppException(ErrorCode.TOKEN_NOT_FOUND);
        }
        logger.info("Employee id [{}] confirm scheudle id [{}] with body", payload.getUserId(), id, requestBody.toString());
        ScheduleEntity data = service.confirmSchedule(payload, id, requestBody);
        return ResponseFactory.success(data, ScheduleEntity.class);
    }

    @PutMapping(value = "/status/{id}")
    @ApiOperation(value = "API CMS cập nhật trạng thái cho lịch hẹn (hoàn thành / hủy)")
    public ResponseEntity updateStatusSchedule(@PathVariable("id") Long id,
                                               @RequestParam("status") Integer status,
                                               @RequestAttribute(name = Definition.PAYLOAD, required = false) Payload payload) {
        if (payload == null) {
            logger.info("Mã truy cập không hợp lệ");
            throw new AppException(ErrorCode.TOKEN_NOT_FOUND);
        }
        logger.info("Employee id [{}] update status for schedule id [{}] to [{}]", payload.getUserId(), id, status);
        ScheduleEntity data = service.updateStatusSchedule(payload, id, status);
        return ResponseFactory.success(data, ScheduleEntity.class);

    }

}
