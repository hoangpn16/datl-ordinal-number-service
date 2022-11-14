package fet.datn.controller.admin;

import fet.datn.exceptions.AppException;
import fet.datn.exceptions.ErrorCode;
import fet.datn.factory.ResponseFactory;
import fet.datn.interceptor.Payload;
import fet.datn.repositories.entities.OrdinalNumberEntity;
import fet.datn.service.OrdinalNumberService;
import fet.datn.service.impl.OrdinalNumberServiceImpl;
import fet.datn.utils.Definition;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
import java.util.Map;

@RestController
@RequestMapping(value = "/admin/ordinal-number")
public class AdminOrdinalNumberController {
    private static final Logger logger = LoggerFactory.getLogger(AdminOrdinalNumberController.class);

    @Autowired
    private OrdinalNumberService service;

    @GetMapping(value = "/report")
    @ApiOperation(value = "API CMS lấy báo cáo khách hàng")
    public ResponseEntity getAllScheduleByStatus(@RequestAttribute(name = Definition.PAYLOAD, required = false) Payload payload) {
//        if (payload == null) {
//            logger.info("Mã truy cập không hợp lệ");
//            throw new AppException(ErrorCode.TOKEN_NOT_FOUND);
//        }
        Map<String, Integer> data = service.getReportCustomer();
        return ResponseFactory.success(data, Map.class);
    }

    @PutMapping(value = "/location")
    @ApiOperation(value = "API nhân viên chọn vị trí")
    public ResponseEntity chooseLocation(@RequestAttribute(name = Definition.PAYLOAD, required = false) Payload payload,
                                         @RequestParam("location") Integer location) {
        if (payload == null) {
            logger.info("Mã truy cập không hợp lệ");
            throw new AppException(ErrorCode.TOKEN_NOT_FOUND);
        }
        service.chooseLocation(payload, location);
        return ResponseFactory.success();
    }

    @GetMapping(value = "/handle")
    @ApiOperation(value = "API lấy số để xử lí")
    public ResponseEntity handleNumber(@RequestAttribute(name = Definition.PAYLOAD, required = false) Payload payload) {
        if (payload == null) {
            logger.info("Mã truy cập không hợp lệ");
            throw new AppException(ErrorCode.TOKEN_NOT_FOUND);
        }
        OrdinalNumberEntity data = service.handleNumber(payload);
        logger.info("Handle ordinal number [{}]", data.getOrdinalNumber());
        return ResponseFactory.success(data, OrdinalNumberEntity.class);
    }

    @PutMapping(value = "/{id}/status")
    @ApiOperation(value = "API cập nhật trạng thái cho số thứ tự")
    public ResponseEntity updateStatus(@RequestAttribute(name = Definition.PAYLOAD, required = false) Payload payload, @PathVariable("id") Long id,
                                       @RequestParam("status") Integer status) {
        if (payload == null) {
            logger.info("Mã truy cập không hợp lệ");
            throw new AppException(ErrorCode.TOKEN_NOT_FOUND);
        }
        logger.info("Update status if ordinal number id [{}] to [{}]", id, status);
        OrdinalNumberEntity data = service.updateStatus(id, status);
        return ResponseFactory.success(data, OrdinalNumberEntity.class);
    }

    @GetMapping(value = "/status")
    @ApiOperation(value = "API lấy tất cả số thứ tự theo trạng thái")
    public ResponseEntity getAllByStatus(@RequestAttribute(name = Definition.PAYLOAD, required = false) Payload payload, @RequestParam("status") Integer status) {
//        if (payload == null) {
//            logger.info("Mã truy cập không hợp lệ");
//            throw new AppException(ErrorCode.TOKEN_NOT_FOUND);
//        }
        logger.info("Get all ordinal-number by status [{}]", status);
        List<OrdinalNumberEntity> data = service.getOrdinalNumberByStatus(status);
        return ResponseFactory.success(data, List.class);
    }
}
