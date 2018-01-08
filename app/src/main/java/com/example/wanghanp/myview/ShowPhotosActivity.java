package com.example.wanghanp.myview;

import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.wanghanp.losephone.R;

import butterknife.ButterKnife;
import butterknife.InjectView;
import butterknife.OnClick;

/**
 * Created by wanghanping on 2018/1/8.
 */

public class ShowPhotosActivity extends AppCompatActivity {

    Toolbar mToolbar;
    @InjectView(R.id.bar_back)
    ImageView mBack;
    @InjectView(R.id.bar_title)
    TextView mTitle;
    @InjectView(R.id.bar_lay_complete)
    LinearLayout mCompletelay;
    @InjectView(R.id.bar_complete)
    TextView mCompleteText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_photos);
        ButterKnife.inject(this);
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
