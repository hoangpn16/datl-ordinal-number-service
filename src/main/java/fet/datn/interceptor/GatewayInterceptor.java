package fet.datn.interceptor;

import fet.datn.exceptions.AppException;
import fet.datn.exceptions.ErrorCode;
import fet.datn.repositories.CustomerRepository;
import fet.datn.repositories.EmployeesRepository;
import fet.datn.repositories.TokenRepository;
import fet.datn.repositories.entities.CustomerEntity;
import fet.datn.repositories.entities.EmployeesEntity;
import fet.datn.repositories.entities.TokenEntity;
import fet.datn.utils.Definition;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.ThreadContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class GatewayInterceptor implements HandlerInterceptor {

    private static final Logger logger = LoggerFactory.getLogger(GatewayInterceptor.class);

    @Autowired
    private EmployeesRepository employeeDao;

    @Autowired
    private TokenRepository tokenRepository;

    @Autowired
    private CustomerRepository customerRepository;

    private static final Map<String, Payload> authenCaching = new ConcurrentHashMap<>();

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String correlationId = request.getHeader(Definition.REQUEST_ID_KEY);
        String ipClient = request.getRemoteAddr();
        if (correlationId == null) {
            correlationId = RandomStringUtils.randomAlphabetic(6);
        }
        ThreadContext.put(Definition.REQUEST_ID_KEY, correlationId);
        logger.info("========== Start process request [{}]:[{}] from IP [{}]", request.getMethod(), request.getServletPath(), ipClient);
        request.setAttribute(Definition.PROCESS_TIME_KEY, System.currentTimeMillis());
        return verifyRequest(request);
    }

    @Override
    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView) throws Exception {
        Long startTime = (Long) request.getAttribute(Definition.PROCESS_TIME_KEY);
        Long processTime = System.currentTimeMillis() - startTime;
        logger.info("========= End process request [{}]:[{}] with [{}]. Processing time [{}]", request.getMethod(), request.getServletPath(), response.getStatus(), processTime);
    }

    private boolean verifyRequest(HttpServletRequest request) {
        String httpMethod = request.getMethod();
        String servletPath = request.getServletPath();
        if (servletPath.contains("swagger")) {
            return true;
        }
        String jwtToken = request.getHeader(Definition.AUTHORIZATION_KEY);
        if (!StringUtils.isBlank(jwtToken)) {

            Payload payload = getPayload(jwtToken);


            request.setAttribute(Definition.PAYLOAD, payload);
        }

        logger.info("Request validated. Start forward request to backend");
        return true;
    }

    private Payload getPayload(String jwtToken) {
        Payload payload = authenCaching.get(jwtToken);
        if (payload != null) {
            checkExpiredTime(payload);
            return payload;
        }

        payload = new Payload();
        TokenEntity token = tokenRepository.findOneByToken(jwtToken);
        if (token == null) {
            logger.info("Not found token [{}]", jwtToken);
            throw new AppException(ErrorCode.TOKEN_NOT_FOUND);
        }
        payload.setToken(jwtToken);
        payload.setExpiredTime(token.getExpiredTime());
        if (token.getExpiredTime().before(new Date(System.currentTimeMillis()))) {
            throw new AppException(ErrorCode.TOKEN_EXPIRED);
        }

        // TODO gán thông tin cho payload
        if (jwtToken.contains("Admin")) {
            EmployeesEntity employeesEntity = employeeDao.findOneByUserId(token.getUserId());
            if (employeesEntity == null) {
                logger.info("Not found user id [{}] with token [{}]", token.getUserId(), token.getToken());
                throw new AppException(ErrorCode.ENTITY_NOT_EXISTS);
            }

            payload.setUserId(employeesEntity.getUserId());
            payload.setUserName(employeesEntity.getUserName());

        } else if (jwtToken.contains("Customer")) {
            CustomerEntity customerEntity = customerRepository.findOneByUserId(token.getUserId());

            payload.setUserId(customerEntity.getUserId());
            payload.setUserName(customerEntity.getPhone());
        }
        saveToCache(payload);

        return payload;
    }

    private void checkExpiredTime(Payload payload) {
        if (payload.getExpiredTime().before(new Date(System.currentTimeMillis()))) {
            removeTokenFromCache(payload.getToken());
            throw new AppException(ErrorCode.TOKEN_EXPIRED);
        }
    }

    public void saveToCache(Payload payload) {
        authenCaching.put(payload.getToken(), payload);
    }

    public void removeTokenFromCache(String accessToken) {
        authenCaching.remove(accessToken);
    }

}
