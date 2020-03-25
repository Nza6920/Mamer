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
        this.withlayout=context.getResources().getDisplayMetrics().widthPixels*1/7;
        this.hightlayout=context.getResources().getDisplayMetrics().heightPixels*1/9;
        this.picwTextView=withlayout*1/5;
        this.pichTextView=hightlayout*3/5;
        this.textwTextView=withlayout*1/4;
        this.texthTextView=hightlayout*1/5;

    }

    private void initUI(Context context){
        tvPic=new TextView(context);
        tvText=new TextView(context);
        linearLayout= new LinearLayout(context);
        params = new LinearLayout.LayoutParams(withlayout, hightlayout);
        linearLayout.setLayoutParams(params);
//        纵向排列
        linearLayout.setOrientation(LinearLayout.VERTICAL);
        linearLayout.setGravity(Gravity.CENTER);

        tvPic.setWidth(picwTextView);
        tvPic.setHeight(pichTextView);
        tvText.setWidth(textwTextView);
        tvText.setHeight(texthTextView);
        tvPic.setGravity(Gravity.CENTER);
        tvText.setGravity(Gravity.CENTER);
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
    public LinearLayout getTagsView(Context context,String tags) {
        init(context);
        initUI(context);

        linearLayout.setId(IdUtils.generateViewId());

        Drawable tvClosePic=ContextCompat.getDrawable(context,R.mipmap.ic_popup_topic_manage_like);
        tvPic.setBackground(tvClosePic);
        tvText.setText(tags);

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
