package com.anke.vehicle.activity;
/**
 * 功能：搜索位置信息
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.anke.vehicle.R;
import com.anke.vehicle.entity.PoiSearchAdapter;
import com.anke.vehicle.entity.PoiSearchList;
import com.anke.vehicle.utils.Utility;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.search.core.CityInfo;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;

import java.util.ArrayList;
import java.util.List;

public class BaiduMapPoiSearch extends Activity implements OnGetPoiSearchResultListener {
    private PoiSearch mPoiSearch = null;
    private EditText etbaidumapsearch;
    private Button btsearchcancel;
    private String city = "";
    private String initAddress = "";
    private PoiSearchAdapter sugAdapter = null;
    private List<PoiSearchList> poiSearchListList = new ArrayList<PoiSearchList>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SDKInitializer.initialize(getApplicationContext());
        setContentView(R.layout.baidumappoisearch);
        Intent intent = getIntent();
        city = intent.getExtras().getString("city"); // 搜索城市
        etbaidumapsearch = (EditText) findViewById(R.id.etbaidumapsearch);
        btsearchcancel = (Button) findViewById(R.id.btsearchcancel);
        mPoiSearch = PoiSearch.newInstance();//POI关键字搜索
        mPoiSearch.setOnGetPoiSearchResultListener(BaiduMapPoiSearch.this);
        sugAdapter = new PoiSearchAdapter(BaiduMapPoiSearch.this,
                R.layout.poisearchlist_item, poiSearchListList);

        ListView listViewSearchResult = (ListView) findViewById(R.id.listView_search_result);
        listViewSearchResult.setAdapter(sugAdapter);
        listViewSearchResult.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String str = sugAdapter.getItem(position).getFirstSearchLocationResult();
                Intent intent = new Intent();
                intent.putExtra("searchAddress", str);
                intent.putExtra("flag", false);
                setResult(1, intent);
                BaiduMapPoiSearch.this.finish();
            }
        });

        /**
         * 取消关键字搜索
         */
        btsearchcancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                etbaidumapsearch.setText("");
            }
        });

        /**
         * 当输入关键字变化时，动态更新建议列表
         */
        etbaidumapsearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable arg0) {

            }

            @Override
            public void beforeTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {

            }

            @Override
            public void onTextChanged(CharSequence cs, int arg1, int arg2, int arg3) {
                if (cs.length() <= 0) {
                    return;
                }
                mPoiSearch.searchInCity(new PoiCitySearchOption()
                        .keyword(cs.toString()).city(city));
            }
        });
    }

    @Override
    public void onGetPoiDetailResult(PoiDetailResult result) {
        // TODO Auto-generated method stub
        if (result.error != SearchResult.ERRORNO.NO_ERROR) {
//            Toast.makeText(BaiduMapPoiSearch.this, "抱歉，未找到结果", Toast.LENGTH_SHORT)
//                    .show();
            Utility.ToastShow(BaiduMapPoiSearch.this, "抱歉，未找到结果");
        }
    }

    @Override
    public void onGetPoiResult(PoiResult result) {
        // TODO Auto-generated method stub
        if (result == null || result.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
            return;
        }

        if (result.error == SearchResult.ERRORNO.NO_ERROR) {
            sugAdapter.clear();
            for (PoiInfo info : result.getAllPoi()) {
                PoiSearchList poiSearchListResult = new PoiSearchList(info.name, info.address);
                poiSearchListList.add(poiSearchListResult);
            }
            sugAdapter.notifyDataSetChanged();
            return;
        }

        if (result.error == SearchResult.ERRORNO.AMBIGUOUS_KEYWORD) {
            // 当输入关键字在本市没有找到，但在其他城市找到时，返回包含该关键字信息的城市列表
            String strInfo = "在";
            for (CityInfo cityInfo : result.getSuggestCityList()) {
                strInfo += cityInfo.city;
                strInfo += ",";
            }
            strInfo += "找到结果";
//            Toast.makeText(BaiduMapPoiSearch.this, strInfo, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPoiSearch.destroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

}
