package fet.datn.service.impl;

import fet.datn.exceptions.AppException;
import fet.datn.exceptions.ErrorCode;
import fet.datn.factory.PageResponse;
import fet.datn.factory.PageResponseUtil;
import fet.datn.factory.ResponseFactory;
import fet.datn.interceptor.Payload;
import fet.datn.repositories.ScheduleRepository;
import fet.datn.repositories.entities.ScheduleEntity;
import fet.datn.request.ConfirmScheduleRequest;
import fet.datn.request.ScheduleRequest;
import fet.datn.service.ScheduleService;
import fet.datn.service.SmsSenderService;
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


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class ScheduleServiceImpl implements ScheduleService {
    private static final Logger logger = LoggerFactory.getLogger(ScheduleServiceImpl.class);

    @Autowired
    private ScheduleRepository scheduleRepository;

    @Autowired
    private SmsSenderService senderService;

    @Override
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

    @Override
    public ResponseEntity getReportSchedule(Payload payload, String start, String end, String orderBy, String direction, Integer pageNum, Integer pageSize) {
        DateTimeUtils.validateDateTime(start, end);
        Sort sort = Sort.by(Sort.Direction.ASC, orderBy);
        if (Sort.Direction.DESC.name().equals(direction)) {
            sort = Sort.by(Sort.Direction.DESC, orderBy);
        }
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize, sort);
        Page pageResult = scheduleRepository.findAllByTimeCreated(start, end, pageRequest);
        PageResponse pageResponse = PageResponseUtil.buildPageResponse(pageResult);
        return ResponseFactory.success(pageResult.getContent(), pageResponse);
    }

    @Override
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
        entity.setStatus(Constants.SCHEDULE_STATUS.WAITING_CONFIRM);

        return scheduleRepository.save(entity);
    }

    @Override
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

    @Override
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

    @Override
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


    //API for admin
    @Override
    public ScheduleEntity confirmSchedule(Payload payload, Long id, ConfirmScheduleRequest requestBody) {
        ScheduleEntity entity = scheduleRepository.findOneById(id);
        if (entity == null) {
            throw new AppException(ErrorCode.ENTITY_NOT_EXISTS);
        }

        AppUtils.copyPropertiesIgnoreNull(requestBody, entity);
        entity.setEmployeeConfirmId(payload.getUserId());
        entity.setTimeConfirm(DateTimeUtils.getDateTimeNow());
        entity.setStatus(Constants.SCHEDULE_STATUS.CONFIRM);

        String mess = "Lịch hẹn của bạn đã được xác nhận , mời bạn hẹn gặp vào lúc " + requestBody.getTimeSchedule() + "tại " + requestBody.getLocation()
                + ". Vui lòng xem thông tin chi tiết trên hệ thống.";

        senderService.sendMessageViaTele(mess);

        return scheduleRepository.save(entity);
    }

    @Override
    public ScheduleEntity updateStatusSchedule(Payload payload, Long id, Integer status) {
        ScheduleEntity entity = scheduleRepository.findOneById(id);
        if (entity == null) {
            throw new AppException(ErrorCode.ENTITY_NOT_EXISTS);
        }
        if (entity.getStatus().equals(Constants.SCHEDULE_STATUS.DONE)) {
            throw new AppException(ErrorCode.CANT_UPDATE_SCHEDULE);
        }
        entity.setStatus(status);
        return scheduleRepository.save(entity);
    }

    @Override
    public ResponseEntity findAllSchedule(Integer status, String orderBy, String direction, Integer pageNum, Integer pageSize) {
        Sort sort = Sort.by(Sort.Direction.ASC, orderBy);
        if (Sort.Direction.DESC.name().equals(direction)) {
            sort = Sort.by(Sort.Direction.DESC, orderBy);
        }
        PageRequest pageRequest = PageRequest.of(pageNum, pageSize, sort);
        Page pageResult = null;
        if (Objects.nonNull(status)) {
            pageResult = scheduleRepository.findAllByStatus(status, pageRequest);
        } else {
            List<Integer> statusLis = new ArrayList<>();
            statusLis.add(0);
            statusLis.add(1);
            pageResult = scheduleRepository.findAllByStatusIn(pageRequest, statusLis);
        }

        PageResponse pageResponse = PageResponseUtil.buildPageResponse(pageResult);
        return ResponseFactory.success(pageResult.getContent(), pageResponse);
    }
}
