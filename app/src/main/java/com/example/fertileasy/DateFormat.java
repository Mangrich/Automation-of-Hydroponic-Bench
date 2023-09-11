package com.example.fertileasy;

import android.app.Activity;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;


//Interface responsável por converter dados de data
public interface DateFormat {
     static String getFormatedDateTime(String dateStr, String strReadFormat, String strWriteFormat) {

        String formattedDate = dateStr;

        SimpleDateFormat readFormat = (SimpleDateFormat) new SimpleDateFormat(strReadFormat, Locale.getDefault());
        SimpleDateFormat writeFormat = (SimpleDateFormat) new SimpleDateFormat(strWriteFormat, Locale.getDefault());

        Date date = null;

        try {
            date = readFormat.parse(dateStr);
        } catch (ParseException e) {
        }

        if (date != null) {
            formattedDate = writeFormat.format(date);
        }

        return formattedDate;
    }
}
