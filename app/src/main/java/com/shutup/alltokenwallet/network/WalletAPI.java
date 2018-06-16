package com.shutup.alltokenwallet.network;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WalletAPI {

    static WalletService mWalletService = null;

    public static WalletService getInstance() {
        if (mWalletService == null) {
            if (mWalletService == null) synchronized (WalletService.class) {

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl("http://192.168.4.134:8080/")
//                        .baseUrl("http://1.11.1.1")
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                mWalletService = retrofit.create(WalletService.class);
            }
            else {
                return mWalletService;
            }
        }
        return mWalletService;
    }

}
