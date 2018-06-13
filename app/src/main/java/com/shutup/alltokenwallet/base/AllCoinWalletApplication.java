package com.shutup.alltokenwallet.base;

import android.support.multidex.MultiDexApplication;

import io.realm.Realm;

public class AllCoinWalletApplication extends MultiDexApplication {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(getApplicationContext());
    }
}
