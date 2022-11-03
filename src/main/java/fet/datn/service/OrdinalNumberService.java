package fet.datn.service;

import fet.datn.interceptor.Payload;
import fet.datn.repositories.entities.OrdinalNumberEntity;

import java.util.List;

public interface OrdinalNumberService {
    OrdinalNumberEntity genOrdinalNumber(Payload payload);

    OrdinalNumberEntity getNumberOfUser(Payload payload);

    Integer getNumberOfUserIsWaiting(Payload payload);

    //TODO:  API for admin
    OrdinalNumberEntity handleNumber();

    OrdinalNumberEntity updateStatus(Long id, Integer status);

    List<OrdinalNumberEntity> getOrdinalNumberByStatus(Integer status);
}
