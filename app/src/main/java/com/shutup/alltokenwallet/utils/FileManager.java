package com.shutup.alltokenwallet.utils;

import android.os.Environment;

import java.io.File;

public class FileManager {
    static String Accounts_DIR = "all_coin_wallet_accounts";

    private static boolean isSDCardAvailable() {
        String state = Environment.getExternalStorageState();
        if (state.equals(Environment.MEDIA_MOUNTED)) {
            return true;
        }
        return false;
    }

    private static File getSDCardPath() {
        if (isSDCardAvailable()) {
            return Environment.getExternalStorageDirectory();
        }
        return null;
    }


    private static boolean createDir(String dirName) {
        if (isSDCardAvailable()) {
            File file = new File(getSDCardPath(), dirName);
            if (file.exists()) {

            } else {
                return file.mkdir();
            }
        }
        return false;
    }

    public static File getAccounts_DIR() {
        createDir(Accounts_DIR);
        return new File(getSDCardPath(), Accounts_DIR);
    }

    public static boolean saveAccount() {

        return false;
    }

    public String getAccountFilePathToSdcard(String fileName) {
        File dir = new File(getSDCardPath(), Accounts_DIR);
        return new File(dir, fileName).getAbsolutePath();
    }
}
