package ru.practicum.ewm.mainservice.advice.constant;

public interface ValidationConst {
    int NAME_MIN = 2;
    int NAME_MAX = 250;

    int EMAIL_MIN = 6;
    int EMAIL_MAX = 254;
    int EMAIL_LOCAL_MAX = 64;
    int EMAIL_LABEL_MAX = 63;

    int CATEGORY_NAME_MAX = 50;

    int EVENT_TITLE_MIN = 3;
    int EVENT_TITLE_MAX = 120;
    int EVENT_ANN_MIN = 20;
    int EVENT_ANN_MAX = 2000;
    int EVENT_DESC_MIN = 20;
    int EVENT_DESC_MAX = 7000;

    int COMP_TITLE_MIN = 1;
    int COMP_TITLE_MAX = 50;


    String EMAIL_REGEX =
            "^[A-Za-z0-9._%+-]{1," + EMAIL_LOCAL_MAX + "}@" +
                    "(?:[A-Za-z0-9-]{1," + EMAIL_LABEL_MAX + "}\\.)+" +
                    "[A-Za-z0-9-]{2," + EMAIL_LABEL_MAX + "}$";
}
