package com.example.my.mamer;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.chinalwb.are.AREditText;
import com.example.my.mamer.bean.ReplyUser;
import com.example.my.mamer.bean.TopicContent;
import com.example.my.mamer.util.BaseUtils;
import com.example.my.mamer.util.HttpUtil;
import com.example.my.mamer.util.LoadingDraw;
import com.example.my.mamer.util.PopupItemStyle.PopupStyle;
import com.example.my.mamer.util.StringToDate;
import com.example.my.mamer.util.TopicManagePopup;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.my.mamer.MyApplication.getContext;
import static com.example.my.mamer.config.Config.DISMISS_DIALOG;
import static com.example.my.mamer.config.Config.HTTP_NOT_FOUND;
import static com.example.my.mamer.config.Config.HTTP_OVERTIME;
import static com.example.my.mamer.config.Config.HTTP_USER_GET_INFORMATION;
import static com.example.my.mamer.config.Config.MESSAGE_ERROR;
import static com.example.my.mamer.config.Config.UNLOGIN;
import static com.example.my.mamer.config.Config.USER_TOPIC_DEL;

public class TopicParticularsActivity extends AppCompatActivity {
//    title
    private TextView tvBack;
    private TextView tvTitle;
    private TextView tvBtnNext;
//作者头像，名字，文章创建时间
    private ImageView tvAuthorPic;
    private TextView tvAuthorName;
    private TextView tvCreatedTime;
//    文章
    private TextView tvEssayTitle;
    private AREditText mEditTextArticle;
//    当前用户
    private TopicManagePopup topicManagePopup;
    private AlertDialog.Builder delDialogBuilder;
    private View.OnClickListener onClickListener;
//    外部评论//评论列表仅显示一个,有评论就显示，没有就不显示
    private ArrayList<TopicContent> listData=new ArrayList<>();
    private LoadingDraw loadingDraw;
    private LinearLayout layoutComment;
    private LinearLayout layoutCommentList;
    private ImageView imgAvatar;
    private TextView tvReplyUserName;
    private TextView tvReplyTime;
    private TextView tvReplyCount;
    private AREditText mEditTextReply;


    public final Handler msgHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
                switch (msg.what){
                    case DISMISS_DIALOG:
                        ((LoadingDraw)msg.obj).dismiss();
                        break;
                    case MESSAGE_ERROR:
                        Toast.makeText(TopicParticularsActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
                        break;
                    case UNLOGIN:
                        Toast.makeText(TopicParticularsActivity.this,"登录以体验更多",Toast.LENGTH_SHORT).show();
                        break;
                    case USER_TOPIC_DEL:
                        delAlert();
                        break;
                    default:
                        break;
                }
                return false;
            }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        ReplyUser replyUser=new ReplyUser();
        MyApplication.globalTopicReply.reply.replyUser=replyUser;
        MyApplication.globalTopicReply.reply.replyUser.setEssayId(prefs.getString("id",null));
        MyApplication.globalTopicReply.reply.replyUser.setUserId(prefs.getString("userId",null));
        MyApplication.globalTopicReply.reply.tagId=prefs.getString("tagId",null);

        setContentView(R.layout.activity_topic_particulars);
        loadingDraw=new LoadingDraw(this);
        getTopicParticulas();
        getTopicReply();
        init();

    }

    private void init(){
        tvBack=findViewById(R.id.title_tv_close);
        tvTitle=findViewById(R.id.title_tv_name);
        tvBtnNext=findViewById(R.id.title_btn_next);
        tvAuthorPic=findViewById(R.id.topic_particulars_author_pic);
        tvAuthorName=findViewById(R.id.topic_particulars_author_name);
        tvCreatedTime=findViewById(R.id.topic_particulars_time);
        tvEssayTitle=findViewById(R.id.topic_particulars_title);
        mEditTextArticle=findViewById(R.id.topic_particulars_content);
        mEditTextReply=findViewById(R.id.reply_content);
        layoutComment=findViewById(R.id.reply_comment);
        layoutCommentList=findViewById(R.id.reply_comment_list);
        imgAvatar=findViewById(R.id.reply_user_avatar);
        tvReplyUserName=findViewById(R.id.reply_name);
        tvReplyTime=findViewById(R.id.reply_time);
        tvReplyCount=findViewById(R.id.reply_count);
//        填充
        Drawable tvBackPic=ContextCompat.getDrawable(this,R.mipmap.ic_title_back);
        tvBack.setBackground(tvBackPic);
        Drawable tvBtnNextPic=ContextCompat.getDrawable(this,R.mipmap.ic_reply_popup_show);
        tvBtnNext.setBackground(tvBtnNextPic);


//        判断用户是否登陆，
        if (MyApplication.globalUserInfo.token!=null){
            if (MyApplication.globalTopicReply.reply.replyUser.getUserId().equals(MyApplication.globalUserInfo.user.getUserId())){
                //            登陆，作者本人访问可删除和编辑帖子，以及评论
                //        管理点击事件
                tvBtnNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        getmm("author");
                    }
                });
            }else {
                //            登录,非作者就只能评论
                tvBtnNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        getmm("reader");
                    }
                });
            }
        }else {
            tvBtnNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message msg1=new Message();
                    msg1.what=UNLOGIN;
                    msgHandler.sendMessage(msg1);
                }
            });
        }
//        返回调用界面
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
//        跳到评论列表界面
        layoutComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("tag",MyApplication.globalTopicReply.reply.replyUser.getEssayId());
                Intent intent=new Intent(TopicParticularsActivity.this,TopicReplyListActivity.class);
                intent.putExtra("topicUserName",listData.get(0).getTopicAuthorName());
                startActivity(intent);
            }
        });

    }
//  获得话题详情
    private void getTopicParticulas(){
        String essayId=MyApplication.globalTopicReply.reply.replyUser.getEssayId();
//        loadingDraw.show();
        HttpUtil.sendOkHttpGetTopicParticulars(essayId,new Callback() {
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
                try {
                    JSONObject jresp=new JSONObject(response.body().string());
                    switch (response.code()){
                        case HTTP_USER_GET_INFORMATION:
                            Message msg3=new Message();
                            msg3.what=DISMISS_DIALOG;
                            msg3.obj=loadingDraw;
                            msgHandler.sendMessage(msg3);

                            Log.e("Tag","话题详情--获取具体数据");
                            TopicContent topicContent=new TopicContent();
                            topicContent.setTopicId(jresp.getString("id"));
                            topicContent.setTopicTitle(jresp.getString("title"));
                            topicContent.setTopicConten(jresp.getString("body"));
                            topicContent.setReplyCount(jresp.getString("reply_count"));
                            topicContent.setCreateTime(StringToDate.stringToShort(jresp.getString("updated_at")));
                            if (jresp.has("user")){
                                Log.e("Tag","话题详情--获取作者数据");
                                JSONObject user=jresp.getJSONObject("user");
                                topicContent.setTopicAuthorName(user.getString("name"));
                                topicContent.setTopicAuthorPic(user.getString("avatar"));
                                topicContent.setTopicAuthorId(user.getString("id"));
                            }
                            Log.e("Tag","话题详情--暂存数据");
                            if (jresp.has("category")){
                                JSONObject category=jresp.getJSONObject("category");
                                topicContent.setCategoryId(category.getString("id"));
                                topicContent.setCategoryName(category.getString("name"));
                            }
                            listData.add(topicContent);
//处理内容
//                            jsoupUtil(topicContent.getTopicConten());
                            Log.e("Tag","话题详情--更新数据");
                            final Runnable setAvatarRunable=new Runnable() {
                                @Override
                                public void run() {
                                    RequestOptions options=new RequestOptions()
                                            .error(R.mipmap.ic_image_error)
                                            .placeholder(R.mipmap.ic_image_error);
                                    Glide.with(getContext())
                                            .asBitmap()
                                            .load(listData.get(0).getTopicAuthorPic())
                                            .apply(options)
                                            .into(tvAuthorPic);
                                    Log.e("Tag","话题详情--设置头像");
                                    tvAuthorName.setText(listData.get(0).getTopicAuthorName());
                                    Log.e("Tag","话题详情--姓名");
                                    tvEssayTitle.setText(listData.get(0).getTopicTitle());
                                    Log.e("Tag","话题详情--标题");
                                    tvCreatedTime.setText("更新于"+listData.get(0).getCreateTime());
                                    Log.e("Tag","话题详情--创建时间");
                                    BaseUtils.contentUtil(getContext(),mEditTextArticle,listData.get(0).getTopicConten());
                                    tvTitle.setText(listData.get(0).getCategoryName());

                                }};
                            new Thread(){
                                public void run(){
                                    msgHandler.post(setAvatarRunable);
                                }
                            }.start();

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
//    获取评论
    private void getTopicReply(){

        HttpUtil.sendOkHttpGetTopicReplyList(MyApplication.globalTopicReply.reply.replyUser.getEssayId(),1, new Callback() {
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
                                    JSONObject jsonObject=jsonArray.getJSONObject(0);
                                    final ReplyUser replyUser=new ReplyUser();
                                    replyUser.setReplyId(jsonObject.getString("id"));
                                    replyUser.setEssayId(jsonObject.getString("topic_id"));
                                    replyUser.setContent(jsonObject.getString("content"));
                                    replyUser.setTime(StringToDate.stringToShort(jsonObject.getString("updated_at")));
                                    if (jsonObject.has("user")){
                                        JSONObject userStr=jsonObject.getJSONObject("user");
                                        replyUser.setUserId(userStr.getString("id"));
                                        replyUser.setUserName(userStr.getString("name"));
                                        replyUser.setUserImg(userStr.getString("avatar"));
                                        }
                                    final Runnable setAvatarRunable=new Runnable() {
                                        @Override
                                        public void run() {
                                            layoutCommentList.setVisibility(View.VISIBLE);
                                            tvReplyTime.setVisibility(View.VISIBLE);
                                            RequestOptions options=new RequestOptions()
                                                    .error(R.mipmap.ic_image_error)
                                                    .placeholder(R.mipmap.ic_image_error);
                                            Glide.with(getContext())
                                                    .asBitmap()
                                                    .load(replyUser.getUserImg())
                                                    .apply(options)
                                                    .into(imgAvatar);
                                            tvReplyUserName.setText(replyUser.getUserName());
                                            BaseUtils.contentUtil(getContext(),mEditTextReply,replyUser.getContent());
                                            tvReplyTime.setText(replyUser.getTime());


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
                                    final Runnable setCountRunable=new Runnable() {
                                        @Override
                                        public void run() {
                                            tvReplyCount.setText(count);
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

//    删除提醒
    private void delAlert(){
        delDialogBuilder=new AlertDialog.Builder(this)
                .setMessage("删除后无法恢复")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        delTopic();

                    }
                })
                .setNegativeButton("取消",new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        delDialogBuilder.show();
    }
//    删除话题
    private void delTopic(){
        String essayId=MyApplication.globalTopicReply.reply.replyUser.getEssayId();
        loadingDraw.show();
        HttpUtil.sendOkHttpDelTopic(essayId, new Callback() {
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
                Log.e("话题删除","del:"+response.code());
                    switch (response.code()){
                        case HTTP_USER_GET_INFORMATION:
                            Message msg1=new Message();
                            msg1.what=DISMISS_DIALOG;
                            msg1.obj=loadingDraw;
                            msgHandler.sendMessage(msg1);

                            Message msg3=new Message();
                            msg3.what=MESSAGE_ERROR;
                            msg3.obj="删除成功";
                            msgHandler.sendMessage(msg3);

                            finish();
                            break;
                        case HTTP_OVERTIME:
                            Message msg4=new Message();
                            msg4.what=DISMISS_DIALOG;
                            msg4.obj=loadingDraw;
                            msgHandler.sendMessage(msg4);

                            Message msg2=new Message();
                            msg2.what=MESSAGE_ERROR;
                            msg2.obj="出错啦,请稍后再试";
                            msgHandler.sendMessage(msg2);
                        case HTTP_NOT_FOUND:
                            Message msg6=new Message();
                            msg6.what=DISMISS_DIALOG;
                            msg6.obj=loadingDraw;
                            msgHandler.sendMessage(msg6);

                            Message msg5=new Message();
                            msg5.what=MESSAGE_ERROR;
                            msg5.obj="出错啦，请稍后再试";
                            msgHandler.sendMessage(msg5);
                            break;
                            default:
                                break;

                    }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
//    popupwindow
    private void getmm(final String tag){
        //    popupwindow
         PopupStyle popupStyle=new PopupStyle();
         ArrayList<LinearLayout> views=new ArrayList<>();
         final SparseArray<LinearLayout> viewSparseArray=new SparseArray<>();
        //删除
        LinearLayout viewDel=popupStyle.getDelView(this);
        final  int idDel=viewDel.getId();
        //        编辑
        LinearLayout viewEdit=popupStyle.getEditView(this);
        final int idEdit=viewEdit.getId();
        //        返回首页
        LinearLayout viewHome=popupStyle.getHomeView(this);
        final int idHome=viewHome.getId();

        switch (tag){
            case "author":
                views.add(viewEdit);
                views.add(viewDel);
                views.add(viewHome);
                viewSparseArray.put(idEdit,viewEdit);
                viewSparseArray.put(idDel,viewDel);
                viewSparseArray.put(idHome,viewHome);
                break;
            case "reader":
                views.add(viewHome);
                viewSparseArray.put(idHome,viewHome);
                break;
                default:break;
        }

        final TopicManagePopup popup=new TopicManagePopup(TopicParticularsActivity.this,viewSparseArray,views, new TopicManagePopup.ClickListener() {
            @Override
            public void setUplistener(final TopicManagePopup.TopicManagePopupUtil popupUtil) {
                if (tag.equals("author")){
                    popupUtil.getView(idEdit).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                            editor.putString("title",listData.get(0).getTopicTitle());
                            editor.putString("body",listData.get(0).getTopicConten());
                            editor.putString("categoryId",listData.get(0).getCategoryId());
                            editor.putString("categoryName",listData.get(0).getCategoryName());
                            editor.putString("topicId",listData.get(0).getTopicId());
                            editor.putString("tagId","1");
                            editor.apply();

                            Intent intent=new Intent(TopicParticularsActivity.this,TopicActivity.class);
                            startActivity(intent);
                            popupUtil.dismiss();
                        }
                    });
                    popupUtil.getView(idDel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            delAlert();
                            popupUtil.dismiss();
                        }
                    });
                }
                popupUtil.getView(idHome).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        popupUtil.dismiss();
                        finish();
                    }
                });


            }

        }) ;
    }

}
