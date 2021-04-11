package com.example.dbprovidertest.Utils;

import java.text.SimpleDateFormat;

public class TimerUtils {
    public static String getCurrentTime(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat( "yyyy-MM-dd hh:mm:ss");
        String time = simpleDateFormat.format(System.currentTimeMillis());
        return time;
    }
}

