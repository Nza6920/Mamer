package com.example.my.mamer.util;

import android.animation.LayoutTransition;
import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

public class RichTextEditor extends ScrollView {
    private static final int EDIT_PADDING=0;
//    tag
    private int ViewTagIndex=0;
//    图片个数
    private int imgCount=0;
//    所有子view的容器
    private LinearLayout allLayout;
    private LayoutInflater inflater;
//    editText，软键盘监听器
    private OnKeyListener keyListener;
//    删除图片按钮监听
    private OnClickListener btnListener;
//    editText焦点监听
    private OnFocusChangeListener focusChangeListener;
//    最新被聚焦的editText
    private EditText lastFocusEdit;
//    在图片添加或删除时，触发transition动画
    private LayoutTransition mTransitioner;

    public RichTextEditor(Context context) {
        super(context);
    }

    public RichTextEditor(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public RichTextEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }
}
