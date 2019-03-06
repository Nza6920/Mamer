package com.example.my.mamer.config;
//配置类
public class Config {
    public static final String PHONE_NUMBER="https://mamer.club/api/captchas";
    public static final String PIC_CODE="https://mamer.club/api/verificationCodes";
    public static final String REGISTER="https://mamer.club/api/users";

    //    http返回码
    public final static int HTTP_OK=201;
    public final static int HTTP_ILLEGAL=401;
    public final static int HTTP_OVERTIME=422;
//    循环消息常量
    public final static int DISMISS_DIALOG=7;
    public final static int MESSAGE_ERROR=9;
}
