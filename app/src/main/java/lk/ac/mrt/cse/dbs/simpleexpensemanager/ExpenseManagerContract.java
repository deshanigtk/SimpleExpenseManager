package lk.ac.mrt.cse.dbs.simpleexpensemanager;

import android.provider.BaseColumns;

/**
 * Created by Deshani on 11/20/2016.
 */

public final class ExpenseManagerContract {
    private ExpenseManagerContract() {
    }

    public static class AccountEntry implements BaseColumns {
        public static final String TABLE_NAME = "account";
        public static final String COLUMN_ACCOUNT_NO = "accountNo";
        public static final String COLUMN_BANK_NAME = "bankName";
        public static final String COLUMN_ACCOUNT_HOLDER_NAME = "accountHolderName";
        public static final String COLUMN_BALANCE = "balance";

        public static final String SQL_CREATE_ENTRY = "create table " + TABLE_NAME + " (" + COLUMN_ACCOUNT_NO + " TEXT PRIMARY KEY, " + COLUMN_BANK_NAME + " TEXT, " + COLUMN_ACCOUNT_HOLDER_NAME + " TEXT, " + COLUMN_BALANCE + " REAL)";

    }

    public static class TransactionEntry implements BaseColumns {
        public static final String TABLE_NAME = "transactions";
        public static final String COLUMN_ID = "id";
        public static final String COLUMN_DATE = "date";
        public static final String COLUMN_ACCOUNT_NO = "accountNo";
        public static final String COLUMN_EXPENSE_TYPE = "expenseType";
        public static final String COLUMN_AMOUNT = "amount";

        public static final String SQL_CREATE_ENTRY = "create table " + TABLE_NAME + " (" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " + COLUMN_DATE + " REAL, " + COLUMN_ACCOUNT_NO + " TEXT, " + COLUMN_EXPENSE_TYPE + " INTEGER, " + COLUMN_AMOUNT + " REAL, FOREIGN KEY(accountNo) REFERENCES account(accountNo))";

    }

}
