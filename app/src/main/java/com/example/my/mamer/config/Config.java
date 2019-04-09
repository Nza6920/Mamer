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

    public class InitData{
        public static final String LINK_CSS = "<style type=\"text/css\"> " +

                "body {margin:0px;padding:0px;}" +

                "p {font-family: PingFang-SC-Regular;" +

                "font-size: 16px;" +

                "color: #333333;" +

                "letter-spacing: 0.2px;" +

                "line-height: 28px;}" +

                "</style>";

        public static final String URL_PIC = "https://timgsa.baidu.com/timg?image&quality=80&size=b9999_10000&sec=1519468205527&di=5c7f3a21b5b2b09a1a0dc5e0eaef5c27&imgtype=0&src=http%3A%2F%2Fimg1.3lian.com%2F2015%2Fa1%2F70%2Fd%2F81.jpg";

        public static final int WRITE_READ_EXTERNAL = 100;//读取请求码

        public static final String CHANGE_IMAGE = "com.example.lttechdemo.action.CHANGE_IMAGE"; //AppWidget的Action

        public static final String EXTRA_DATA = "extra_data";
    }
}
