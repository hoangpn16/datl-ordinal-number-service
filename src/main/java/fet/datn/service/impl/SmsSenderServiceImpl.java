package fet.datn.service.impl;

import com.fasterxml.jackson.databind.ObjectMapper;
import fet.datn.repositories.entities.OtpEntity;
import fet.datn.service.SmsSenderService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;


@Service
public class SmsSenderServiceImpl implements SmsSenderService {
    private static Logger logger = LogManager.getLogger(SmsSenderServiceImpl.class);

    @Autowired
    RestTemplate restTemplate;
    @Autowired
    ObjectMapper mapper;

    @Value("${app.tele_url}")
    String urlTele;
    @Value("${app.tele_chat_id}")
    String chatIdTele;
    @Value("${app.otp.tele.template}")
    String templateTele;

    @Value("${otp.mode}")
    String otpMode;


    private static String SMS_TOKEN = null;

    @Override
    public void sendSmsMessage(OtpEntity otpEntity) {
        logger.info("Send sms to phone [{}]", otpEntity.getMobileNumber());
        if (otpMode.equals("TELE")) {
            String content = String.format(templateTele, otpEntity.getOtpCode(), otpEntity.getMobileNumber());
            sendMessageViaTele(content);
        }
    }

    private void sendMessageViaTele(String message) {
        String urlSend = String.format(urlTele, chatIdTele, message);
        logger.info("Sending tele to url [{}]", urlSend);
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Content-Type", "application/json");
        HttpEntity request = new HttpEntity(httpHeaders);
        ResponseEntity<String> response = restTemplate.exchange(urlSend,
                HttpMethod.POST,
                request,
                new ParameterizedTypeReference<String>() {
                });
        logger.info("Received response [{}]", response.getBody());
    }



}
