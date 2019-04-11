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

import com.example.my.mamer.R;

public class CircleImageView extends android.support.v7.widget.AppCompatImageView {
//    图片
    private static Bitmap bitmap;
//    画圆形头像的笔
    private Paint mPaintCircle;
//    画圆形边界的笔
    private Paint mPaintBorder;
//    画圆形背景的笔
    private  Paint mPaintBackground;
//    图像着色器,用来画圆
    private BitmapShader mBitmapShader;
//    图片变换处理器，用来适应view控件的大小
    private Matrix mMatrix;
//    获得控件的宽高半径,边界宽度，边界边框颜色，圆形头像背景色
    private int mWidth;
    private int mHeight;
    private int radius;
    private int mCircleBorderWidth;
    private int mCircleBorderColor;
    private int mCircleBackgroundColor;

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public void setmWidth(int mWidth) {
        this.mWidth = mWidth;
    }

    public void setmHeight(int mHeight) {
        this.mHeight = mHeight;
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
                    mCircleBorderColor=typedArray.getColor(attr,Color.WHITE);
                    break;
                case R.styleable.attrsCircle_backgroundColor:
                    mCircleBackgroundColor=(int)typedArray.getDimension(attr,Color.YELLOW);
                    break;

            }
        }
        init();
    }

   private void init(){
//初始图片变化处理器
        mMatrix=new Matrix();

        mPaintCircle=new Paint();
//        抗锯齿
        mPaintCircle.setAntiAlias(true);
//        图片边界宽度
        mPaintCircle.setStrokeWidth(5);

//       图形加边框
       mPaintBorder=new Paint();
       mPaintBorder.setAntiAlias(true);
       mPaintBorder.setStyle(Paint.Style.STROKE);
       mPaintBorder.setStrokeWidth(mCircleBorderWidth);
       mPaintBorder.setColor(mCircleBorderColor);
//       背景颜色笔
       mPaintBackground=new Paint();
       mPaintBackground.setColor(mCircleBackgroundColor);
       mPaintBackground.setAntiAlias(true);
       mPaintBackground.setStyle(Paint.Style.FILL);


   }
    //       bitmapshader画圆图形
    private void setBipmapShader(){
//        将bitmap放进图片着色器，后面两个模式是x y轴的缩放模式，CLAMP表示拉伸
        mBitmapShader=new BitmapShader(bitmap,Shader.TileMode.CLAMP,Shader.TileMode.CLAMP);
        float scale=1.0f;
//        将图片宽度高度的最小者作为图片的边长，用来和view来计算伸缩比列
        int bitmapSize=Math.min(bitmap.getHeight(),bitmap.getWidth());
//         计算缩放比例，view的大小和图片的大小比例
        scale=mWidth*1.0f/bitmapSize;
//        利用图像变化处理器，设置伸缩比例，长宽以相同比例伸缩
        mMatrix.setScale(scale,scale);
        mBitmapShader.setLocalMatrix(mMatrix);
        mPaintCircle.setShader(mBitmapShader);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        mWidth=getWidth();
        mHeight=getHeight();
        int mCircleSize=Math.min(mHeight,mWidth);
        radius=mCircleSize/2-mCircleBorderWidth;
    }

    @Override
    protected void onDraw(Canvas canvas) {

        if (getDrawable()!=null){

            setBipmapShader();
            canvas.drawRect(0,0,mWidth,mHeight,mPaintBackground);
            canvas.drawCircle(mWidth/2,mHeight/2,radius,mPaintCircle);
            canvas.drawCircle(mWidth/2,mHeight/2,radius+mCircleBorderWidth,mPaintBorder);

        }else {
//            没有图片的时候为默认ImageView
            super.onDraw(canvas);
        }

    }
}
