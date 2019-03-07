package com.example.my.mamer.config;

import okhttp3.MediaType;

//配置类
public class Config {
    public static final String PHONE_NUMBER="https://mamer.club/api/captchas";
    public static final String PIC_CODE="https://mamer.club/api/verificationCodes";
    public static final String REGISTER="https://mamer.club/api/users";
    public static final String LOGIN="https://mamer.club/api/authorizations";

    //    http返回码
    public final static int HTTP_OK=201;
    public final static int HTTP_ILLEGAL=401;
    public final static int HTTP_OVERTIME=422;
    public final static int HTTP_OVERNUM=500;
//    循环消息常量
    public final static int DISMISS_DIALOG=7;
    public final static int MESSAGE_ERROR=9;
//
    public static final MediaType JSON=MediaType.parse("application/json;charset=utf-8");
}
