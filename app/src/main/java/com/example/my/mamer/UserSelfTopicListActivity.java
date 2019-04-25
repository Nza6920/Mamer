package com.example.my.mamer;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.my.mamer.adapter.UserSelfTopicAdapter;
import com.example.my.mamer.bean.TopicContent;
import com.example.my.mamer.util.HttpUtil;
import com.example.my.mamer.util.LoadingDraw;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

import static com.example.my.mamer.config.Config.DISMISS_DIALOG;
import static com.example.my.mamer.config.Config.HTTP_USER_GET_INFORMATION;
import static com.example.my.mamer.config.Config.MESSAGE_ERROR;
import static com.example.my.mamer.config.Config.USER_SET_INFORMATION;

public class UserSelfTopicListActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<TopicContent> listData=new ArrayList<>();
    private UserSelfTopicAdapter mAdapter;
    private TextView tvBack;
    private TextView tvTitle;

    //ui
    private final Handler msgHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case DISMISS_DIALOG:
                    ((LoadingDraw)msg.obj).dismiss();
                    break;
                case MESSAGE_ERROR:
                    Toast.makeText(UserSelfTopicListActivity.this, (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                case USER_SET_INFORMATION:
                    mAdapter.notifyDataSetChanged();
                    Log.e("Tag","数据刷新完成");
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_self_topic_list);
        initUI();
        onDataLoad();
        mAdapter=new UserSelfTopicAdapter(getApplicationContext(),getListData());
        listView.setAdapter(mAdapter);
        initEvent();

    }

    public ArrayList<TopicContent> getListData() {
        Log.e("Tag","返回数据完成");
        return listData;
    }

    public void setListData(ArrayList<TopicContent> listData) {
        this.listData = listData;
    }

    private void initUI(){
        listView=findViewById(R.id.user_self_topic_list);
        tvBack=findViewById(R.id.title_tv_close);
        tvTitle=findViewById(R.id.title_tv_name);

        Drawable tvBackPic=ContextCompat.getDrawable(this,R.mipmap.ic_title_back);
        tvBack.setBackground(tvBackPic);

        tvTitle.setText("我的话题");
        tvTitle.setTextSize(25);
        Log.e("Tag","UI完成");
    }
    //    数据加载接口
    public void onDataLoad() {
        Log.e("Tag","进入数据获取");
        HttpUtil.sendOkHttpGetUserTopicList(1, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {

                Message msg2 = new Message();
                msg2.what = MESSAGE_ERROR;
                msg2.obj = "服务器异常,请检查网络";
                msgHandler.sendMessage(msg2);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                JSONObject jresp = null;
                JSONArray jsonArray=null;
                try {
                    jresp = new JSONObject(response.body().string());
                    switch (response.code()) {
                        case HTTP_USER_GET_INFORMATION:

                            if (jresp.has("data")) {
                                jsonArray=jresp.getJSONArray("data");
                                int jsonSize=jsonArray.length();
                                for (int i=0;i<jsonSize;i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    TopicContent topicContent = new TopicContent();
                                    topicContent.setTopicId(jsonObject.getString("id"));
                                    topicContent.setTopicTitle(jsonObject.getString("title"));
                                    topicContent.setTopicAuthorId(jsonObject.getString("user_id"));
                                    topicContent.setCategoryId(jsonObject.getString("category_id"));
                                    topicContent.setReplyCount(jsonObject.getString("reply_count"));
                                    topicContent.setUpdateTime(jsonObject.getString("updated_at"));
                                    listData.add(topicContent);
                                }

                                Message msg3 = new Message();
                                msg3.what = USER_SET_INFORMATION;
                                msgHandler.sendMessage(msg3);

//       notifyDataSetChanged方法通过一个外部的方法控制如果适配器的内容改变时需要强制调用getView来刷新每个Item的内容
                            }
                            break;
                        default:
                            break;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
    }

//    listView监听事件,
// adapterView发生点击的adapterView，
// view被点击的item的view通过它获得该项中的各个组件
// position adapterView中的行位置，
//    id adapter数据源的第几条记录
private void initEvent(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//                暂存数据
                SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(UserSelfTopicListActivity.this).edit();
                editor.putString("id",listData.get(position).getTopicId());
                editor.putString("categoryId",listData.get(position).getCategoryId());
                editor.apply();

                Intent intent=new Intent(UserSelfTopicListActivity.this,TopicParticularsActivity.class);
                startActivity(intent);
            }
        });
}
}
