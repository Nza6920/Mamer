package com.example.my.mamer.view;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.my.mamer.R;
import com.example.my.mamer.util.BaseUtils;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

public class RichTextEditor extends ScrollView {
    private static final int EDIT_PADDING=0;
//    tag
    private int viewTagIndex=0;
//    图片个数
    private int imgCount=0;
//    所有子view的容器
    private LinearLayout allLayout;
    private LayoutInflater inflater;
//    editText，软键盘监听器
    private OnKeyListener keyListener;
//    删除图片按钮监听
    private OnClickListener picDelListener;
//    editText焦点监听
    private OnFocusChangeListener focusChangeListener;
//    最新被聚焦的editText
    private EditText lastFocusEdit;
//    在图片添加或删除时，触发transition动画
    private LayoutTransition mTransitioner;
    private int editNormalPadding=0;
    private int disappearingImageIndex=0;
//    首个EditText
    private EditText firstEdit;

    public RichTextEditor(Context context) {
        this(context,null);
    }

    public RichTextEditor(Context context, AttributeSet attrs) {
        this(context, attrs,0);
    }

    public RichTextEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        Log.e("tAg", String.valueOf(context));
        inflater=LayoutInflater.from(context);

//        初始化allLayout
        allLayout=new LinearLayout(context);
        allLayout.setOrientation(LinearLayout.VERTICAL);
        setupLayoutTransitions();
        LayoutParams layoutParams=new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
//        设置间距，
        allLayout.setPadding(50,15,50,15);
        addView(allLayout,layoutParams);

//        初始化键盘退格监听，处理点击回删按钮，view的一些合并操作
        keyListener=new OnKeyListener() {
            @Override
            public boolean onKey(View view, int i, KeyEvent keyEvent) {
                if (keyEvent.getAction()==KeyEvent.ACTION_DOWN&& keyEvent.getKeyCode()==KeyEvent.KEYCODE_DEL){
                    EditText editText= (EditText) view;
                    onBackspacePress(editText);
                }
                return false; }
        };
//        图片删除处理
        picDelListener=new OnClickListener() {
            @Override
            public void onClick(View view) {
                RelativeLayout parentView= (RelativeLayout) view.getParent();
                onImageCloseClick(parentView);
            }
        };

        focusChangeListener=new OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
//                view发生变化的视图，b判断视图是否获得焦点
                if (b){
                    lastFocusEdit= (EditText) view;
                }
            }
        };

        LinearLayout.LayoutParams firstEditParam=new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
        firstEdit=createEditText("tomorrow will be better",dip2px(context,(float)EDIT_PADDING));
        allLayout.addView(firstEdit,firstEditParam);
        lastFocusEdit=firstEdit;

    }

//        初始化transition动画
    private  void setupLayoutTransitions(){
        mTransitioner=new LayoutTransition();
        allLayout.setLayoutTransition(mTransitioner);
        mTransitioner.addTransitionListener(new LayoutTransition.TransitionListener() {
            @Override
            public void startTransition(LayoutTransition layoutTransition, ViewGroup viewGroup, View view, int i) {

            }

            @Override
            public void endTransition(LayoutTransition layoutTransition, ViewGroup viewGroup, View view, int i) {
                if (!layoutTransition.isRunning() && i==LayoutTransition.CHANGE_DISAPPEARING){
//                    动画结束，合并EditText
//                    mergeEditText();
                }
            }
        });
        mTransitioner.setDuration(300);
    }

    public  int dip2px(Context context,Float dipValue){
        float m=context.getResources().getDisplayMetrics().density;
        return (int)(dipValue*m+0.5f);
    }

//    处理软键盘backspace回退，editText光标所在的文本输入框
    private  void onBackspacePress(EditText editTxt){
        int startSelection=editTxt.getSelectionStart();
//        只有光标已经顶到文本框输入的最前方，在判定是否删除之前的图片，或两个view合并
        if (startSelection==0){
            int editIndex=allLayout.indexOfChild(editTxt);
//            如果editText-1<0，则返null
            View preView=allLayout.getChildAt(editIndex-1);
            if (null!=preView){
                if (preView instanceof RelativeLayout){
//                    光标EditText的上一个View对应的是图片
                    onImageCloseClick(preView);
                }else if (preView instanceof EditText){
//                    光标EditText的上一个View对应的还是文本框EditText
                    String str1=editTxt.getText().toString();
                    EditText preEdit= (EditText) preView;
                    String str2=preEdit.getText().toString();

                    allLayout.removeView(editTxt);
                    viewTagIndex--;

//                    文本合并
                    preEdit.setText(str2+str1);
                    preEdit.requestFocus();
//                    ????????
                    preEdit.setSelection(str2.length(),str2.length());
                    lastFocusEdit=preEdit;
                }
            }
        }
    }

//    处理删除图片点击事件，View整个image对应的relativeLayout View
    private void onImageCloseClick(View view){
        disappearingImageIndex=allLayout.indexOfChild(view);
//        删除文件夹里面的图片
        List<EditData> dataList=buildEditData();
        EditData editData=dataList.get(disappearingImageIndex);
        allLayout.removeView(view);
        viewTagIndex--;
        imgCount--;
//        当没有图片的时候，显示
        if (imgCount==0){
            firstEdit.setHint("tomorrow will be better");
        }
    }

    public void clearAllLayout(){
        allLayout.removeAllViews();
    }

    public int getLastIndex(){
        int lastEditIndex=allLayout.getChildCount();
        return lastEditIndex;
    }

//    生成文本输入框
    public EditText createEditText(String hint,int padding){
        EditText editText= (EditText) inflater.inflate(R.layout.rich_edit_text,null);
        editText.setOnKeyListener(keyListener);
        editText.setTag(viewTagIndex++);
        editText.setPadding(editNormalPadding,padding,editNormalPadding,padding);
        editText.setHint(hint);
        editText.setOnFocusChangeListener(focusChangeListener);
        editText.setLineSpacing(BaseUtils.getInstance().dip2px(6),1);
//        动态修改光标颜色，反射
        Field fCursorDrawableRes= null;
        try {
            fCursorDrawableRes = TextView.class.getDeclaredField("mCursorDrawableRes");
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        fCursorDrawableRes.setAccessible(true);
        try {
            fCursorDrawableRes.set(editText,R.drawable.shape_edit_text_cursor);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return  editText;
    }
//    生成图片view
    private RelativeLayout createImageLayout(){
        RelativeLayout layout= (RelativeLayout) inflater.inflate(R.layout.rich_edit_imageview,null);
        layout.setTag(viewTagIndex++);
        View closeView=layout.findViewById(R.id.image_close);
        closeView.setTag(layout.getTag());
        closeView.setOnClickListener(picDelListener);
        imgCount++;
        firstEdit.setHint("");
        return layout;
    }
////    根据绝对路径添加View
    public void insertImage(String imagePath,int width){
        Bitmap bitmap=getScaledBitmap(imagePath,width);
        insertImage(bitmap,imagePath);
    }
//    插入一张图片
    public void insertImage(Bitmap bitmap,String imagePath){
        String lastEditStr=lastFocusEdit.getText().toString();
        int cursorIndex=lastFocusEdit.getSelectionStart();
        String editStr1=lastEditStr.substring(0,cursorIndex);
        int lastEditIndex=allLayout.indexOfChild(lastFocusEdit);

        if (lastEditStr.length()==0|| editStr1.length()==0){
//            如果editText为空，或者光标已经定在了editText的最前面，则直接插入图片，并且EditText下移
            addImageViewAtIndex(lastEditIndex,imagePath);
        }else {
//            如果EditText非空，且光标不在最顶端，则需要添加imageView和EditText
            lastFocusEdit.setText(editStr1);
            String editStr2=lastEditStr.substring(cursorIndex);
            if (editStr2.length()==0){
                editStr2="";
            }
//            如果获取焦点的editText不是ViewGroup中的最后一个元素且光标位于EditTEXT的最后
//            否则插入EditText用来存储被图片分割的文字
            if (!(lastEditIndex<viewTagIndex-1)&&(TextUtils.isEmpty(editStr2))){
                addEditTextAtIndex(lastEditIndex+1,imagePath);
            }
            addImageViewAtIndex(lastEditIndex+1,imagePath);
            lastFocusEdit.setSelection(lastFocusEdit.getText().length());
        }
        hideKeyBoard();
    }

//        隐藏小键盘
    public void hideKeyBoard(){
        InputMethodManager inputMethodManager= (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(lastFocusEdit.getWindowToken(),0);
    }
//    在特定位置插入EditText，index位置，editStr EditText显示的文字
    public void  addEditTextAtIndex(final int index,CharSequence editStr){
        EditText editText2=createEditText("",EDIT_PADDING);
        editText2.setText(editStr);
        editText2.requestFocus();
        editText2.setOnFocusChangeListener(focusChangeListener);

        allLayout.addView(editText2,index);
    }
//    在特定位置添加imageView
    public void addImageViewAtIndex(final int index,String imagePath){
        final RelativeLayout imageLayout=createImageLayout();
        DataImageView imageView=imageLayout.findViewById(R.id.edit_image_view);
//       从网络中加载图片,Glide4.0以后的写法
        RequestOptions options=new RequestOptions()
//                加载失败的图片
                .error(R.mipmap.ic_title_close)
//                等待加载时的图片,默认占位图
                .placeholder(R.mipmap.ic_dialog_loading)
//                图片缩放，<=imageView
                .fitCenter();
            Glide.with(getContext())
                    .load(imagePath)
                    .apply(options)
                    .into(imageView);
//        保存数据
        imageView.setAbsolutePath(imagePath);
        allLayout.addView(imageLayout,index);
    }
//    根据view的宽度动态缩放bitmap尺寸,width view的宽度
    public Bitmap getScaledBitmap(String filePath,int width){
        BitmapFactory.Options options=new BitmapFactory.Options();
        options.inJustDecodeBounds=true;
        BitmapFactory.decodeFile(filePath,options);
        int sampleSize=options.outWidth>width?options.outWidth/width+1:1;
        options.inJustDecodeBounds=false;
        options.inSampleSize=sampleSize;
        return BitmapFactory.decodeFile(filePath,options);
    }
//    对外提供的接口，生成编译数据上传
    public List<EditData> buildEditData(){
        List<EditData> dataList=new ArrayList<>();
        int num=allLayout.getChildCount();
        for (int index=0;index<num;index++){
            View itemView=allLayout.getChildAt(index);
            EditData itemData=new EditData();
            if (itemView instanceof EditText){
                EditText item= (EditText) itemView;
                itemData.inputStr=item.getText().toString();
            }else if (itemView instanceof RelativeLayout){
                DataImageView item=itemView.findViewById(R.id.edit_image_view);
                itemData.imagePath=item.getAbsolutePath();
            }
            dataList.add(itemData);
        }
        return dataList;
    }
//    返回用户输入内容
//    public List<EditData> editDataStr(){
//        List<EditData> dataStr=new ArrayList<>();
//        int num=allLayout.getChildCount();
//        for (int index=0;index<num;index++){
//            View itemView=allLayout.getChildAt(index);
//            EditData itemDataStr=new EditData();
//            if (itemView instanceof EditText){
//                EditText item= (EditText) itemView;
//                itemDataStr.inputStr=item.getText().toString();
//            }
//            dataStr.add(itemDataStr);
//        }
//        return dataStr;
//    }

    public  class EditData{
        public String inputStr;
        public String imagePath;
    }

}
