package com.bignerdranch.android.undebt;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.widget.Toast;

import com.bignerdranch.android.undebt.database.DebtBaseHelper;
import com.bignerdranch.android.undebt.database.DebtCursorWrapper;
import com.bignerdranch.android.undebt.database.DebtDbSchema;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static com.bignerdranch.android.undebt.database.DebtDbSchema.*;

public class DebtLab {

    private static DebtLab sDebtLab;

//    private double totalAmount = 0.0;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static DebtLab get(Context context) {
        if(sDebtLab == null) {
            sDebtLab = new DebtLab(context);
        }
        return sDebtLab;
    }
    private DebtLab(Context context){
        mContext = context.getApplicationContext();
        mDatabase = new DebtBaseHelper(mContext)
                    .getWritableDatabase();



    }

    public void addDebt(Debt c) {
        ContentValues values = getContentValues(c);
        mDatabase.insert(DebtTable.NAME, null, values);
    }

    public List<Debt> getDebts() {
            List<Debt> debts = new ArrayList<>();

            DebtCursorWrapper cursor = queryDebts(null, null);

            try{
                cursor.moveToFirst();
                while(!cursor.isAfterLast()) {
                    debts.add(cursor.getDebt());
                    cursor.moveToNext();
                }
            }finally {
                cursor.close();
            }
            return debts;
    }

    public Debt getDebt(UUID id) {
       DebtCursorWrapper cursor = queryDebts(
               DebtTable.Cols.UUID + "= ?",
               new String[]{ id.toString() }
       );
       try {
           if(cursor.getCount() == 0 ){
               return null;
           }
           cursor.moveToFirst();
           return cursor.getDebt();
       }finally {
           cursor.close();
       }
    }


    public File getPhotoFile(Debt debt) {
        File filesDir = mContext.getFilesDir();
        return new File(filesDir, debt.getPhotoFilename());
    }

    public void updateDebt(Debt debt) {
        String uuidString = debt.getId().toString();
        ContentValues values = getContentValues(debt);
        String FindValues = values.toString();

        //if title is null, the data will not insert into database
        if(FindValues.contains("title=null") || FindValues.contains("title= ")){
            mDatabase.delete(DebtTable.NAME, DebtTable.Cols.UUID + "=?",
                    new String[]{uuidString});

        }else{
            mDatabase.update(DebtTable.NAME, values,
                    DebtTable.Cols.UUID + "=?",
                    new String[] { uuidString });
        }

    }

    private DebtCursorWrapper queryDebts(String whereClause, String[] whereArgs) {
        Cursor cursor = mDatabase.query(
                DebtTable.NAME,
                null,
                whereClause,
                whereArgs,
                null,
                null,
                null
        );
        return new DebtCursorWrapper(cursor);
    }

    private static ContentValues getContentValues(Debt debt) {
        ContentValues values = new ContentValues();
        values.put(DebtTable.Cols.UUID, debt.getId().toString());
        values.put(DebtTable.Cols.TITLE, debt.getTitle());
        values.put(DebtTable.Cols.AMOUNT, debt.getAmount());
        values.put(DebtTable.Cols.DATE, debt.getDate().getTime());
        values.put(DebtTable.Cols.SETTLED, debt.isSettled() ? 1 : 0);

        return values;
    }

    public void deleteDebt(Debt d){ //method to delete item from database
        String uuidString = d.getId().toString();
        ContentValues values = getContentValues(d);

        mDatabase.delete(DebtTable.NAME, DebtTable.Cols.UUID + "=?",
                new String[] {uuidString});
    }

    public void ClearSettledDebt(){ //method to clear all settled item from database
        mDatabase.delete(DebtTable.NAME, DebtTable.Cols.SETTLED + "=1",
                null);
    }





}
