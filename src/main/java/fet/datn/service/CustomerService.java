package fet.datn.service;

import fet.datn.exceptions.AppException;
import fet.datn.exceptions.ErrorCode;
import fet.datn.interceptor.Payload;
import fet.datn.repositories.OrdinalNumberRepository;
import fet.datn.repositories.ScheduleRepository;
import fet.datn.repositories.entities.OrdinalNumberEntity;
import fet.datn.repositories.entities.ScheduleEntity;
import fet.datn.request.ScheduleRequest;
import fet.datn.utils.AppUtils;
import fet.datn.utils.Constants;
import fet.datn.utils.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomerService {
    private static final Logger logger = LoggerFactory.getLogger(CustomerService.class);

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

    public ScheduleEntity bookSchedule(Payload payload, ScheduleRequest requestBody) {
        List<ScheduleEntity> schedule = scheduleRepository.findAllByCustomerIdAndStatus(payload.getUserId(), Constants.SCHEDULE_STATUS.WAITING_CONFIRM);
        if (schedule != null && schedule.size() > 0) {
            logger.info("UserId có lịch chưa xử lý");
            throw new AppException(ErrorCode.CANT_BOOK_SCHEDULE);
        }

        ScheduleEntity entity = new ScheduleEntity();

        entity.setCustomerId(payload.getUserId());
        entity.setTitle(requestBody.getTitle());
        entity.setCustomerNote(requestBody.getCustomerNote());
        entity.setTimeCreated(DateTimeUtils.getDateTimeNow());

        return scheduleRepository.save(entity);
    }

    public ScheduleEntity updateSchedule(Payload payload, Long id, ScheduleRequest request) {
        ScheduleEntity entity = scheduleRepository.findOneById(id);
        if (entity == null) {
            throw new AppException(ErrorCode.ENTITY_NOT_EXISTS);
        }
        if (!entity.getStatus().equals(Constants.SCHEDULE_STATUS.WAITING_CONFIRM)) {
            throw new AppException(ErrorCode.CANT_UPDATE_SCHEDULE);
        }
        AppUtils.copyPropertiesIgnoreNull(request, entity);
        return scheduleRepository.save(entity);

    }

    public ScheduleEntity cancelSchedule(Payload payload, Long id) {
        ScheduleEntity entity = scheduleRepository.findOneById(id);
        if (entity == null) {
            throw new AppException(ErrorCode.ENTITY_NOT_EXISTS);
        }
        if (entity.getStatus().equals(Constants.SCHEDULE_STATUS.DONE)) {
            throw new AppException(ErrorCode.CANT_UPDATE_SCHEDULE);
        }
        entity.setStatus(Constants.SCHEDULE_STATUS.CUSTOMER_CANCELED);
        return scheduleRepository.save(entity);
    }

    public void deleteSchedule(Payload payload, Long id) {
        ScheduleEntity entity = scheduleRepository.findOneById(id);
        if (entity == null) {
            throw new AppException(ErrorCode.ENTITY_NOT_EXISTS);
        }
        if (!entity.getStatus().equals(Constants.SCHEDULE_STATUS.WAITING_CONFIRM)) {
            throw new AppException(ErrorCode.CANT_UPDATE_SCHEDULE);
        }
        scheduleRepository.delete(entity);
    }
}
