package com.example.my.mamer.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class StringToDate  extends Date{
    public StringToDate() {
    }

    public static Date stringToDate(String strTime)throws ParseException{
        SimpleDateFormat format=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Date date=format.parse(strTime);
        return date;
    }

    public static  String stringToShort(String srtLongTime){
        String str=srtLongTime.substring(0,10);
        return str;

    }
}
