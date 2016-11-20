package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.DatabaseHelper;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.ExpenseManagerContract;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

/**
 * Created by Deshani on 11/20/2016.
 */

public class PersistentAccountDAO implements AccountDAO {
    private DatabaseHelper db;
    Context context;

    public PersistentAccountDAO(Context context) {
        this.context = context;
        db = new DatabaseHelper(context);
    }

    @Override
    public List<String> getAccountNumbersList() {
        SQLiteDatabase database = db.getReadableDatabase();
        List<String> accountNumbersList = new ArrayList<>();

        Cursor c = database.rawQuery("SELECT " + ExpenseManagerContract.AccountEntry.COLUMN_ACCOUNT_NO + " FROM " + ExpenseManagerContract.AccountEntry.TABLE_NAME, null);

        if (c.moveToFirst()) {
            do {
                String accountNo = c.getString(c.getColumnIndex(ExpenseManagerContract.AccountEntry.COLUMN_ACCOUNT_NO));
                accountNumbersList.add(accountNo);
            } while (c.moveToNext());
        }
        c.close();
        return accountNumbersList;
    }


    @Override
    public List<Account> getAccountsList() {
        SQLiteDatabase database = db.getReadableDatabase();
        List<Account> accounts = new ArrayList<>();

        Cursor c = database.rawQuery("SELECT * FROM " + ExpenseManagerContract.AccountEntry.TABLE_NAME, null);

        if (c.moveToFirst()) {
            do {
                String accountNo = c.getString(c.getColumnIndex(ExpenseManagerContract.AccountEntry.COLUMN_ACCOUNT_NO));
                String bankName = c.getString(c.getColumnIndex(ExpenseManagerContract.AccountEntry.COLUMN_BANK_NAME));
                String accountHolderName = c.getString(c.getColumnIndex(ExpenseManagerContract.AccountEntry.COLUMN_ACCOUNT_HOLDER_NAME));
                double balance = c.getDouble(c.getColumnIndex(ExpenseManagerContract.AccountEntry.COLUMN_BALANCE));

                Account account = new Account(accountNo, bankName, accountHolderName, balance);
                accounts.add(account);
            } while (c.moveToNext());
        }
        c.close();
        return accounts;
    }


    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase database = db.getReadableDatabase();
        Account account = null;

        Cursor c = database.rawQuery("SELECT * FROM " + ExpenseManagerContract.AccountEntry.TABLE_NAME + " WHERE " + ExpenseManagerContract.AccountEntry.COLUMN_ACCOUNT_NO + "=" + accountNo + "'", null);

        if (c.moveToFirst()) {
            String bankName = c.getString(c.getColumnIndex(ExpenseManagerContract.AccountEntry.COLUMN_BANK_NAME));
            String accountHolderName = c.getString(c.getColumnIndex(ExpenseManagerContract.AccountEntry.COLUMN_ACCOUNT_HOLDER_NAME));
            double balance = c.getDouble(c.getColumnIndex(ExpenseManagerContract.AccountEntry.COLUMN_BALANCE));
            c.close();
            account = new Account(accountNo, bankName, accountHolderName, balance);
            return account;
        }

        String msg = "Account " + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);

    }


    @Override
    public void addAccount(Account account) {

        SQLiteDatabase database = db.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        contentValues.put(ExpenseManagerContract.AccountEntry.COLUMN_ACCOUNT_NO, account.getAccountNo());
        contentValues.put(ExpenseManagerContract.AccountEntry.COLUMN_BANK_NAME, account.getBankName());
        contentValues.put(ExpenseManagerContract.AccountEntry.COLUMN_ACCOUNT_HOLDER_NAME, account.getAccountHolderName());
        contentValues.put(ExpenseManagerContract.AccountEntry.COLUMN_BALANCE, account.getBalance());

        database.insert(ExpenseManagerContract.AccountEntry.TABLE_NAME, null, contentValues);
    }


    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        SQLiteDatabase database = db.getWritableDatabase();
        int result = database.delete(ExpenseManagerContract.AccountEntry.TABLE_NAME, ExpenseManagerContract.AccountEntry.COLUMN_ACCOUNT_NO + " = " + accountNo, null);
        if (!(result > 0)) {
            String msg = "Account " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
    }


    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        SQLiteDatabase database = db.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        switch (expenseType) {
            case EXPENSE:
                contentValues.put(ExpenseManagerContract.AccountEntry.COLUMN_BALANCE, -amount);
                break;
            case INCOME:
                contentValues.put(ExpenseManagerContract.AccountEntry.COLUMN_BALANCE, +amount);
                break;
        }
        database.update(ExpenseManagerContract.AccountEntry.TABLE_NAME, contentValues, " WHERE " + ExpenseManagerContract.AccountEntry.COLUMN_ACCOUNT_NO + "=?", new String[]{accountNo});
    }
}
