package com.rowfis.shareme.adapter;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.rowfis.shareme.R;
import com.rowfis.shareme.model.AppInfo;
import com.rowfis.shareme.util.Utility;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by Dan Chumo on 11/28/2017.
 */

public class GridAppsAdapter extends BaseAdapter {

    ArrayList<AppInfo> apps;
    Context context;
    private int currentTheme;
    private final DecimalFormat df;
    ArrayList origin_apps = new ArrayList();

    public GridAppsAdapter(ArrayList<AppInfo> apps, Context context) {
        this.apps = apps;
        this.origin_apps.addAll(apps);
        this.context = context;
        this.df = new DecimalFormat("0.0");
        float photo_padding = TypedValue.applyDimension(1, 30.0f, context.getResources().getDisplayMetrics());
    }

    @Override
    public int getCount() {
        return apps.size();
    }

    @Override
    public Object getItem(int position) {
        return apps.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {

            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.unit_app, parent, false);
//            convertView = ((LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.apps_grid_item, parent, false);
        }

        ImageView appIcon = convertView.findViewById(R.id.app_image);
        TextView appName = convertView.findViewById(R.id.app_name);
        TextView appSize = convertView.findViewById(R.id.app_size);
        CardView ll_item = convertView.findViewById(R.id.unit_app_type);
        ImageView ll_check_image = convertView.findViewById(R.id.imgCheck);

/*
LinearLayout ll_active = (LinearLayout) convertView.findViewById(R.id.ll_active);
//
ViewGroup.LayoutParams params = ll_active.getLayoutParams();
params.width = ll_item.getWidth();
params.height = ll_item.getHeight();
ll_active.setLayoutParams(params);
ll_active.getBackground().setAlpha(40);
*/

        AppInfo appItem = (AppInfo) getItem(position);
        appIcon.setImageDrawable(appItem.getDrawable());
        appName.setText(appItem.getName());
        // to do
        // add utility stuff here
        appSize.setText(Utility.getReadableFileSize(appItem.getAppSizeValue()).replace(" ", ""));


        if (appItem.isChecked()) {
            ll_item.setBackgroundColor(Color.parseColor("#e8eaf6"));
            ll_check_image.setVisibility(View.VISIBLE);


        } else {

            ll_item.setBackgroundColor(Color.parseColor("#FAFAFA"));
            ll_check_image.setVisibility(View.GONE);
        }


        return convertView;
    }

    public void deleteApp(String pkg) {
        Iterator iterator = this.apps.iterator();
        while (iterator.hasNext()) {
            if (((AppInfo) iterator.next()).getPackageName().equals(pkg)) {
                iterator.remove();
                notifyDataSetChanged();
                return;
            }
        }


    }

    public ArrayList<AppInfo> getList() {
        return apps;

    }

    public void setList(ArrayList<AppInfo> appInfos) {

        apps.clear();
        origin_apps.clear();
        apps.addAll(appInfos);
        origin_apps.addAll(appInfos);
        notifyDataSetChanged();
    }

    public void resetItems() {
        Iterator iterator = origin_apps.iterator();
        while (iterator.hasNext()) {
            ((AppInfo) iterator.next()).setChecked(false);
        }
        notifyDataSetChanged();
    }

    public void search(String filter) {
        if (origin_apps.size() > 0) {
            apps.clear();
            Iterator iterator = origin_apps.iterator();
            while (iterator.hasNext()) {
                AppInfo appInfo = (AppInfo) iterator.next();
                if (appInfo.getName().toLowerCase().contains(filter.toLowerCase())) {
                    apps.add(appInfo);
                }
            }
        }
        notifyDataSetChanged();
    }

    public ArrayList<AppInfo> getOriginalList() {

        return origin_apps;
    }

    public void filterList(Activity activity) {

        Iterator iterator = origin_apps.iterator();
        while (iterator.hasNext()) {
            AppInfo appInfo = (AppInfo) iterator.next();


        }
    }
}
