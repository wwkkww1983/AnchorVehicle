package com.anke.vehicle.activity;

import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.anke.vehicle.R;
import com.anke.vehicle.status.KnowledgeList;
import com.anke.vehicle.database.DBHelper;
import com.anke.vehicle.entity.KnowLedgeInfo;

import java.util.List;

/**
 * 知识库
 */
public class KnowledgeActivity extends Activity {
    private DBHelper dbHelper;
    private Button btdupin;
    private Button bthuaxue;
    private Button btjzcs;
    private TextView tvkl;
    private EditText etsearch;
    private ImageView imgknowdelete;
    private int type = KnowledgeList.DRUG_KNOWLEDGE;
    private List<KnowLedgeInfo> kinfos;
    private PopupWindow popView;
    private GJAdapter cja;
    private ListView listView;
    private Button hisback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_knowledge);
        initUI();//初始化UI控件
        dbHelper = new DBHelper(this);
        cja = new GJAdapter();
        etsearch.setOnKeyListener(onKey);
        etsearch.addTextChangedListener(new searchWatch());
        btnAllPressed();//处理点击事件
        imgknowdelete.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // TODO Auto-generated method stub
                etsearch.setText("");
            }
        });
    }

    /**
     * 处理点击事件
     */
    private void btnAllPressed() {
        btdupin.setOnClickListener(listener);
        bthuaxue.setOnClickListener(listener);
        btjzcs.setOnClickListener(listener);
        hisback.setOnClickListener(listener);
    }

    /**
     * 搜索框文字改变时显示数据
     */
    class searchWatch implements TextWatcher {


        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                                  int count) {
            // TODO Auto-generated method stub
            if (count > 0) {
                if (popView != null) {
                    popView.dismiss();
                    popView = null;
                }
                ShowHospital();
            }
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                                      int after) {
            // TODO Auto-generated method stub

        }

        @Override
        public void afterTextChanged(Editable s) {
            // TODO Auto-generated method stub

        }
    }


    /**
     * 初始化UI控件
     */
    private void initUI() {
        btdupin = (Button) findViewById(R.id.btdupin);
        bthuaxue = (Button) findViewById(R.id.bthuaxue);
        btjzcs = (Button) findViewById(R.id.btjzcs);//救治措施
        hisback = (Button) findViewById(R.id.hisback);
        tvkl = (TextView) findViewById(R.id.tvkl);
        etsearch = (EditText) findViewById(R.id.etsearch);
        imgknowdelete = (ImageView) findViewById(R.id.imgknowdelete);
    }

    private void ShowHospital() {
        String temp = etsearch.getText().toString();
        switch (type) {
            case KnowledgeList.DRUG_KNOWLEDGE:
                kinfos = dbHelper.getHxp(temp);
                break;
            case KnowledgeList.CHEMISTRY_KNOWLEDGE:
                kinfos = dbHelper.getDanger(temp);
                break;
            case KnowledgeList.FIRST_AID_KNOWLEDGE:
                kinfos = dbHelper.getCure(temp);
                break;
            default:
                kinfos = dbHelper.getCure(temp);
                break;
        }

        if (kinfos != null && !kinfos.isEmpty()) {
            initPopView();
        }
    }

    private void initPopView() {
        LayoutInflater inflater = LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.knowlist, null);
        listView = (ListView) view.findViewById(R.id.lvknow);
        listView.setAdapter(cja);

        popView = new PopupWindow(view, LayoutParams.MATCH_PARENT,
                LayoutParams.MATCH_PARENT);
        popView.setBackgroundDrawable(getResources().getDrawable(
                R.drawable.lhtopbar));
        popView.setFocusable(false);
        popView.setOutsideTouchable(true);
        popView.setAnimationStyle(android.R.style.Animation_Dialog);
        popView.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);
        popView.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        popView.showAsDropDown(etsearch);
    }

    // 处理回车事件
    OnKeyListener onKey = new OnKeyListener() {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {

            // TODO Auto-generated method stub

            if (keyCode == 67 && event.getAction() == KeyEvent.ACTION_UP
                    && etsearch.getText().toString().length() > 0) {
                if (popView != null) {
                    popView.dismiss();
                    popView = null;
                }
                ShowHospital();
            }
            return false;
        }

    };

    private OnClickListener listener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Button bt = (Button) v;
            switch (bt.getId()) {
                case R.id.btdupin:
                    type = KnowledgeList.DRUG_KNOWLEDGE;
                    etsearch.setText("");
                    tvkl.setText("");
                    btdupin.setBackgroundDrawable(getResources().getDrawable(
                            R.drawable.klon));
                    bthuaxue.setBackgroundDrawable(getResources().getDrawable(
                            R.drawable.kloff));
                    btjzcs.setBackgroundDrawable(getResources().getDrawable(
                            R.drawable.kloff));
                    break;
                case R.id.bthuaxue:
                    type = KnowledgeList.CHEMISTRY_KNOWLEDGE;
                    etsearch.setText("");
                    tvkl.setText("");
                    btdupin.setBackgroundDrawable(getResources().getDrawable(
                            R.drawable.kloff));
                    bthuaxue.setBackgroundDrawable(getResources().getDrawable(
                            R.drawable.klon));
                    btjzcs.setBackgroundDrawable(getResources().getDrawable(
                            R.drawable.kloff));
                    break;
                case R.id.btjzcs:
                    type = KnowledgeList.FIRST_AID_KNOWLEDGE;
                    etsearch.setText("");
                    tvkl.setText("");
                    btdupin.setBackgroundDrawable(getResources().getDrawable(
                            R.drawable.kloff));
                    bthuaxue.setBackgroundDrawable(getResources().getDrawable(
                            R.drawable.kloff));
                    btjzcs.setBackgroundDrawable(getResources().getDrawable(
                            R.drawable.klon));
                    break;
                case R.id.hisback:
                    KnowledgeActivity.this.finish();
                    break;
                default:
                    break;
            }

        }
    };

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_main, menu);
        return true;
    }

    class GJAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return kinfos.size();
        }

        @Override
        public Object getItem(int arg0) {
            // TODO Auto-generated method stub
            return kinfos.get(arg0);
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
            if (convertView == null) { // 这句就是重用的关键
                convertView = LayoutInflater.from(KnowledgeActivity.this).inflate(
                        R.layout.knowitem, null);
            }
            final TextView tvtitle = (TextView) convertView
                    .findViewById(R.id.tvknow);
            final String title = kinfos.get(position).getName();
            tvtitle.setText(title);
            convertView.setTag(position);
            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    String contentString = "";
                    // TODO Auto-generated method stub
                    if (title != null && title != "") {
                        switch (type) {
                            case KnowledgeList.DRUG_KNOWLEDGE:
                                contentString = dbHelper.getHxpString(title);
                                break;
                            case KnowledgeList.CHEMISTRY_KNOWLEDGE:
                                contentString = dbHelper.getDangerString(title);
                                break;
                            case KnowledgeList.FIRST_AID_KNOWLEDGE:
                                contentString = dbHelper.getCureString(title);
                                break;
                            default:
                                contentString = dbHelper.getCureString(title);
                                break;
                        }
                        tvkl.setText(contentString);
                        etsearch.setText(title);
                        etsearch.setSelection(title.length());
                        if (popView != null)
                            popView.dismiss();
                    }
                }
            });

            return convertView;
        }
    }

}
