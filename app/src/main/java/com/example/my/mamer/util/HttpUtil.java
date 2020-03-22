package com.example.my.mamer.util;

import android.util.Log;

import com.example.my.mamer.MyApplication;

import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;

import static com.example.my.mamer.config.Config.CONTENT_TYPEs;
import static com.example.my.mamer.config.Config.NOTIFICATION_LIST;
import static com.example.my.mamer.config.Config.NOTIFICATION_READ;
import static com.example.my.mamer.config.Config.NOTIFICATION_STATE;
import static com.example.my.mamer.config.Config.RECOMMEND_RESOURCE;
import static com.example.my.mamer.config.Config.TOPIC_DIVID;
import static com.example.my.mamer.config.Config.USER_RECOMMEND;

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
        Request request=new Request.Builder().addHeader("Authorization", MyApplication.globalUserInfo.tokenType+MyApplication.globalUserInfo.token ).url(address).build();
        client.newCall(request).enqueue(callback);
    }
//编辑用户信息
    public static void sendOkHttpRequestPatch(String address,final  RequestBody requestBody,final okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder().patch(requestBody).addHeader("Authorization", MyApplication.globalUserInfo.tokenType+MyApplication.globalUserInfo.token ).addHeader("Content-Type",CONTENT_TYPEs).url(address).build();
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

        Request request=new Request.Builder().addHeader("Authorization", MyApplication.globalUserInfo.tokenType+MyApplication.globalUserInfo.token ).url(address).post(requestBody).build();
        client.newCall(request).enqueue(callback);
    }
//    上传新建话题
    public static void sendOkHttpRequestNewTopic(String address,final RequestBody requestBody,final okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();

        Request request=new Request.Builder().addHeader("Authorization", MyApplication.globalUserInfo.tokenType+MyApplication.globalUserInfo.token ).url(address).post(requestBody).build();
        client.newCall(request).enqueue(callback);
    }
//获取所有话题列表
    public static void sendOkHttpGetTopicList(String include,int categoryId,String order,int pageCount,okhttp3.Callback callback){
        String TOPIC_LIST="http://www.mamer.club/api/topics?include="+include+"&category_id="+categoryId+"&order="+order+"&page="+pageCount;
        Log.e("sendOkHttpGetTopicList",TOPIC_LIST);
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(TOPIC_LIST).build();
        client.newCall(request).enqueue(callback);
    }
//    获取某一用户话题列表
    public static void sendOkHttpGetUserTopicList(String userId,int pageCount,okhttp3.Callback callback){
        String USER_TOPIC_LIST="http://www.mamer.club/api/users/"+userId+"/topics?page="+pageCount;
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(USER_TOPIC_LIST).build();
        client.newCall(request).enqueue(callback);
    }
//    获取某一话题详情
    public static void sendOkHttpGetTopicParticulars(String essayId,okhttp3.Callback callback){
        String TOPIC_PARTICULARS="http://www.mamer.club/api/topics/"+essayId+"?include=user";
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(TOPIC_PARTICULARS).build();
        client.newCall(request).enqueue(callback);
    }
//    删除话题
    public static void sendOkHttpDelTopic(String essayId,okhttp3.Callback callback){
        String DEL_TOPIC="http://www.mamer.club/api/topics/"+essayId;
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().delete().addHeader("Authorization", MyApplication.globalUserInfo.tokenType+MyApplication.globalUserInfo.token).addHeader("Content-Type",CONTENT_TYPEs).url(DEL_TOPIC).build();
        client.newCall(request).enqueue(callback);
    }
//    用户发布评论
    public static  void sendOkHttpPostReply(String essayId,RequestBody requestBody,okhttp3.Callback callback){
        String Post_Reply="http://www.mamer.club/api/topics/"+essayId+"/replies";

        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().post(requestBody).addHeader("Authorization",MyApplication.globalUserInfo.tokenType+MyApplication.globalUserInfo.token).url(Post_Reply).build();
        client.newCall(request).enqueue(callback);
    }
//    获取话题回复列表
    public static void sendOkHttpGetTopicReplyList(String essayId,okhttp3.Callback callback){
        String GET_TOPIC_REPLY_LIST="http://www.mamer.club/api/topics/"+essayId+"/replies?include=user&page=1";
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(GET_TOPIC_REPLY_LIST).build();
        client.newCall(request).enqueue(callback);
    }
//    获取用户回复列表
    public static void sendOkHttpGetUserReplyList(String userId,int page,okhttp3.Callback callback){
        String GET_USER_REPLY_LIST="http://www.mamer.club/api/users/"+userId+"/replies?page="+page;
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(GET_USER_REPLY_LIST).build();
        client.newCall(request).enqueue(callback);
    }
//    删除回复
    public static void sendOkHttpDelReply(String essayId,String replyId,okhttp3.Callback callback){
        String DEL_REPLY="http://mamer.club/api/topics/"+essayId+"/replies/"+replyId;
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().delete().addHeader("Authorization",MyApplication.globalUserInfo.tokenType+MyApplication.globalUserInfo.token).url(DEL_REPLY).build();
        client.newCall(request).enqueue(callback);
    }
//    消息通知列表
    public static void sendOkHttpGetNotificationList(okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().get().addHeader("Authorization",MyApplication.globalUserInfo.tokenType+MyApplication.globalUserInfo.token).url(NOTIFICATION_LIST).build();
        client.newCall(request).enqueue(callback);
    }
//    未读消息数
    public static void sendOkHttpGetNotificationState(okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().get().addHeader("Authorization",MyApplication.globalUserInfo.tokenType+MyApplication.globalUserInfo.token).url(NOTIFICATION_STATE).build();
        client.newCall(request).enqueue(callback);

    }
//    消息已读
    public static void sendOkHttpGetNotificationRead(okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().get().addHeader("Authorization",MyApplication.globalUserInfo.tokenType+MyApplication.globalUserInfo.token).url(NOTIFICATION_READ).build();
        client.newCall(request).enqueue(callback);
    }
//    获取活跃用户
    public static void sendOkHttpGetUserRecommend(okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(USER_RECOMMEND).build();
        client.newCall(request).enqueue(callback);
    }
//    获取推荐资源
    public static void sendOkHttpGetRecommendResource(okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(RECOMMEND_RESOURCE).build();
        client.newCall(request).enqueue(callback);
    }
//    刷新token
    public static void sendOkHttpRefreshToken(String address,okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().addHeader("Authorization",MyApplication.globalUserInfo.tokenType+MyApplication.globalUserInfo.token).url(address).build();
        client.newCall(request).enqueue(callback);
    }
//    获取话题分类
    public static void sendOkHttpGetTopicDivid(okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        Request request=new Request.Builder().url(TOPIC_DIVID).build();
        client.newCall(request).enqueue(callback);
    }
}
