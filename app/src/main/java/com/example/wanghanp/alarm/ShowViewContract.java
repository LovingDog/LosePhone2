package com.example.wanghanp.alarm;

import android.widget.TextView;

public class ShowViewContract {

    public interface TopView{
        TextView getTimeTextView();
        TextView getDateTextView();
        TextView getSecondTextView();
    }
}
