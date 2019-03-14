package com.example.my.mamer.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.View;

import com.example.my.mamer.R;

public class CircleImageView extends View {
//    图片
    private static Bitmap bitmap;
//    画圆形头像的笔
    private Paint mPaintCircle;
//    画圆形边界的笔
    private Paint mPaintBorder;
//    画圆形背景的笔
    private  Paint mPaintBackgroud;
//    图像着色器
    private BitmapShader mBitmapShader;
//    图片变换处理器，用来适应view控件的大小
    private Matrix mMatrix;
//    获得控件的宽高半径,边界宽度，边界边框颜色，圆形头像背景色
    private int width;
    private int height;
    private int radius;
    private int mCircleBorderWidth;
    private int mCircleBorderColer;
    private int mCircleBackgroudColor;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public CircleImageView(Context context) {
        super(context);
    }

    public CircleImageView(Context context,  AttributeSet attrs) {
        super(context, attrs);
        TypedArray typedArray=context.obtainStyledAttributes(attrs,R.styleable.attrsCircle );
        int n= typedArray.getIndexCount();
        for (int i=0;i<n;i++){
            int attr=typedArray.getIndex(i);
            switch (attr){
                case R.styleable.attrsCircle_circleBorderHeadWidth:
                    mCircleBorderWidth=(int)typedArray.getDimension(attr,0);
                    break;
                case R.styleable.attrsCircle_ringHeadColor:
                    mCircleBorderColer=typedArray.getColor(attr,Color.WHITE);
                    break;
                case R.styleable.attrsCircle_backgroundColor:
                    mCircleBackgroudColor=(int)typedArray.getDimension(attr,Color.YELLOW);
                    break;

            }
        }
        init();
    }

   private void init(){

        mMatrix=new Matrix();
        mPaintCircle=new Paint();
        mPaintCircle.setAntiAlias(true);
        mPaintCircle.setStrokeMiter(5);
//        阴影
       this.setLayerType(LAYER_TYPE_SOFTWARE,mPaintCircle);
       mPaintCircle.setShadowLayer(13.0f,5.0f,5.0f,Color.GRAY);
//       图形加边框
       mPaintBorder=new Paint();
       mPaintBorder.setAntiAlias(true);
       mPaintBorder.setStyle(Paint.Style.STROKE);
       mPaintBorder.setStrokeWidth(mCircleBorderColer);
//       背景颜色笔
       mPaintBackgroud=new Paint();
       mPaintBackgroud.setColor(mCircleBackgroudColor);
       mPaintBackgroud.setAntiAlias(true);
       mPaintBackgroud.setStyle(Paint.Style.FILL);


   }
    //       bitmapshader画圆图形
    private void setBipmapShader(){
        mBitmapShader=new BitmapShader(bitmap,Shader.TileMode.CLAMP,Shader.TileMode.CLAMP);
        float scale=1.0f;
        int bitmapSize=Math.min(bitmap.getHeight(),bitmap.getWidth());

        scale=width*1.0f/bitmapSize;
        mMatrix.setScale(scale,scale);
        mBitmapShader.setLocalMatrix(mMatrix);
        mPaintCircle.setShader(mBitmapShader);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        width=getWidth();
        height=getHeight();
        int mCircleSize=Math.min(height,width);
        radius=mCircleSize/2-mCircleBorderWidth;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (getBitmap()!=null){

            setBipmapShader();
            canvas.drawRect(0,0,width,height,mPaintBackgroud);
            canvas.drawCircle(width/2,height/2,radius,mPaintCircle);
            canvas.drawCircle(width/2,height/2,radius+mCircleBorderWidth/2,mPaintBorder);

        }else {
            super.onDraw(canvas);
        }

    }
}
