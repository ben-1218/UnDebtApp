package com.bignerdranch.android.undebt.database;

public class DebtDbSchema {
    public static final class DebtTable {
        public static final String NAME = "debts";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String TITLE = "title";
            public static final String AMOUNT = "totalAmount";
            public static final String DATE = "date";
            public static final String SETTLED = "settled";
        }
    }

}
