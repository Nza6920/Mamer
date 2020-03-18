//package com.example.my.mamer.util;
//
//import android.app.Activity;
//import android.content.Context;
//import android.content.Intent;
//import android.os.Handler;
//import android.view.Gravity;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.WindowManager;
//import android.widget.LinearLayout;
//import android.widget.PopupWindow;
//
//import com.example.my.mamer.R;
//import com.example.my.mamer.TopicReplyPublishActivity;
//import com.example.my.mamer.config.GlobalTopicReply;
//
//import static com.example.my.mamer.config.Config.USER_TOPIC_UPDATE;
//
//public class TopicManagePopup extends PopupWindow {
//
//    private View popupView;
//    private Handler handler;
//
//    public TopicManagePopup(final Activity context){
//        super(context);
//        initView(context);
//    }
//
//    private void initView(final Activity context){
//        LayoutInflater mInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
//        popupView= mInflater.inflate(R.layout.popup_topic_manage,null,false);
//        LinearLayout layoutEdit=popupView.findViewById(R.id.layout_popup_edit);
//        LinearLayout layoutUpdate=popupView.findViewById(R.id.layout_popup_update);
//        LinearLayout layoutDel=popupView.findViewById(R.id.layout_popup_del);
//        LinearLayout layoutCancel=popupView.findViewById(R.id.layout_popup_cancel);
////        获取屏幕高宽
//        int weight= context.getResources().getDisplayMetrics().widthPixels;
//        final int height=context.getResources().getDisplayMetrics().heightPixels*1/6;
//
//        final PopupWindow popupWindow=new PopupWindow(popupView,weight,height);
//        popupWindow.setAnimationStyle(R.style.popup_window_anim);
//        popupWindow.setFocusable(true);
////        点击外部popupwindow消失
//        popupWindow.setOutsideTouchable(true);
////        点击事件
////        删除
//        layoutDel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                popupWindow.dismiss();
//
//            }
//        });
////        编辑
//        layoutEdit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//            }
//        });
////        评论
//        layoutUpdate.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent=new Intent(context,TopicReplyPublishActivity.class);
//                intent.putExtra("essayId",GlobalTopicReply.reply.replyUser.getEssayId());
//                context.startActivityForResult(intent,USER_TOPIC_UPDATE);
//                popupWindow.dismiss();
//            }
//        });
////        取消
//        layoutCancel.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                popupWindow.dismiss();
//            }
//        });
////        屏幕不透明
//        popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener(){
//            @Override
//            public void onDismiss() {
//                WindowManager.LayoutParams layoutParams=context.getWindow().getAttributes();
//                layoutParams.alpha=1.0f;
//                context.getWindow().setAttributes(layoutParams);
//            }
//        });
//        WindowManager.LayoutParams layoutParam=context.getWindow().getAttributes();
//        layoutParam.alpha=0.3f;
//        context.getWindow().setAttributes(layoutParam);
//        popupWindow.showAtLocation(popupView,Gravity.BOTTOM,0,10);
//    }
//}