package com.example.myapplication;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.usage.UsageEvents;
import android.app.usage.UsageStats;
import android.app.usage.UsageStatsManager;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.provider.Settings;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.time.Year;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    int timerangeid;
    MyAdapter myadapter;
    ListView lv;
    UsageStatsManager usm;
    long endCal ;
    long  beginCal;
    Calendar beginCAl;
    SQLiteDatabase db;
    ArrayList<newappdata> appDatas;
    List<UsageStats> pinfos;
    UsageEvents ues;
    EditText editText;
    PackageManager packageManager;
    HashMap<String,Long> ftimemap;
    HashMap<String,Long> ltimemap;
    HashMap<String,Long> ttimemap;
    HashMap<String,Long> lasttimemap;
    HashSet<String> pkgnames;
    HashMap<String,Integer> time;
    mySQLiteOpenHelper mtsqlitehelper;
    appdatasSQL appdatassql;
    ServiceConnection sc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //getUsageSetting();
        init();
        //getappDatasByQUSS();
        //startActivity(new Intent(Settings.ACTION_USAGE_ACCESS_SETTINGS));

    }

    private void init() {
        setContentView(R.layout.activity_main);
        lv=findViewById(R.id.lv);
        mtsqlitehelper=new mySQLiteOpenHelper(this);
        appdatassql=new appdatasSQL(this);
        editText=findViewById(R.id.EditText_appname);
        appDatas=new ArrayList<>();
        usm= (UsageStatsManager) MainActivity.this.getSystemService(Context.USAGE_STATS_SERVICE);
        packageManager=getPackageManager();
        ftimemap=new HashMap<>();
        ltimemap=new HashMap<>();
        ttimemap=new HashMap<>();
        lasttimemap=new HashMap<>();
        pkgnames=new HashSet<>();
        time=new HashMap<>();
        timerangeid=2;
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

    public void bt_showdata_click(View view) {
        appDatas.clear();
        refreshFromSQLite();

        endCal=System.currentTimeMillis();
        beginCAl=Calendar.getInstance();
        setTime();
        beginCal=beginCAl.getTimeInMillis();
        myadapter=new MyAdapter(appDatas,this);
        lv.setAdapter(myadapter);
        lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                newappdata thisappdata=appDatas.get(position);
                AlertDialog alertdialog=null;
                AlertDialog.Builder builder=new AlertDialog.Builder(MainActivity.this);
                alertdialog=builder.setTitle("应用使用情况详细信息").setPositiveButton("确定",null).setMessage("应用名称: "+thisappdata.getName()+"\n\n"+"首次启动时间: "+appData.translateTime(thisappdata.getFrt())+"\n\n最后启动时间: "
                        +appData.translateTime(thisappdata.getLrt()) +"\n\n总共使用时间: "+thisappdata.getTotalRunTime()+"\n\n启动次数: "+thisappdata.getStarttimes()+"\n\n每小时启动次数： "+String.format("%.2f", thisappdata.getStarttimes()/((endCal-beginCal)/1000.0/60/60))).setCancelable(true)
                        .setIcon(thisappdata.getAppicon()).create();
                alertdialog.show();
            }
        });
    }

    private void refreshFromSQLite() {
        db=mtsqlitehelper.getReadableDatabase();
        Cursor cursor =db.query("appdatas"+(timerangeid+2),null,null,null,null,null,"ttime DESC",null);
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
            if(editText.getText().toString().length()!=0)//如果搜索框不为空，则按名搜索
                if(!editText.getText().toString().equals(appname.toLowerCase()))
                    continue;
            Drawable d = packageManager.getApplicationIcon(appinfo);
            newapp.setName(appname);
            newapp.setAppicon(d);
            appDatas.add(newapp);
        }
        cursor.close();
        db.close();
    }

    public void bt_fresh_onclick(View view) {
        endCal=System.currentTimeMillis();
        beginCAl=Calendar.getInstance();
        setTime();
        beginCal=beginCAl.getTimeInMillis();
        getappDatasByQES();


        saveToSqlite();
        saveappDatas();
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

    private void saveToSqlite(){
        db=mtsqlitehelper.getReadableDatabase();
        //根据timerangid选择保存到哪个table
        String tablename="appdatas"+(timerangeid+2);
        //删除原来数据
        db.delete(tablename,null,null);
        //插入所有数据
        for(newappdata appdata:appDatas) {
            ContentValues values = new ContentValues();
            values.put("name",appdata.getName());
            //values.put("ftime",e);
            values.put("ftime",appdata.getFrt());
            values.put("ltime",appdata.getLrt());
            values.put("ttime",appdata.getTrt());
            values.put("starttimes",appdata.getStarttimes());
            db.insert(tablename,null,values);
        }
        db.close();

    }

    private  void saveappDatas(){
        db=appdatassql.getReadableDatabase();
        String tablename="appdatas";
        //删除原来数据
        db.delete(tablename,null,null);
        //插入所有数据
        for(newappdata appdata:appDatas) {
            ContentValues values = new ContentValues();
            values.put("name",appdata.getName());
            values.put("ftime",appdata.getFrt());
            values.put("ltime",appdata.getLrt());
            values.put("ttime",appdata.getTrt());
            values.put("starttimes",appdata.getStarttimes());
            db.insert(tablename,null,values);
        }
        //System.out.println(appDatas.size());
        db.close();
    }

    public void bt_choice_click(View view) {
        final String[] timerange={"一周","一天","一小时"};
        new AlertDialog.Builder(this).setTitle("请选择时间范围").setCancelable(true).setSingleChoiceItems(timerange, timerangeid, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                timerangeid=which;
            }
        }).setPositiveButton("完成",null).show();
    }

    public void btn_startservice(View view) {
        Intent service=new Intent(getApplicationContext(),MyService.class);
        sc=new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {

            }

            @Override
            public void onServiceDisconnected(ComponentName name) {

            }
        };
        bindService(service,sc,BIND_AUTO_CREATE);
        Toast toast = Toast.makeText(this,"后台开始自动更新...",Toast.LENGTH_SHORT);
        toast.show();
    }

    public void bt_stopservice(View view) {
        if(sc!=null) {
            unbindService(sc);
            Toast toast = Toast.makeText(this, "后台停止自动更新...", Toast.LENGTH_SHORT);
            toast.show();
        }
        sc=null;
    }

    public void bt_toDataGraph(View view) {
        Intent intent=new Intent(this,MainActivity_fliter.class);
        startActivity(intent);
    }
}