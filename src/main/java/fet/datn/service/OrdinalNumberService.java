package fet.datn.service;

import fet.datn.exceptions.AppException;
import fet.datn.exceptions.ErrorCode;
import fet.datn.interceptor.Payload;
import fet.datn.repositories.OrdinalNumberRepository;
import fet.datn.repositories.ScheduleRepository;
import fet.datn.repositories.entities.OrdinalNumberEntity;
import fet.datn.utils.Constants;
import fet.datn.utils.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OrdinalNumberService {
    private static final Logger logger = LoggerFactory.getLogger(OrdinalNumberService.class);

    @Autowired
    private OrdinalNumberRepository ordinalNumberDao;

    @Autowired
    private ScheduleRepository scheduleRepository;

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

    public OrdinalNumberEntity getNumberOfUser(Payload payload) {
        return ordinalNumberDao.findOrdinalNumberByUserIdAndCreateTime(payload.getUserId());
    }


    //TODO:  API for admin

    public OrdinalNumberEntity handleNumber() {
        OrdinalNumberEntity entity = ordinalNumberDao.findNextOrdinalNumber();
        entity.setStatus(Constants.STATUS.IS_PROCESSING);
        return entity;
    }

    public OrdinalNumberEntity updateStatus(Long id, Integer status) {
        OrdinalNumberEntity entity = ordinalNumberDao.findOneById(id);
        if (entity == null) {
            logger.info("Not found orfinal id [{}]", id);
            throw new AppException(ErrorCode.ENTITY_NOT_EXISTS);
        }
        entity.setStatus(status);
        return ordinalNumberDao.save(entity);
    }

    public List<OrdinalNumberEntity> getOrdinalNumberByStatus(Integer status) {
        return ordinalNumberDao.findAllByStatus(status);
    }


}
