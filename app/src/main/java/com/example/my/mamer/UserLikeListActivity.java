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
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.my.mamer.adapter.UserLikeAdapter;
import com.example.my.mamer.bean.TopicContent;
import com.example.my.mamer.util.BaseUtils;
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

import static com.example.my.mamer.MyApplication.getContext;
import static com.example.my.mamer.config.Config.DISMISS_DIALOG;
import static com.example.my.mamer.config.Config.KEYBOARD_DOWN;
import static com.example.my.mamer.config.Config.KEYBOARD_UP;
import static com.example.my.mamer.config.Config.MESSAGE_ERROR;
import static com.example.my.mamer.config.Config.REFRESH_TOKEN;
import static com.example.my.mamer.config.Config.USER_SET_INFORMATION;

public class UserLikeListActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayList<TopicContent> listData=new ArrayList<>();
    private UserLikeAdapter mAdapter;
    private TextView tvBack;
    private TextView tvTitle;
    private LoadingDraw loadingDraw;
    private RelativeLayout rlLike;

    private Intent intent=null;


    private final Handler msgHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case KEYBOARD_UP:
                    BaseUtils.keyboardUpUtil(getContext(), (View) msg.obj);
                    break;
                case KEYBOARD_DOWN:
                    BaseUtils.keyboardDownUtil(getContext(), (View) msg.obj);
                    break;
                case MESSAGE_ERROR:
                    Toast.makeText(getContext(),(String)msg.obj,Toast.LENGTH_SHORT).show();
                    break;
                case USER_SET_INFORMATION:
                    mAdapter.clearData();
                    mAdapter.updateData(listData);
                    break;
                case DISMISS_DIALOG:
                    loadingDraw.dismiss();
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
        setContentView(R.layout.activity_user_self_like_list);
        loadingDraw=new LoadingDraw(this);
        intent=getIntent();
        initUI();
        getUserlike(1);

        mAdapter=new UserLikeAdapter(getContext());
        listView.setAdapter(mAdapter);
        initEvent();

    }

    private void initUI(){
        listView=findViewById(R.id.user_like_list);
        tvBack=findViewById(R.id.title_tv_close);
        tvTitle=findViewById(R.id.title_tv_name);

        Drawable tvBackPic=ContextCompat.getDrawable(this,R.mipmap.ic_title_close);
        tvBack.setBackground(tvBackPic);
        tvTitle.setText("我的点赞");

        tvBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    //    获取点赞列表
    private void getUserlike(int pageCount){
        loadingDraw.show();
        HttpUtil.sendOkHttpGetLike(pageCount, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg1=new Message();
                msg1.what=DISMISS_DIALOG;
                msg1.obj=loadingDraw;
                msgHandler.sendMessage(msg1);

                Message msg2 = new Message();
                msg2.what = MESSAGE_ERROR;
                msg2.obj = "服务器异常,请检查网络";
                msgHandler.sendMessage(msg2);
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Message msg1=new Message();
                msg1.what=DISMISS_DIALOG;
                msg1.obj=loadingDraw;
                msgHandler.sendMessage(msg1);

                JSONObject jresp = null;
                JSONArray jsonArray=null;
                try {
                    jresp=new JSONObject(response.body().string());
                    if (jresp.has("data")){
                        jsonArray=jresp.getJSONArray("data");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject jrespStr=jsonArray.getJSONObject(i);
                            TopicContent content=new TopicContent();
                            content.setTopicId(jrespStr.getString("id"));
                            content.setTopicTitle(jrespStr.getString("title"));
                            content.setTopicAuthorId(jrespStr.getString("user_id"));
                            content.setTopicExcerpt(jrespStr.getString("excerpt"));
                            listData.add(content);
                        }
                        final Runnable setUserReplyCount=new Runnable() {
                            @Override
                            public void run() {
                                if ((listData.size()!=0)&&mAdapter!=null){
                                    Log.e("likeView设置数据:","-----------------------");
                                    Log.e("likeView:","-"+listData+"-"+mAdapter.toString()+"---------------------");
                                    mAdapter.clearData();
                                    mAdapter.updateData(listData);
                                }
                            }};
                        new Thread(){
                            public void run(){
                                msgHandler.post(setUserReplyCount);
                            }
                        }.start();
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        });
    }

    //    每个itemView的点击事件，点击可以跳到当前回复对象的个人首页
    private void initEvent(){
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(final AdapterView<?> adapterView, final View view, final int position, long l) {
                Intent intent=new Intent(UserLikeListActivity.this,TopicParticularsActivity.class);
                intent.putExtra("id",listData.get(position).getTopicId());
                intent.putExtra("userId",listData.get(position).getTopicAuthorId());
                intent.putExtra("categoryId",listData.get(position).getCategoryId());
                Log.e("intent:", String.valueOf(intent));
                startActivity(intent);
            }
        });
    }

    //    刷新
    public  void  refreshKey(){
        HttpUtil.sendOkHttpRefreshToken(REFRESH_TOKEN, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Message msg1 = new Message();
                msg1.what = MESSAGE_ERROR;
                msg1.obj = "登录已失效，请重新登录";
                msgHandler.sendMessage(msg1);

                Intent intent1 = new Intent(UserLikeListActivity.this, LoginActivity.class);
                startActivity(intent1);
                finish();
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                try {
                    JSONObject jrep= new JSONObject(response.body().string());
                    String key=jrep.getString("access_token");
                    String type=jrep.getString("token_type");
                    MyApplication.globalUserInfo.token=key;
                    MyApplication.globalUserInfo.tokenType=type;
                    SharedPreferences.Editor editor=PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
                    editor.putString("key",key);
                    editor.putString("type",type);
                    editor.apply();

                    Message msg1 = new Message();
                    msg1.what = MESSAGE_ERROR;
                    msg1.obj = "已刷新，请重试";
                    msgHandler.sendMessage(msg1);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });

    }
}
