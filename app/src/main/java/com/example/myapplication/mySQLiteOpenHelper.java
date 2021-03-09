package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class mySQLiteOpenHelper extends SQLiteOpenHelper {

    mySQLiteOpenHelper(Context mcontext){
        super(mcontext,"AppDataDB.db",null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql0="create table appdatas(_id integer primary key autoincrement,name varchar(50),ftime integer,ltime integer,ttime integer,starttimes integer)";
        String sql1="create table appdatas1(_id integer primary key autoincrement,name varchar(50),ftime integer,ltime integer,ttime integer,starttimes integer)";
        String sql2="create table appdatas2(_id integer primary key autoincrement,name varchar(50),ftime integer,ltime integer,ttime integer,starttimes integer)";
        String sql3="create table appdatas3(_id integer primary key autoincrement,name varchar(50),ftime integer,ltime integer,ttime integer,starttimes integer)";
        String sql4="create table appdatas4(_id integer primary key autoincrement,name varchar(50),ftime integer,ltime integer,ttime integer,starttimes integer)";
        db.execSQL(sql1);
        db.execSQL(sql2);
        db.execSQL(sql3);
        db.execSQL(sql4);
        db.execSQL(sql0);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
