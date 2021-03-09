package com.example.myapplication;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class MyAdapter extends BaseAdapter {

    private ArrayList<newappdata> appDatas;
    private Context mContext;

    MyAdapter(ArrayList<newappdata> appDatas,Context mContext){
        this.appDatas=appDatas;
        this.mContext=mContext;
    }
    @Override
    public int getCount() {
        return appDatas.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View newView;
        if(convertView==null){
            newView= LayoutInflater.from(mContext).inflate(R.layout.item,parent,false);
        }
        else
            newView=convertView;

        ImageView appicon=(ImageView)newView.findViewById(R.id.AppIcon);
        TextView appname=newView.findViewById(R.id.appname);
        TextView firsttime=newView.findViewById(R.id.firstRTText);
        TextView lasttime=newView.findViewById(R.id.lastRTText);
        TextView totaltime=newView.findViewById(R.id.totalRTText);
        appicon.setImageDrawable(appDatas.get(position).getAppicon());
        appname.setText(""+appDatas.get(position).getName());
        firsttime.setText(""+appDatas.get(position).getFistRunTime());
        lasttime.setText(""+appDatas.get(position).getLastRunTime());
        totaltime.setText(""+appDatas.get(position).getTotalRunTime());
        return newView;
    }
}
