package com.example.my.mamer.util;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.TextView;

import com.example.my.mamer.R;

public class LoadingDraw extends Dialog {
    private TextView tvText;
    public LoadingDraw(Context context) {
        super(context);
//        对话框背景
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(R.layout.loading);
        tvText=findViewById(R.id.loading_message);
       setCanceledOnTouchOutside(false);

    }
    public LoadingDraw setMessage(String message){
        tvText.setText(message);
        return this;
    }


}
