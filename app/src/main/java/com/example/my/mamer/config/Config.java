package com.example.my.mamer.config;

import okhttp3.MediaType;

//配置类
public class Config {

//    private class Globle {
//        User user;
//        String token;
//
//        Globle(User user, String token)
//        {
//            this.user = user;
//            this.token = token;
//        }
//    }
//    public static Globle globle = null;
//
//    public void login(User user, String token)
//    {
//        if (Config.globle == null) {
//            Config.globle = new Config.Globle(user, token);
//        }
//    }
    public static final String PHONE_NUMBER="https://mamer.club/api/captchas";
    public static final String PIC_CODE="https://mamer.club/api/verificationCodes";
    public static final String REGISTER="https://mamer.club/api/users";
    public static final String LOGIN="https://mamer.club/api/authorizations";
    public static final String USER_INFORMATION="https://mamer.club/api/user";
    public static final String USER_AVATAR_IMG="https://mamer.club/api/images";
    public static final String NEW_TOPIC_INFO="https://mamer.club/api/topics";
    public static final String USER_RECOMMEND="https://mamer.club/api/actived/users";
    public static final String RECOMMEND_RESOURCE="https://mamer.club/api/links";
    public static final String REFRESH_TOKEN="https://mamer.club/api/authorizations/current";
    public static final String NOTIFICATION_LIST="https://mamer.club/api/user/notifications";
    public static final String NOTIFICATION_STATE="https://mamer.club/api/user/notifications/stats";
    public static final String NOTIFICATION_READ="https://mamer.club/api/user/read/notifications";

    //    http返回码
    public final static int HTTP_OK=201;
    public final static int HTTP_USER_GET_INFORMATION=200;
    public final static int HTTP_DEL_REPLY_OK=204;
    public final static int HTTP_USER_ERROR=401;
    public final static int HTTP_USER_NULL=422;
    public final static int HTTP_USER_FORMAT_ERROR=403;
    public final static int HTTP_OVERNUM=500;
    public final static int HTTP_OVERTIME=429;
    public final static int HTTP_NOT_FOUND=404;

//    循环消息常量
    public final static int DISMISS_DIALOG=7;
    public final static int MESSAGE_ERROR=9;
    public final static int USER_SET_INFORMATION=5;
    public final static int UNLOGIN=10;

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
