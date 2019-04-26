package com.cl.cloud.service;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.app.Service;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.cl.cloud.R;
import com.cl.cloud.app.App;
import com.cl.cloud.app.Constant;
import com.cl.cloud.util.LogUtils;

/**
 * Android 8.0 自动同步功能已经关闭，需要点击进行手动同步
 */

public class AccountService extends Service {

    private static final String TYPE = "com.cl.cloud.account.type";
    private static final String PROVIDER = "com.cl.cloud.account.provider";
    private static final int INTERVAL_TIME = Constant.SECOND_30;

    private Account mAccount;

    private Context sContext = App.getContext();

    private boolean addAccount(){
        AccountManager am = (AccountManager) sContext.getSystemService(Context.ACCOUNT_SERVICE);
        assert am != null;
        Account[] accounts = am.getAccountsByType(TYPE);
        if(accounts.length > 0){
            mAccount = accounts[0];
        }else{
            mAccount = new Account(sContext.getString(R.string.account_name), TYPE);
        }
        return am.addAccountExplicitly(mAccount, null, null);
    }

    private void syncAccount(Bundle bundle){
        if(addAccount()){
            ContentResolver.setIsSyncable(mAccount, PROVIDER, 1);
            ContentResolver.setSyncAutomatically(mAccount, PROVIDER,true);
            ContentResolver.addPeriodicSync(mAccount, PROVIDER, bundle, INTERVAL_TIME);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        syncAccount(new Bundle());
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
