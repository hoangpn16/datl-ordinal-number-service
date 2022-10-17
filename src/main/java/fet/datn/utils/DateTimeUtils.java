package fet.datn.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateTimeUtils {
    private static final Logger logger = LoggerFactory.getLogger(DateTimeUtils.class);
    private static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");


    public static String getDateTimeNow() {
        Date dateNow = new Date(System.currentTimeMillis());
        return formatter.format(dateNow);
    }


}
