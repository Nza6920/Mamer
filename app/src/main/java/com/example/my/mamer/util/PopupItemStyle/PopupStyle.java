package com.example.my.mamer.util.PopupItemStyle;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.view.Gravity;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.my.mamer.R;
import com.example.my.mamer.util.BaseUtils;
import com.example.my.mamer.util.IdUtils;

public class PopupStyle {
    private int withlayout;
    private int hightlayout;
    private int picwTextView;
    private int pichTextView;
    private int textwTextView;
    private int texthTextView;
    private BaseUtils baseUtils=BaseUtils.getInstance();
    private TextView tvPic;
    private TextView tvText;
    private LinearLayout linearLayout;
    private LinearLayout.LayoutParams params;
    private void init(Context context){
        this.withlayout=95;
        this.hightlayout=110;
        this.picwTextView=90;
        this.pichTextView=85;
        this.textwTextView=90;
        this.texthTextView=25;

    }

    private void initUI(Context context){
        tvPic=new TextView(context);
        tvText=new TextView(context);
        linearLayout= new LinearLayout(context);
        params = new LinearLayout.LayoutParams(withlayout,hightlayout);
        params.setMargins(20,0,0,0);
        linearLayout.setLayoutParams(params);
//        纵向排列
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);
//        Drawable layoutL=ContextCompat.getDrawable(context,R.drawable.photo_popup_window_close);
//        linearLayout.setBackground(layoutL);



        tvPic.setWidth(picwTextView);
        tvPic.setHeight(pichTextView);
        tvText.setWidth(textwTextView);
        tvText.setHeight(texthTextView);
        tvPic.setGravity(Gravity.CENTER);
        tvText.setGravity(Gravity.CENTER);
        tvPic.setBackgroundColor(R.drawable.backgraoud_color);


    }
//    编辑
    public LinearLayout getEditView(Context context) {
        init(context);
        initUI(context);

        linearLayout.setId(IdUtils.generateViewId());
        Drawable tvClosePic=ContextCompat.getDrawable(context,R.mipmap.ic_popup_topic_manage_edit);
        tvPic.setBackground(tvClosePic);
        tvText.setText("编辑话题");

        linearLayout.addView(tvPic);
        linearLayout.addView(tvText);
        return linearLayout;
    }
//    删除
    public LinearLayout getDelView(Context context) {
        init(context);
        initUI(context);

        linearLayout.setId(IdUtils.generateViewId());

        Drawable tvClosePic=ContextCompat.getDrawable(context,R.mipmap.ic_popup_topic_manage_del);
        tvPic.setBackground(tvClosePic);
        tvText.setText("删除话题");

        linearLayout.addView(tvPic);
        linearLayout.addView(tvText);

        return linearLayout;
    }
//    关注，取关，关注列表
    public LinearLayout getFocusView(Context context,String author) {
        init(context);
        initUI(context);

        linearLayout.setId(IdUtils.generateViewId());

        Drawable tvClosePic=ContextCompat.getDrawable(context,R.mipmap.ic_popup_topic_manage_author);
        tvPic.setBackground(tvClosePic);
        tvText.setText(author);

        linearLayout.addView(tvPic);
        linearLayout.addView(tvText);
        return linearLayout;
    }
//    点赞，取消点赞，点赞列表
    public LinearLayout getLikeView(Context context) {
        init(context);
        initUI(context);

        linearLayout.setId(IdUtils.generateViewId());


        Drawable tvClosePic2=ContextCompat.getDrawable(context,R.mipmap.ic_popup_topic_manage_dislike);
        tvPic.setBackground(tvClosePic2);
        tvText.setText("点赞列表");


        linearLayout.addView(tvPic);
        linearLayout.addView(tvText);
        return linearLayout;
    }
//    返回首页
    public LinearLayout getHomeView(Context context){
            init(context);
            initUI(context);

            linearLayout.setId(IdUtils.generateViewId());

            Drawable tvClosePic=ContextCompat.getDrawable(context,R.mipmap.ic_popup_topic_manage_home);
            tvPic.setBackground(tvClosePic);
            tvText.setText("返回首页");

            linearLayout.addView(tvPic);
            linearLayout.addView(tvText);
            return linearLayout;
    }

    private int withPx(int withdip){
        return baseUtils.dip2px(withdip);
    }

    private int hightPx(int hightdip){
        return baseUtils.dip2px(hightdip);
    }
}
