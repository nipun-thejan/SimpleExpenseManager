package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.Constants;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class SQLiteTransactionDAO implements TransactionDAO {

    SQLiteDatabase db;
    java.io.File filename = Constants.CONTEXT.getFilesDir();
    public SQLiteTransactionDAO()
    {
        db = SQLiteDatabase.openOrCreateDatabase(filename.getAbsolutePath() + "/190184A.sqlite", null);
        db.execSQL("CREATE TABLE IF NOT EXISTS Transactions(accountNo VARCHAR(50),expenseType VARCHAR(50),amount NUMERIC(10,2), date_value Date);");
    }
    @Override
    public void logTransaction(Date date, String accountNo, ExpenseType expenseType, double amount) {
        db.execSQL("INSERT INTO Transactions VALUES('"+accountNo+"','"+((expenseType==ExpenseType.INCOME)?"INCOME":"EXPENSE")+"','"+amount+"','"+date.toString()+"');");

    }

    @Override
    public List<Transaction> getAllTransactionLogs() {
        @SuppressLint("Recycle") Cursor result_db = db.rawQuery("Select * from Transactions",null);
        result_db.moveToFirst();
        List<Transaction> result = new ArrayList<>();
        while(!result_db.isAfterLast())
        {
            result.add( new Transaction(new Date(result_db.getString(3)),result_db.getString(0),((result_db.getString(1).equals("INCOME"))?ExpenseType.INCOME:ExpenseType.EXPENSE), Double.parseDouble(result_db.getString(2) ) ));
            result_db.moveToNext();
        }
        return result;
    }

    @Override
    public List<Transaction> getPaginatedTransactionLogs(int limit) {
        @SuppressLint("Recycle") Cursor result_db = db.rawQuery("Select * from Transactions ORDER BY date_value LIMIT "+limit,null);
        result_db.moveToFirst();
        List<Transaction> result = new ArrayList<>();
        while(!result_db.isAfterLast())
        {
            result.add( new Transaction(new Date(result_db.getString(3)),result_db.getString(0),((result_db.getString(1)=="INCOME")?ExpenseType.INCOME:ExpenseType.EXPENSE), Double.parseDouble(result_db.getString(2) ) ));
            result_db.moveToNext();
        }
        return result;
    }
}
