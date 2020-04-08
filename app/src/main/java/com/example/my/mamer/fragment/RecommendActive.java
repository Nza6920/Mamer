package com.example.my.mamer.fragment;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.my.mamer.MyApplication;
import com.example.my.mamer.R;
import com.example.my.mamer.adapter.RecommendActiveAdapter;
import com.example.my.mamer.bean.RecommendResource;
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

/**
 * 活跃用户
 */
public class RecommendActive extends BaseLazyLoadFragment  {
    private ListView listView;
    private ArrayList<RecommendResource> listData=new ArrayList<>();
    private RecommendActiveAdapter mAdapter;
    //    标志位
    private boolean isVisible=false;


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
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view=inflater.inflate(R.layout.fragment_recommend_view,container,false);
        listView=view.findViewById(R.id.recommend_list_view);
        mAdapter=new RecommendActiveAdapter(getContext());
        onLazyLoad(1);
        return view;
    }

    public ArrayList<RecommendResource> getListData() {
        return listData;
    }

    public void setListData(ArrayList<RecommendResource> listData) {
        this.listData = listData;
    }

    @Override
    public void onLazyLoad(int page) {
        HttpUtil.sendOkHttpGetUserRecommend(new Callback() {
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
                    switch (response.code()){
                        case HTTP_USER_GET_INFORMATION:
                            if (jresp.has("data")) {
                                jsonArray=jresp.getJSONArray("data");
                                if (jsonArray!=null){
                                    for (int i=0;i<jsonArray.length();i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        RecommendResource recommendResource=new RecommendResource();
                                        recommendResource.setRecommendUserId(jsonObject.getString("id"));
                                        recommendResource.setRecommendUserName(jsonObject.getString("name"));
                                        recommendResource.setRecommendUserAvatar(jsonObject.getString("avatar"));
                                        recommendResource.setRecommendUserIntroduction(jsonObject.getString("introduction"));

                                        listData.add(recommendResource);
                                    }
                                    final Runnable setData=new Runnable() {
                                        @Override
                                        public void run() {
                                            if (mAdapter!=null){
                                                Log.e("listFragment","资源推荐");
                                                listView.setAdapter(mAdapter);
                                                mAdapter.updateData(listData);
                                            }
                                        }};
                                    new Thread(){
                                        public void run(){
                                            msgHandler.post(setData);
                                        }
                                    }.start();
                                }
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

    @Override
    public void initEvent() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (MyApplication.globalUserInfo.token==null){
//                                    跳转到用户详情
                    Message msg3 = new Message();
                    msg3.what = MESSAGE_ERROR;
                    msg3.obj="登陆后体验更多";
                    msgHandler.sendMessage(msg3);
                }
            }
        });

    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter!=null){
            mAdapter.clearData();
            onLazyLoad(1);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            if (mAdapter!=null){
                mAdapter.clearData();
                onLazyLoad(1);
            }
        }
    }
}
