package com.example.my.mamer.util;

import com.example.my.mamer.config.GlobalUserInfo;

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
//获取用户信息
    public static void sendOkHttpRequestGet(String address,okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().addHeader("Authorization", GlobalUserInfo.userInfo.tokenType+GlobalUserInfo.userInfo.token ).url(address).build();
        client.newCall(request).enqueue(callback);
    }
//编辑用户信息
    public static void sendOkHttpRequestPatch(String address,final  RequestBody requestBody,final okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder().patch(requestBody).addHeader("Authorization", GlobalUserInfo.userInfo.tokenType+GlobalUserInfo.userInfo.token ).addHeader("Content-Type",CONTENT_TYPEs).url(address).build();
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

        Request request=new Request.Builder().addHeader("Authorization", GlobalUserInfo.userInfo.tokenType+GlobalUserInfo.userInfo.token ).url(address).post(requestBody).build();
        client.newCall(request).enqueue(callback);
    }
//    上传新建话题
    public static void sendOkHttpRequestNewTopic(String address,final RequestBody requestBody,final okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder().addHeader("Authorization", GlobalUserInfo.userInfo.tokenType+GlobalUserInfo.userInfo.token ).url(address).post(requestBody).build();
        client.newCall(request).enqueue(callback);
    }
//获取所有话题列表
    public static void sendOkHttpGetTopicList(int categoryId,String order,int pageCount,okhttp3.Callback callback){
        String TOPIC_LIST="https://mamer.club/api/topics?include=user&category_id="+categoryId+"&order="+order+"&page="+pageCount;
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().addHeader("Authorization", GlobalUserInfo.userInfo.tokenType+GlobalUserInfo.userInfo.token ).url(TOPIC_LIST).build();
        client.newCall(request).enqueue(callback);
    }
//    获取某一用户话题列表
    public static void sendOkHttpGetUserTopicList(int pageCount,okhttp3.Callback callback){
        String USER_TOPIC_LIST="https://mamer.club/api/users/:id/topics?include=&page="+pageCount;
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().addHeader("Authorization", GlobalUserInfo.userInfo.tokenType+GlobalUserInfo.userInfo.token ).url(USER_TOPIC_LIST).build();
        client.newCall(request).enqueue(callback);
    }
//    获取某一话题详情
    public static void sendOkHttpGetTopicParticulars(okhttp3.Callback callback){
        String TOPIC_PARTICULARS="https://mamer.club/api/topics/:id?include=user";
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().addHeader("Authorization", GlobalUserInfo.userInfo.tokenType+GlobalUserInfo.userInfo.token ).url(TOPIC_PARTICULARS).build();
        client.newCall(request).enqueue(callback);
    }
}
