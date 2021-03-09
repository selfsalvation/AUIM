package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.usage.UsageStatsManager;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.ValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class MainActivity_fliter extends AppCompatActivity {
    SQLiteDatabase db;
    appdatasSQL soh;
    ArrayList<newappdata> appDatas;
    UsageStatsManager usm;
    private PackageManager packageManager;
    BarChart bar;
    PieChart pie;
    List<BarEntry> barlist;
    List<PieEntry> pielist;
    ImageView moststartIV,longestuseIV;
    TextView moststartTV,longestuseTV;
    private void init(){
        usm = (UsageStatsManager)this.getSystemService(Context.USAGE_STATS_SERVICE);
        barlist=new ArrayList<>();
        pielist=new ArrayList<>();
        appDatas=new ArrayList<>();
        packageManager=getPackageManager();
        setContentView(R.layout.activity_main_fliter);
        soh=new appdatasSQL(this);
        getappDatas();
        bar=findViewById(R.id.bar);
        pie=findViewById(R.id.pie);
        moststartIV=findViewById(R.id.imageView_moststart);
        longestuseIV=findViewById(R.id.imageView_longestuse);
        moststartTV=findViewById(R.id.textView4);
        longestuseTV=findViewById(R.id.textView5);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_fliter);
        init();
        setMostStart();
        setLongestUse();
        setBar();
        setPie();
        //System.out.println("appDatas"+appDatas.size());
    }

    private void setPie() {
        ArrayList<Integer> colorlist=new ArrayList<>();

        colorlist.add(0xFF00BFFF);//设置饼状图颜色
        colorlist.add(0xccFF4500);
        colorlist.add(0xaa00FFFF);
        colorlist.add(0x880000FF);
        colorlist.add(0xaaADFF2F);
        colorlist.add(0xccB0E0E6);
        colorlist.add(0xddDAA520);
        colorlist.add(0x77000000);
        colorlist.add(0xcc4B0082);
        colorlist.add(0xdd006400);
        colorlist.add(0xbb9400D3);
        colorlist.add(0xccB22222);
        int i=0;
        long other=0;
        List<Integer> colors = new ArrayList<>();
        for(newappdata app:appDatas) {
            if(i<=6) {
                pielist.add(new PieEntry(app.getTrt() / 1000, app.getName()));//读取数据库的数据，这是图表的数据源
                colors.add(colorlist.get(i));
                ++i;
            }
            else{
                other+=app.getTrt()/1000;
            }
        }
        if(i>6) {
            pielist.add(new PieEntry(other, "其他应用"));
            colors.add(colorlist.get(i));
        }
        pie.setCenterText("app运行时间");//设置饼状图其他信息
        pie.setCenterTextSize(20f);
        pie.setCenterTextColor(0xffffffff);
        pie.setTransparentCircleColor(0x00000000);
        pie.setHoleColor(0x88000000);
        PieDataSet dataSet = new PieDataSet(pielist,"");
        dataSet.setColors(colors);
        dataSet.setValueTextColor(0xffffffff);
        dataSet.setYValuePosition(PieDataSet.ValuePosition.OUTSIDE_SLICE);
        dataSet.setValueTextSize(14f);
        PieData pieData = new PieData(dataSet);
        pie.getDescription().setEnabled(false);
        pieData.setDrawValues(true);
        pie.animateXY(3000,3000);
        pie.setData(pieData);

    }

    private void setMostStart(){
        int m=0;
        for(int i=1;i<appDatas.size();++i){
            if(appDatas.get(i).getStarttimes()>appDatas.get(m).getStarttimes()){
                m=i;
            }
        }
        moststartIV.setImageDrawable(appDatas.get(m).getAppicon());
        moststartTV.setText(""+appDatas.get(m).getName());
    }

    private void setLongestUse() {
        longestuseIV.setImageDrawable(appDatas.get(0).getAppicon());
        longestuseTV.setText(""+appDatas.get(0).getName());
    }

    private void setBar(){
        int i=0;
        long otherapp=0;
        for(newappdata app:appDatas){//设置图表数据
            if(i<=8) {
                barlist.add(new BarEntry(i, appDatas.get(i).getStarttimes()));
                ++i;
            }
            else{
                otherapp+=appDatas.get(i).getStarttimes();
            }
        }
        if(otherapp!=0)
            barlist.add(new BarEntry(i, otherapp));

        BarDataSet barDataSet=new BarDataSet(barlist,"app启动次数");//设置图标其他数据
        BarData barData=new BarData(barDataSet);
        barData.setValueTextColor(0xffffffff);
        bar.getXAxis().setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                if(Math.abs(value-9)<=1e-6)
                    return "其他应用";
                for(int i=0;i<=8;++i){
                    if(Math.abs(value-i)<=1e-6)
                        return appDatas.get(i).getName();
                }
                return "";
            }
        });
        bar.setData(barData);
        bar.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        bar.getXAxis().setTextSize(10f);

        bar.getXAxis().setTextColor(0xFFFFFFFF);
        bar.getXAxis().setDrawGridLines(false);
        bar.getLegend().setTextSize(15f);
        bar.getLegend().setTextColor(0xffffffff);
        bar.getDescription().setEnabled(false);//隐藏右下角英文
        bar.setExtraBottomOffset(10f);
        bar.animateY(3000);
        bar.getAxisRight().setEnabled(false);
        bar.getAxisLeft().setTextColor(0xffffffff);
    }

    private void getappDatas() {
        db=soh.getReadableDatabase();
        Cursor cursor =db.query("appdatas",null,null,null,null,null,"ttime DESC",null);
        while(cursor.moveToNext()){
            String name=cursor.getString(1);
            long ftime=cursor.getLong(cursor.getColumnIndex("ftime"));
            long ltime=cursor.getLong(3);
            long ttime=cursor.getLong(4);
            int stattimes=cursor.getInt(5);
            newappdata newapp=new newappdata();
            newapp.setFistRunTime(ftime);
            newapp.setLastRunTime(ltime);
            newapp.setStarttimes(stattimes);
            newapp.setTotalRunTime(ttime);
            PackageInfo pkginfo= null;
            try {
                pkginfo = packageManager.getPackageInfo(name,0);
            } catch (Exception e) {
                e.printStackTrace();
                continue;
            }
            ApplicationInfo appinfo= null;
            try {
                appinfo = packageManager.getApplicationInfo(name,0);
            } catch (PackageManager.NameNotFoundException e) {
                e.printStackTrace();
                continue;
            }
            String appname=pkginfo.applicationInfo.loadLabel(packageManager).toString();
            if("系统桌面".equals(appname))
                continue;
            Drawable d = packageManager.getApplicationIcon(appinfo);
            newapp.setName(appname);
            newapp.setAppicon(d);
            appDatas.add(newapp);
        }
        cursor.close();
        db.close();
    }

    public void bt_getback_onclick(View view) {
        finish();
    }
}