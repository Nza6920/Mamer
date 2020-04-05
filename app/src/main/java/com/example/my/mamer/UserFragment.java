package com.example.my.mamer;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.my.mamer.adapter.recycleview.UserBaseAdapter;
import com.example.my.mamer.bean.ReplyUser;
import com.example.my.mamer.bean.TopicContent;
import com.example.my.mamer.util.HttpUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.my.mamer.config.Config.HTTP_OK;
import static com.example.my.mamer.config.Config.HTTP_USER_GET_INFORMATION;
import static com.example.my.mamer.config.Config.HTTP_USER_NULL;
import static com.example.my.mamer.config.Config.MESSAGE_ERROR;
import static com.example.my.mamer.config.Config.UNLOGIN;
import static com.example.my.mamer.config.Config.USER_SET_INFORMATION;

public class UserFragment extends Fragment {

//    用户
    private LinearLayout userMamerEnergyLayout;
    private LinearLayout userUnloginLayout;
    private ImageView userAvatar;
    private TextView tvUserName;
    private LinearLayout layoutToUserMine;
    private LinearLayout layoutIntroduction;
    private TextView tvUserIntroduction;

    private String count;
//    话题
    private ArrayList<TopicContent> contentArrayList=new ArrayList<>();
    private TextView tvTopicsCount;
    private LinearLayout layoutTopics;
    private LinearLayout layoutTopicsItem;
//    回复
    private ArrayList<ReplyUser> replyArrayList =new ArrayList<>();
    private TextView tvReplysCount;
    private LinearLayout layoutReplys;
    private LinearLayout layoutReplysItem;
//    收藏
    //    UI
    private final Handler msgHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){

                case HTTP_USER_NULL:
                    Toast.makeText(getActivity(),(String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                case HTTP_OK:
                    Toast.makeText(getActivity(),(String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                case UNLOGIN:
                    Toast.makeText(getActivity(),"登陆以体验更多",Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_ERROR:
                    Toast.makeText(getActivity(),(String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                case USER_SET_INFORMATION:
                    UserBaseAdapter mAdapter= (UserBaseAdapter) msg.obj;
                    mAdapter.notifyDataSetChanged();
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_user,container,false);
//        初始化控件
        userAvatar=view.findViewById(R.id.user_avatar);
        tvUserName=view.findViewById(R.id.user_name);
        layoutToUserMine=view.findViewById(R.id.user_mine_to);
        tvUserIntroduction=view.findViewById(R.id.user_introduction);
        layoutIntroduction=view.findViewById(R.id.user_introduction_layout);

        userMamerEnergyLayout=view.findViewById(R.id.user_mamer_energy_layout);
        userUnloginLayout=view.findViewById(R.id.user_un_login_layout);


        tvTopicsCount=view.findViewById(R.id.user_my_topic_count);
        layoutTopics=view.findViewById(R.id.user_topics_more);
        tvReplysCount=view.findViewById(R.id.user_my_reply_count);
        layoutReplys=view.findViewById(R.id.user_reply_more);

        initTopicsView(view);
        initReplyView(view);
        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        //        判断是否登录
        if (MyApplication.globalUserInfo.token ==null){
            userUnloginLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
//                    跳到登录界面
                    Intent intent=new Intent(getActivity(),LoginActivity.class);
                    startActivity(intent);
                }
            });

        }else {
            userUnloginLayout.setVisibility(View.GONE);
            userMamerEnergyLayout.setVisibility(View.VISIBLE);

            initUserInfo();
//            请求数据，个人话题数，个人回复数，个人收藏数



        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
 //        获取数据
//        点击事件
        layoutToUserMine.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.globalUserInfo.token==null){
                    Message msg1=new Message();
                    msg1.what=UNLOGIN;
                    msgHandler.sendMessage(msg1);
                }else {
                    Intent intent = new Intent(getActivity(), UserHomePageActivity.class);
                    startActivity(intent);
                }
            }
        });
//        话题更多-->话题列表
        layoutTopics.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.globalUserInfo.token==null){
                    Message msg1=new Message();
                    msg1.what=UNLOGIN;
                    msgHandler.sendMessage(msg1);
                }else {
                    Intent intent = new Intent(getActivity(), UserSelfTopicListActivity.class);
                    startActivity(intent);
                }
            }
        });
//        回复更多-->回复列表
        layoutReplys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.globalUserInfo.token==null){
                    Message msg1=new Message();
                    msg1.what=UNLOGIN;
                    msgHandler.sendMessage(msg1);
                }else {
                    Intent intent = new Intent(getActivity(), UserSelfReplyActivity.class);
                    startActivity(intent);
                }
            }
        });



    }

    private  void initUserInfo(){
        final Runnable setAvatarRunable=new Runnable() {
            @Override
            public void run() {
                RequestOptions options=new RequestOptions()
                        .error(R.mipmap.ic_image_error)
                        .placeholder(R.mipmap.ic_image_error);
                Glide.with(getContext())
                        .asBitmap()
                        .load(MyApplication.globalUserInfo.user.getUserImg())
                        .apply(options)
                        .into(userAvatar);
               tvUserName.setText(MyApplication.globalUserInfo.user.getUserName());
               if (MyApplication.globalUserInfo.user.getUserIntroduction().equals("")){
                   layoutIntroduction.setVisibility(View.GONE);
               }else {
                   tvUserIntroduction.setText(MyApplication.globalUserInfo.user.getUserIntroduction()
                   );
               }

            }};
        new Thread(){
            public void run(){
                msgHandler.post(setAvatarRunable);
            }
        }.start();
    }
//    初始化话题recyclerView
    private void initTopicsView(View view){
        if (MyApplication.globalUserInfo.token!=null) {
            RecyclerView mRecyclerView = view.findViewById(R.id.user_topics_recyclerview);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            mRecyclerView.setLayoutManager(mLayoutManager);
            final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(getContext().getResources().getDisplayMetrics().widthPixels * 7 / 9, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, getContext().getResources().getDisplayMetrics().widthPixels * 1 / 9, 0);
            if (getContentArrayList() != null) {
                UserBaseAdapter mAdapter = new UserBaseAdapter(getContentArrayList(), getContext(), R.layout.user_topics_item) {
                    @Override
                    public void onBindViewHolder(UserBaseAdapter.mViewHolder viewHolder, int i) {
                        super.onBindViewHolder(viewHolder, i);
                        layoutTopicsItem = (LinearLayout) viewHolder.view(R.id.user_topics_item);
                        TextView tvTitle = (TextView) viewHolder.view(R.id.user_topics_title);
                        TextView tvExcerpt = (TextView) viewHolder.view(R.id.user_topics_excerpt);
                        layoutTopicsItem.setLayoutParams(params);
                        tvTitle.setText(getContentArrayList().get(i).getTopicTitle());
                        tvExcerpt.setText(getContentArrayList().get(i).getTopicExcerpt());
                    }
                };
                getUserTopics(1, mAdapter);
                mAdapter.setOnItemClickListener(new UserBaseAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Toast.makeText(getContext(), "点击测试", Toast.LENGTH_SHORT).show();
                    }
                });
                mRecyclerView.setAdapter(mAdapter);
            }
        }
    }
//    回复recyclerView
    private void initReplyView(View view) {
        if (MyApplication.globalUserInfo.token!=null) {
            RecyclerView mRecyclerView = view.findViewById(R.id.user_reply_recyclerview);
            RecyclerView.LayoutManager mLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
            mRecyclerView.setLayoutManager(mLayoutManager);

            final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(getContext().getResources().getDisplayMetrics().widthPixels * 7 / 9, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, getContext().getResources().getDisplayMetrics().widthPixels * 1 / 9, 0);
            if (getReplyArrayList() != null) {
                UserBaseAdapter mAdapter = new UserBaseAdapter(getReplyArrayList(), getContext(), R.layout.user_replys_item) {
                    @Override
                    public void onBindViewHolder(UserBaseAdapter.mViewHolder viewHolder, int i) {
                        super.onBindViewHolder(viewHolder, i);
                        layoutReplysItem = (LinearLayout) viewHolder.view(R.id.user_reply_item);
                        TextView tvTitle = (TextView) viewHolder.view(R.id.user_reply_topic_title);
                        TextView tvExcerpt = (TextView) viewHolder.view(R.id.user_reply_content);
                        layoutReplysItem.setLayoutParams(params);
                        tvTitle.setText(getReplyArrayList().get(i).getTitle());
                        tvExcerpt.setText(getReplyArrayList().get(i).getContent());
                    }
                };
                Log.e("回复adapter:", mAdapter.toString());
                getUserReply(1, mAdapter);
                mAdapter.setOnItemClickListener(new UserBaseAdapter.OnItemClickListener() {
                    @Override
                    public void onItemClick(int position) {
                        Toast.makeText(getContext(), "点击测试reply", Toast.LENGTH_SHORT).show();
                    }
                });
                mRecyclerView.setAdapter(mAdapter);
            }
        }
    }

//    收藏recyclerView
//    获取用户个人话题（数+内容）
    private void  getUserTopics(int pageCount, final UserBaseAdapter adapter){
        HttpUtil.sendOkHttpGetUserTopicList("1", pageCount, new Callback() {
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
                try{
                    jresp = new JSONObject(response.body().string());
                    switch (response.code()){
                        case HTTP_USER_GET_INFORMATION:
                            if (jresp.has("data")) {
                                jsonArray = jresp.getJSONArray("data");
                                if (jsonArray == null) {
                                    Message msg2 = new Message();
                                    msg2.what = MESSAGE_ERROR;
                                    msg2.obj = "您还没有发表过文章呢";
                                    msgHandler.sendMessage(msg2);
                                } else {
                                    int jsonSize = jsonArray.length();
                                    for (int i = 0; i < jsonSize; i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        TopicContent topicContent = new TopicContent();
                                        topicContent.setTopicId(jsonObject.getString("id"));
                                        topicContent.setTopicTitle(jsonObject.getString("title"));
                                        topicContent.setTopicAuthorId(jsonObject.getString("user_id"));
                                        topicContent.setCategoryId(jsonObject.getString("category_id"));
                                        topicContent.setTopicExcerpt(jsonObject.getString("excerpt"));
                                        contentArrayList.add(topicContent);
                                    }
                                    Message msg3 = new Message();
                                    msg3.what = USER_SET_INFORMATION;
                                    msg3.obj=adapter;
                                    msgHandler.sendMessage(msg3);
                                }
                            }
                            if (jresp.has("meta")) {
                                jresp=jresp.getJSONObject("meta");
                                if (jresp.has("pagination")){
                                    jresp=jresp.getJSONObject("pagination");
                                    final String count=jresp.getString("count");
                                    final Runnable setUserCount=new Runnable() {
                                        @Override
                                        public void run() {
                                            tvTopicsCount.setText(count);
                                        }};
                                    new Thread(){
                                        public void run(){
                                            msgHandler.post(setUserCount);
                                        }
                                    }.start();
                                }

                            }
                            break;
                        default:
                            break;
                    }
                }catch (JSONException e){
                    e.printStackTrace();
                }
            }
        });
    }
//    获取用户个人回复(数+内容）
    private void getUserReply(int pageCount, final UserBaseAdapter adapter){

        HttpUtil.sendOkHttpGetUserReplyList("1",pageCount, new Callback() {
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
                                        replyUser.setUserId(jsonObject.getString("user_id"));
                                        replyUser.setEssayId(jsonObject.getString("topic_id"));
                                        replyUser.setContent(jsonObject.getString("content"));
                                        replyArrayList.add(replyUser);
                                    }
                                    Message msg3 = new Message();
                                    msg3.what = USER_SET_INFORMATION;
                                    msg3.obj=adapter;
                                    msgHandler.sendMessage(msg3);

                                }
                                if (jresp.has("meta")) {
                                    jresp=jresp.getJSONObject("meta");
                                    if (jresp.has("pagination")){
                                        jresp=jresp.getJSONObject("pagination");
                                        count=jresp.getString("count");
                                        final Runnable setUserReplyCount=new Runnable() {
                                            @Override
                                            public void run() {
                                                tvReplysCount.setText(count);
                                            }};
                                        new Thread(){
                                            public void run(){
                                                msgHandler.post(setUserReplyCount);
                                            }
                                        }.start();
                                    }

                                }
                            }
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    public ArrayList<TopicContent> getContentArrayList() {
        return contentArrayList;
    }

    public void setContentArrayList(ArrayList<TopicContent> contentArrayList) {
        this.contentArrayList = contentArrayList;
    }

    public ArrayList<ReplyUser> getReplyArrayList() {
        return replyArrayList;
    }

    public void setReplyArrayList(ArrayList<ReplyUser> replyArrayList) {
        this.replyArrayList = replyArrayList;
    }
}
