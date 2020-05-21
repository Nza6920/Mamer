package com.example.my.mamer;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
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
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.chinalwb.are.android.inner.Html;
import com.example.my.mamer.adapter.recycleview.UserBaseAdapter;
import com.example.my.mamer.bean.ReplyUser;
import com.example.my.mamer.bean.TopicContent;
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
import static com.example.my.mamer.config.Config.HTTP_OK;
import static com.example.my.mamer.config.Config.HTTP_OVERTIME;
import static com.example.my.mamer.config.Config.HTTP_USER_GET_INFORMATION;
import static com.example.my.mamer.config.Config.HTTP_USER_NULL;
import static com.example.my.mamer.config.Config.MESSAGE_ERROR;
import static com.example.my.mamer.config.Config.REFRESH_TOKEN;
import static com.example.my.mamer.config.Config.UNLOGIN;

public class UserFragment extends Fragment {
    private LoadingDraw loadingDraw;
//    用户
    private LinearLayout userMamerEnergyLayout;
    private LinearLayout userUnloginLayout;
    private ImageView userAvatar;
    private TextView tvUserName;
    private LinearLayout layoutToUserMine;
    private LinearLayout layoutIntroduction;
    private TextView tvUserIntroduction;
//    关注
    private TextView tvFans;
    private TextView tvAttention;
    private LinearLayout layoutAttention;
//    话题
    private ArrayList<TopicContent> contentArrayList=new ArrayList<>();
    private TextView tvTopicsCount;
    private LinearLayout layoutTopics;
    private LinearLayout layoutTopicsItem;
    private RecyclerView mTopicsRecyclerView;
    private UserBaseAdapter mTopicsAdapter=null;
    private RecyclerView.LayoutManager mTopicsLayoutManager=null;
    //    回复
    private ArrayList<ReplyUser> replyArrayList =new ArrayList<>();
    private TextView tvReplysCount;
    private LinearLayout layoutReplys;
    private LinearLayout layoutReplysItem;
    private RecyclerView mReplyRecyclerView;
    private UserBaseAdapter mReplyAdapter=null;
    private RecyclerView.LayoutManager mReplyLayoutManager=null;

    //    点赞
    private ArrayList<TopicContent> likeArrayList =new ArrayList<>();
    private TextView tvLikeCount;
    private LinearLayout layoutLike;
    private LinearLayout layoutLikeItem;
    private RecyclerView mLikeRecyclerView;
    private UserBaseAdapter mLikeAdapter=null;
    private RecyclerView.LayoutManager mLikeLayoutManager=null;
    //    UI
    private final Handler msgHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case DISMISS_DIALOG:
                    ((LoadingDraw)msg.obj).dismiss();
                    break;
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
                    Toast.makeText(getContext(),(String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        loadingDraw=new LoadingDraw(getContext());
        View view=inflater.inflate(R.layout.fragment_user,container,false);
//        初始化控件
        userAvatar=view.findViewById(R.id.user_avatar);
        tvUserName=view.findViewById(R.id.user_name);
        layoutToUserMine=view.findViewById(R.id.user_mine_to);
        tvUserIntroduction=view.findViewById(R.id.user_introduction);
        layoutIntroduction=view.findViewById(R.id.user_introduction_layout);

        userMamerEnergyLayout=view.findViewById(R.id.user_mamer_energy_layout);
        userUnloginLayout=view.findViewById(R.id.user_un_login_layout);

        tvFans=view.findViewById(R.id.user_attention_);
        tvAttention=view.findViewById(R.id.user_to_attention_);
        layoutAttention=view.findViewById(R.id.attention_to_list);
        tvTopicsCount=view.findViewById(R.id.user_my_topic_count);
        layoutTopics=view.findViewById(R.id.user_topics_more);
        tvReplysCount=view.findViewById(R.id.user_my_reply_count);
        layoutReplys=view.findViewById(R.id.user_reply_more);
        tvLikeCount=view.findViewById(R.id.user_my_like_count);
        layoutLike=view.findViewById(R.id.user_like_more);

        mTopicsLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mTopicsRecyclerView = view.findViewById(R.id.user_topics_recyclerview);
        mTopicsRecyclerView.setLayoutManager(mTopicsLayoutManager);
        initTopicsView();
        Log.e("inittopicsView:","-----------------------");
        mTopicsRecyclerView.setAdapter(mTopicsAdapter);

        mReplyLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mReplyRecyclerView = view.findViewById(R.id.user_reply_recyclerview);
        mReplyRecyclerView.setLayoutManager(mReplyLayoutManager);
        initReplyView();
        Log.e("initreplyView:","-----------------------");
        mReplyRecyclerView.setAdapter(mReplyAdapter);

        mLikeLayoutManager = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        mLikeRecyclerView = view.findViewById(R.id.user_like_recyclerview);
        mLikeRecyclerView.setLayoutManager(mLikeLayoutManager);
        initLikeView();
        Log.e("initreplyView:","-----------------------");
        mLikeRecyclerView.setAdapter(mLikeAdapter);

        return view;

    }

    @Override
    public void onResume() {
        super.onResume();
        //        判断是否登录
        if (MyApplication.globalUserInfo.token ==null || MyApplication.globalUserInfo.token.equals("")){
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
            contentArrayList.clear();
            replyArrayList.clear();
            likeArrayList.clear();
            Log.e("请求topics数据:","-----------------------");
            getUserTopics(1);
            Log.e("请求reply数据:","-----------------------");
            getUserReply(1);
            attention(1);
            Log.e("请求like数据:","-----------------------");
            getUserlike(1);
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
        layoutAttention.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.globalUserInfo.token==null){
                    Message msg1=new Message();
                    msg1.what=UNLOGIN;
                    msgHandler.sendMessage(msg1);
                }else {
                    Intent intent = new Intent(getActivity(), UserAttentionFansActivity.class);
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
//        点赞更多-->点赞列表
        layoutLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (MyApplication.globalUserInfo.token==null){
                    Message msg1=new Message();
                    msg1.what=UNLOGIN;
                    msgHandler.sendMessage(msg1);
                }else {
                    Intent intent = new Intent(getActivity(), UserLikeListActivity.class);
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
                Log.e("leixing:","---"+MyApplication.globalUserInfo.user.avatarType);
        if (MyApplication.globalUserInfo.user.avatarType.equals("fig")){
            Glide.with(getContext())
                    .load(MyApplication.globalUserInfo.user.getUserImg())
                    .apply(options)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {

                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                           return false;
                        }
                    })
                    .into(userAvatar);
        }else {
            Glide.with(getContext())
                    .asBitmap()
                    .load(MyApplication.globalUserInfo.user.getUserImg())
                    .apply(options)
                    .into(userAvatar);
        }
               tvUserName.setText(MyApplication.globalUserInfo.user.getUserName());
               if (MyApplication.globalUserInfo.user.getUserIntroduction().equals("")){
                   tvUserIntroduction.setText("还没有个人简介哦");
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
    private void initTopicsView(){
        if (null==mTopicsAdapter) {
            final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(getContext().getResources().getDisplayMetrics().widthPixels * 7 / 9, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, getContext().getResources().getDisplayMetrics().widthPixels * 1 / 9, 0);
            UserBaseAdapter mAdapter = new UserBaseAdapter(getContext(), R.layout.user_topics_item) {
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
            this.mTopicsAdapter = mAdapter;
            mAdapter.setOnItemClickListener(new UserBaseAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    Intent intent = new Intent(getActivity(), UserSelfTopicListActivity.class);
                    startActivity(intent);
                }
            });
        }
    }
//    回复recyclerView,没有就创建一个adapter
    private void initReplyView() {
        if (null==mReplyAdapter) {
            final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(getContext().getResources().getDisplayMetrics().widthPixels * 7 / 9, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, getContext().getResources().getDisplayMetrics().widthPixels * 1 / 9, 0);
            UserBaseAdapter mAdapter = new UserBaseAdapter(getContext(), R.layout.user_replys_item) {
                @Override
                public void onBindViewHolder(UserBaseAdapter.mViewHolder viewHolder, int i) {
                    super.onBindViewHolder(viewHolder, i);
                    layoutReplysItem = (LinearLayout) viewHolder.view(R.id.user_reply_item);
                    TextView tvTitle = (TextView) viewHolder.view(R.id.user_reply_topic_title);
                    TextView tvExcerpt = (TextView) viewHolder.view(R.id.user_reply_content);
                    layoutReplysItem.setLayoutParams(params);
                    tvTitle.setText(getReplyArrayList().get(i).getTitle());
                    tvExcerpt.setText(Html.fromHtml(getReplyArrayList().get(i).getContent(),Html.FROM_HTML_MODE_COMPACT));
                }
            };
            this.mReplyAdapter = mAdapter;
            Log.e("回复adapter:", mAdapter.toString());
//                getUserReply(1, mAdapter);
            mAdapter.setOnItemClickListener(new UserBaseAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    Intent intent = new Intent(getActivity(), UserSelfReplyActivity.class);
                    startActivity(intent);
                }
            });
        }
    }
//    点赞recyclerView
    private void initLikeView(){
        if (null==mLikeAdapter) {
            final LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(getContext().getResources().getDisplayMetrics().widthPixels * 7 / 9, LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 0, getContext().getResources().getDisplayMetrics().widthPixels * 1 / 9, 0);
            UserBaseAdapter mAdapter = new UserBaseAdapter(getContext(), R.layout.user_topics_item) {
                @Override
                public void onBindViewHolder(UserBaseAdapter.mViewHolder viewHolder, int i) {
                    super.onBindViewHolder(viewHolder, i);
                    layoutLikeItem = (LinearLayout) viewHolder.view(R.id.user_topics_item);
                    TextView tvTitle = (TextView) viewHolder.view(R.id.user_topics_title);
                    TextView tvExcerpt = (TextView) viewHolder.view(R.id.user_topics_excerpt);
                    layoutLikeItem.setLayoutParams(params);
                    tvTitle.setText(getLikeArrayList().get(i).getTopicTitle());
                    tvExcerpt.setText(Html.fromHtml(getLikeArrayList().get(i).getTopicExcerpt(),Html.FROM_HTML_MODE_COMPACT));
                }
            };
            this.mLikeAdapter = mAdapter;
            Log.e("点赞adapter:", mAdapter.toString());
//                getUserReply(1, mAdapter);
            mAdapter.setOnItemClickListener(new UserBaseAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(int position) {
                    Toast.makeText(getContext(), "点击测试like", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
//    获取用户个人话题（数+内容）
    private void  getUserTopics(int pageCount){
        loadingDraw.show();
        HttpUtil.sendOkHttpGetUserTopicList(MyApplication.globalUserInfo.user.getUserId(), pageCount, new Callback() {
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
                                    final Runnable setUserCount=new Runnable() {
                                        @Override
                                        public void run() {
                                            loadingDraw.dismiss();
                                            if ((contentArrayList.size()!=0)&&(mTopicsAdapter!=null)){
                                                Log.e("inittopicsView设置数据:","-----------------------");
                                                mTopicsAdapter.clear();
                                                mTopicsAdapter.updateData(contentArrayList);
                                            }
                                        }};
                                    new Thread(){
                                        public void run(){
                                            msgHandler.post(setUserCount);
                                        }
                                    }.start();


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
    private void getUserReply(int pageCount){
        loadingDraw.show();
        Log.e("loadingdraw显示:","-----------------------");
        HttpUtil.sendOkHttpGetUserReplyList(MyApplication.globalUserInfo.user.getUserId(),pageCount, new Callback() {
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
                Log.e("请求数据成功:","-----------------------");
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
                                    Log.e("装入设置数据:","-----------------------");
                                    for (int i=0;i<jsonArray.length();i++){
                                        JSONObject jsonObject=jsonArray.getJSONObject(i);
                                        ReplyUser replyUser=new ReplyUser();
                                        replyUser.setReplyId(jsonObject.getString("id"));
                                        replyUser.setUserId(jsonObject.getString("user_id"));
                                        replyUser.setEssayId(jsonObject.getString("topic_id"));
                                        replyUser.setContent(jsonObject.getString("content"));
                                        if (jsonObject.has("topic")){
                                            JSONObject topicStr=jsonObject.getJSONObject("topic");
                                            replyUser.setTitle(topicStr.getString("title"));
                                        }
                                        replyArrayList.add(replyUser);
                                    }
                                    final Runnable setUserReplyCount=new Runnable() {
                                        @Override
                                        public void run() {
                                            loadingDraw.dismiss();
                                            if ((replyArrayList.size()!=0)&&mReplyAdapter!=null){
                                                Log.e("initreplyView设置数据:","-----------------------");
                                                Log.e("initreplyView:","-"+replyArrayList+"-"+mReplyAdapter.toString()+"---------------------");
                                                mReplyAdapter.clear();
                                                mReplyAdapter.updateData(replyArrayList);
                                            }
                                        }};
                                    new Thread(){
                                        public void run(){
                                            msgHandler.post(setUserReplyCount);
                                        }
                                    }.start();
                                }
                            }
                            if (jresp.has("meta")) {
                                jresp=jresp.getJSONObject("meta");
                                if (jresp.has("pagination")){
                                    jresp=jresp.getJSONObject("pagination");
                                    final String  count=jresp.getString("count");
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
//    获取关注数（关注我的|我关注的）
    private void attention(int pageCount){
        loadingDraw.show();
        HttpUtil.sendOkHttpGetFans(MyApplication.globalUserInfo.user.getUserId(), pageCount, new Callback() {
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
                Message msg1=new Message();
                msg1.what=DISMISS_DIALOG;
                msg1.obj=loadingDraw;
                msgHandler.sendMessage(msg1);

                JSONObject jresp = null;
                try {
                    jresp=new JSONObject(response.body().string());
                    switch (response.code()) {
                        case HTTP_USER_GET_INFORMATION:
                                if (jresp.has("meta")) {
                                    jresp=jresp.getJSONObject("meta");
                                    if (jresp.has("pagination")){
                                        jresp=jresp.getJSONObject("pagination");
                                        final String  count=jresp.getString("count");
                                        final Runnable setUserReplyCount=new Runnable() {
                                            @Override
                                            public void run() {
                                                tvFans.setText(count);
                                            }};
                                        new Thread(){
                                            public void run(){
                                                msgHandler.post(setUserReplyCount);
                                            }
                                        }.start();
                                    }
                            }
                            break;
                            default:
                                break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        HttpUtil.sendOkHttpGetAttention(MyApplication.globalUserInfo.user.getUserId(), pageCount, new Callback() {
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
                Message msg1=new Message();
                msg1.what=DISMISS_DIALOG;
                msg1.obj=loadingDraw;
                msgHandler.sendMessage(msg1);

                JSONObject jresp = null;
                try {
                    jresp=new JSONObject(response.body().string());
                    switch (response.code()) {
                        case HTTP_USER_GET_INFORMATION:
                                if (jresp.has("meta")) {
                                    jresp=jresp.getJSONObject("meta");
                                    if (jresp.has("pagination")){
                                        jresp=jresp.getJSONObject("pagination");
                                       final String  count=jresp.getString("count");
                                        final Runnable setUserReplyCount=new Runnable() {
                                            @Override
                                            public void run() {
                                                tvAttention.setText(count);
                                            }};
                                        new Thread(){
                                            public void run(){
                                                msgHandler.post(setUserReplyCount);
                                            }
                                        }.start();
                                    }
                            }
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
//    获取点赞数
    private void getUserlike(int pageCount){
        loadingDraw.show();
        HttpUtil.sendOkHttpGetLike(pageCount, new Callback() {
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
                Message msg1=new Message();
                msg1.what=DISMISS_DIALOG;
                msg1.obj=loadingDraw;
                msgHandler.sendMessage(msg1);

                JSONObject jresp = null;
                JSONArray jsonArray=null;
                try {
                    jresp=new JSONObject(response.body().string());
                    switch (response.code()){
                        case HTTP_USER_GET_INFORMATION:
                            if (jresp.has("data")){
                                jsonArray=jresp.getJSONArray("data");
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jrespStr=jsonArray.getJSONObject(i);
                                    TopicContent content=new TopicContent();
                                    content.setTopicId(jrespStr.getString("id"));
                                    content.setTopicTitle(jrespStr.getString("title"));
                                    content.setTopicExcerpt(jrespStr.getString("excerpt"));
                                    likeArrayList.add(content);
                                }
                                final Runnable setLike=new Runnable() {
                                    @Override
                                    public void run() {
                                        loadingDraw.dismiss();
                                        if ((likeArrayList.size()!=0)&&mLikeAdapter!=null){
                                            Log.e("likeView设置数据:","-----------------------");
                                            Log.e("likeView:","-"+likeArrayList+"-"+mLikeAdapter.toString()+"---------------------");
                                            mLikeAdapter.clear();
                                            mLikeAdapter.updateData(likeArrayList);
                                        }
                                    }};
                                new Thread(){
                                    public void run(){
                                        msgHandler.post(setLike);
                                    }
                                }.start();
                            }
                            if (jresp.has("meta")){
                                jresp=jresp.getJSONObject("meta");
                                if (jresp.has("pagination")){
                                    jresp=jresp.getJSONObject("pagination");
                                    final String  count=jresp.getString("count");
                                    final Runnable setUserReplyCount=new Runnable() {
                                        @Override
                                        public void run() {
                                            tvLikeCount.setText(count);
                                        }};
                                    new Thread(){
                                        public void run(){
                                            msgHandler.post(setUserReplyCount);
                                        }
                                    }.start();
                                }
                            }
                            break;
                        case HTTP_OVERTIME:
                            Message message=new Message();
                            message.what=MESSAGE_ERROR;
                            message.obj="等待数据加载";
                            msgHandler.sendMessage(message);
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


    //    刷新
    public  void  refreshKey(){
        HttpUtil.sendOkHttpRefreshToken(REFRESH_TOKEN, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg1 = new Message();
                msg1.what = MESSAGE_ERROR;
                msg1.obj = "登录已失效，请重新登录";
                msgHandler.sendMessage(msg1);

            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jrep= new JSONObject(response.body().string());
                    String key=jrep.getString("access_token");
                    String type=jrep.getString("token_type");
                    MyApplication.globalUserInfo.token=key;
                    MyApplication.globalUserInfo.tokenType=type;

                    Message msg1 = new Message();
                    msg1.what = MESSAGE_ERROR;
                    msg1.obj = "已刷新，请重试";
                    msgHandler.sendMessage(msg1);
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

    public ArrayList<TopicContent> getLikeArrayList() {
        return likeArrayList;
    }

    public void setLikeArrayList(ArrayList<TopicContent> likeArrayList) {
        this.likeArrayList = likeArrayList;
    }
}
