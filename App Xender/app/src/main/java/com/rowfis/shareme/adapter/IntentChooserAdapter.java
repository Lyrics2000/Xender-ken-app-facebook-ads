package com.rowfis.shareme.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.rowfis.shareme.R;
import com.rowfis.shareme.model.AppInfo;

import java.util.ArrayList;


/**
 * Created by Dan Chumo on 12/3/2017.
 */

public class IntentChooserAdapter extends BaseAdapter {
    private ArrayList<AppInfo> appInfo;
    Context context;
    LayoutInflater inflater;

    public IntentChooserAdapter( Context context,ArrayList<AppInfo> appInfo) {
        this.appInfo = appInfo;
        this.context = context;
        this.inflater = LayoutInflater.from(context);

    }


    @Override
    public int getCount() {
        return appInfo.size();
    }

    @Override
    public Object getItem(int position) {
        return appInfo.get(position);

    }

    @Override
    public long getItemId(int position) {
        return (long) position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = this.inflater.inflate(R.layout.inent_chooser_lisstview_item, null, false);
        }
        TextView title = (TextView) convertView.findViewById(R.id.title);
        ImageView icon = (ImageView) convertView.findViewById(R.id.icon);
        AppInfo appInfo = (AppInfo) getItem(position);
        title.setText(appInfo.getName());
        icon.setImageDrawable(appInfo.getDrawable());
        return convertView;

    }

    public void remove(int pos) {
        this.appInfo.remove(pos);
        notifyDataSetChanged();
    }


}
