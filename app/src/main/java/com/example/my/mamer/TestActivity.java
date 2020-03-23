package com.example.my.mamer;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;

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
        View viewComment=popupStyle.getCommentView(this);
        final int idComment=popupStyle.getCommentView(this).getId();
        View viewDel=popupStyle.getDelView(this);
        final int idDel=popupStyle.getDelView(this).getId();

        final ArrayList<View> views=new ArrayList<>();
        views.add(viewComment);
        views.add(viewDel);

        final SparseArray<View> viewSparseArray=new SparseArray<>();
        viewSparseArray.put(idComment,viewComment);
        viewSparseArray.put(idDel,viewDel);

        TopicManagePopup popup=new TopicManagePopup(TestActivity.this,viewSparseArray,views, new TopicManagePopup.ClickListener() {
            @Override
            public void setUplistener(final TopicManagePopup.TopicManagePopupUtil popupUtil) {
                popupUtil.getView(idComment).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });
                popupUtil.getView(idDel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                });

            }

        }) ;
    }






}
