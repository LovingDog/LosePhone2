package com.example.wanghanp.myview;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wanghanp.losephone.R;
import com.example.wanghanp.losephone.Shake.contract.adapter.ShowPhotosAdapter;

import java.util.ArrayList;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by wanghanping on 2018/1/8.
 */

public class ShowPhotosActivity extends AppCompatActivity {

    public static final String LIST_EXTRA = "list";
    Toolbar mToolbar;
    @InjectView(R.id.bar_back)
    ImageView mBack;
    @InjectView(R.id.bar_title)
    TextView mTitle;
    @InjectView(R.id.bar_lay_complete)
    LinearLayout mCompletelay;
    @InjectView(R.id.bar_complete)
    TextView mCompleteText;
    @InjectView(R.id.container)
    ViewPager mViewPager;

    private ArrayList<String> mPaths;
    private ShowPhotosAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_photos);
        ButterKnife.inject(this);
        mPaths = getIntent().getStringArrayListExtra(LIST_EXTRA);
        initData();
        initAdapter();
    }

    private void initData() {
        mTitle.setText(getResources().getString(R.string.current_page,mViewPager.getCurrentItem()+1,mPaths.size()));
    }

    public void initAdapter() {
        mAdapter = new ShowPhotosAdapter(mPaths,this);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                initData();
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    @OnClick({R.id.bar_back,R.id.bar_lay_complete})
    public void OnClick(View view){
        switch (view.getId()) {
            case R.id.bar_back:
                finish();
                break;
            case R.id.bar_lay_complete:
                finish();
                break;
        }
    }

}
