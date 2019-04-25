package com.example.my.mamer;

import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic_particulars);
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

        SharedPreferences prefs= PreferenceManager.getDefaultSharedPreferences(this);
        switch (prefs.getString("categoryId",null)){
            case "1":
                tvTitle.setText("分享");
                break;
            case "2":
                tvTitle.setText("教程");
                break;
            case "3":
                tvTitle.setText("问答");
                break;
            case "4":
                tvTitle.setText("公告");
                break;
                default:
                    break;
        }

        
    }
}
