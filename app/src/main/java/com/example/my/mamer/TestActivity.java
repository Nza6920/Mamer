package com.example.my.mamer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.example.my.mamer.util.PopupItemStyle.PopupStyle;
import com.example.my.mamer.util.TopicManagePopup;

import java.util.ArrayList;

public class TestActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);
        Button  button=findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getmm();
            }
        });
    }

    private void getmm(){
        final PopupStyle popupStyle=new PopupStyle();

        LinearLayout viewDel=popupStyle.getDelView(this);
        final int idDel=popupStyle.getDelView(this).getId();

        final ArrayList<LinearLayout> views=new ArrayList<>();

        views.add(viewDel);

        final SparseArray<LinearLayout> viewSparseArray=new SparseArray<>();

        viewSparseArray.put(idDel,viewDel);

        TopicManagePopup popup=new TopicManagePopup(TestActivity.this,viewSparseArray,views, new TopicManagePopup.ClickListener() {
            @Override
            public void setUplistener(final TopicManagePopup.TopicManagePopupUtil popupUtil) {

                popupUtil.getView(idDel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Toast.makeText(TestActivity.this,"删除话题评论",Toast.LENGTH_SHORT).show();
                    }
                });


            }

        }) ;
    }






}
