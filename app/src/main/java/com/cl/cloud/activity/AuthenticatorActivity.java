package com.cl.cloud.activity;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.cl.cloud.R;
import com.cl.cloud.app.Constant;
import com.cl.cloud.service.AccountProvider;

/**
 * Created by work2 on 2019/4/25.
 */

public class AuthenticatorActivity extends AppCompatActivity {

    // TYPE必须与 authenticator.xml 中的TYPE保持一致
    public static final String ACCOUNT_TYPE  = "com.cl.cloud.account.type";

    private static final long INTERVAL_TIME = Constant.SECOND_30;

    private AccountManager mAccountManager;
    private Account mAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authenticator);

        mAccountManager = (AccountManager) getSystemService(ACCOUNT_SERVICE);
        assert mAccountManager != null;
        Account[] accounts = mAccountManager.getAccountsByType(ACCOUNT_TYPE);
        if(accounts.length > 0){ // 已经添加了账户
            mAccount = accounts[0];
            finish();
        }

        Button btnAddAccount = (Button)findViewById(R.id.btn_add_account);
        btnAddAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mAccount = new Account(getString(R.string.account_name), ACCOUNT_TYPE);
                mAccountManager.addAccountExplicitly(mAccount, null, null);

                // 自动同步
                Bundle bundle = new Bundle();
                ContentResolver.setIsSyncable(mAccount, AccountProvider.AUTHORITY, 1);
                ContentResolver.setSyncAutomatically(mAccount, AccountProvider.AUTHORITY,true);
                ContentResolver.addPeriodicSync(mAccount, AccountProvider.AUTHORITY, bundle, INTERVAL_TIME); // 间隔时间为30秒

                finish();
            }
        });
    }
}
