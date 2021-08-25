package com.hevlar.accounting.controller;

import com.hevlar.accounting.model.Account;
import com.hevlar.accounting.model.AccountGroup;
import com.hevlar.accounting.model.BalanceSheetAccount;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ChartOfAccountsTest {

    ChartOfAccounts chartOfAccounts;

    @BeforeEach
    void setUp() {
        chartOfAccounts = new ChartOfAccounts();
        chartOfAccounts.newCurrentAsset("Bank A", LocalDate.of(2021,1, 1), "SGD", 1000.0);
        chartOfAccounts.newCurrentAsset("Bank B", LocalDate.of(2021,1, 1), "SGD", 1000.0);
        chartOfAccounts.newCurrentLiability("Student Loan", LocalDate.of(2000, 1, 1), "SGD", 1000.0);
        chartOfAccounts.newCreditCard("Credit Card A", LocalDate.of(2021,1, 1), "SGD", 0.0, "Bank A", 1, 12);
        chartOfAccounts.newExpense("Food");
        chartOfAccounts.newRevenue("Salary");
    }

    @Test
    void ensure_no_duplicate_account_name(){
        assertFalse(chartOfAccounts.newRevenue("Food"));
    }

    @Test
    void getAccounts_return_correctly(){
        Map<String, Account> currentAssets = chartOfAccounts.getAccounts(AccountGroup.CURRENT_ASSETS);
        assertEquals(currentAssets.size(), 2);
        assertTrue(currentAssets.containsKey("Bank A"));
        assertTrue(currentAssets.containsKey("Bank B"));
    }

    @Test
    void lock_success() {
        chartOfAccounts.lock();
        assertTrue(chartOfAccounts.getAccount("Bank A").isLocked());
        assertTrue(chartOfAccounts.getAccount("Bank B").isLocked());
        assertTrue(chartOfAccounts.getAccount("Credit Card A").isLocked());
        assertTrue(chartOfAccounts.getAccount("Food").isLocked());
        assertTrue(chartOfAccounts.getAccount("Salary").isLocked());
        assertFalse(chartOfAccounts.getAccount("Bank A").setName("New Bank"));
    }

    @Test
    void deleteAccount_not_allowed_for_locked_account() {
        assertTrue(chartOfAccounts.deleteAccount("Bank A"));
        chartOfAccounts.lock();
        assertFalse(chartOfAccounts.deleteAccount("Bank A"));
    }

    @Test
    void getAccount() {
        BalanceSheetAccount bank = (BalanceSheetAccount) chartOfAccounts.getAccount("Bank A");
        assertEquals(bank.getOpenBal(), new BigDecimal(1000));
    }

    @Test
    void creditCard_is_current_liability(){
        assertEquals(chartOfAccounts.getAccount("Credit Card A").getAccountGroup(), AccountGroup.CURRENT_LIABILITIES);
    }
}