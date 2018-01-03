package com.example.wanghanp.losephone.Shake.contract.adapter;

import android.support.v4.view.PagerAdapter;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by wanghanping on 2018/1/3.
 */

public class BannerAdapter extends PagerAdapter {
    //数据源
    private List<ImageView> mList;

    public BannerAdapter(List<ImageView> list) {
        this.mList = list;
    }

    @Override
    public int getCount() {
        //取超大的数，实现无线循环效果
        return Integer.MAX_VALUE;
    }

    @Override
    public boolean isViewFromObject(View arg0, Object arg1) {
        return arg0 == arg1;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        container.removeView(mList.get(position % mList.size()));
        container.addView(mList.get(position % mList.size()));
        return mList.get(position % mList.size());
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView(mList.get(position % mList.size()));
    }

}
