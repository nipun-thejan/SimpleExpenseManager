package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.Nullable;
import android.util.Log;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.Constants;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class SQLiteTransactionDAO extends SQLiteOpenHelper implements TransactionDAO {
    private final List<Transaction> transactions;
    private static final String SQL_CREATE_TRANSACTIONS_TABLE = "CREATE TABLE Transactions(" +
            "date TEXT NOT NULL, " +
            "accountNo TEXT NOT NULL, " +
            "expenseType TEXT NOT NULL, " +
            "amount REAL NOT NULL, " +
            "FOREIGN KEY(accountNo) " +
            "REFERENCES Accounts(accountNo)" +
            ")";

    public SQLiteTransactionDAO(Context context) {
        super(context, "190184A.db", null, 1);
        transactions = new LinkedList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT * FROM Transactions", null);

        int rows = cursor.getCount();

        if (cursor.moveToFirst()) {
            do {
                String dateString = cursor.getString(0);
                String accountNo = cursor.getString(1);
                String expenseTypeString = cursor.getString(2);
                double amount = cursor.getDouble(3);
                try {
                    Date date = new SimpleDateFormat("dd/MM/yyyy").parse(dateString);
                    ExpenseType expenseType = ExpenseType.valueOf(expenseTypeString);
                    transactions.add(new Transaction(date, accountNo, expenseType, amount));
                } catch (Exception e) {
                    // Log.e(e.toString(),"error!!");
                    e.printStackTrace();
                }
            }while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_TRANSACTIONS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP TABLE IF EXISTS Transactions");
    }

    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        Transaction transaction = new Transaction(date, accountNo, expenseType, amount);
        transactions.add(transaction);

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("date", dateFormat.format(date));
        cv.put("accountNo", accountNo);
        cv.put("expenseType", expenseType.name());
        cv.put("amount", amount);
        db.insert("Transactions", null, cv);

        db.close();
    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        return transactions;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        int size = transactions.size();
        if (size <= limit) {
            return transactions;
        }
        return transactions.subList(size - limit, size);
    }
}

//    SQLiteDatabase db;
//    java.io.File filename = Constants.CONTEXT.getFilesDir();
//    public SQLiteTransactionDAO()
//    {
//        db = SQLiteDatabase.openOrCreateDatabase(filename.getAbsolutePath() + "/190184A.sqlite", null);
//        db.execSQL("CREATE TABLE IF NOT EXISTS Transactions(accountNo VARCHAR(50),expenseType VARCHAR(50),amount NUMERIC(10,2), date_value Date);");
//    }
//    @Override
//    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
//        db.execSQL("INSERT INTO Transactions VALUES('"+accountNo+"','"+((expenseType==ExpenseType.INCOME)?"INCOME":"EXPENSE")+"','"+amount+"','"+date.toString()+"');");
//
//    }
//
//    @Override
//    public List<Transaction> getAllTransactionLogs() {
//        @SuppressLint("Recycle") Cursor result_db = db.rawQuery("Select * from Transactions",null);
//        result_db.moveToFirst();
//        List<Transaction> result = new ArrayList<>();
//        while(!result_db.isAfterLast())
//        {
//            result.add( new Transaction(new Date(result_db.getString(3)),result_db.getString(0),((result_db.getString(1).equals("INCOME"))?ExpenseType.INCOME:ExpenseType.EXPENSE), Double.parseDouble(result_db.getString(2) ) ));
//            result_db.moveToNext();
//        }
//        return result;
//    }
//
//    @Override
//    public List<Transaction> getPaginatedTransactionLogs(int limit) {
//        @SuppressLint("Recycle") Cursor result_db = db.rawQuery("Select * from Transactions ORDER BY date_value LIMIT "+limit,null);
//        result_db.moveToFirst();
//        List<Transaction> result = new ArrayList<>();
//        while(!result_db.isAfterLast())
//        {
//            result.add( new Transaction(new Date(result_db.getString(3)),result_db.getString(0),((result_db.getString(1)=="INCOME")?ExpenseType.INCOME:ExpenseType.EXPENSE), Double.parseDouble(result_db.getString(2) ) ));
//            result_db.moveToNext();
//        }
//        return result;
//    }

