package com.anke.vehicle.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

/**
 * 创建作者： 张蔡奇
 * 创建时间： 2016/10/14
 * 创建公司： 珠海市安克电子技术有限公司合肥分公司
 */
public class CustomDialogs {
    String which;
    public AlertDialog alertDialog;

    public CustomDialogs(Context context, final String[] str1, final int type, String title) {
        AlertDialog.Builder creatDialog = new AlertDialog.Builder(context, AlertDialog.THEME_HOLO_LIGHT);
        creatDialog.setTitle(title);
        creatDialog.setSingleChoiceItems(str1, -1,
                new DialogInterface.OnClickListener() // 把循环添加的实体作为选项
                {
                    public void onClick(DialogInterface dialog, int whichcountry) {
                        which = str1[whichcountry]; // 获取实际标识
                    }
                });
        creatDialog.setNegativeButton("取消",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface d, int which) {
                        d.dismiss();
                    }
                });

        creatDialog.setPositiveButton("确定",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialoginterface, int i) {
                        if (dialogsInterface != null) {
                            dialogsInterface.dealDIalogs(type, which);
                        }

                    }
                });
        alertDialog = creatDialog.create();
        alertDialog.show();
    }

    public interface DialogsInterface {
        void dealDIalogs(int type, String which);
    }

    private DialogsInterface dialogsInterface;

    public void setDialogsInterface(DialogsInterface dialogsInterface) {
        this.dialogsInterface = dialogsInterface;
    }
}
