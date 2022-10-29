package fet.datn.controller.admin;

import fet.datn.factory.ResponseFactory;
import fet.datn.interceptor.Payload;
import fet.datn.repositories.entities.OrdinalNumberEntity;
import fet.datn.service.OrdinalNumberService;
import io.swagger.models.auth.In;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.xml.ws.Response;
import java.util.List;

@RestController
@RequestMapping(value = "/admin/ordinal-number")
public class AdminOrdinalNumberController {
    private static final Logger logger = LoggerFactory.getLogger(AdminOrdinalNumberController.class);

    @Autowired
    private OrdinalNumberService service;

    @Autowired
    private ResponseFactory factory;


    @GetMapping(value = "/handle")
    public ResponseEntity handleNumber(@RequestAttribute Payload payload) {
        OrdinalNumberEntity data = service.handleNumber();
        logger.info("Handle ordinal number [{}]", data.getOrdinalNumber());
        return factory.success(data, OrdinalNumberEntity.class);
    }

    @PutMapping(value = "/{id}/status")
    public ResponseEntity updateStatus(@PathVariable("id") Long id,
                                       @RequestParam("status") Integer status) {
        logger.info("Update status if ordinal number id [{}] to [{}]", id, status);
        OrdinalNumberEntity data = service.updateStatus(id, status);
        return factory.success(data, OrdinalNumberEntity.class);
    }

    @GetMapping(value = "/status")
    public ResponseEntity getAllByStatus(@RequestParam("status") Integer status) {
        logger.info("Get all ordinal-number by status [{}]", status);
        List<OrdinalNumberEntity> data = service.getOrdinalNumberByStatus(status);
        return factory.success(data, List.class);
    }
}
