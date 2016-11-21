package com.anke.vehicle.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import com.anke.vehicle.R;
import com.anke.vehicle.status.FunctionList;
import com.anke.vehicle.database.DBHelper;
import com.anke.vehicle.entity.FunctionInfo;
import com.anke.vehicle.utils.Utility;

import java.util.ArrayList;
import java.util.List;

/**
 * 功能页面
 */
public class FunctionPage extends Activity {

    private DBHelper dbHelper;
    private Button fpback;
    private GridView gvfp;
    private PictureAdapter adapter;
    private List<FunctionInfo> finfos = new ArrayList<FunctionInfo>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.functionpage);
        fpback = (Button) findViewById(R.id.fback);
        dbHelper = new DBHelper(FunctionPage.this);
        gvfp = (GridView) findViewById(R.id.gvfunction);
        addFunction();//添加功能
        fpback.setOnClickListener(listener);
        adapter = new PictureAdapter();
        if (!finfos.isEmpty())
            gvfp.setAdapter(adapter);
    }

    /**
     * 添加功能
     */
    private void addFunction() {
        FunctionInfo fInfo = new FunctionInfo();
        fInfo.setId(FunctionList.EXPERT);
        fInfo.setTitle("专家知识");
        finfos.add(fInfo);
        FunctionInfo fInfo2 = new FunctionInfo();
        fInfo2.setId(FunctionList.PAY);
        fInfo2.setTitle("收费系统");
        finfos.add(fInfo2);
        FunctionInfo fInfo3 = new FunctionInfo();
        fInfo3.setId(FunctionList.KILOMETER);
        fInfo3.setTitle("公里油耗");
        finfos.add(fInfo3);
        FunctionInfo fInfo1 = new FunctionInfo();
        fInfo1.setId(FunctionList.Video);
        fInfo1.setTitle("视频传输");
        finfos.add(fInfo1);
        FunctionInfo fInfo5 = new FunctionInfo();
        fInfo5.setId(FunctionList.HOSPITAL);
        fInfo5.setTitle("告知医院");
        finfos.add(fInfo5);
    }

    private OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Button bt = (Button) v;
            switch (bt.getId()) {
                case R.id.fback:// 返回
                    onBackPressed();
                    break;
                default:
                    break;
            }

        }
    };

    class PictureAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return finfos.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return finfos.get(arg0);
        }

        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }

        @Override
        public View getView(final int position, View convertView,
                            ViewGroup parent) {
            // TODO Auto-generated method stub
            if (convertView == null)
                convertView = LayoutInflater.from(FunctionPage.this).inflate(
                        R.layout.pageitem, null);
            final TextView title = (TextView) convertView
                    .findViewById(R.id.tvfp);
            final ImageView iViews = (ImageView) convertView
                    .findViewById(R.id.ivfp);

            final FunctionInfo cv = finfos.get(position);
            final int idString = cv.getId();
            title.setText(cv.getTitle());
            switch (idString) {
                case FunctionList.EXPERT:
                    iViews.setBackgroundResource(R.drawable.zhuanjia);
                    break;
                case FunctionList.Video:
                    iViews.setBackgroundResource(R.drawable.view);
                    break;
                case FunctionList.PAY:
                    iViews.setBackgroundResource(R.drawable.shoufei);
                    break;
                case FunctionList.KILOMETER:
                    iViews.setBackgroundResource(R.drawable.gongli);
                    break;
                case FunctionList.HOSPITAL:
                    iViews.setBackgroundResource(R.drawable.yiyuan);
                    break;
                default:
                    break;
            }
            convertView.setTag(position);
            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    switch (idString) {
                        case FunctionList.EXPERT:
                            Intent intentkl = new Intent();
                            intentkl.setClass(FunctionPage.this, KnowledgeActivity.class);
                            startActivity(intentkl);
                            break;
                        case FunctionList.Video:
                            //iViews.setBackgroundResource(R.drawable.view);
                            Utility.ToastShow(FunctionPage.this, "视频");
                            break;
                        case FunctionList.PAY:
                            //iViews.setBackgroundResource(R.drawable.shoufei);
                            Utility.ToastShow(FunctionPage.this, "收费");
                            break;
                        case FunctionList.KILOMETER:
                            //iViews.setBackgroundResource(R.drawable.gongli);
                            Utility.ToastShow(FunctionPage.this, "公里");
                            break;
                        case FunctionList.HOSPITAL:
                            //iViews.setBackgroundResource(R.drawable.gongli);
                            Utility.ToastShow(FunctionPage.this, "医院");
                            break;
                        default:
                            break;
                    }
                }
            });
            return convertView;
        }

        public void addItem(FunctionInfo item) {
            finfos.add(item);
        }
    }
}
