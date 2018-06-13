package com.shutup.alltokenwallet.db;

import io.realm.Realm;
import io.realm.RealmConfiguration;

public class RealmManager {
    public static Realm getRealmInstance() {
        RealmConfiguration config = new RealmConfiguration.Builder()
                .schemaVersion(1)
                .migration(new MyMigration())
                .build();
        return Realm.getInstance(config);
    }
}


