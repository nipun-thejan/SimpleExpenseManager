package lk.ac.mrt.cse.dbs.simpleexpensemanager.control;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.AccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.TransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteAccountDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl.SQLiteTransactionDAO;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Transaction;

public class PersistentExpenseManager extends ExpenseManager{

    private  AccountDAO sqliteAccountDAO;
    private TransactionDAO sqliteTransactionDAO;

    public PersistentExpenseManager(AccountDAO sqliteAccountDAO, TransactionDAO sqliteTransactionDAO) {
        this.sqliteAccountDAO = sqliteAccountDAO;
        this.sqliteTransactionDAO = sqliteTransactionDAO;
        setup();
    }

    @Override
    public void setup() {
        setAccountsDAO(sqliteAccountDAO);
        setTransactionsDAO(sqliteTransactionDAO);

        // dummy data
//        Account dummyAcct1 = new Account("12345A", "Yoda Bank", "Anakin Skywalker", 10000.0);
//        Account dummyAcct2 = new Account("78945Z", "Clone BC", "Obi-Wan Kenobi", 80000.0);
//        getAccountsDAO().addAccount(dummyAcct1);
//        getAccountsDAO().addAccount(dummyAcct2);

    }
}
