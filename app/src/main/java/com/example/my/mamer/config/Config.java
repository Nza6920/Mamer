package com.example.my.mamer.config;

import okhttp3.MediaType;

//配置类
public class Config {
    public static final String PHONE_NUMBER="https://mamer.club/api/captchas";
    public static final String PIC_CODE="https://mamer.club/api/verificationCodes";
    public static final String REGISTER="https://mamer.club/api/users";
    public static final String LOGIN="https://mamer.club/api/authorizations";
    public static final String USER_INFORMATION="https://mamer.club/api/user";
    public static final String USER_AVATAR_IMG="https://mamer.club/api/images";

    //    http返回码
    public final static int HTTP_OK=201;
    public final static int HTTP_USER_GET_INFORMATION=200;
    public final static int HTTP_USER_ERROR=401;
    public final static int HTTP_USER_NULL=422;
    public final static int HTTP_USER_FORMAT_ERROR=403;
    public final static int HTTP_OVERNUM=500;
    public final static int HTTP_OVERTIME=429;
//    循环消息常量
    public final static int DISMISS_DIALOG=7;
    public final static int MESSAGE_ERROR=9;
    public final static int USER_SET_INFORMATION=5;

//    拍摄
    public final static int RESULT_CAMERA_IMAGE=6;
//    相册
    public final static int RESULT_LODA_IMAGE=8;
//    裁剪
    public final static int RESULT_CROP=13;
//
    public static final MediaType JSON=MediaType.parse("application/json;charset=utf-8");
    public static final String CONTENT_TYPEs="application/x-www-form-urlencoded;charset=utf-8";
    public static final MediaType MEDIA_TYPE_IMAGE=MediaType.parse("image/*");
}
