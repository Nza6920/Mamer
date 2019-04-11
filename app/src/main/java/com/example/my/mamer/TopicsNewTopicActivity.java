package com.example.my.mamer;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.support.v4.content.ContextCompat;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.my.mamer.view.RichTextEditor;

import org.json.JSONException;

import java.util.List;

import static com.example.my.mamer.config.Config.RESULT_LODA_IMAGE;
import static com.example.my.mamer.config.Config.USER_SET_INFORMATION;

public class TopicsNewTopicActivity extends TopicsNewTopicBase implements View.OnClickListener {

//    title栏
    private TextView tvClose;
    private TextView tvTitleName;
    private Button btnCommit;
//    topic title栏
    private LinearLayout layoutTopicTitle;
    private EditText etTopicTitle;
    private String strTopicTitle;
//    分类选择栏
    private LinearLayout layoutTopicClassify;
    private TextView tvShare;
    private TextView tvTech;
    private TextView tvQueAnswer;
    private TextView tvNotice;
//    显示选择话题分类
    private LinearLayout layoutTopicClassifyShow;
    private TextView tvShowSelected;
    private TextView tvShowSelectedInfo;
//    选中的话题
    private int categoryId=0;
//    富文本编辑控件
    private RichTextEditor richTextEditor;
    private String strContent;
//    添加图片
    private TextView tvPic;
    public static int richTextWidth;

    private final Handler msgHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case USER_SET_INFORMATION:
                    Toast.makeText(TopicsNewTopicActivity.this,(String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void setContentView() {
        setContentView(R.layout.activity_topics_new_topic);
    }

    @Override
    protected void initTitle() {
        tvClose=findViewById(R.id.title_tv_close);
        tvTitleName=findViewById(R.id.title_tv_name);
        btnCommit=findViewById(R.id.title_btn_next);

        Drawable tvClosePic=ContextCompat.getDrawable(this,R.mipmap.ic_title_close);
        tvClose.setBackground(tvClosePic);
        tvClose.setOnClickListener(this);
        tvTitleName.setText("新建话题");
        tvTitleName.setTextSize(20);
        btnCommit.setText("完成");
        btnCommit.setOnClickListener(this);

    }

    @Override
    protected void initViews() {
        layoutTopicTitle=findViewById(R.id.new_topic_title);
        etTopicTitle=findViewById(R.id.new_topic_title_edit);
        layoutTopicClassify=findViewById(R.id.new_topic_classify);
        tvShare=findViewById(R.id.new_topic_share);
        tvTech=findViewById(R.id.new_topic_tech);
        tvQueAnswer=findViewById(R.id.new_topic_question_answering);
        tvNotice=findViewById(R.id.new_topic_notice);
        layoutTopicClassifyShow=findViewById(R.id.new_topic_classify_show);
        tvShowSelected=findViewById(R.id.new_topic_selected);
        tvShowSelectedInfo=findViewById(R.id.new_topic_selected_info);
        richTextEditor=findViewById(R.id.new_topic_rich_text_editor);
        tvPic=findViewById(R.id.new_topic_pic);

//        标题
        etTopicTitle.addTextChangedListener(etWatcher);
//        分类
        tvShare.setOnClickListener(this);
        tvShare.setText("分享");
        tvTech.setOnClickListener(this);
        tvTech.setText("教程");
        tvQueAnswer.setOnClickListener(this);
        tvQueAnswer.setText("问答");
        tvNotice.setOnClickListener(this);
        tvNotice.setText("公告");
        tvShowSelected.setOnClickListener(this);
//        图片
        tvPic.setOnClickListener(this);
//        view.post方式获得该控件的宽度
        richTextEditor.post(new Runnable() {
            @Override
            public void run() {
                richTextWidth=richTextEditor.getWidth();
            }
        });
    }

//    点击事件监听
    @Override
    public void onClick(View view) {
        int v=view.getId();
        switch (v){
//            分类选择后展示
            case  R.id.new_topic_share:
                layoutTopicClassify.setVisibility(View.GONE);
                layoutTopicClassifyShow.setVisibility(View.VISIBLE);
                tvShowSelected.setText("分享");
                tvShowSelectedInfo.setText("分享创造, 分享发现");
                setCategoryId(1);
                break;
            case R.id.new_topic_tech:
                layoutTopicClassify.setVisibility(View.GONE);
                layoutTopicClassifyShow.setVisibility(View.VISIBLE);
                tvShowSelected.setText("教程");
                tvShowSelectedInfo.setText("开发经验, 推荐扩展包");
                setCategoryId(2);
                break;
            case R.id.new_topic_question_answering:
                layoutTopicClassify.setVisibility(View.GONE);
                layoutTopicClassifyShow.setVisibility(View.VISIBLE);
                tvShowSelected.setText("问答");
                tvShowSelectedInfo.setText("请保持友善, 互帮互");
                setCategoryId(3);
                break;
            case R.id.new_topic_notice:
                layoutTopicClassify.setVisibility(View.GONE);
                layoutTopicClassifyShow.setVisibility(View.VISIBLE);
                tvShowSelected.setText("公告");
                tvShowSelectedInfo.setText("站点公告");
                setCategoryId(4);
                break;
            case R.id.new_topic_selected:
                layoutTopicClassifyShow.setVisibility(View.GONE);
                layoutTopicClassify.setVisibility(View.VISIBLE);
                setCategoryId(0);
                break;

            case R.id.new_topic_pic:
                Intent intent=new Intent("android.intent.action.GET_CONTENT");
                intent.setType("image/*");
                startActivityForResult(intent,RESULT_LODA_IMAGE);
                break;
            case R.id.title_btn_next:
                if (isCommit()){

                }
                break;
            case R.id.title_tv_close:
                Intent i=new Intent(TopicsNewTopicActivity.this,BottomNavigationBarActivity.class);
                startActivity(i);
                finish();
                break;
                default:
                    break;
        }
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
            if (!(cal>=2||cal<=50)){
                Message msg1=new Message();
                msg1.what=USER_SET_INFORMATION;
                msg1.obj="标题应在2至50个字符内";
                msgHandler.sendMessage(msg1);
            }
        }
    };
//话题分类id get set
    public int getCategoryId() {
        return categoryId;
    }
    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }
//    获得标题输入值
    private void getTopicTitleEditStr(){
        strTopicTitle=etTopicTitle.getText().toString().trim();
    }
//    计算位数
    private static int calculatePlaces(String str) {
        int m = 0;
        char arr[] = str.toCharArray();
        for (int i = 0; i < arr.length; i++) {
            char c = arr[i];
//            中文字符
            if ((c >= 0x0391 && c <= 0xFFE5)) {
                m = m + 2;
            } else if ((c >= 0x0000 && c <= 0x00FF)) {
                m = m + 1;
            }
        }
        return m;
    }
//    插入图片
    private void insertImage(String imagePath){
        richTextEditor.insertImage(imagePath);
    }
//     回调图片
    @Override
    protected void onActivityResult(int requestCode, int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode==RESULT_OK){
//            从相册获取
            if (requestCode==RESULT_LODA_IMAGE&& null!=data){
//判断手机系统版本号
                try {
                    handleImageOnKitKat(data);
                    Log.e("imagePath", String.valueOf(data));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }

    }
//    处理图片
    private void handleImageOnKitKat(Intent data) throws JSONException {
        String imagePath=null;
        Uri uri=data.getData();
        if (DocumentsContract.isDocumentUri(this,uri)){
            String docId=DocumentsContract.getDocumentId(uri);
            if ("com.android.providers.media.documents".equals(uri.getAuthority())){
//                解析出数字格式id
                String id=docId.split(":")[1];
                String selection=MediaStore.Images.Media._ID+"="+id;
                imagePath=getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,selection);
            }else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())){
                Uri contentUri=ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"),Long.valueOf(docId));
                imagePath=getImagePath(contentUri,null);
            }
        }else if ("content".equalsIgnoreCase(uri.getScheme())){
            imagePath=getImagePath(uri,null);
        }else if ("file".equalsIgnoreCase(uri.getScheme())){
            imagePath=uri.getPath();
        }
//        Log.e("imagePath",imagePath);
        insertImage(imagePath);
    }
//    图片路径
    private String getImagePath(Uri uri,String selection){
        String path=null;
        Cursor cursor=getContentResolver().query(uri,null,selection,null,null);
        if (cursor!=null){
            if (cursor.moveToFirst()){
                path=cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }
//    生成控件中的数据
    private String getEditData(){
        List<RichTextEditor.EditData> editDataList=richTextEditor.buildEditData();
        StringBuilder content=new StringBuilder();
        if (editDataList.size()>0){
            content.append("<div class=\"content\">");
            for (RichTextEditor.EditData itemData:editDataList){
                if (itemData.inputStr!=null){
//                    将EditText中的换行符，空格符转换成html
                    String inputStr=itemData.inputStr.replace("\n","<p></p>").replace("","&nbsp");
                    content.append("<p>").append(inputStr).append("</p>");
                }else if (itemData.imagePath!=null){
                    content.append("<p style=\"text-align:center\"><img width=\"100%\" src=\"").append(itemData.imagePath).append("\"/></p>");
                }
            }
            content.append("</div>");
        }
        return content.toString();
    }
//    字数限制
    private void getInputEditStr() {
        List<RichTextEditor.EditData> editDataList = richTextEditor.buildEditData();
        if (editDataList.size() > 0) {
            for (RichTextEditor.EditData itemData : editDataList) {
                if (itemData.inputStr != null) {
//                    将EditText中的换行符，空格符转换成空
                   strContent = itemData.inputStr.replace("\n", "").replace("", "");
                }
            }
        }
    }
//    提交条件，通行证,topictitle,choiceClassify,content
    private boolean isCommit(){
        getTopicTitleEditStr();
        getInputEditStr();
        int inputTitleStrCount=calculatePlaces(strTopicTitle);
        int cId=getCategoryId();
        int inputStrCount=calculatePlaces(strContent);
        if (inputTitleStrCount>50||inputTitleStrCount<2){
            Message msg1=new Message();
            msg1.what=USER_SET_INFORMATION;
            msg1.obj="标题应在2至50个字符内";
            msgHandler.sendMessage(msg1);
            return false;
        }else if (cId==0){
            Message msg1=new Message();
            msg1.what=USER_SET_INFORMATION;
            msg1.obj="请选择话题分类";
            msgHandler.sendMessage(msg1);
            return false;
        }else if (inputStrCount<3){
            Message msg1=new Message();
            msg1.what=USER_SET_INFORMATION;
            msg1.obj="请输入至少三个字符";
            msgHandler.sendMessage(msg1);
            return false;
        }else {
            return true;
        }
    }
//    提交


}

