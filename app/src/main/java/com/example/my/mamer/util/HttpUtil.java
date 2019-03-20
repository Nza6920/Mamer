package com.example.my.mamer.util;

import com.example.my.mamer.config.User;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static com.example.my.mamer.config.Config.CONTENT_TYPEs;

public class HttpUtil {

    private static final MediaType JSON=MediaType.parse("application/json;charset=utf-8");

    public static void sendOkHttpRequest(final String address, final RequestBody requestBody, final okhttp3.Callback callback){
//        创建okHttpClient对象
        OkHttpClient client=new OkHttpClient();
//        创建一个Request
        Request request=new Request.Builder().url(address).post(requestBody).build();
        client.newCall(request).enqueue(callback);
    }

    public static void sendOkHttpRequestGet(String address,okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().addHeader("Authorization", User.getUserPassKey_type()+User.getUserPassKey()).url(address).build();
        client.newCall(request).enqueue(callback);
    }
//获取用户信息
    public static void sendOkHttpRequestPatch(String address,final  RequestBody requestBody,final okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder().patch(requestBody).addHeader("Authorization", User.getUserPassKey_type()+User.getUserPassKey()).addHeader("Content-Type",CONTENT_TYPEs).url(address).build();
        client.newCall(request).enqueue(callback);
    }
//下载头像
    public static void sendOkHttpRequestAvatar(String address,final okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(address).build();
        client.newCall(request).enqueue(callback);
    }
//上传头像
    public static void sendOkHttpRequestAvatars(String address, final RequestBody requestBody,final okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder().addHeader("Authorization", User.getUserPassKey_type()+User.getUserPassKey()).url(address).post(requestBody).build();
        client.newCall(request).enqueue(callback);
    }

}
