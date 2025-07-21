package ru.practicum.ewm.mainservice.advice.constant;

public final class ValidationConst {
    private ValidationConst() {

    }

    public static final int NAME_MIN = 2;
    public static final int NAME_MAX = 250;

    public static final int EMAIL_MIN = 6;
    public static final int EMAIL_MAX = 254;
    public static final int EMAIL_LOCAL_MAX = 64;
    public static final int EMAIL_LABEL_MAX = 63;

    public static final int CATEGORY_NAME_MAX = 50;

    public static final int EVENT_TITLE_MIN = 3;
    public static final int EVENT_TITLE_MAX = 120;
    public static final int EVENT_ANN_MIN = 20;
    public static final int EVENT_ANN_MAX = 2000;
    public static final int EVENT_DESC_MIN = 20;
    public static final int EVENT_DESC_MAX = 7000;

    public static final int COMP_TITLE_MIN = 1;
    public static final int COMP_TITLE_MAX = 50;


    public static final String EMAIL_REGEX =
            "^[A-Za-z0-9._%+-]{1," + EMAIL_LOCAL_MAX + "}@" +
                    "(?:[A-Za-z0-9-]{1," + EMAIL_LABEL_MAX + "}\\.)+" +
                    "[A-Za-z0-9-]{2," + EMAIL_LABEL_MAX + "}$";
}
