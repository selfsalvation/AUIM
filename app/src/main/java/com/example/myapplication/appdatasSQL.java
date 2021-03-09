package com.example.myapplication;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class appdatasSQL extends SQLiteOpenHelper {

    appdatasSQL(Context mcontext){
        super(mcontext,"appdatas.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sql0="create table appdatas(_id integer primary key autoincrement,name varchar(50),ftime integer,ltime integer,ttime integer,starttimes integer)";
        db.execSQL(sql0);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
