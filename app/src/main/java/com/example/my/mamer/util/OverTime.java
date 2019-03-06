package com.example.my.mamer.util;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class OverTime {

    public OverTime() {
    }
    public Boolean endTime(String end_Time) throws ParseException {
        StringToDate end=new StringToDate();
        Date endTime=end.stringToDate(end_Time);
//        获取当前时间
        Date date=new Date();
        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String dateStr=simpleDateFormat.format(date);
        Date dateNow=end.stringToDate(dateStr);
        Long subDate=endTime.getTime()-dateNow.getTime();
//        超过时间不可点击
        if (subDate<0||subDate==0){
            return false;
        }else {
            return true;
        }

    }
}
