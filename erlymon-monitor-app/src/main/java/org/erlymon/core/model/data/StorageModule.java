package org.erlymon.core.model.data;

import android.content.Context;

import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite;

/**
 * Created by sergey on 1/7/17.
 */

public class StorageModule {
    private static StorageModule ourInstance = new StorageModule();
    private StorIOSQLite storIOSQLite;

    public synchronized static StorageModule getInstance() {
        return ourInstance;
    }

    private StorageModule() {}

    public void init(Context context) {
        storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(new DbOpenHelper(context))
                .addTypeMapping(Server.class, new ServerSQLiteTypeMapping())
                .addTypeMapping(User.class, new UserSQLiteTypeMapping())
                .addTypeMapping(Device.class, new DevicesTable.DeviceSQLiteTypeMapping())
                .addTypeMapping(Position.class, new PositionsTable.PositionSQLiteTypeMapping())
                .build();
    }

    public StorIOSQLite getStorage() {
        return storIOSQLite;
    }
}
