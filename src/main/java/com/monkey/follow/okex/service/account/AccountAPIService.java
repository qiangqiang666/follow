package com.monkey.follow.okex.service.account;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.monkey.follow.okex.bean.account.param.Transfer;
import com.monkey.follow.okex.bean.account.param.Withdraw;
import com.monkey.follow.okex.bean.account.result.Currency;
import com.monkey.follow.okex.bean.account.result.Wallet;
import com.monkey.follow.okex.bean.account.result.WithdrawFee;

import java.math.BigDecimal;
import java.util.List;


public interface AccountAPIService {

    List<Wallet> getWallet();

    List<Wallet> getWallet(String currency);

    JSONObject transfer(Transfer transfer);

    JSONObject withdraw(Withdraw withdraw);

    List<Currency> getCurrencies();

    //
    JSONArray getLedger(String type, String currency, String before, String after, String limit);



    JSONArray getDepositAddress(String currency);

    List<WithdrawFee> getWithdrawFee(String currency);

    JSONArray getOnHold(String currency);

    JSONObject lock(String currency, BigDecimal amount);

    JSONObject unlock(String currency, BigDecimal amount);

    JSONArray getDepositHistory();

    JSONArray getDepositHistory(String currency);
    //查看所有提币记录
    JSONArray getWithdrawalHistory();
    //查看单个提币记录
    JSONArray getWithdrawalHistory(String currency);

    //获取子账户余额
    JSONObject getSubAccount(String sub_account);

    JSONObject getAllAccount(String account_type, String valuation_currency);

}
