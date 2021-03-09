package com.example.myapplication;

import android.app.Service;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStatsManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.IBinder;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {
    mySQLiteOpenHelper msql;
    SQLiteDatabase db;
    PackageManager packageManager;
    UsageStatsManager usm;
    long endCal ;
    long  beginCal;
    Calendar beginCAl;
    int timerangeid;
    ArrayList<newappdata> appDatas;
    HashMap<String,Long> ftimemap;
    HashMap<String,Long> ltimemap;
    HashMap<String,Long> ttimemap;
    HashMap<String,Long> lasttimemap;
    HashSet<String> pkgnames;
    HashMap<String,Integer> time;
    UsageEvents ues;
    mySQLiteOpenHelper mtsqlitehelper;
    public MyService() {
    }

    private Timer timer;

    private void init() {
        mtsqlitehelper=new mySQLiteOpenHelper(this);
        appDatas=new ArrayList<>();
        usm= (UsageStatsManager) this.getSystemService(Context.USAGE_STATS_SERVICE);
        packageManager=getPackageManager();
        ftimemap=new HashMap<>();
        ltimemap=new HashMap<>();
        ttimemap=new HashMap<>();
        lasttimemap=new HashMap<>();
        pkgnames=new HashSet<>();
        time=new HashMap<>();
        timerangeid=0;
    }
    @Override
    public IBinder onBind(Intent intent) {
        init();
        if (timer==null) {
            timer=new Timer(true);
            TimerTask tt=new TimerTask() {

                @Override
                public void run() {
                    for(timerangeid=0;timerangeid<=2;++timerangeid) {
                        endCal = System.currentTimeMillis();
                        beginCAl = Calendar.getInstance();
                        setTime();
                        beginCal = beginCAl.getTimeInMillis();
                        getappDatasByQES();
                        db = mtsqlitehelper.getReadableDatabase();
                        saveToSqlite();
                        db.close();
                    }

                }
            };
            timer.schedule(tt, 0, 1000);
        }
        return null;
    }
    @Override
    public boolean onUnbind(Intent intent) {
        timer.cancel();
        return super.onUnbind(intent);
    }

    private void getappDatasByQES(){
        appDatas.clear();
        ftimemap.clear();
        ltimemap.clear();
        ttimemap.clear();
        lasttimemap.clear();
        pkgnames.clear();
        time.clear();
        ues=usm.queryEvents(beginCal,endCal);//usm是uersStatsManager实体

        while(ues.hasNextEvent()){
            UsageEvents.Event temp=new UsageEvents.Event();
            ues.getNextEvent(temp);
            if(temp.getEventType()== UsageEvents.Event.ACTIVITY_RESUMED) {
                pkgnames.add(temp.getPackageName());
                time.put(temp.getPackageName(),time.get(temp.getPackageName())==null?1:time.get(temp.getPackageName())+1);
                if (!ftimemap.containsKey(temp.getPackageName())) {
                    ftimemap.put(temp.getPackageName(), temp.getTimeStamp());
                }
                lasttimemap.put(temp.getPackageName(),temp.getTimeStamp());
            }
            else if(temp.getEventType()== UsageEvents.Event.ACTIVITY_PAUSED){
                pkgnames.add(temp.getPackageName());
                if(ftimemap.get(temp.getPackageName())==null) {
                    ftimemap.put(temp.getPackageName(), beginCal);
                    lasttimemap.put(temp.getPackageName(), beginCal);
                }
                if(ltimemap.get(temp.getPackageName())==null)
                    ttimemap.put(temp.getPackageName(),(long)0);
                ltimemap.put(temp.getPackageName(),temp.getTimeStamp());
                long ttmvalue=ttimemap.get(temp.getPackageName());
                ttimemap.put(temp.getPackageName(),ttmvalue+temp.getTimeStamp()-lasttimemap.get(temp.getPackageName()));
            }
        }

        for(String pkgname:pkgnames){
            newappdata newapp=new newappdata();
            newapp.setName(pkgname);
            //设置首次运行时间
            newapp.setFistRunTime(ftimemap.get(pkgname));

            //设置启动次数
            newapp.setStarttimes(time.get(pkgname)==null?0:time.get(pkgname));

            //最后运行时间
            if(ltimemap.get(pkgname)==null){
                newapp.setLastRunTime(endCal);
                ttimemap.put(pkgname,endCal-lasttimemap.get(pkgname));
            }
            else if(lasttimemap.get(pkgname)>ltimemap.get(pkgname)){
                newapp.setLastRunTime(endCal);
                ttimemap.put(pkgname,ttimemap.get(pkgname)+endCal-lasttimemap.get(pkgname));
            }
            else{
                newapp.setLastRunTime(ltimemap.get(pkgname));
            }

            //总时间
            newapp.setTotalRunTime(ttimemap.get(pkgname));

            appDatas.add(newapp);
        }
    }
    private void saveToSqlite() {
        //根据timerangid选择保存到哪个table
        String tablename = "appdatas" + (timerangeid + 2);
        //删除原来数据
        db.delete(tablename, null, null);
        //插入所有数据
        for (newappdata appdata : appDatas) {
            ContentValues values = new ContentValues();
            values.put("name", appdata.getName());
            //values.put("ftime",e);
            values.put("ftime", appdata.getFrt());
            values.put("ltime", appdata.getLrt());
            values.put("ttime", appdata.getTrt());
            values.put("starttimes", appdata.getStarttimes());
            db.insert(tablename, null, values);
        }
    }
    private void setTime() {
//        switch (timerangeid){
//            case 0:
//                beginCAl.add(Calendar.YEAR,-1);;break;
//            case 1:
//                beginCAl.add(Calendar.MONTH,-1);break;
//            case 2:
//                beginCAl.add(Calendar.DATE,-7);break;
//            case 3:
//                beginCAl.add(Calendar.DAY_OF_YEAR,-1);break;
//            case 4:
//                beginCAl.add(Calendar.HOUR,-1);break;
//            default:break;
//        }
        switch (timerangeid){
            case 0:
                beginCAl.add(Calendar.DATE,-7);break;
            case 1:
                beginCAl.add(Calendar.DAY_OF_YEAR,-1);break;
            case 2:
                beginCAl.add(Calendar.HOUR,-1);break;
            default:break;
        }
    }

}
