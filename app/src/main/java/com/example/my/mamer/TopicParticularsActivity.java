package com.example.my.mamer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.my.mamer.bean.TopicContent;
import com.example.my.mamer.config.GlobalUserInfo;
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
import static com.example.my.mamer.config.Config.HTTP_USER_GET_INFORMATION;
import static com.example.my.mamer.config.Config.MESSAGE_ERROR;
import static com.example.my.mamer.config.Config.UNLOGIN;
import static com.example.my.mamer.config.Config.USER_SET_INFORMATION;

public class TopicParticularsActivity extends AppCompatActivity {
//    title
    private TextView tvBack;
    private TextView tvTitle;
//作者头像，名字，文章创建时间
    private ImageView tvAuthorPic;
    private TextView tvAuthorName;
    private TextView tvCreatedTime;
//    文章
    private TextView tvEssayTitle;
    private TextView tvEssayContent;
    private TextView tvEssayParticulars;
//    当前用户
    private LinearLayout layoutNowUser;
    private Button btnDel;
    private Button btnEdit;
//    外部评论
    private LinearLayout layoutComment;
    private ArrayList<TopicContent> listData=new ArrayList<>();
    private LoadingDraw loadingDraw;

    private final Handler msgHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
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
                    loadingDraw.dismiss();
                    break;
                case MESSAGE_ERROR:
                    Toast.makeText(TopicParticularsActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                case  USER_SET_INFORMATION:
                    RequestOptions options=new RequestOptions()
                            .error(R.mipmap.ic_image_error)
                            .placeholder(R.mipmap.ic_image_error);
                    Glide.with(getContext())
                            .asBitmap()
                            .load(listData.get(0).getTopicAuthorPic())
                            .apply(options)
                            .into(tvAuthorPic);
                    tvAuthorName.setText(listData.get(0).getTopicAuthorName());
                    tvEssayTitle.setText(listData.get(0).getTopicTitle());
                    tvCreatedTime.setText(listData.get(0).getCreateTime());
                    break;
                case UNLOGIN:
                    Toast.makeText(TopicParticularsActivity.this,"登录以体验更多",Toast.LENGTH_SHORT).show();
                    break;
                    default:
                        break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_particulars);
        loadingDraw=new LoadingDraw(this);
        init();

    }
    private void init(){
        tvBack=findViewById(R.id.title_tv_close);
        tvTitle=findViewById(R.id.title_tv_name);
        tvAuthorPic=findViewById(R.id.topic_particulars_author_pic);
        tvAuthorName=findViewById(R.id.topic_particulars_author_name);
        tvCreatedTime=findViewById(R.id.topic_particulars_time);
        tvEssayTitle=findViewById(R.id.topic_particulars_title);
        tvEssayContent=findViewById(R.id.topic_particulars_content);
        tvEssayParticulars=findViewById(R.id.topic_particulars_comment_list);
        layoutNowUser=findViewById(R.id.set_topic);
        btnDel=findViewById(R.id.topic_particulars_delete);
        btnEdit=findViewById(R.id.topic_particulars_edit);
        layoutComment=findViewById(R.id.topic_particulars_comment);

//        填充
        Drawable tvBackPic=ContextCompat.getDrawable(this,R.mipmap.ic_title_back);
        tvBack.setBackground(tvBackPic);

        tvTitle.setTextSize(20);
        final SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        getTopicParticulas(prefs.getString("id",null));
//        动态显示话题分类
        switch (prefs.getString("categoryId",null)){
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
        if (GlobalUserInfo.userInfo.token!=null){
            if (prefs.getString("userId",null)!=GlobalUserInfo.userInfo.user.getUserId()){
//            登录,非作者就只能评论
                layoutComment.setVisibility(View.VISIBLE);

            }else if (prefs.getString("userId",null)==GlobalUserInfo.userInfo.user.getUserId()){
//            登陆，作者本人访问可删除和编辑帖子，以及评论
                layoutNowUser.setVisibility(View.VISIBLE);
            }
        }else {
            Message msg1=new Message();
            msg1.what=UNLOGIN;
            msgHandler.sendMessage(msg1);

        }

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                从主页访问，返回首页
                if (prefs.getString("tagId",null)=="1"){
                    Intent intent=new Intent(TopicParticularsActivity.this,BottomNavigationBarActivity.class);
                    startActivity(intent);
                    finish();
                }else if (prefs.getString("tagId",null)=="2"){
//                从个人访问，返回个人话题列表
                    Intent intent=new Intent(TopicParticularsActivity.this,UserSelfTopicListActivity.class);
                    startActivity(intent);
                    finish();
                }



            }
        });
//        删除话题
        btnDel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
//        编辑话题
        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
//        发表评论
        layoutComment.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void getTopicParticulas(String essayId){
        loadingDraw.show();
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

                            TopicContent topicContent=new TopicContent();
                            topicContent.setTopicTitle(jresp.getString("title"));
                            topicContent.setTopicConten(jresp.getString("body"));
                            topicContent.setReplyCount(jresp.getString("reply_count"));
                            topicContent.setCreateTime(jresp.getString("created_at"));
                            if (jresp.has("user")){
                                JSONObject user=jresp.getJSONObject(jresp.getString("user"));
                                topicContent.setTopicAuthorName(user.getString("name"));
                                topicContent.setTopicAuthorPic(user.getString("avatar"));
                                topicContent.setTopicAuthorId(user.getString("id"));
                            }
                            listData.add(topicContent);
//处理内容
                            jsoupUtil(topicContent.getTopicConten());

                            Message msg4 = new Message();
                            msg4.what = USER_SET_INFORMATION;
                            msgHandler.sendMessage(msg4);

                            break;
                            default:
                                break;
                    }
                } catch (JSONException e) {


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
}
