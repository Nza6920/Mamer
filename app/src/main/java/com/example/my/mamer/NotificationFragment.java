package com.example.my.mamer;

import android.os.Handler;
import android.os.Message;
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
import com.example.my.mamer.fragment.BaseLazyLoadFragment;
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
import static com.example.my.mamer.config.Config.USER_SET_INFORMATION;

public class NotificationFragment extends BaseLazyLoadFragment {
    private ArrayList<NotificationUser> notificationUsers=new ArrayList<>();
    private ListView listView;
    private NotificationAdapter mAdapter;
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
                    Toast.makeText(getActivity(),(String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                case USER_SET_INFORMATION:
                    mAdapter.notifyDataSetChanged();
                    Log.e("Tag","数据刷新完成");
                    break;
                    default:
                        break;
            }
            return false;
        }
    });

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        loadingDraw=new LoadingDraw(getContext());
        final View view=inflater.inflate(R.layout.fragment_notification,container,false);
        tvNotificationInfo=view.findViewById(R.id.notification_info);
        tvNotificationRead=view.findViewById(R.id.notification_read);
        layoutread=view.findViewById(R.id.notification_read_layout);
        layoutUnlogin=view.findViewById(R.id.notification_unlogin);
        layoutText=view.findViewById(R.id.notification_text);
        layoutContent=view.findViewById(R.id.notification_content);

        if (MyApplication.globalUserInfo.token!=null){
            Message msg1 = new Message();
            msg1.what = DISMISS_DIALOG;
            msg1.obj=loadingDraw;
            msgHandler.sendMessage(msg1);

            final Runnable setRunable=new Runnable() {
                @Override
                public void run() {
                    layoutUnlogin.setVisibility(View.GONE);
                    layoutText.setVisibility(View.VISIBLE);
                    layoutContent.setVisibility(View.VISIBLE);
                    listView=view.findViewById(R.id.notification_list);
                    mAdapter=new NotificationAdapter(getContext(),getNotificationUsers());
                    listView.setAdapter(mAdapter);
                }};
            new Thread(){
                public void run(){
                    msgHandler.post(setRunable);
                }
            }.start();

        }else {
            Message msg1 = new Message();
            msg1.what = DISMISS_DIALOG;
            msg1.obj=loadingDraw;
            msgHandler.sendMessage(msg1);


        }


        return view;
    }



    public ArrayList<NotificationUser> getNotificationUsers() {
        return notificationUsers;
    }

    public void setNotificationUsers(ArrayList<NotificationUser> notificationUsers) {
        this.notificationUsers = notificationUsers;
    }

    @Override
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
                    JSONObject jresp = null;
                    JSONArray jsonArray=null;

                    try {
                        jresp = new JSONObject(response.body().string());

                        switch (response.code()){
                            case HTTP_USER_GET_INFORMATION:
                                Message msg1 = new Message();
                                msg1.what = DISMISS_DIALOG;
                                msg1.obj=loadingDraw;
                                msgHandler.sendMessage(msg1);

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
                                        Message msg3 = new Message();
                                        msg3.what = USER_SET_INFORMATION;
                                        msgHandler.sendMessage(msg3);
                                    }

                                }

                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            });
        }

    }
    @Override
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
}
