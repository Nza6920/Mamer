package com.example.my.mamer.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.my.mamer.LoginActivity;
import com.example.my.mamer.MyApplication;
import com.example.my.mamer.R;
import com.example.my.mamer.ShowReplyUserActivity;
import com.example.my.mamer.adapter.TopicReplyAdapter;
import com.example.my.mamer.bean.ReplyUser;
import com.example.my.mamer.config.GlobalTopicReply;
import com.example.my.mamer.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.my.mamer.config.Config.HTTP_DEL_REPLY_OK;
import static com.example.my.mamer.config.Config.HTTP_USER_ERROR;
import static com.example.my.mamer.config.Config.HTTP_USER_GET_INFORMATION;
import static com.example.my.mamer.config.Config.MESSAGE_ERROR;
import static com.example.my.mamer.config.Config.REFRESH_TOKEN;

public class TopicReplyFragment extends BaseLazyLoadFragment {

    private ArrayList<ReplyUser> replyUsers=new ArrayList<>();
    private ListView listView;
    private TopicReplyAdapter mAdapter;



    private  final Handler msgHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case MESSAGE_ERROR:
                    Toast.makeText(getActivity(), (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
            return false;
        }
    });


//初始化视图
    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view=inflater.inflate(R.layout.fragment_topic_reply,container,false);
        listView=view.findViewById(R.id.reply_user_list);
        mAdapter=new TopicReplyAdapter(getContext(),getReplyUsers());
        listView.setAdapter(mAdapter);

        return view;
    }
    public ArrayList<ReplyUser> getReplyUsers() {
        return replyUsers;
    }

    public void setReplyUsers(ArrayList<ReplyUser> replyUsers) {
        this.replyUsers = replyUsers;
    }

//    数据
    @Override
    public void onLazyLoad(int page) {

        HttpUtil.sendOkHttpGetTopicReplyList(GlobalTopicReply.reply.replyUser.getEssayId(),page, new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {

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
                        jresp=new JSONObject(response.body().string());
                        switch (response.code()){
                            case HTTP_USER_GET_INFORMATION:
                                if (jresp.has("data")){
                                    jsonArray=jresp.getJSONArray("data");
//                               有评论
                                    if (jsonArray!=null){
                                        for (int i=0;i<jsonArray.length();i++){
                                            JSONObject jsonObject=jsonArray.getJSONObject(i);
                                            ReplyUser replyUser=new ReplyUser();
                                            replyUser.setReplyId(jsonObject.getString("id"));
                                            replyUser.setEssayId(jsonObject.getString("topic_id"));
                                            replyUser.setContent(jsonObject.getString("content"));
                                            if (jsonObject.has("user")){
                                                JSONObject userStr=jsonObject.getJSONObject("user");
                                                replyUser.setUserId(userStr.getString("id"));
                                                replyUser.setUserName(userStr.getString("name"));
                                                replyUser.setUserImg(userStr.getString("avatar"));
                                                replyUser.setUserInfo(userStr.getString("introduction"));
                                                replyUser.setEmil(userStr.getString("email"));
                                            }

                                            replyUsers.add(replyUser);

                                        }
                                        final Runnable setAvatarRunable=new Runnable() {
                                            @Override
                                            public void run() {
                                                mAdapter.notifyDataSetChanged();
                                            }};
                                        new Thread(){
                                            public void run(){
                                                msgHandler.post(setAvatarRunable);
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


    }
    @Override
    public void initEvent() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, long l) {
                if (MyApplication.globalUserInfo.token!=null){
                    if (replyUsers.get(position).getUserId().equals(MyApplication.globalUserInfo.user.getUserId())){
                        AlertDialog.Builder builder=new AlertDialog.Builder(getContext());
                        builder.setPositiveButton("删除该回复", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                view.setVisibility(View.GONE);
                                delReply( replyUsers.get(position).getEssayId(),replyUsers.get(position).getReplyId());

                            }
                        })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
//不做处理
                                    }
                                })
                                .setNeutralButton("查看回复用户", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                                        editor.putString("userName",replyUsers.get(position).getUserName());
                                        editor.putString("userImg",replyUsers.get(position).getUserImg());
                                        editor.putString("userInfo",replyUsers.get(position).getUserInfo());
                                        editor.putString("email",replyUsers.get(position).getEmil());
                                        editor.apply();

                                        Intent intent=new Intent(getContext(),ShowReplyUserActivity.class);
                                        startActivity(intent);
                                    }
                                });
                        builder.show();
                    }
                }else {
                    SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                    editor.putString("userName",replyUsers.get(position).getUserName());
                    editor.putString("userImg",replyUsers.get(position).getUserImg());
                    editor.putString("userInfo",replyUsers.get(position).getUserInfo());
                    editor.putString("email",replyUsers.get(position).getEmil());
                    editor.apply();

                    Intent intent=new Intent(getContext(),ShowReplyUserActivity.class);
                    startActivity(intent);
                }

            }

        });
    }


    private void delReply(String essayId,String replyId){

        HttpUtil.sendOkHttpDelReply(essayId, replyId, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                Message msg2=new Message();
                msg2.what = MESSAGE_ERROR;
                msg2.obj = "服务器异常,请检查网络";
                msgHandler.sendMessage(msg2);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jresp=new JSONObject(response.body().string());
                    switch (response.code()){
                        case HTTP_DEL_REPLY_OK:

                            Message msg4=new Message();
                            msg4.what=MESSAGE_ERROR;
                            msg4.obj="删除成功";
                            msgHandler.sendMessage(msg4);
                            break;
                        case HTTP_USER_ERROR:

                            HttpUtil.sendOkHttpRefreshToken(REFRESH_TOKEN, new Callback() {
                                @Override
                                public void onFailure(Call call, IOException e) {
                                    Message msg1 = new Message();
                                    msg1.what = MESSAGE_ERROR;
                                    msg1.obj = "登录已失效，请重新登录";
                                    msgHandler.sendMessage(msg1);

                                    Intent intent1=new Intent(getContext(),LoginActivity.class);
                                    startActivity(intent1);
                                }

                                @Override
                                public void onResponse(Call call, Response response) throws IOException {
                                    try {
                                        JSONObject jrep= new JSONObject(response.body().string());

                                        MyApplication.globalUserInfo.token=jrep.getString("access_token");
                                        MyApplication.globalUserInfo.tokenType=jrep.getString("token_type");

                                        Message msg1 = new Message();
                                        msg1.what = MESSAGE_ERROR;
                                        msg1.obj = "已刷新，请重试";
                                        msgHandler.sendMessage(msg1);

                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            });
                            break;
                        default:
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
