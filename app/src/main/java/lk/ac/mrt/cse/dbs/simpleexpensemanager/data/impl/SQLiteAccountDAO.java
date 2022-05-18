package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;

public class SQLiteAccountDAO extends SQLiteOpenHelper implements AccountDAO {

    private final Map<String, Account> accounts;

    private static final String SQL_CREATE_ACCOUNTS_TABLE = "CREATE TABLE Accounts(" +
            "accountNo TEXT PRIMARY KEY, " +
            "bankName TEXT NOT NULL, " +
            "accountHolderName TEXT NOT NULL, " +
            "balance REAL NOT NULL " +
            ")";


    public SQLiteAccountDAO(Context context) {
        super(context, "190184A.DB", null, 1);
        this.accounts = new HashMap<>();

        //get existing accounts from the database
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Accounts", null);

        if (cursor.moveToFirst()) {
            do {
                String accountNo = cursor.getString(0);
                String bankName = cursor.getString(1);
                String accountHolderName = cursor.getString(2);
                double balance = cursor.getDouble(3);
                accounts.put(accountNo, new Account(accountNo, bankName, accountHolderName, balance));
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ACCOUNTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS Accounts");
    }

    @Override
    public List<String> getAccountNumbersList() {
        return new ArrayList<>(accounts.keySet());
    }

    @Override
    public List<Account> getAccountsList() {
        return new ArrayList<>(accounts.values());
    }

    @Override
    public Account getAccount(String accountNo) throws InvalidAccountException {
        if (accounts.containsKey(accountNo)) {
            return accounts.get(accountNo);
        }
        String msg = "The account :" + accountNo + " is invalid.";
        throw new InvalidAccountException(msg);
    }

    @Override
    public boolean addAccount(Account account) {
        accounts.put(account.getAccountNo(), account);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("accountNo", account.getAccountNo());
        cv.put("bankName", account.getBankName());
        cv.put("accountHolderName", account.getAccountHolderName());
        cv.put("balance", account.getBalance());

        long result =-1;
        result = db.insert("Accounts", null, cv);
        db.close();
        return result!=-1;
    }

    @Override
    public void removeAccount(String accountNo) throws InvalidAccountException {
        if (!accounts.containsKey(accountNo)) {
            String msg = "The Account: " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }

        accounts.remove(accountNo);

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Accounts WHERE accountNo=?", new String[]{accountNo});
        if (cursor.getCount() > 0) {
            db.delete("Accounts", "accountNo=?", new String[]{accountNo});
        }
        cursor.close();
        db.close();
    }

    @Override
    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
        // long result = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Accounts WHERE accountNo=?", new String[] {accountNo});

        if (expenseType==ExpenseType.EXPENSE) {
            amount=-amount;
        }
        if (accounts.containsKey(accountNo) && cursor.getCount()>0) {

            Account account = accounts.get(accountNo);
            account.setBalance(account.getBalance() + amount);
            accounts.put(accountNo, account);

            cursor.moveToFirst();
            double newAmount = cursor.getDouble(3)+amount;
            ContentValues contentValues = new ContentValues();
            contentValues.put("accountNo", cursor.getString(0));
            contentValues.put("bankName", cursor.getString(1));
            contentValues.put("accountHolderName", cursor.getString(2));
            contentValues.put("balance", newAmount);
            db.update("Accounts", contentValues, "accountNo=?", new String[] {accountNo});
            //result = db.update("Accounts", contentValues, "accountNo=?", new String[] {accountNo});
        }else if (accounts.containsKey(accountNo)){
            accounts.remove(accountNo);
        }

        cursor.close();
        db.close();

        if (!accounts.containsKey(accountNo)) {
            String msg = "The Account: " + accountNo + " is invalid.";
            throw new InvalidAccountException(msg);
        }
//        if (result == -1) {
//            throw new InvalidAccountException("Account not found!");
//        }
        //return true;
    }
}

//    SQLiteDatabase db;
//    java.io.File filename = Constants.CONTEXT.getFilesDir();
//
//    public SQLiteAccountDAO()
//    {
//        db = SQLiteDatabase.openOrCreateDatabase(filename.getAbsolutePath() + "/190184A.sqlite", null);
//        db.execSQL("CREATE TABLE IF NOT EXISTS Account(accountNo VARCHAR(50),bankName VARCHAR(50),accountHolderName VARCHAR(50), balance NUMERIC(10,2));");
//    }
//
//
//    @Override
//    public List<String> getAccountNumbersList() {
//        @SuppressLint("Recycle") Cursor result_db = db.rawQuery("Select accountNo from Account",null);
//        List<String> result = new ArrayList<>();
//        result_db.moveToFirst();
//        while(!result_db.isAfterLast())
//        {
//            result.add(result_db.getString(0));
//            result_db.moveToNext();
//        }
//        return result;
//    }
//
//    @Override
//    public List<Account> getAccountsList() {
//        @SuppressLint("Recycle") Cursor result_db = db.rawQuery("Select * from Account;",null);
//        List<Account> result = new ArrayList<>();
//        result_db.moveToFirst();
//        while(!result_db.isAfterLast())
//        {
//
//            result.add( new Account(result_db.getString(0),result_db.getString(1),
//                    result_db.getString(2), Double.parseDouble(result_db.getString(3) ) ));
//            result_db.moveToNext();
//        }
//        return result;
//    }
//
//    @Override
//    public Account getAccount(String accountNo) throws InvalidAccountException {
//        @SuppressLint("Recycle") Cursor result_db = db.rawQuery("Select * from Account where accountNo='" + accountNo+"';", null);
//        result_db.moveToFirst();
//        if (result_db.isAfterLast()) {
//            throw new InvalidAccountException("Account No:" + accountNo + " is not valid!");
//        }
//        return new Account(result_db.getString(0), result_db.getString(1), result_db.getString(2),
//                Double.parseDouble(result_db.getString(3)));
//    }
//
//    @Override
//    public void addAccount(Account account) {
//        db.execSQL("INSERT INTO Account VALUES('"+account.getAccountNo()+"','"+account.getBankName()+"','"+account.getAccountHolderName()+"','"+account.getBalance()+"');");
//
//    }
//
//    @Override
//    public void removeAccount(String accountNo) throws InvalidAccountException {
//        db.execSQL("DELETE FROM Account WHERE accountNo='"+accountNo+"';");
//
//    }
//
//    @Override
//    public void updateBalance(String accountNo, ExpenseType expenseType, double amount) throws InvalidAccountException {
//        Account account = getAccount(accountNo);
//
//        double balance = account.getBalance();
//        if (ExpenseType.INCOME == expenseType) {
//            balance += amount;
//        } else
//            balance-=amount;
//        db.execSQL("UPDATE Account SET balance='"+balance+"' WHERE accountNo='"+accountNo+"';");
//    }

