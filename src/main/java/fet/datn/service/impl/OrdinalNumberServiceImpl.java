package fet.datn.service.impl;

import fet.datn.exceptions.AppException;
import fet.datn.exceptions.ErrorCode;
import fet.datn.interceptor.Payload;
import fet.datn.repositories.DataDao;
import fet.datn.repositories.EmployeesRepository;
import fet.datn.repositories.OrdinalNumberRepository;
import fet.datn.repositories.entities.EmployeesEntity;
import fet.datn.repositories.entities.OrdinalNumberEntity;
import fet.datn.response.ReportModel;
import fet.datn.service.OrdinalNumberService;
import fet.datn.service.SmsSenderService;
import fet.datn.utils.Constants;
import fet.datn.utils.DateTimeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class OrdinalNumberServiceImpl implements OrdinalNumberService {
    private static final Logger logger = LoggerFactory.getLogger(OrdinalNumberServiceImpl.class);

    @Autowired
    private OrdinalNumberRepository ordinalNumberDao;

    @Autowired
    private SmsSenderService smsSenderService;

    @Autowired
    private EmployeesRepository employeesRepository;

    @Autowired
    private DataDao dataDao;

    @Override
    public List<ReportModel> getReportCustomer(String from, String to) throws ParseException {
        DateTimeUtils.validateDateTime(from, to);

        List<String> dates = DateTimeUtils.getDaysBetweenDates(from, to);

        List<ReportModel> data = dataDao.reportCustomer(from, to);
        List<String> lsDateEx = data.stream().map(e -> e.getDate()).collect(Collectors.toList());

        List<ReportModel> dataFn = new ArrayList<>();
        for (String dt : dates) {
            if (!lsDateEx.contains(dt)) {
                dataFn.add(new ReportModel(dt, 0));
            }
        }
        dataFn.addAll(data);

        if (dataFn.size() > 1) {
            Collections.sort(dataFn, new Comparator<ReportModel>() {
                @Override
                public int compare(ReportModel o1, ReportModel o2) {
                    return o1.getDate().compareTo(o2.getDate());
                }
            });
        }
        return dataFn;
    }

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
        ordinalNum = ordinalNumberDao.save(ordinalNum);
        ordinalNum.setNumberWaiting(ordinalNumberDao.countUserIsWaiting(payload.getUserId()));

        return ordinalNum;
    }

    @Override
    public OrdinalNumberEntity getNumberOfUser(Payload payload) {
        OrdinalNumberEntity oNum = ordinalNumberDao.findOrdinalNumberByUserIdAndCreateTime(payload.getUserId());
        if (oNum == null) {
            return null;
        }
        Integer countWaiting = ordinalNumberDao.countUserIsWaiting(payload.getUserId());
        if (countWaiting != null)
            oNum.setNumberWaiting(countWaiting);
        return oNum;
    }

    @Override
    public Integer getNumberOfUserIsWaiting(Payload payload) {
        return ordinalNumberDao.countUserIsWaiting(payload.getUserId());
    }

    //TODO:  API for admin
    @Override
    public void chooseLocation(Payload payload, Integer location) {
        EmployeesEntity em = employeesRepository.findOneByUserId(payload.getUserId());
        em.setLocation(location);
        employeesRepository.save(em);
    }

    @Override
    public OrdinalNumberEntity handleNumber(Payload payload) {
        OrdinalNumberEntity entity = ordinalNumberDao.findNextOrdinalNumber();
        if (entity == null) {
            throw new AppException(ErrorCode.DONT_HAS_CUSTOMER);
        }
        entity.setStatus(Constants.STATUS.IS_PROCESSING);
        EmployeesEntity em = employeesRepository.findOneByUserId(payload.getUserId());
        String mess = String.format("Xin mời khách hàng có số thứ tự %s vào cửa số %d", entity.getOrdinalNumber(), em.getLocation());
        smsSenderService.sendMessageViaTele(mess);
        return ordinalNumberDao.save(entity);
    }

    @Override
    public OrdinalNumberEntity updateStatus(Long id, Integer status) {
        OrdinalNumberEntity entity = ordinalNumberDao.findOneById(id);
        if (entity == null) {
            logger.info("Not found orfinal id [{}]", id);
            throw new AppException(ErrorCode.ENTITY_NOT_EXISTS);
        }
        entity.setStatus(status);
        if (status.equals(Constants.STATUS.CANCELED)) {
            smsSenderService.sendMessageViaTele("Số thứ tự của bạn đã bị hủy lúc " + DateTimeUtils.getDateTimeNow() + " do bạn đã không có mặt");
        }
        return ordinalNumberDao.save(entity);
    }

    @Override
    public List<OrdinalNumberEntity> getOrdinalNumberByStatus(Integer status) {
        String date = DateTimeUtils.getCurrentDate();
        return ordinalNumberDao.findAllByStatusAndCreatedTimeGreaterThan(status, date);
    }


}
