package fet.datn.utils;

import fet.datn.exceptions.AppException;
import fet.datn.exceptions.ErrorCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtils {
    private static final Logger logger = LoggerFactory.getLogger(DateTimeUtils.class);
    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static SimpleDateFormat formatter2 = new SimpleDateFormat("yyyy-MM-dd");


    public static String getDateTimeNow() {
        Date dateNow = new Date(System.currentTimeMillis());
        return formatter.format(dateNow);
    }

    public static String getCurrentDate() {
        Date dateNow = new Date(System.currentTimeMillis());
        String date = formatter2.format(dateNow) + " 00:00:00";
        return date;
    }

    public static void validateDateTime(String from, String to) {
        // Validate dateFrom and dateTo
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        try {
            Date dateFrom = formatter.parse(from);
            Date dateTo = formatter.parse(to);
            if (from.compareTo(to) > 0) {
                logger.info("DateFrom {} need before DateTo {}", from, to);
                throw new AppException(ErrorCode.DATE_NOT_VALID);
            }
        } catch (ParseException e) {
            logger.info("Datetime is not valid", from, to);
            throw new AppException(ErrorCode.DATE_NOT_VALID);
        }
    }


}
