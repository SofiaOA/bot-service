package com.hedvig.botService.enteties.userContextHelpers;

import com.hedvig.botService.enteties.UserContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AutogiroData {
    private static final String BANK_ACCOUNTS_COUNT = "{BANK_ACCOUNTS_COUNT}";
    private static final String BANK_ACCOUNT_NAME = "{BANK_ACCOUNT_NAME_%s}";
    private static final String BANK_ACCOUNT_CLEARING_NO = "{BANK_ACCOUNT_CLEARING_NO_%s}";
    private static final String BANK_ACCOUNT_NUMBER = "{BANK_ACCOUNT_NUMBER_%s}";
    private static final String BANK_ACCOUNT_AMOUNT = "{BANK_ACCOUNT_AMOUNT_%s}";
    private static final String BANK_ACCOUNT_SELECTED = "{BANK_ACCOUNT_SELECTED}" ;
    private static final String BANK_SHORT = "{BANK}";
    private static final String BANK_FULL = "{BANK_FULL}";

    private UserContext ctx;

    public AutogiroData(UserContext ctx) {
        this.ctx = ctx;
    }

    public int getAccountCount() {
        return Integer.parseInt(ctx.getDataEntry(BANK_ACCOUNTS_COUNT));
    }

    public List<BankAccount> getAccounts() {
        List<BankAccount> accounts = new ArrayList<>();
        if(ctx.getDataEntry(BANK_ACCOUNTS_COUNT) != null) {
            for(int i=0; i<getAccountCount(); i++) {
                accounts.add(new BankAccount(getAccountName(i),getAccountClearingNo(i),getAccountNo(i),getAccountAmount(i)));
            }
        }
        return accounts;
    }

    public void setSelecteBankAccount(int i) {
        if(i < 0 || i > getAccountCount())
            throw new RuntimeException(
                    String.format("Account selected is out of bounds, selected: %s, noAccounts: %s", i, getAccountCount()));
        ctx.putUserData(BANK_ACCOUNT_SELECTED, Objects.toString(i));
    }

    public void setAccounts(List<BankAccount> accounts) {
        for(int i=0; i<accounts.size(); i++) {
            BankAccount account = accounts.get(i);
            setAccountInformation(BANK_ACCOUNT_NAME, i, account.getName());
            setAccountInformation(BANK_ACCOUNT_CLEARING_NO, i, account.getClearingNo());
            setAccountInformation(BANK_ACCOUNT_NUMBER, i, account.getAccountNo());
            setAccountInformation(BANK_ACCOUNT_AMOUNT, i, account.getAmonut().toString());
        }
        ctx.putUserData(BANK_ACCOUNTS_COUNT, Objects.toString(accounts.size()));
    }

    private void setAccountInformation(String ctxId, int i, String value) {
        ctx.putUserData(String.format(ctxId, i), value);
    }

    private String getAccountName(int i) {
        return ctx.getDataEntry(String.format(BANK_ACCOUNT_NAME, i));
    }

    private String getAccountClearingNo(int i) {
        return ctx.getDataEntry(String.format(BANK_ACCOUNT_CLEARING_NO, i));
    }

    private String getAccountNo(int i) {
        return ctx.getDataEntry(String.format(BANK_ACCOUNT_NUMBER, i));
    }

    private Long getAccountAmount(int i) {
        return Long.parseLong(ctx.getDataEntry(String.format(BANK_ACCOUNT_AMOUNT, i)));
    }

    public void selectBank(String shortName, String fullName) {
        ctx.putUserData(BANK_SHORT, shortName);
        ctx.putUserData(BANK_FULL, fullName);
    }

    public String getBankFullName() {
        return ctx.getDataEntry(BANK_FULL);
    }

    public String getBankShort() {
        return ctx.getDataEntry(BANK_SHORT);
    }
}
