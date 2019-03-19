package com.example.my.mamer.util;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.content.FileProvider;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.example.my.mamer.BuildConfig;
import com.example.my.mamer.R;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import static com.example.my.mamer.config.Config.RESULT_CAMERA_IMAGE;
import static com.example.my.mamer.config.Config.RESULT_LODA_IMAGE;

public class PhotoPopupWindow extends PopupWindow{
    public static String cameraPhotoPath;
    private View popupView;

    public static String getCameraPhotoPath() {
        return cameraPhotoPath;
    }

    public static void setCameraPhotoPath(String cameraPhotoPath) {
        PhotoPopupWindow.cameraPhotoPath = cameraPhotoPath;
    }

    public PhotoPopupWindow(final Activity context){
        super(context);
        initView(context);
    }

    private void initView(final Activity context){
        LayoutInflater mInflater= (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        popupView=mInflater.inflate(R.layout.photo_popup_window,null);
        TextView tvAlbum=popupView.findViewById(R.id.popup_album);
        TextView tvCamera=popupView.findViewById(R.id.popup_camera);
        TextView tvCancel=popupView.findViewById(R.id.popup_cancel);
//        获取屏幕高宽
        int weight= context.getResources().getDisplayMetrics().widthPixels;
        int height=context.getResources().getDisplayMetrics().heightPixels*1/3;

        final PopupWindow popupWindow=new PopupWindow(popupView,weight,height);
        popupWindow.setAnimationStyle(R.style.popup_window_anim);
        popupWindow.setFocusable(true);
//        点击外部popupwindow消失
        popupWindow.setOutsideTouchable(true);

//        点击事件
//        相册
        tvAlbum.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent("android.intent.action.GET_CONTENT");
                intent.setType("image/*");
                context.startActivityForResult(intent,RESULT_LODA_IMAGE);
                popupWindow.dismiss();
            }
        });
//        拍照
        tvCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                takeCamera(RESULT_CAMERA_IMAGE,context);
                popupWindow.dismiss();

            }
        });
//        取消
        tvCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
        layoutParam.alpha=0.5f;
        context.getWindow().setAttributes(layoutParam);
        popupWindow.showAtLocation(popupView,Gravity.BOTTOM,0,50);

    }
//拍照
    private void takeCamera(int num,final Activity mContext) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(mContext.getPackageManager()) != null) {
            File photoFile=null;
            photoFile=createImgFile();
//            android7.0以上获取文件uri
            if (photoFile!=null){
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N) {
                    Uri contentUri = FileProvider.getUriForFile(mContext, BuildConfig.APPLICATION_ID + ".fileProvider", photoFile);
//                    将拍照的照片，保存到指定uri
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri);
                }else {
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
                }
            }
//        跳转界面传回拍照所得数据
            mContext.startActivityForResult(takePictureIntent, num);
        }
    }
//   为图片创建不同的名称用于保存，避免覆盖
        public static String createFileName(){
        String fileName=null;
//        系统当前时间
            Date date=new Date(System.currentTimeMillis());
            SimpleDateFormat dateFormat=new SimpleDateFormat("'IMG'_yyyyMMdd_HHmmss");
//            创建图片格式
            fileName=dateFormat.format(date);
            return fileName;
        }
//        创建图片文件
    private File createImgFile(){
        File imgDir=Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM);
        File image=null;
        try {
            image=File.createTempFile(createFileName(),".jpg",imgDir);
        } catch (IOException e) {
            e.printStackTrace();
        }
        setCameraPhotoPath(image.getAbsolutePath());
        return image;
    }




}
