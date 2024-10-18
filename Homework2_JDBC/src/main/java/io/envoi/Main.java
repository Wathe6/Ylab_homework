package io.envoi;

import io.envoi.dao.AccountDAO;
import io.envoi.model.Account;

import java.util.List;

public class Main
{
    public static void main(String[] args)
    {
        AccountDAO accountDao = new AccountDAO();
        List<Account> accounts = accountDao.getAll();
        System.out.println(accounts);
    }
}