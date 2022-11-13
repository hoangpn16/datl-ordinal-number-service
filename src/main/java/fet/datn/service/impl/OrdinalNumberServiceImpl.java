package fet.datn.service.impl;

import fet.datn.exceptions.AppException;
import fet.datn.exceptions.ErrorCode;
import fet.datn.interceptor.Payload;
import fet.datn.repositories.EmployeesRepository;
import fet.datn.repositories.OrdinalNumberRepository;
import fet.datn.repositories.entities.EmployeesEntity;
import fet.datn.repositories.entities.OrdinalNumberEntity;
import fet.datn.service.OrdinalNumberService;
import fet.datn.service.SmsSenderService;
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

    @Autowired
    private SmsSenderService smsSenderService;

    @Autowired
    private EmployeesRepository employeesRepository;

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
        if(entity == null){
            throw new AppException(ErrorCode.DONT_HAS_CUSTOMER);
        }
        entity.setStatus(Constants.STATUS.IS_PROCESSING);
        EmployeesEntity em = employeesRepository.findOneByUserId(payload.getUserId());
        String mess = String.format("Xin mời khách hàng có số thứ tự %s vào cửa số %d", entity.getOrdinalNumber(), em.getLocation());
        smsSenderService.sendMessageViaTele(mess);
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
