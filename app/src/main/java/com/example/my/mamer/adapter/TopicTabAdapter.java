package com.example.my.mamer.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.view.ViewGroup;

import java.util.List;

public class TopicTabAdapter extends FragmentPagerAdapter {
//    fragment列名
    private List<Fragment> listFragment;
//    tab名的列表
    private List<String> listTitle;
    public TopicTabAdapter(FragmentManager fm,List<Fragment> listFragment,List<String> listTitle) {
        super(fm);
        this.listFragment=listFragment;
        this.listTitle=listTitle;
    }

    @Override
    public Fragment getItem(int i) {
        return listFragment.get(i);
    }

    @Override
    public int getCount() {
        return listTitle.size();
    }
    @Override
    public CharSequence getPageTitle(int position){
        return listTitle.get(position%listTitle.size());
    }
//    给定的位置移除相应的view
    @Override
    public void destroyItem(ViewGroup container, int position,Object object) {
        super.destroyItem(container, position, object);
    }
}
