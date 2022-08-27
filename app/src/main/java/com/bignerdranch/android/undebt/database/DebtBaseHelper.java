package com.bignerdranch.android.undebt.database;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.bignerdranch.android.undebt.database.DebtDbSchema;
import com.bignerdranch.android.undebt.database.DebtDbSchema.DebtTable;

import java.util.ArrayList;

public class DebtBaseHelper extends SQLiteOpenHelper {
    private static  final int VERSION = 1;
    private static final String DATABASE_NAME = "debtBase.db";

    public DebtBaseHelper(Context context){
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + DebtTable.NAME +
                "(" + "_id integer primary key autoincrement, "+
                DebtTable.Cols.UUID + ", " +
                DebtTable.Cols.TITLE + ", " +
                DebtTable.Cols.AMOUNT + ", "+
                DebtTable.Cols.DATE + ", " +
                DebtTable.Cols.SETTLED +
                ")"
        );
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int OldVersion, int newVersion) {

    }
}
