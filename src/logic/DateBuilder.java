package logic;

import java.util.Calendar;
import java.util.Date;

/** Returns a Date object at a fixed time for each day */
public class DateBuilder {

    private static final int HOUR = 03;
    private static final int MINUTE_CANCELLATION = 40;
    private static final int MINUTE_CLOSING = 41;
    private static final int SECOND = 0;
    private static final int MILLISECOND = 0;
    private static final int ONE_DAY = 1;


    public static Date getCancellationTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, HOUR);
        calendar.set(Calendar.MINUTE, MINUTE_CANCELLATION);
        calendar.set(Calendar.SECOND, SECOND);
        calendar.set(Calendar.MILLISECOND, MILLISECOND);

        calendar.add(Calendar.DATE, 1);

        return calendar.getTime();
    }

    public static Date getMarketOnCloseTime() {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, HOUR);
        calendar.set(Calendar.MINUTE, MINUTE_CLOSING);
        calendar.set(Calendar.SECOND, SECOND);
        calendar.set(Calendar.MILLISECOND, MILLISECOND);

        calendar.add(Calendar.DATE, ONE_DAY);

        return calendar.getTime();
    }
}
