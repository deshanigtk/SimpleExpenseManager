package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.provider.ContactsContract;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.DatabaseHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.ExpenseManagerContract;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.control.ExpenseManager;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

/**
 * Created by Deshani on 11/20/2016.
 */

public class PersistentTransactionDAO implements TransactionDAO {
    private final SQLiteDatabase database;

    public PersistentTransactionDAO(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        ContentValues contentValues = new ContentValues();

        int expense;
        if (expenseType == ExpenseType.EXPENSE) {
            expense = 0;
        } else {
            expense = 1;
        }

        contentValues.put(ExpenseManagerContract.TransactionEntry.COLUMN_DATE, date.getTime());
        contentValues.put(ExpenseManagerContract.TransactionEntry.COLUMN_ACCOUNT_NO, accountNo);
        contentValues.put(ExpenseManagerContract.TransactionEntry.COLUMN_EXPENSE_TYPE, expense);
        contentValues.put(ExpenseManagerContract.TransactionEntry.COLUMN_AMOUNT, amount);

        database.insert(ExpenseManagerContract.TransactionEntry.TABLE_NAME, null, contentValues);

    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        List<Transaction> transactions = new LinkedList<>();

        Cursor c = database.rawQuery("SELECT * FROM " + ExpenseManagerContract.TransactionEntry.TABLE_NAME, null);

        if (c.moveToFirst()) {
            do {
                Long dateLong = c.getLong(c.getColumnIndex(ExpenseManagerContract.TransactionEntry.COLUMN_DATE));
                String accountNo = c.getString(c.getColumnIndex(ExpenseManagerContract.TransactionEntry.COLUMN_ACCOUNT_NO));
                int expenseTypeInt = c.getInt(c.getColumnIndex(ExpenseManagerContract.TransactionEntry.COLUMN_EXPENSE_TYPE));
                Double amount = c.getDouble(c.getColumnIndex(ExpenseManagerContract.TransactionEntry.COLUMN_AMOUNT));

                Date date = new Date(dateLong);

                ExpenseType expenseType;
                if (expenseTypeInt == 0) {
                    expenseType = ExpenseType.EXPENSE;
                } else {
                    expenseType = ExpenseType.INCOME;
                }

                Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
                transactions.add(transaction);
            } while (c.moveToNext());
        }
        c.close();
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        int size = (int) DatabaseUtils.queryNumEntries(database, ExpenseManagerContract.TransactionEntry.TABLE_NAME);
        if (size <= limit) {
            return getAllTransactionLogs();
        }
        // return the last <code>limit</code> number of transaction logs
        return getAllTransactionLogs().subList(size - limit, size);

    }
}




