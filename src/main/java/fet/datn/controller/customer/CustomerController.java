package fet.datn.controller.customer;

import fet.datn.factory.ResponseFactory;
import fet.datn.interceptor.Payload;
import fet.datn.repositories.entities.OrdinalNumberEntity;
import fet.datn.service.OrdinalNumberService;
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
    private OrdinalNumberService service;

    @PostMapping(value = "/ordinal-number")
    public ResponseEntity genOrdinalNumber(@RequestAttribute(name = Definition.PAYLOAD, required = false) Payload payload) {

        logger.info("UserId [{}] get ordinal number",payload.getUserId());
        OrdinalNumberEntity data = service.genOrdinalNumber(payload);
        return factory.success(data, OrdinalNumberEntity.class);
    }

    @GetMapping(value = "/ordinal-number")
    public ResponseEntity getNumberOfUser(@RequestAttribute(name = Definition.PAYLOAD, required = false) Payload payload){
        logger.info("UserId [{}] get number",payload.getUserId());
        OrdinalNumberEntity data = service.getNumberOfUser(payload);
        return factory.success(data, OrdinalNumberEntity.class);
    }
}
