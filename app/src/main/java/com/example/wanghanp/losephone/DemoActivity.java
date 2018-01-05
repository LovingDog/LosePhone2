package com.example.wanghanp.losephone;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.widget.ImageView;

import com.example.wanghanp.base.bean.TakePhotoBean;
import com.zhy.adapter.recyclerview.CommonAdapter;
import com.zhy.adapter.recyclerview.base.ViewHolder;

import java.util.ArrayList;
import java.util.List;

import butterknife.ButterKnife;
import butterknife.InjectView;

/**
 * Created by wanghanping on 2018/1/5.
 */

public class DemoActivity extends Activity {

    @InjectView(R.id.recyclerview1)
    RecyclerView mRecyclerView;
    private CommonAdapter<TakePhotoBean> mCommonAdapter;
    private List<TakePhotoBean> mPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_demo);
        ButterKnife.inject(this);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));//设置为listview的布局
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());//设置动画
        mRecyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));//添加分割线
        mPath = new ArrayList<>();
        mPath.add(new TakePhotoBean());
        mPath.add(new TakePhotoBean());
        mPath.add(new TakePhotoBean());
        initTakePhotosAdpater(mPath);
    }

    private void initTakePhotosAdpater(List<TakePhotoBean> path) {
        Log.d("wanghp0077", "initTakePhotosAdpater: "+path.size());
        mCommonAdapter = new CommonAdapter<TakePhotoBean>(this,R.layout.list_item_takephotos,path) {
            @Override
            protected void convert(ViewHolder holder, TakePhotoBean takePhotoBean, int position) {
                Log.d("wanghp0077", "convert: path== " +takePhotoBean.getPath());
                ImageView img = holder.getView(R.id.iv_takephotos);
                img.setBackgroundResource(R.mipmap.ic_launcher);
            }
        };
        mRecyclerView.setAdapter(mCommonAdapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }
}
