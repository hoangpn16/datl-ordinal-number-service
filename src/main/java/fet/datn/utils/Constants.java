package fet.datn.utils;

public interface Constants {
    interface STATUS {
        int WAITING = 0;
        int IS_PROCESSING = 1;
        int DONE = 2;
        int CANCELED = 3;
    }

    interface SCHEDULE_STATUS {
        int WAITING_CONFIRM = 0;
        int CONFIRM = 1;
        int DONE = 2;
        int CUSTOMER_CANCELED = 3;
        int ADMIN_CANCELED = 4;
    }
}
