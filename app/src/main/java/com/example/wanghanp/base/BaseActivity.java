package com.example.wanghanp.base;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;

import com.example.wanghanp.losephone.R;
import com.example.wanghanp.util.StringUtils;
import com.example.wanghanp.util.ToastUtils;

import java.util.ArrayList;

//import com.ws.wsplus.utils.ZTLUtils;


/**
 * Created by Administrator on 2016/7/22.
 */
public class BaseActivity extends Activity {
    public Activity mActivity;
    private boolean[] mChoiceItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mActivity = this;
//        new ZTLUtils(mActivity).setTranslucentStatus();
        initViews();
    }


    public void initViews() {

    }


    //编辑标签
    public void showTagDialog(final OnEditorLisntener onEditorLisntener) {
        final AlertDialog.Builder builder=new AlertDialog.Builder(this);
//        AlertDialog dialog=builder.create();
        View edit= View.inflate(this, R.layout.dialog_tag,null);
        final EditText tag= (EditText) edit.findViewById(R.id.et_tag);
      /*  dialog.setTitle();
        dialog.setView(edit);*/
        builder.setTitle("闹钟标签");
        builder.setIcon(getResources().getDrawable(R.mipmap.person));
        builder.setView(edit);
        builder.setPositiveButton("完成", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String desc=tag.getText().toString();
                if (StringUtils.isReal(desc)) {
                    onEditorLisntener.negativeCall(desc);
                    dialog.dismiss();

                } else {
                    ToastUtils.showShortToast("不能为空",BaseActivity.this);
                }
            }
        });
        builder.setNegativeButton("取消",new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        builder.show();
    }

    //选择赖床级数
    public void showLazyDialog(final boolean[] item, final OnEditorLisntener onEditorLisntener) {
        mChoiceItem = item;
        final AlertDialog.Builder dialog=new AlertDialog.Builder(this);
        dialog.setTitle("要提醒");
        String[] str =new String[]{"周一","周二"
                ,"周三","周四","周五","周六","周日"};
        final ArrayList<String> chooseLists = new ArrayList<>();
        dialog.setMultiChoiceItems(str,item, new DialogInterface.OnMultiChoiceClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                int temp = which+1;
                if (!chooseLists.contains(temp)) {
                    if (isChecked) {
                        chooseLists.add(String.valueOf(temp));
                    }
                } else {
                    if (!isChecked) {
                        chooseLists.remove(String.valueOf(temp));
                    }
                }
                Log.d("wanghp","which = "+temp);
            }
        });
        dialog.setPositiveButton("确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                int[] list = new int[chooseLists.size()];
                for (int j = 0; j < chooseLists.size(); j++) {
                    String item = chooseLists.get(j);
                    int a = Integer.parseInt(item);
                    list[j] = a;
                }
                onEditorLisntener.negativeCall(list);
            }
        });
        dialog.show();
    }

    public interface OnEditorLisntener{
        void positionCall();
        void negativeCall(String value);
        void negativeCall(int[] value);
    }
}
