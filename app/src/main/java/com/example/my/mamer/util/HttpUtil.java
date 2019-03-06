package com.example.my.mamer.util;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

public class HttpUtil {

    private static final MediaType JSON=MediaType.parse("application/json;charset=utf-8");

    public static void sendOkHttpRequest(final String address, final RequestBody requestBody, final okhttp3.Callback callback){
//        创建okHttpClient对象
        OkHttpClient client=new OkHttpClient();

//        创建一个Request
        Request request=new Request.Builder().url(address).post(requestBody).build();
        client.newCall(request).enqueue(callback);

    }

}
