package fet.datn.service;

import fet.datn.interceptor.Payload;
import fet.datn.repositories.entities.OrdinalNumberEntity;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.Map;

public interface OrdinalNumberService {
    Map<String, Integer> getReportCustomer();

    OrdinalNumberEntity genOrdinalNumber(Payload payload);

    OrdinalNumberEntity getNumberOfUser(Payload payload);

    Integer getNumberOfUserIsWaiting(Payload payload);

    //TODO:  API for admin
    void chooseLocation(Payload payload, Integer location);

    OrdinalNumberEntity handleNumber(Payload payload);

    OrdinalNumberEntity updateStatus(Long id, Integer status);

    List<OrdinalNumberEntity> getOrdinalNumberByStatus(Integer status);
}
