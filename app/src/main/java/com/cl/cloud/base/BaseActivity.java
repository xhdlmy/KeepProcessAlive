package com.cl.cloud.base;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

public abstract class BaseActivity extends AppCompatActivity {

    protected Context mContext;
    protected BaseActivity mActivity;
    protected String TAG;
    protected Handler mHandler;
    protected Intent mIntent;
    protected Bundle mBundle;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext = this;
        mActivity = this;
        setContentView(getResId());
        TAG = this.getClass().getSimpleName();
        mIntent = getIntent();
        mBundle = mIntent.getExtras();
        initView();
        initData();
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        mContext = null;
        mActivity = null;
        mHandler = null;
    }

    protected abstract int getResId();
    public abstract void initView();
    public abstract void initData();

    /*================= gotoActivity ===================*/

    public <T extends Activity> void goToActivity(Class<T> cls) {
        goToActivity(cls, true, null);
    }

    public <T extends Activity> void goToActivity(Class<T> cls, boolean isFinish) {
        goToActivity(cls, isFinish, null);
    }

    public <T extends Activity> void goToActivity(Class<T> cls, Bundle bundle) {
        goToActivity(cls, true, bundle);
    }

    public <T extends Activity> void goToActivity(Class<T> cls, boolean isFinish, Bundle bundle) {
        Intent intent = new Intent(this, cls);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        startActivity(intent);
        if (isFinish) {
            finish();
        }
    }

}
