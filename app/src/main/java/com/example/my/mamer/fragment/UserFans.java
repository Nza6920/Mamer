package com.example.my.mamer.fragment;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.preference.PreferenceManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.my.mamer.MyApplication;
import com.example.my.mamer.R;
import com.example.my.mamer.ToUserActivity;
import com.example.my.mamer.adapter.UserAttentionAdapter;
import com.example.my.mamer.bean.ReplyUser;
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

public class UserFans extends BaseLazyLoadFragment {

    private LoadingDraw loadingDraw;
    private ListView listView;
    private ArrayList<ReplyUser> userArrayList=new ArrayList<>();
    private UserAttentionAdapter mAdapter;
    private SharedPreferences prefs;

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
                    if (mAdapter!=null){
                        mAdapter.clearData();
                        mAdapter.updateData(userArrayList);
                    }
                    break;
                default:
                    break;
            }
            return false;
        }
    });

    @Override
    public void onLazyLoad(int pageCount) {
        userArrayList.clear();
        HttpUtil.sendOkHttpGetFans(MyApplication.globalUserInfo.user.getUserId(), pageCount, new Callback() {
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
                    switch (response.code()) {
                        case HTTP_USER_GET_INFORMATION:
                            if (jresp.has("data")) {
                                jsonArray=jresp.getJSONArray("data");
                                if (jsonArray==null){
                                    Message msg2 = new Message();
                                    msg2.what = MESSAGE_ERROR;
                                    msg2.obj="还没有用户关注你";
                                    msgHandler.sendMessage(msg2);
                                }else {
                                    int jsonSize=jsonArray.length();
                                    for (int i=0;i<jsonSize;i++) {
                                        JSONObject jsonObject = jsonArray.getJSONObject(i);
                                        ReplyUser replyUser=new ReplyUser();
                                        replyUser.setUserId(jsonObject.getString("id"));
                                        replyUser.setUserName(jsonObject.getString("name"));
                                        replyUser.setUserImg(jsonObject.getString("avatar"));
                                        String introduction=jsonObject.getString("introduction");
                                        if (introduction.equals("")){
                                            replyUser.setUserInfo("");
                                        }else {
                                            replyUser.setUserInfo(introduction);
                                        }
                                        userArrayList.add(replyUser);
                                    }

                                    Message msg3 = new Message();
                                    msg3.what = USER_SET_INFORMATION;
                                    msgHandler.sendMessage(msg3);
                                }
                                if (jresp.has("pagination")){
                                    jresp=jresp.getJSONObject("pagination");
                                    String  count=jresp.getString("count");
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
    public View initView(LayoutInflater inflater, ViewGroup container) {
        View view=inflater.inflate(R.layout.fragment_user_attention_view,container,false);
        listView=view.findViewById(R.id.user_attention_list_view);
        loadingDraw=new LoadingDraw(getContext());

        mAdapter=new UserAttentionAdapter(getContext());
        listView.setAdapter(mAdapter);
        onLazyLoad(1);
        return view;
    }

    @Override
    public void initEvent() {
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
//                跳转到话题详情
                Intent intent=new Intent(getContext(),ToUserActivity.class);
                intent.putExtra("userId",userArrayList.get(position).getUserId());
                intent.putExtra("userName",userArrayList.get(position).getUserName());
                intent.putExtra("userAvatar",userArrayList.get(position).getUserImg());
                intent.putExtra("userIntro",userArrayList.get(position).getUserInfo());
                startActivity(intent);
            }
        });
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser){
            if (mAdapter!=null){
                onLazyLoad(1);
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        prefs= PreferenceManager.getDefaultSharedPreferences(getContext());
        if (prefs.getBoolean("changeAttentionStatus",false)){
            mAdapter.clearData();
            onLazyLoad(1);
        }
    }
}
