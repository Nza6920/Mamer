package com.example.my.mamer;

import android.app.Activity;
import android.content.Context;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.my.mamer.bean.ReplyUser;
import com.example.my.mamer.bean.TopicContent;
import com.example.my.mamer.config.GlobalTopicReply;
import com.example.my.mamer.util.HttpUtil;
import com.example.my.mamer.util.LoadingDraw;

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
import static com.example.my.mamer.config.Config.USER_TOPIC_UPDATE;

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
    private TextView tvEssayContent;
//    当前用户
    private TopicManagePopup topicManagePopup;
    private AlertDialog.Builder delDialogBuilder;
    private View.OnClickListener onClickListener;
//    外部评论
    private ArrayList<TopicContent> listData=new ArrayList<>();
    private LoadingDraw loadingDraw;
//评论列表仅显示一个,有评论就显示，没有就不显示



    private TextView replyNone;

    public final Handler msgHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
                switch (msg.what){
                    case 1:
                        tvTitle.setText("分享");
                        break;
                    case 2:
                        tvTitle.setText("教程");
                        break;
                    case 3:
                        tvTitle.setText("问答");
                        break;
                    case 4:
                        tvTitle.setText("公告");
                        break;
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
        GlobalTopicReply.reply.replyUser=replyUser;
        GlobalTopicReply.reply.replyUser.setEssayId(prefs.getString("id",null));
        GlobalTopicReply.reply.replyUser.setUserId(prefs.getString("userId",null));
        GlobalTopicReply.reply.categoryId=prefs.getString("categoryId",null);
        GlobalTopicReply.reply.tagId=prefs.getString("tagId",null);

        setContentView(R.layout.activity_topic_particulars);
        loadingDraw=new LoadingDraw(this);

        init();
    }

    @Override
    protected void onResume() {
        super.onResume();
        getTopicParticulas();
    }

    private void init(){
        tvBack=findViewById(R.id.title_tv_close);
        tvTitle=findViewById(R.id.title_tv_name);
        tvBtnNext=findViewById(R.id.title_btn_next);
        tvAuthorPic=findViewById(R.id.topic_particulars_author_pic);
        tvAuthorName=findViewById(R.id.topic_particulars_author_name);
        tvCreatedTime=findViewById(R.id.topic_particulars_time);
        tvEssayTitle=findViewById(R.id.topic_particulars_title);
        tvEssayContent=findViewById(R.id.topic_particulars_content);
        replyNone=findViewById(R.id.reply_none);
//        填充
        Drawable tvBackPic=ContextCompat.getDrawable(this,R.mipmap.ic_title_back);
        tvBack.setBackground(tvBackPic);

        getTopicParticulas();
//        动态显示话题分类
        switch (GlobalTopicReply.reply.categoryId){
            case "1":
                Message msg1 = new Message();
                msg1.what = 1;
                msgHandler.sendMessage(msg1);
                break;
            case "2":
                Message msg2 = new Message();
                msg2.what = 2;
                msgHandler.sendMessage(msg2);
                break;
            case "3":
                Message msg3 = new Message();
                msg3.what = 3;
                msgHandler.sendMessage(msg3);
                break;
            case "4":
                Message msg4 = new Message();
                msg4.what = 2;
                msgHandler.sendMessage(msg4);
                break;
                default:
                    break;
        }
//        判断用户是否登陆，
        if (MyApplication.globalUserInfo.token!=null){
            if (GlobalTopicReply.reply.replyUser.getUserId().equals(MyApplication.globalUserInfo.user.getUserId())){
                //            登陆，作者本人访问可删除和编辑帖子，以及评论
                tvBtnNext.setText("管理");
                //        管理点击事件
                tvBtnNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        topicManagePopup=new TopicManagePopup(TopicParticularsActivity.this);
                    }
                });
            }else {
                //            登录,非作者就只能评论
                tvBtnNext.setText("评论");
                tvBtnNext.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent=new Intent(TopicParticularsActivity.this,TopicReplyPublishActivity.class);
                        intent.putExtra("essayId",GlobalTopicReply.reply.replyUser.getEssayId());
                        startActivity(intent);
                    }
                });
            }
        }else {
            Message msg1=new Message();
            msg1.what=UNLOGIN;
            msgHandler.sendMessage(msg1);
        }
//        返回调用界面
        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }
//获得话题详情
    private void getTopicParticulas(){
        String essayId=GlobalTopicReply.reply.replyUser.getEssayId();
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
                            topicContent.setTopicTitle(jresp.getString("title"));
                            topicContent.setTopicConten(jresp.getString("body"));
                            topicContent.setReplyCount(jresp.getString("reply_count"));
                            topicContent.setCreateTime(jresp.getString("created_at"));
                            if (jresp.has("user")){
                                Log.e("Tag","话题详情--获取作者数据");
                                JSONObject user=jresp.getJSONObject("user");
                                topicContent.setTopicAuthorName(user.getString("name"));
                                topicContent.setTopicAuthorPic(user.getString("avatar"));
                                topicContent.setTopicAuthorId(user.getString("id"));
                            }
                            Log.e("Tag","话题详情--暂存数据");
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
                                    tvCreatedTime.setText(listData.get(0).getCreateTime());
                                    Log.e("Tag","话题详情--创建时间");
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
//    解析html
    private void jsoupUtil(String contentStr){
//        从String加载文档
//spaned
//        Spanned contentStrs= Html.fromHtml();

    }
//    删除提醒
    private void delAlert(){
        delDialogBuilder=new AlertDialog.Builder(this)
                .setMessage("删除后无法恢复")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
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
        String essayId=GlobalTopicReply.reply.replyUser.getEssayId();
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

                            Intent intent=new Intent(TopicParticularsActivity.this,UserSelfTopicListActivity.class);
                            startActivity(intent);
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
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case USER_TOPIC_DEL:
                delAlert();
                break;
            case USER_TOPIC_UPDATE:
                break;
            default:
                break;
        }
    }

    public class TopicManagePopup extends PopupWindow {

        private View popupView;

        public TopicManagePopup(final Activity context){
            super(context);
            initView(context);
        }

        private void initView(final Activity context){
            LayoutInflater mInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            popupView= mInflater.inflate(R.layout.popup_topic_manage,null,false);
            LinearLayout layoutEdit=popupView.findViewById(R.id.layout_popup_edit);
            LinearLayout layoutUpdate=popupView.findViewById(R.id.layout_popup_update);
            LinearLayout layoutDel=popupView.findViewById(R.id.layout_popup_del);
            LinearLayout layoutCancel=popupView.findViewById(R.id.layout_popup_cancel);
//        获取屏幕高宽
            int weight= context.getResources().getDisplayMetrics().widthPixels;
            final int height=context.getResources().getDisplayMetrics().heightPixels*1/6;

            final PopupWindow popupWindow=new PopupWindow(popupView,weight,height);
            popupWindow.setAnimationStyle(R.style.popup_window_anim);
            popupWindow.setFocusable(true);
//        点击外部popupwindow消失
            popupWindow.setOutsideTouchable(true);
//        点击事件
//        删除
            layoutDel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Message msg=new Message();
                    msg.what=USER_TOPIC_DEL;
                    msgHandler.sendMessage(msg);
                    popupWindow.dismiss();
                }
            });
//        编辑
            layoutEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                }
            });
//        评论
            layoutUpdate.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent=new Intent(context,TopicReplyPublishActivity.class);
                    intent.putExtra("essayId",GlobalTopicReply.reply.replyUser.getEssayId());
                    context.startActivityForResult(intent,USER_TOPIC_UPDATE);
                    popupWindow.dismiss();
                }
            });
//        取消
            layoutCancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popupWindow.dismiss();
                }
            });
//        屏幕不透明
            popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener(){
                @Override
                public void onDismiss() {
                    WindowManager.LayoutParams layoutParams=context.getWindow().getAttributes();
                    layoutParams.alpha=1.0f;
                    context.getWindow().setAttributes(layoutParams);
                }
            });
            WindowManager.LayoutParams layoutParam=context.getWindow().getAttributes();
            layoutParam.alpha=0.3f;
            context.getWindow().setAttributes(layoutParam);
            popupWindow.showAtLocation(popupView,Gravity.BOTTOM,0,10);
        }
    }

}
