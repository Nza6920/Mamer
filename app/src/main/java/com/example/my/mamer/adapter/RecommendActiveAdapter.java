package com.example.my.mamer.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.my.mamer.R;
import com.example.my.mamer.bean.RecommendResource;

import java.util.ArrayList;

import static com.example.my.mamer.MyApplication.getContext;

public class RecommendActiveAdapter extends BaseAdapter {

    private ArrayList<RecommendResource> data;
    private LayoutInflater layoutInflater;
    private Context context;

    public RecommendActiveAdapter(ArrayList<RecommendResource> data,  Context context) {
        this.data = data;
        this.layoutInflater = LayoutInflater.from(context);
        this.context = context;
    }

    //    组建对应listView中的控件
    public final class listItem{
        private ImageView userAvatar;
        private TextView userName;
        private TextView userIntroduction;
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int i) {
        return data.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        RecommendActiveAdapter.listItem listViewItem=null;
        if (convertView==null){
            listViewItem=new RecommendActiveAdapter.listItem();
//            获得组件并实例化
            convertView=layoutInflater.inflate(R.layout.fragment_recommend_active_item,null);
            listViewItem.userAvatar=convertView.findViewById(R.id.recommend_user_avatar);
            listViewItem.userName=convertView.findViewById(R.id.recommend_user_name);
            listViewItem.userIntroduction=convertView.findViewById(R.id.recommend_user_introduction);
            convertView.setTag(listViewItem);
        }else {
            listViewItem= (RecommendActiveAdapter.listItem) convertView.getTag();
        }
//        绑定数据
        RequestOptions options=new RequestOptions()
                .error(R.mipmap.ic_image_error)
                .placeholder(R.mipmap.ic_image_error);
        Glide.with(getContext())
                .asBitmap()
                .load(data.get(position).getRecommendUserAvatar())
                .apply(options)
                .into(listViewItem.userAvatar);
        listViewItem.userName.setText(data.get(position).getRecommendUserName());
        listViewItem.userIntroduction.setText(data.get(position).getRecommendUserIntroduction());

        return convertView;
    }
}
