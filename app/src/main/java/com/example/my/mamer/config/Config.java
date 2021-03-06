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
    public static final String BASE_URL="http://www.mamer.club/api";
    public static final String PHONE_NUMBER="http://www.mamer.club/api/captchas";
    public static final String PIC_CODE="http://www.mamer.club/api/verificationCodes";
    public static final String REGISTER="http://www.mamer.club/api/users";
    public static final String LOGIN="http://www.mamer.club/api/authorizations";
    public static final String USER_INFORMATION="http://www.mamer.club/api/user";
    public static final String USER_AVATAR_IMG="http://www.mamer.club/api/images";
    public static final String NEW_TOPIC_INFO="http://www.mamer.club/api/topics";
    public static final String USER_RECOMMEND="http://www.mamer.club/api/actived/users";
    public static final String RECOMMEND_RESOURCE="http://www.mamer.club/api/links";
    public static final String REFRESH_TOKEN="http://www.mamer.club/api/authorizations/current";
    public static final String NOTIFICATION_LIST="http://www.mamer.club/api/user/notifications";
    public static final String NOTIFICATION_STATE="http://www.mamer.club/api/user/notifications/stats";
    public static final String NOTIFICATION_READ="http://www.mamer.club/api/user/read/notifications";
    public static final String TOPIC_DIVID="http://www.mamer.club/api/categories";
    public static final String ATTENTION="http://www.mamer.club/api/users/followers";
    public static final String SMS_LOGIN="http://www.mamer.club/api/msg/authorizations";


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
    public final static int HTTP_MORE_REQUEST=429;

//    循环消息常量
    public final static int DISMISS_DIALOG=7;
    public final static int MESSAGE_ERROR=9;
    public final static int USER_SET_INFORMATION=5;
    public final static int UNLOGIN=10;
    public final static int SET_TEXTVIEW=1;
    public final static int KEYBOARD_UP=3;
    public final static int KEYBOARD_DOWN=11;
    public final static int SHOW_DIALOG=17;
//    ========================请求成功
    public final static int REQUEST_OK=1005;
    public final static int REQUEST_OK_TOKENREFRESH=1007;
    public final static int REQUEST_OK_SMSLOGIN=1006;
    public final static int REQUEST_OK_GETLIKE=1001;
    public final static int REQUEST_OK_TopicParticulas=1002;
    public final static int REQUEST_OK_TopicReply=1003;

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
