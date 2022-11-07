package fet.datn.controller.customer;

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

@RestController
@RequestMapping(value = "/user")
public class OrdinalNumberController {
    private static final Logger logger = LoggerFactory.getLogger(OrdinalNumberController.class);

    @Autowired
    private OrdinalNumberService service;

    @PostMapping(value = "/ordinal-number")
    @ApiOperation(value = "API client gen số thứ tự")
    public ResponseEntity genOrdinalNumber(@RequestAttribute(name = Definition.PAYLOAD, required = false) Payload payload) {
        if (payload == null) {
            logger.info("Mã truy cập không hợp lệ");
            throw new AppException(ErrorCode.TOKEN_NOT_FOUND);
        }
        logger.info("User id [{}] get ordinal number", payload.getUserId());
        OrdinalNumberEntity data = service.genOrdinalNumber(payload);
        return ResponseFactory.success(data, OrdinalNumberEntity.class);
    }

    @GetMapping(value = "/ordinal-number")
    @ApiOperation(value = "API get số thứ tự của mình")
    public ResponseEntity getNumberOfUser(@RequestAttribute(name = Definition.PAYLOAD, required = false) Payload payload) {
        if (payload == null) {
            logger.info("Mã truy cập không hợp lệ");
            throw new AppException(ErrorCode.TOKEN_NOT_FOUND);
        }
        logger.info("User id [{}] get number", payload.getUserId());
        OrdinalNumberEntity data = service.getNumberOfUser(payload);
        return ResponseFactory.success(data, OrdinalNumberEntity.class);
    }

//    @GetMapping(value = "/ordinal-number/count-waiting")
//    @ApiOperation(value = "API lấy số người đang chờ trước số của mình")
//    public ResponseEntity getNumberOfUserIsWaiting(@RequestAttribute(name = Definition.PAYLOAD, required = false) Payload payload) {
//        if (payload == null) {
//            logger.info("Mã truy cập không hợp lệ");
//            throw new AppException(ErrorCode.TOKEN_NOT_FOUND);
//        }
//        logger.info("User id [{}] get number of user is waiting", payload.getUserId());
//        Integer data = service.getNumberOfUserIsWaiting(payload);
//        return ResponseFactory.success(data, Integer.class);
//    }


}
