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
import com.cl.cloud.activity.AuthenticatorActivity;
import com.cl.cloud.app.App;
import com.cl.cloud.app.Constant;
import com.cl.cloud.base.KeepAliveManager;
import com.cl.cloud.util.LogUtils;

/**
 * Created by work2 on 2019/4/25.
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
//        return getAccountAuthenticator().getIBinder();
        return null;
    }

    private AccountAuthenticator mAccountAuthenticator;

    public AccountAuthenticator getAccountAuthenticator(){
        if(mAccountAuthenticator == null) mAccountAuthenticator = new AccountAuthenticator(App.getContext());
        return mAccountAuthenticator;
    }

    public static class AccountAuthenticator extends AbstractAccountAuthenticator {

        public AccountAuthenticator(Context context) {
            super(context);
        }

        @Override
        public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
            return null;
        }

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
                ContentResolver.addPeriodicSync(mAccount, PROVIDER, bundle, INTERVAL_TIME); // 间隔时间为30秒
            }
        }

        @Override
        public Bundle addAccount(AccountAuthenticatorResponse response, String accountType, String authTokenType, String[] requiredFeatures, Bundle options) throws NetworkErrorException {
            LogUtils.i("AccountAuthenticator", "addAccount");
            final Bundle bundle = new Bundle();
            final Intent intent = new Intent(App.getContext(), AuthenticatorActivity.class);
            intent.putExtra(AccountManager.KEY_ACCOUNT_AUTHENTICATOR_RESPONSE, response);
            bundle.putParcelable(AccountManager.KEY_INTENT, intent);
//            Bundle bundle = new Bundle();
//            syncAccount(bundle);
            return bundle;
        }

        @Override
        public Bundle confirmCredentials(AccountAuthenticatorResponse response, Account account, Bundle options) throws NetworkErrorException {
            return null;
        }

        @Override
        public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
            return null;
        }

        @Override
        public String getAuthTokenLabel(String authTokenType) {
            return null;
        }

        @Override
        public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account, String authTokenType, Bundle options) throws NetworkErrorException {
            return null;
        }

        @Override
        public Bundle hasFeatures(AccountAuthenticatorResponse response, Account account, String[] features) throws NetworkErrorException {
            return null;
        }
    }

}
