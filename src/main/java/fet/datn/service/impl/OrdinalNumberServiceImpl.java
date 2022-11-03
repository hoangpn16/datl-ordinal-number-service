package fet.datn.service.impl;

import fet.datn.exceptions.AppException;
import fet.datn.exceptions.ErrorCode;
import fet.datn.interceptor.Payload;
import fet.datn.repositories.OrdinalNumberRepository;
import fet.datn.repositories.entities.OrdinalNumberEntity;
import fet.datn.service.OrdinalNumberService;
import fet.datn.utils.Constants;
import fet.datn.utils.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdinalNumberServiceImpl implements OrdinalNumberService {
    private static final Logger logger = LoggerFactory.getLogger(OrdinalNumberServiceImpl.class);

    @Autowired
    private OrdinalNumberRepository ordinalNumberDao;

    @Override
    public OrdinalNumberEntity genOrdinalNumber(Payload payload) {
        OrdinalNumberEntity ordinalNum = ordinalNumberDao.findOrdinalNumberByUserIdAndCreateTime(payload.getUserId());
        if (ordinalNum != null) {
            logger.info("Bạn đã lấy số thứ tự");
            throw new AppException(ErrorCode.ORDINALNUM_EXISTED);
        }

        ordinalNum = new OrdinalNumberEntity();

        ordinalNum.setUserId(payload.getUserId());
        ordinalNum.setStatus(Constants.STATUS.WAITING);
        ordinalNum.setCreatedTime(DateTimeUtils.getDateTimeNow());

        Integer count = ordinalNumberDao.countOrdinalNumber();
        ordinalNum.setOrdinalNumber(count + 1);

        return ordinalNumberDao.save(ordinalNum);
    }

    @Override
    public OrdinalNumberEntity getNumberOfUser(Payload payload) {
        return ordinalNumberDao.findOrdinalNumberByUserIdAndCreateTime(payload.getUserId());
    }

    @Override
    public Integer getNumberOfUserIsWaiting(Payload payload) {
        return ordinalNumberDao.countUserIsWaiting(payload.getUserId());
    }

    //TODO:  API for admin
    @Override
    public OrdinalNumberEntity handleNumber() {
        OrdinalNumberEntity entity = ordinalNumberDao.findNextOrdinalNumber();
        entity.setStatus(Constants.STATUS.IS_PROCESSING);
        return entity;
    }

    @Override
    public OrdinalNumberEntity updateStatus(Long id, Integer status) {
        OrdinalNumberEntity entity = ordinalNumberDao.findOneById(id);
        if (entity == null) {
            logger.info("Not found orfinal id [{}]", id);
            throw new AppException(ErrorCode.ENTITY_NOT_EXISTS);
        }
        entity.setStatus(status);
        return ordinalNumberDao.save(entity);
    }

    @Override
    public List<OrdinalNumberEntity> getOrdinalNumberByStatus(Integer status) {
        return ordinalNumberDao.findAllByStatus(status);
    }


}