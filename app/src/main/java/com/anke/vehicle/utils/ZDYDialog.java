package com.anke.vehicle.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.anke.vehicle.R;

/**
 * 创建作者： 张蔡奇
 * 创建时间： 2016/10/14
 * 创建公司： 珠海市安克电子技术有限公司合肥分公司
 * isShow 是否显示输入密码editText
 */
public class ZDYDialog {
    public ZDYDialog(Context context, String title, Boolean isShow) {
        AlertDialog.Builder bd = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT);
        final AlertDialog alertDialog = bd.create();
        View v1 = View.inflate(context, R.layout.custom_input, null);
        TextView tvTitle = (TextView) v1.findViewById(R.id.tv_tilte);
        tvTitle.setText(title);
        final EditText editText = (EditText) v1.findViewById(R.id.et_input);
        LinearLayout llDiallog = (LinearLayout) v1.findViewById(R.id.ll_dialog);
        Button btnOk = (Button) v1.findViewById(R.id.btn_ok);
        Button btnCancel = (Button) v1.findViewById(R.id.btn_cancel);
        if (isShow) {
            llDiallog.setVisibility(View.VISIBLE);

            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String inpust = editText.getText().toString().trim();
                    if (zdYinterface != null) {
                        zdYinterface.btnOkonClick(1, inpust);//数字1 输入密码 inpust密码的值
                        alertDialog.dismiss();
                    }

                }
            });

        } else {
            llDiallog.setVisibility(View.GONE);
            btnOk.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (zdYinterface != null) {
                        zdYinterface.btnOkonClick(2, "");
                        alertDialog.dismiss();
                    }
                }
            });
        }


        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alertDialog.dismiss();
            }
        });
        alertDialog.setView(v1, 0, 0, 0, 0);
        alertDialog.show();

    }

    public interface ZDYinterface {
        void btnOkonClick(int type, String inpust);
    }

    public void setZdYinterface(ZDYinterface zdYinterface) {
        this.zdYinterface = zdYinterface;
    }

    private ZDYinterface zdYinterface;

}
