package fet.datn.service;

import fet.datn.repositories.entities.OtpEntity;

public interface SmsSenderService {
    void sendSmsMessage(OtpEntity otpEntity);
}
