package com.example.my.mamer;

import android.graphics.Color;
import android.print.PrinterId;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class CenterActivity extends AppCompatActivity implements View.OnClickListener{
//    title
    private TextView more;
//
    private RelativeLayout rFragmentBody;
    private RelativeLayout rHomepageBar;
    private TextView tvHomepage;
    private ImageView imgHomepage;

    private RelativeLayout rIssueBar;
    private TextView tvIssue;
    private ImageView imgIssue;

    private RelativeLayout rPersonal;
    private TextView tvPersonal;
    private ImageView imgPersonal;

    private LinearLayout bottomBar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_center);

        init();
    }
    private void init(){
        rFragmentBody=findViewById(R.id.fragment_body);
        rHomepageBar=findViewById(R.id.nav_bottom_bar_homepage);
        tvHomepage=findViewById(R.id.home_page_prompt);
        imgHomepage=findViewById(R.id.home_page_img);
        rIssueBar=findViewById(R.id.nav_bottom_bar_issue);
        tvIssue=findViewById(R.id.issue_prompt);
        imgIssue=findViewById(R.id.issue_img);
        rPersonal=findViewById(R.id.nav_bottom_bar_personal);
        tvPersonal=findViewById(R.id.personal_prompt);
        imgPersonal=findViewById(R.id.personal_img);
        bottomBar=findViewById(R.id.nav_bottom_bar);

        rHomepageBar.setOnClickListener(this);
        rIssueBar.setOnClickListener(this);
        rPersonal.setOnClickListener(this);


    }
    private  void setSelectStatus(int index){
        switch (index){
            case 0:
//                变homepage
                break;
//                变issue
            case 1:
                break;
            case 2:
//                变personal
                tvPersonal.setTextColor(Color.parseColor("#12A3E9"));
                imgPersonal.setImageResource(R.mipmap.ic_personal_change);
//                不变
                tvHomepage.setTextColor(Color.parseColor("#666666"));
                tvIssue.setTextColor(Color.parseColor("#666666"));

                imgHomepage.setImageResource(R.mipmap.ic_home_page);
                imgIssue.setImageResource(R.mipmap.ic_home_page);
                break;
                default:
                    break;
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.nav_bottom_bar_homepage:
                setSelectStatus(0);
                break;
            case R.id.nav_bottom_bar_issue:
                setSelectStatus(1);
                break;
            case R.id.nav_bottom_bar_personal:
                setSelectStatus(2);
                break;
                default:
                    break;
        }
    }
}
