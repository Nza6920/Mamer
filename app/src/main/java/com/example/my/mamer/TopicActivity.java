package com.example.my.mamer;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.chinalwb.are.AREditText;
import com.chinalwb.are.styles.toolbar.IARE_Toolbar;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_AlignmentCenter;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_AlignmentLeft;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_AlignmentRight;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_At;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Bold;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Hr;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Image;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Italic;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Link;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_ListBullet;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_ListNumber;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Quote;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Underline;
import com.chinalwb.are.styles.toolitems.IARE_ToolItem;
import com.example.my.mamer.config.GlobalTopicReply;

import static com.example.my.mamer.config.Config.MESSAGE_ERROR;
import static com.example.my.mamer.config.Config.USER_SET_INFORMATION;

//该activity用于编辑文章和新建文章
public class TopicActivity extends AppCompatActivity implements View.OnClickListener{

//    头部栏
    private TextView tvClose;
    private TextView tvTitle;
    private Button btnNext;
//    标题
    private EditText editTitle;
    private String strTopicTitle;
//    标签
    private LinearLayout layoutFlag;
    private LinearLayout layoutFlagT;
    private TextView tvFlag;
    private String flagStr;
    private TextView tvFlagClose;
//    底部工具栏
    private IARE_Toolbar mToolbar;
    private LinearLayout layoutBottom;
//    富文本
    private AREditText mEditText;
//    键盘的收起与弹出
    private TextView tvKeyboardDown;

    private final Handler msgHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case USER_SET_INFORMATION:
//                    显示标签
                    layoutFlag.setVisibility(View.GONE);
                    layoutFlagT.setVisibility(View.VISIBLE);
                    tvFlag.setText("#"+msg.obj+"#");
                    break;
                case MESSAGE_ERROR:
                    Toast.makeText(TopicActivity.this,(String)msg.obj,Toast.LENGTH_LONG).show();
                    break;
                    default:
                        break;
            }
            return false;
        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_topic);
        initTitle();
        initToolbar();
    }
//初始化title
    private void initTitle(){
        tvClose=findViewById(R.id.title_tv_close);
        tvTitle=findViewById(R.id.title_tv_name);
        btnNext=findViewById(R.id.title_btn_next);
        editTitle=findViewById(R.id.topic_title_);
        layoutFlag=findViewById(R.id.topic_flag_);
        layoutFlagT=findViewById(R.id.layout_topic_flag);
        tvFlag=findViewById(R.id.topic_flag_t);
        tvFlagClose=findViewById(R.id.topic_flag_t_close);
        layoutBottom=findViewById(R.id.topic_bottombar);
        tvKeyboardDown=findViewById(R.id.topic_keybaord_down);

        Drawable tvClosePic=ContextCompat.getDrawable(this,R.mipmap.ic_title_close);
        tvClose.setBackground(tvClosePic);
        tvClose.setOnClickListener(this);
        tvTitle.setText("撰写文章");
        btnNext.setText("发布");
        editTitle.addTextChangedListener(etWatcher);
        editTitle.setOnClickListener(this);
        btnNext.setTextColor(getResources().getColor(R.color.colorPop));
        btnNext.setOnClickListener(this);
        layoutFlag.setOnClickListener(this);
        tvFlagClose.setOnClickListener(this);
        tvKeyboardDown.setOnClickListener(this);

    }

    private void initToolbar() {
        mToolbar = this.findViewById(R.id.topic_areToolbar_);
//        加粗
        IARE_ToolItem bold = new ARE_ToolItem_Bold();
//        斜体
        IARE_ToolItem italic = new ARE_ToolItem_Italic();
//        下划线
        IARE_ToolItem underline = new ARE_ToolItem_Underline();
//        “
        IARE_ToolItem quote = new ARE_ToolItem_Quote();
//        列表123
        IARE_ToolItem listNumber = new ARE_ToolItem_ListNumber();
//        列表abc
        IARE_ToolItem listBullet = new ARE_ToolItem_ListBullet();
//        分割线
        IARE_ToolItem hr = new ARE_ToolItem_Hr();
//        链接
        IARE_ToolItem link = new ARE_ToolItem_Link();
//        左对齐
        IARE_ToolItem left = new ARE_ToolItem_AlignmentLeft();
//        居中
        IARE_ToolItem center = new ARE_ToolItem_AlignmentCenter();
//        右对齐
        IARE_ToolItem right = new ARE_ToolItem_AlignmentRight();
//        图片
        IARE_ToolItem image = new ARE_ToolItem_Image();
//        @
        IARE_ToolItem at = new ARE_ToolItem_At();
        mToolbar.addToolbarItem(bold);
        mToolbar.addToolbarItem(italic);
        mToolbar.addToolbarItem(underline);
        mToolbar.addToolbarItem(quote);
        mToolbar.addToolbarItem(listNumber);
        mToolbar.addToolbarItem(listBullet);
        mToolbar.addToolbarItem(hr);
        mToolbar.addToolbarItem(link);
        mToolbar.addToolbarItem(left);
        mToolbar.addToolbarItem(center);
        mToolbar.addToolbarItem(right);
        mToolbar.addToolbarItem(image);
        mToolbar.addToolbarItem(at);

        mEditText = this.findViewById(R.id.topic_arEditText_);
        mEditText.setOnClickListener(this);
        mEditText.setToolbar(mToolbar);
    }

    private void setHtml(String resposeHtml) {
//        转为html
        String m=mEditText.getHtml();
        Log.e("html:",m);
//        将html展示出来
        mEditText.fromHtml(resposeHtml);
    }


//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.main, menu);
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        int menuId = item.getItemId();
//        if (menuId == com.chinalwb.are.R.id.action_save) {
//            String html = this.mEditText.getHtml();
//            Util.saveHtml(this, html);
//            return true;
//        }
//        if (menuId == R.id.action_show_tv) {
//            String html = this.mEditText.getHtml();
//            Intent intent = new Intent(this, TextViewActivity.class);
//            intent.putExtra(HTML_TEXT, html);
//            startActivity(intent);
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        mToolbar.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.title_tv_close:
                finish();
                break;
            case R.id.title_btn_next:
//                发布文章按钮，判断内容合法性，判断标签是否选择

                break;
            case R.id.topic_flag_:
//                选择标签弹出dialog,选择后显示#xxxx#
                flagDialog();
                break;
            case R.id.topic_flag_t_close:
//                修改标签
                flagDialog();
                break;
            case R.id.topic_title_:
                layoutBottom.setVisibility(View.GONE);
                break;
            case R.id.topic_arEditText_:
                if (!editTitle.isFocused()){
                    layoutBottom.setVisibility(View.VISIBLE);
                }
                break;
                default:
                    break;
        }
    }
//    判断标题合法性
    private void getTopicTitleEditStr() {
        strTopicTitle=editTitle.getText().toString().trim();
    }
    //    计算位数
    private static int calculatePlaces(String str) {
        int m = 0;
        if (str!=null){
            char arr[] = str.toCharArray();
            for (int i = 0; i < arr.length; i++) {
                char c = arr[i];
//            中文字符
                if ((c >= 0x0391 && c <= 0xFFE5)) {
                    m = m + 1;
                } else if ((c >= 0x0000 && c <= 0x00FF)) {
                    m = m + 1;
                }
            }
        }
        return m;
    }
//      标题输入监听
    TextWatcher etWatcher= new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) { }
        @Override
        public void afterTextChanged(Editable editable) {
            getTopicTitleEditStr();
            int cal=calculatePlaces(strTopicTitle);
            if (!((cal>=2) && (cal<=50))){
                Message msg1=new Message();
                msg1.what=MESSAGE_ERROR;
                msg1.obj="标题应在2至50个字符内";
                msgHandler.sendMessage(msg1);
            }
        }
    };



    //    选择标签
    private void flagDialog(){
        final String items[]=null;
        int length=0;
        if (GlobalTopicReply.reply.topicDivid.size()!=0){
            length=GlobalTopicReply.reply.topicDivid.size();
            for (int i=0;i<length;i++){
                items[i]=GlobalTopicReply.reply.topicDivid.get(i).getCategoryName();
            }

            AlertDialog.Builder builder = new AlertDialog.Builder(this,length);
            builder.setTitle("请选择标签");
            builder.setSingleChoiceItems(items, 0,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.e("tag:",items[which]);
                            flagStr=items[which];
                        }
                    });
            builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    Message msg=new Message();
                    msg.what=USER_SET_INFORMATION;
                    msg.obj=flagStr;
                    msgHandler.sendMessage(msg);

                }
            });
            builder.show();
        }
        AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setMessage("当前话题标签不可选");
        dialog.setNegativeButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        dialog.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

    }
}
