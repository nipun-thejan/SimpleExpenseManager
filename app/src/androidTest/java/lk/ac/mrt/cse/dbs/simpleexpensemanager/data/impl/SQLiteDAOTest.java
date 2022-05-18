package lk.ac.mrt.cse.dbs.simpleexpensemanager.data.impl;


import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import static com.google.common.truth.Truth.assertThat;

import org.junit.After;
import org.junit.Before;
import org.junit.FixMethodOrder;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.MethodSorters;

import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.exception.InvalidAccountException;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.Account;
import lk.ac.mrt.cse.dbs.simpleexpensemanager.data.model.ExpenseType;


@RunWith(AndroidJUnit4.class)
@MediumTest
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SQLiteDAOTest {
    private SQLiteAccountDAO accountDAO;

    @Before
    public void setup() {
        accountDAO = new SQLiteAccountDAO(ApplicationProvider.getApplicationContext());
    }

    @After
    public void teardown() {
        accountDAO.close();
    }

    @Test
    public void A_addAccount() {
        Account account = new Account("19999", "BOC", "thejan", 500);
        boolean result = accountDAO.addAccount(account);
        assertThat(result).isTrue();
    }

    @Test
    public void B_getAccount() throws InvalidAccountException {
        Account account = accountDAO.getAccount("19999");

        boolean result = false;
        if (account != null) {
            result = account.getAccountHolderName().equals("thejan") && account.getBankName().equals("BOC")
                    && account.getBalance() == 500;
        }
        assertThat(result).isTrue();
    }

}
