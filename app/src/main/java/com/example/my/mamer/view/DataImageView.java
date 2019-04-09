package com.example.my.mamer.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;

public class DataImageView extends android.support.v7.widget.AppCompatImageView {
//    存放bitmap和path
    private String absolutePath;
    private Bitmap bitmap;

    public DataImageView(Context context) {
        this(context,null);
    }

    public DataImageView(Context context,AttributeSet attrs) {
        this(context, attrs,0);
    }

    public DataImageView(Context context,AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }
}
