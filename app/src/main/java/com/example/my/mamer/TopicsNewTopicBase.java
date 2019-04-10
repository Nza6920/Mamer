package com.example.my.mamer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;

public abstract class TopicsNewTopicBase extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView();
        initTitle();
        initViews();
    }
//    设置布局
    protected abstract void setContentView();
//    初始化标题栏
    protected abstract void initTitle();
//    初始化控件
    protected abstract void  initViews();

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
//            返回按钮
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }
}
