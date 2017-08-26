package com.fanny.bluetoothbloodpresuretest.dao;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Fanny on 17/6/13.
 */

public class DBOpenHelper extends SQLiteOpenHelper {
    public DBOpenHelper(Context context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    public DBOpenHelper(Context context){
        super(context,"BluetoothPressure.db",null,1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table IF NOT EXISTS user(name varchar(50),"+" sex varchar(10),age integer,height integer,weight integer,touxiang blob)");

        db.execSQL("create table IF NOT EXISTS data(name varchar(50),time date,sys integer,dia integer,pul integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists user");
        db.execSQL("drop table if exists data");
        onCreate(db);
    }
}
