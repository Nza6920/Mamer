package com.example.my.mamer;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.my.mamer.adapter.TopicReplyAdapter;
import com.example.my.mamer.bean.ReplyUser;
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

import static com.example.my.mamer.config.Config.HTTP_USER_GET_INFORMATION;
import static com.example.my.mamer.config.Config.MESSAGE_ERROR;

public class TopicReplyListActivity extends AppCompatActivity {
    private ListView listView;
    private ArrayList<ReplyUser> listData=new ArrayList<>();
    private TopicReplyAdapter mAdapter;
    private TextView tvBack;
    private TextView tvTitle;
    private LoadingDraw loadingDraw;
    private TextView tvReplyCount;
    private int current_page;
    private int total_pages;

    private final Handler msgHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_reply_list);
        loadingDraw=new LoadingDraw(this);
        initUI();
        onDataLoad(1);

        mAdapter=new TopicReplyAdapter(getApplicationContext(),listData);
        listView.setAdapter(mAdapter);
        initEvent();

    }
    public ArrayList<ReplyUser> getListData() {
        return listData;
    }

    public void setListData(ArrayList<ReplyUser> listData) {
        this.listData = listData;
    }

    private void initUI(){
        listView=findViewById(R.id.topic_reply_list);
        tvBack=findViewById(R.id.title_tv_close);
        tvTitle=findViewById(R.id.title_tv_name);
        tvReplyCount=findViewById(R.id.topic_reply_count);

        Drawable tvBackPic=ContextCompat.getDrawable(this,R.mipmap.ic_title_close);
        tvBack.setBackground(tvBackPic);

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

//    获取回复列表
    private void onDataLoad(int page){
        HttpUtil.sendOkHttpGetTopicReplyList(MyApplication.globalTopicReply.reply.replyUser.getEssayId(),page, new Callback() {
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
                                            replyUser.setUserImg(userStr.getString("avatar"));
                                            replyUser.setUserName(userStr.getString("name"));
                                        }
                                        if (jsonObject.has("topic")){
                                            JSONObject topicStr=jsonObject.getJSONObject("topic");
                                            replyUser.setUserName(topicStr.getString("title"));
                                        }
                                        listData.add(replyUser);

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
                            if (jresp.has("meta")){
                                jresp=jresp.getJSONObject("meta");
                                if (jresp.has("pagination")){
                                    jresp=jresp.getJSONObject("pagination");
                                    final String count=jresp.getString("total");
                                    current_page=jresp.getInt("current_page");
                                    total_pages=jresp.getInt("total_pages");
                                    final Runnable setCountRunable=new Runnable() {
                                        @Override
                                        public void run() {
                                            tvTitle.setText("共"+count+"条评论");
                                            tvReplyCount.setText("评论("+count+")");
                                        }};
                                    new Thread(){
                                        public void run(){
                                            msgHandler.post(setCountRunable);
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
//    每个itemView的点击事件，点击可以跳到当前回复对象的个人首页
    private void initEvent(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, long l) {

            }
        });
    }
}
