package com.anke.vehicle.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anke.vehicle.R;

/**
 * 创建作者： 张蔡奇
 * 创建时间： 2016/11/1
 * 创建公司： 珠海市安克电子技术有限公司合肥分公司
 *
 */
public class SetView extends LinearLayout {
    private ImageView imageView;
    public TextView tvInfor;
    public TextView tvReason;
    private CharSequence mname;
    private int mResrco;

    public SetView(Context context) {
        this(context, null);
    }

    public SetView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SetView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        TypedArray a = context.getTheme().obtainStyledAttributes(attrs, R.styleable.SetView, defStyleAttr, 0);
        mResrco = a.getResourceId(R.styleable.SetView_custsrc,0);
        mname = a.getText(R.styleable.SetView_name);
        a.recycle();
        initUI();
    }

    private void initUI() {
        View rootView = View.inflate(getContext(), R.layout.set_view, this);
        imageView = (ImageView) rootView.findViewById(R.id.imgInfor);
        imageView.setImageResource(mResrco);
        tvInfor = (TextView) rootView.findViewById(R.id.tvInfor);
        tvInfor.setText(mname);
        tvReason = (TextView) rootView.findViewById(R.id.tvReason);


    }


}
