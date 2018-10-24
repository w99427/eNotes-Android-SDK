package io.enotes.examples.common.utils;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class TransitionDate {
    public static final String YMDHM = "yyyy-MM-dd HH:mm";
    public static final String YMD = "yyyy/MM/dd";
    public static final String YMD_EN = "dd/MM/yyyy";

    public static String DateToStr(long date, String frmtstr) {
        Date d = new Date(date);
        if (d == null)
            return "";
        SimpleDateFormat dd = new SimpleDateFormat(frmtstr);
        String date1 = dd.format(date);
        return date1;
    }

    public static String DateToStr(long date) {
        Date d = new Date(date);
        if (d == null)
            return "";
        SimpleDateFormat dd = new SimpleDateFormat(YMD);
        if (Locale.getDefault().getLanguage().equals("en")) {
            dd = new SimpleDateFormat(YMD_EN);
        }
        String date1 = dd.format(date);
        return date1;
    }
}
