package com.example.my.mamer.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.my.mamer.R;
import com.example.my.mamer.TopicParticularsActivity;
import com.example.my.mamer.adapter.TopicContentAdapter;
import com.example.my.mamer.bean.TopicContent;
import com.example.my.mamer.util.HttpUtil;
import com.example.my.mamer.util.LoadingDraw;
import com.example.my.mamer.util.StringToDate;

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

public class TopicShare extends BaseLazyLoadFragment {

    private ListView listView;
    private ArrayList<TopicContent> listData=new ArrayList<>();
    private TopicContentAdapter mAdapter;
    //    完成评论后刷新
    private SharedPreferences prefs;

    private SharedPreferences.Editor editor;

    //ui
    private final Handler msgHandler=new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            switch (msg.what){
                case DISMISS_DIALOG:
                    ((LoadingDraw)msg.obj).dismiss();
                    break;
                case MESSAGE_ERROR:
                    Toast.makeText(getActivity(), (String) msg.obj, Toast.LENGTH_SHORT).show();
                    break;
                case USER_SET_INFORMATION:
                    if (mAdapter != null) {
                        Log.e("listFragment","视图share");
                        mAdapter.clearData();
                        Log.e("adapter分享数据：",mAdapter.getDataCount()+"++++++++++++++++++++");
                        Log.e("list分享数据：",listData.size()+"++++++++++++++++++++");
                        mAdapter.updateAdd(listData);
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    });
//    初始化视图
    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view=inflater.inflate(R.layout.fragment_topic_content_view,container,false);
        listView=view.findViewById(R.id.topic_content_list_view);
        mAdapter=new TopicContentAdapter(getContext());
        listView.setAdapter(mAdapter);

        editor=PreferenceManager.getDefaultSharedPreferences(getContext()).edit();
        editor.putBoolean("topicReplyListToTopicParticulars",false);
        editor.apply();

        return view;
    }

    //    数据加载接口
    @Override
    public void onLazyLoad(int page) {
        Log.e("加载分享数据：","++++++++++++++++++++");
//        每次进入获取数据时，清空list
        Log.e("清空mAdapter分享数据：",listData.size()+"++++++++++++++++++++++++");
        listData.clear();
        Log.e("清空list分享数据：","++++++++++++++++++++++++");

        HttpUtil.sendOkHttpGetTopicList("user,category",1,"recent", page, new Callback() {
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
                                        topicContent.setCategoryId(jsonObject.getString("category_id"));
                                        topicContent.setCreateTime(StringToDate.stringToShort(jsonObject.getString("created_at")));
                                        topicContent.setReplyCount(jsonObject.getString("reply_count"));
                                        if(jsonObject.has("user")){
                                            JSONObject userInfo=jsonObject.getJSONObject("user");
                                            topicContent.setTopicAuthorName(userInfo.getString("name"));
                                            topicContent.setTopicAuthorId(userInfo.getString("id"));
                                            topicContent.setTopicAuthorPic(userInfo.getString("avatar"));
                                        }
                                        Log.i("话题列表","分享:"+topicContent.getTopicAuthorPic());
                                        listData.add(topicContent);
                                    }
                                    Message msg3 = new Message();
                                    msg3.what = USER_SET_INFORMATION;
                                    msgHandler.sendMessage(msg3);
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

//    初始化事件接口
    @Override
    public void initEvent() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                跳转到话题详情
                Intent intent=new Intent(getContext(),TopicParticularsActivity.class);
                intent.putExtra("id",listData.get(position).getTopicId());
                intent.putExtra("userId",listData.get(position).getTopicAuthorId());
                intent.putExtra("categoryId",listData.get(position).getCategoryId());
                intent.putExtra("tagId","1");
                startActivity(intent);
            }
        });
    }


    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            Log.e("分享setUserVisibleHint：","++++++++++++++++++++");
            onLazyLoad(1);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        prefs= PreferenceManager.getDefaultSharedPreferences(getContext());
        if (prefs.getBoolean("topicReplyListToTopicParticulars",false)){
            mAdapter.clearData();
            Log.e("分享onStart：","++++++++++++++++++++");
            onLazyLoad(1);
        }
    }
}