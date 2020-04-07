package com.example.my.mamer;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.my.mamer.adapter.NotificationAdapter;
import com.example.my.mamer.bean.NotificationUser;
import com.example.my.mamer.util.HttpUtil;
import com.example.my.mamer.util.LoadingDraw;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.my.mamer.config.Config.DISMISS_DIALOG;
import static com.example.my.mamer.config.Config.HTTP_USER_GET_INFORMATION;
import static com.example.my.mamer.config.Config.MESSAGE_ERROR;

public class NotificationFragment extends Fragment {
    private ArrayList<NotificationUser> notificationUsers=new ArrayList<>();
    private ListView listView;
    private NotificationAdapter mAdapter = null;
    private LoadingDraw loadingDraw;
    private TextView tvNotificationInfo;
    private TextView tvNotificationRead;
    private LinearLayout layoutread;
    private LinearLayout layoutUnlogin;
    private LinearLayout layoutText;
    private FrameLayout layoutContent;

//    防止内存泄漏,最正规的写法！！！！！
//    private MyHandler handler=new MyHandler(this);
//    static class MyHandler extends android.os.Handler {
//        WeakReference weakReference;
//        public MyHandler(NotificationFragment notificationFragment){
//            weakReference =new WeakReference(notificationFragment);
//        }
//        @Override
//        public void handleMessage(Message msg){
//
//            }
//        }
//    }

    private Handler msgHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case DISMISS_DIALOG:
                    ((LoadingDraw)msg.obj).dismiss();
                    break;
                case MESSAGE_ERROR:
                    Toast.makeText(getContext(),(String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                    default:
                        break;
            }
            return false;
        }
    });

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {
        loadingDraw=new LoadingDraw(getContext());
        final View view=inflater.inflate(R.layout.fragment_notification,container,false);
        tvNotificationInfo=view.findViewById(R.id.notification_info);
        tvNotificationRead=view.findViewById(R.id.notification_read);
        layoutread=view.findViewById(R.id.notification_read_layout);
        layoutUnlogin=view.findViewById(R.id.notification_unlogin);
        layoutText=view.findViewById(R.id.notification_text);
        layoutContent=view.findViewById(R.id.notification_content);
        listView=view.findViewById(R.id.notification_list);
        this.mAdapter=new NotificationAdapter(getContext());

        return view;
    }

    public ArrayList<NotificationUser> getNotificationUsers() {
        return notificationUsers;
    }

    public void setNotificationUsers(ArrayList<NotificationUser> notificationUsers) {
        this.notificationUsers = notificationUsers;
    }

    public void onLazyLoad(int page) {
        if (MyApplication.globalUserInfo.token!=null){
            loadingDraw.show();
            HttpUtil.sendOkHttpGetNotificationList(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    Message msg1 = new Message();
                    msg1.what = DISMISS_DIALOG;
                    msg1.obj=loadingDraw;
                    msgHandler.sendMessage(msg1);

                    Message msg2 = new Message();
                    msg2.what = MESSAGE_ERROR;
                    msg2.obj = "服务器异常,请检查网络";
                    msgHandler.sendMessage(msg2);
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    Message msg1 = new Message();
                    msg1.what = DISMISS_DIALOG;
                    msg1.obj=loadingDraw;
                    msgHandler.sendMessage(msg1);

                    JSONObject jresp = null;
                    JSONArray jsonArray=null;

                    try {
                        jresp = new JSONObject(response.body().string());

                        switch (response.code()){
                            case HTTP_USER_GET_INFORMATION:
                                if (jresp.has("data")){
                                    jsonArray=jresp.getJSONArray("data");
                                    if (jsonArray==null){
                                        Message msg2 = new Message();
                                        msg2.what = MESSAGE_ERROR;
                                        msg2.obj="当前还没有消息哦";
                                        msgHandler.sendMessage(msg2);
                                    }else {
                                        int jsonSize = jsonArray.length();
                                        for (int i = 0; i < jsonSize; i++) {
                                            JSONObject jsonObject = jsonArray.getJSONObject(i);
                                            NotificationUser notificationUser=new NotificationUser();
                                            if (jsonObject.has("data")) {
                                                JSONObject userStr=jsonObject.getJSONObject("data");
                                                notificationUser.setUserName(userStr.getString("user_name"));
                                                notificationUser.setUserImg(userStr.getString("user_avatar"));
                                                notificationUser.setTopicName(userStr.getString("topic_title"));
                                                notificationUser.setUserContent(userStr.getString("reply_content"));
                                            }
                                            notificationUsers.add(notificationUser);
                                        }
                                        final Runnable setData=new Runnable() {
                                            @Override
                                            public void run() {
                                                if (notificationUsers.size()!=0&&mAdapter!=null){
                                                    Log.e("Active设置数据:","-----------------------");
                                                    listView.setAdapter(mAdapter);
                                                    mAdapter.updateData(notificationUsers);
                                                }
                                            }};
                                        new Thread(){
                                            public void run(){
                                                msgHandler.post(setData);
                                            }
                                        }.start();
                                    }
                                }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
            Message msg1 = new Message();
            msg1.what = DISMISS_DIALOG;
            msg1.obj=loadingDraw;
            msgHandler.sendMessage(msg1);
        }

    }

    public void initEvent() {
        tvNotificationRead.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                NotificationRead();
            }
        });

    }
//一键已读
    private void NotificationRead(){
        loadingDraw.show();
        HttpUtil.sendOkHttpGetNotificationRead(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg1=new Message();
                msg1.what=DISMISS_DIALOG;
                msg1.obj=loadingDraw;
                msgHandler.sendMessage(msg1);

                Message msg2 = new Message();
                msg2.what = MESSAGE_ERROR;
                msg2.obj = "服务器异常,请检查网络";
                msgHandler.sendMessage(msg2);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                switch (response.code()){
                    case HTTP_USER_GET_INFORMATION:
                        Message msg1=new Message();
                        msg1.what=DISMISS_DIALOG;
                        msg1.obj=loadingDraw;
                        msgHandler.sendMessage(msg1);

                        Message msg2=new Message();
                        msg2.what=MESSAGE_ERROR;
                        msg2.obj="没有未读信息";
                        msgHandler.sendMessage(msg2);
                        break;
                        default:
                            break;
                }


            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
        if (MyApplication.globalUserInfo.token!=null){
            initAdapterView();
            onLazyLoad(1);
        }
    }

    private void initAdapterView(){
            Message msg1 = new Message();
            msg1.what = DISMISS_DIALOG;
            msg1.obj=loadingDraw;
            msgHandler.sendMessage(msg1);

            layoutUnlogin.setVisibility(View.GONE);
            layoutText.setVisibility(View.VISIBLE);
            layoutContent.setVisibility(View.VISIBLE);

    }
}
