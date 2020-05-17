package com.example.my.mamer.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.load.resource.gif.GifDrawable;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.Target;
import com.example.my.mamer.R;
import com.example.my.mamer.bean.RecommendResource;

import java.util.ArrayList;

import static com.example.my.mamer.MyApplication.getContext;

public class RecommendActiveAdapter extends BaseAdapter {

    private ArrayList<RecommendResource> data =new ArrayList<>();
    private LayoutInflater layoutInflater;
    private Context context;

    public RecommendActiveAdapter(Context context) {
        this.context = context;
        this.layoutInflater = LayoutInflater.from(context);
    }

    public RecommendActiveAdapter(ArrayList<RecommendResource> data, Context context) {
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
        if (data.size()==0){
            return 0;
        }
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
        listItem listViewItem=null;
        if (convertView==null){
            listViewItem=new listItem();
//            获得组件并实例化
            convertView=layoutInflater.inflate(R.layout.fragment_recommend_active_item,null);
            listViewItem.userAvatar=convertView.findViewById(R.id.recommend_user_avatar);
            listViewItem.userName=convertView.findViewById(R.id.recommend_user_name);
            listViewItem.userIntroduction=convertView.findViewById(R.id.recommend_user_introduction);
            convertView.setTag(listViewItem);
        }else {
            listViewItem= (listItem) convertView.getTag();
        }
        if (null==data) return convertView;

//        绑定数据
        RequestOptions options=new RequestOptions()
                .error(R.mipmap.ic_image_error)
                .placeholder(R.mipmap.ic_image_error);

        if (data.get(position).getRecommendUserAvatarType().equals("fig")){
            Glide.with(getContext())
                    .load(data.get(position).getRecommendUserAvatar())
                    .apply(options)
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                            if (resource instanceof GifDrawable){
                                ((GifDrawable) resource).setLoopCount(5);
                            }
                            return false;
                        }
                    }).into(listViewItem.userAvatar);
        }else {
            Glide.with(getContext())
                    .asBitmap()
                    .load(data.get(position).getRecommendUserAvatar())
                    .apply(options)
                    .into(listViewItem.userAvatar);
        }

        listViewItem.userName.setText(data.get(position).getRecommendUserName());
        listViewItem.userIntroduction.setText(data.get(position).getRecommendUserIntroduction());

        return convertView;
    }

    //    更新数据，并且清除之前的数据
    public void updateData(ArrayList<RecommendResource> list){
        Log.e("RecommendResource更新数据:","-----------------------");

        this.data=list;
        notifyDataSetChanged();
        Log.e("RecommendResource更新视图:","-----------------------");
    }

    public void clearData(){
        this.data.clear();
    }
}
