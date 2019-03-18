package com.example.my.mamer;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.my.mamer.config.User;
import com.example.my.mamer.util.PhotoPopupWindow;

import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static com.example.my.mamer.config.Config.RESULT_CAMERA_IMAGE;
import static com.example.my.mamer.config.Config.RESULT_LODA_IMAGE;

public class TestActivity extends AppCompatActivity {
    private static final MediaType JSON=MediaType.parse("application/json;charset=utf-8");
    private String address="https://mamer.club/api/captchas";
    private Button btn_test;
    private TextView tv_test;
    private PhotoPopupWindow photoPopupWindow;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        tv_test=findViewById(R.id.tv_test);
        btn_test=findViewById(R.id.btn_test);
        btn_test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                postInfomation(address, new okhttp3.Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {


                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {

                        String responseText=response.body().string();
                        showResponse(responseText);
                    }
                });
            }
        });
        User user=new User();
        user.getUserPassKey();
        Log.e("Tag",user.getUserPassKey());

    }



    //数据转为json格式
    private String getJson(String phoneNumber)throws Exception{
        JSONObject jsonParam=new JSONObject();
        jsonParam.put("phone",phoneNumber);
        return jsonParam.toString();
    }
    //    post
    private void postInfomation(String address,okhttp3.Callback callback){
        OkHttpClient client=new OkHttpClient();
        String jsonStr="";
        try {
            jsonStr=getJson("15208242948");
        }catch (Exception e){
            e.printStackTrace();
        }

        RequestBody requestBody=RequestBody.create(JSON,jsonStr);
        Request request=new Request.Builder().url(address).post(requestBody).build();
        client.newCall(request).enqueue(callback);
    }
//    UI
    private void showResponse(final String response){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                tv_test.setText(response);
            }
        });
    }



}
