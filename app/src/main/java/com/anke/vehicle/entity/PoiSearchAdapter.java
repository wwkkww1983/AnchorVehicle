package com.anke.vehicle.entity;
/**
 * 功能：创建自定义的适配器
 */

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.anke.vehicle.R;

import java.util.List;

public class PoiSearchAdapter extends ArrayAdapter<PoiSearchList> {
    private int resourceId;

    public PoiSearchAdapter(Context context, int resource, List<PoiSearchList> objects) {
        super(context, resource, objects);
        resourceId = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        PoiSearchList poiSearchList = getItem(position);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.firstSearchLocationResult
                    = (TextView) view.findViewById(R.id.first_search_location_result);
            viewHolder.secondSearchLocationResult
                    = (TextView) view.findViewById(R.id.second_search_location_result);
            view.setTag(viewHolder);
        } else {
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.firstSearchLocationResult
                .setText(poiSearchList.getFirstSearchLocationResult());
        viewHolder.secondSearchLocationResult
                .setText(poiSearchList.getSecondSearchLocationResult());
        return view;
    }

    class ViewHolder {
        TextView firstSearchLocationResult;
        TextView secondSearchLocationResult;
    }
}
