package com.example.wanghanp.losephone.Shake.contract.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.wanghanp.losephone.MainActivity;
import com.example.wanghanp.losephone.R;
import com.example.wanghanp.util.MobileInfo;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.List;

/**
 * Created by wanghanping on 2018/1/4.
 */

public class TakePhotosAdapter extends CommonAdapter {
    private Context mContext;
    private int mLayout;
    private List mData;
    private MobileInfo mMobileInfo;
    private int mSpanCount;

    public TakePhotosAdapter(Context context, int layoutId, List datas,int spancount) {
        super(context, layoutId, datas);
        this.mContext = context;
        this.mLayoutId = layoutId;
        this.mData = datas;
        this.mSpanCount = spancount;
        this.mMobileInfo = MobileInfo.getInstance((MainActivity)this.mContext);
    }

    @Override
    protected void convert(ViewHolder holder, Object o, int position) {
       ImageView img = holder.getConvertView().findViewById(R.id.iv_takephotos);
        Log.d("wanghp007", "convert: path== " +(String)o);
        Glide.with(mContext).load((String)o)
//                .override(mMobileInfo.getScreenWidth() / mSpanCount -(mSpanCount *5),200)
                .into(img);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

    }
}
