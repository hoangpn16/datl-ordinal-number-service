package fet.datn.service;

import fet.datn.exceptions.AppException;
import fet.datn.exceptions.ErrorCode;
import fet.datn.factory.PageResponse;
import fet.datn.factory.PageResponseUtil;
import fet.datn.factory.ResponseFactory;
import fet.datn.interceptor.Payload;
import fet.datn.repositories.ScheduleRepository;
import fet.datn.repositories.entities.ScheduleEntity;
import fet.datn.request.ScheduleRequest;
import fet.datn.utils.AppUtils;
import fet.datn.utils.Constants;
import fet.datn.utils.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;

import javax.xml.ws.Action;
import java.util.List;

@Service
public class ScheduleService {
    private static final Logger logger = LoggerFactory.getLogger(ScheduleService.class);

    @Autowired
    private ScheduleRepository scheduleRepository;

    public ResponseEntity getAllSchedule(Payload payload, String orderBy, String direction, Integer pageNum, Integer pageSize) {
        Sort sort = Sort.by(Sort.Direction.ASC, orderBy);
        if (Sort.Direction.DESC.name().equals(direction)) {
            sort = Sort.by(Sort.Direction.DESC, orderBy);
        }
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize, sort);
        Page pageResult = scheduleRepository.findAllByCustomerId(payload.getUserId(), pageRequest);
        PageResponse pageResponse = PageResponseUtil.buildPageResponse(pageResult);
        return ResponseFactory.success(pageResult.getContent(), pageResponse);
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
