package com.example.myapplication;

import android.graphics.drawable.Drawable;

import java.util.Calendar;

public class newappdata {

    String name;
    long daytime=1000*60*60*24;
    long hourtime=1000*60*60;
    long minutime=1000*60;
    Drawable appicon;
    long frt;
    long lrt;
    long trt;

    public static String translateTime(long time){
        String s1,s2,s3,s4,s5;
        Calendar c=Calendar.getInstance();
        c.setTimeInMillis(time);
        s1=c.get(Calendar.YEAR)+"年 ";
        s2=(c.get(Calendar.MONTH)+1)+"月 ";
        s3=c.get(Calendar.DAY_OF_MONTH)+"日 ";
        s4=c.get(Calendar.HOUR)+"点 ";
        s5=c.get(Calendar.MINUTE)+"分";
        return s1+s2+s3+s4+s5;

    }

    public long getFrt() {
        return frt;
    }

    public void setFrt(long frt) {
        this.frt = frt;
    }

    public long getLrt() {
        return lrt;
    }

    public void setLrt(long lrt) {
        this.lrt = lrt;
    }

    public long getTrt() {
        return trt;
    }

    public void setTrt(long trt) {
        this.trt = trt;
    }

    public int getStarttimes() {
        return starttimes;
    }

    public void setStarttimes(int starttimes) {
        this.starttimes = starttimes;
    }

    int starttimes;
    String fistRunTime="";
    String lastRunTime="";
    String totalRunTime="";

    public Drawable getAppicon() {
        return appicon;
    }

    public void setAppicon(Drawable appicon) {
        this.appicon = appicon;
    }
    newappdata(){

    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getFistRunTime() {
        return fistRunTime;
    }

    public void setFistRunTime(long fistRunTime) {
        frt=fistRunTime;
        String s1,s2,s3;
        Calendar c=Calendar.getInstance();
        c.setTimeInMillis(fistRunTime);
        s1=c.get(Calendar.YEAR)+"年";
        s2=(c.get(Calendar.MONTH)+1)+"月";
        s3=c.get(Calendar.DAY_OF_MONTH)+"日";
        this.fistRunTime=s1+s2+s3;
    }

    public String getLastRunTime() {
        return lastRunTime;
    }

    public void setLastRunTime(long lastRunTime) {
        lrt=lastRunTime;
        String s1,s2,s3;
        Calendar c=Calendar.getInstance();
        c.setTimeInMillis(lastRunTime);
        s1=c.get(Calendar.YEAR)+"年";
        s2=(c.get(Calendar.MONTH)+1)+"月";
        s3=c.get(Calendar.DAY_OF_MONTH)+"日";
        this.lastRunTime=s1+s2+s3;
    }

    public String getTotalRunTime() {
        return totalRunTime;
    }

    public void setTotalRunTime(long totalRunTime) {

        trt=totalRunTime;
        String s1="",s2="",s3="",s4="";
        long l1,l2,l3,l4;
        l1=totalRunTime/daytime;
        l2=totalRunTime/hourtime%24;
        l3=totalRunTime/minutime%60;
        l4=totalRunTime/1000%60;
        int flag=0;
        if(l1!=0)
        {
            s1=l1+"天";
            flag=1;
        }
        if(l2!=0||flag!=0){
            s2=l2+"小时";
            flag=1;
        }
        if(l3!=0||flag!=0){
            s3=l3+"分钟";
            flag=1;
        }
        s4=l4+"秒";
        this.totalRunTime= s1+s2+s3+s4;
    }
}
