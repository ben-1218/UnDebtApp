package com.bignerdranch.android.undebt.database;

import android.database.Cursor;
import android.database.CursorWrapper;

import com.bignerdranch.android.undebt.Debt;

import  com.bignerdranch.android.undebt.database.DebtDbSchema.DebtTable;

import java.util.Date;
import java.util.UUID;

public class DebtCursorWrapper extends CursorWrapper {
    public DebtCursorWrapper (Cursor cursor) {
        super(cursor);
    }

    public Debt getDebt() {
        String uuidString = getString(getColumnIndex(DebtTable.Cols.UUID));
        String title = getString(getColumnIndex(DebtTable.Cols.TITLE));
        double amount = getDouble(getColumnIndex(DebtTable.Cols.AMOUNT));
        long date = getLong(getColumnIndex(DebtTable.Cols.DATE));
        int isSettled = getInt(getColumnIndex(DebtTable.Cols.SETTLED));

        Debt debt = new Debt(UUID.fromString(uuidString));
        debt.setTitle(title);
        debt.setAmount(amount);
        debt.setDate(new Date(date));
        debt.setSettled(isSettled != 0);

        return debt;

}
}
