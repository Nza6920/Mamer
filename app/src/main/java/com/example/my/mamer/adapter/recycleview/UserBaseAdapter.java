package com.example.my.mamer.adapter.recycleview;


import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

public abstract class UserBaseAdapter<T> extends RecyclerView.Adapter<UserBaseAdapter.mViewHolder> {

    private ArrayList<T> listData=new ArrayList<>();
    private LayoutInflater layoutInflater;
    private Context context;
    protected  int mItemLayoutId;

    public UserBaseAdapter() {
    }

    public UserBaseAdapter(Context context, int mItemLayoutId) {
        this.context = context;
        this.mItemLayoutId = mItemLayoutId;
        this.layoutInflater=LayoutInflater.from(context);
    }

    public UserBaseAdapter(ArrayList<T> listData, Context context, int mItemLayoutId) {
        this.listData = listData;
        this.context = context;
        this.mItemLayoutId = mItemLayoutId;
        this.layoutInflater=LayoutInflater.from(context);
    }

    /**
     * 将布局转换为View
     * 传递给自定义的ViewHolder实例
     */

    @Override
    public mViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View view=layoutInflater.inflate(mItemLayoutId,null);
        mViewHolder viewHolder=new mViewHolder(view);
        return viewHolder;
    }

    /**
     *
     * @param mViewHolder mViewHolder
     * @param i 位置
     *     对recycleView子项进行赋值，每个子项滚动到屏幕内时执行
     */

    @Override
    public void onBindViewHolder(UserBaseAdapter.mViewHolder mViewHolder, final int i) {
        if (onItemClickListener!=null){
            mViewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemClickListener.onItemClick(i);
                }
            });
        }
    }

//    获取item的数量
    @Override
    public int getItemCount() {
        return listData.size();
    }

//    view为recycler子项最外层布局（user_topics_item.xml）
    public class mViewHolder extends RecyclerView.ViewHolder{
        public mViewHolder(View view){
            super(view);
        }
        public <V extends View>V view(int id){
            return itemView.findViewById(id);
        }
    }

//    点击事件
    public interface OnItemClickListener{
        public void onItemClick(int position);
    }

    OnItemClickListener onItemClickListener;

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

//    更新数据，并且清除之前的数据
    public void updateData(ArrayList<T> list){
        Log.e("更新数据:","-----------------------");
        if (null==list)
            return;
        this.listData.clear();
        this.listData=list;
        notifyDataSetChanged();
        Log.e("更新视图完成:","-----------------------");
    }

    public void addItem(T data){
        listData.add(data);
    }

    public void addData(ArrayList<T> list){
        if (null==list)
            return;
        listData.addAll(list);
    }


}
