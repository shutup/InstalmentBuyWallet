package com.shutup.alltokenwallet.base;

import android.app.Application;

import io.realm.Realm;

public class AllCoinWalletApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Realm.init(getApplicationContext());
    }
}
