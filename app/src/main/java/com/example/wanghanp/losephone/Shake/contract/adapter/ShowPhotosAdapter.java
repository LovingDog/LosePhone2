package com.example.wanghanp.losephone.Shake.contract.adapter;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.wanghanp.base.bean.TakePhotoBean;
import com.example.wanghanp.losephone.MainActivity;
import com.example.wanghanp.losephone.R;
import com.example.wanghanp.myview.ShowPhotosActivity;
import com.example.wanghanp.myview.ZoomImageView;
import com.example.wanghanp.util.MobileInfo;

import java.util.ArrayList;

/**
 * Created by wanghanping on 2018/1/8.
 */

public class ShowPhotosAdapter extends PagerAdapter {

    private ArrayList<String> mList;
    private Context mContext;
    private int mScreenWidth;
    private int mScreenheight;
    private MobileInfo mMobileInfo;
    public ShowPhotosAdapter(ArrayList<String> list,Context context){
        this.mContext = context;
        this.mList = list;
        mMobileInfo = new MobileInfo((ShowPhotosActivity) mContext);
    }

    public void setData(ArrayList<String> list) {
        this.mList = list;
    }

    @Override
    public int getCount() {
        return this.mList.size();
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {
        return view == object;
    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.list_item_takephotos,null);
        ZoomImageView zoomImageView = view.findViewById(R.id.iv_takephotos);
        zoomImageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        String path = mList.get(position);
        int targetWidth = mMobileInfo.getScreenWidth();
        int targetHeight = mMobileInfo.getScreenHeight();
        Glide.with(mContext).load(path)
                .override(targetWidth,targetHeight)
                .into(zoomImageView);
        container.addView(view);
        return view;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        container.removeView((View)object);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
    }
}
