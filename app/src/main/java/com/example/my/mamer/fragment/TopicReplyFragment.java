package com.example.my.mamer.fragment;

import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.my.mamer.R;
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

import static com.example.my.mamer.config.Config.HTTP_USER_GET_INFORMATION;
import static com.example.my.mamer.config.Config.MESSAGE_ERROR;

public class TopicReplyFragment extends BaseLazyLoadFragment {

    private ArrayList<ReplyUser> replyUsers=new ArrayList<>();
    private ListView listView;
    private TopicReplyAdapter mAdapter;



    private final Handler msgHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MESSAGE_ERROR:
                    Toast.makeText(getActivity(), (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };




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
    public void onLazyLoad() {

        HttpUtil.sendOkHttpGetTopicReplyList(GlobalTopicReply.reply.replyUser.getEssayId(), new Callback() {
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
                                            replyUser.setContent(jsonObject.getString("content"));
                                            if (jsonObject.has("user")){
                                                JSONObject userStr=jsonObject.getJSONObject("user");
                                                replyUser.setUserId(userStr.getString("id"));
                                                replyUser.setUserName(userStr.getString("name"));
                                                replyUser.setUserImg(userStr.getString("avatar"));
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
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {

            }
        });
    }
}
