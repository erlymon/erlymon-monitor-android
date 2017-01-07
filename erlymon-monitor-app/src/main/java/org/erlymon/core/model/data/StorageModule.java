package org.erlymon.core.model.data;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.delete.DefaultDeleteResolver;
import com.pushtorefresh.storio.sqlite.operations.get.DefaultGetResolver;
import com.pushtorefresh.storio.sqlite.operations.put.DefaultPutResolver;
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.InsertQuery;
import com.pushtorefresh.storio.sqlite.queries.UpdateQuery;

import java.util.Date;

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
                .addTypeMapping(Device.class, new CustomDeviceSQLiteTypeMapping())
                .build();
    }

    public StorIOSQLite getStorage() {
        return storIOSQLite;
    }

    private class CustomDeviceSQLiteTypeMapping extends SQLiteTypeMapping<Device> {
        public CustomDeviceSQLiteTypeMapping() {
            super(new DeviceStorIOSQLitePutResolver(),
                    new DeviceStorIOSQLiteGetResolver(),
                    new DeviceStorIOSQLiteDeleteResolver());
        }
    }

    private class DeviceStorIOSQLitePutResolver extends DefaultPutResolver<Device> {
        /**
         * {@inheritDoc}
         */
        @Override
        @NonNull
        public InsertQuery mapToInsertQuery(@NonNull Device object) {
            return InsertQuery.builder()
                    .table("devices")
                    .build();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @NonNull
        public UpdateQuery mapToUpdateQuery(@NonNull Device object) {
            return UpdateQuery.builder()
                    .table("devices")
                    .where("_id = ?")
                    .whereArgs(object.id)
                    .build();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @NonNull
        public ContentValues mapToContentValues(@NonNull Device object) {
            ContentValues contentValues = new ContentValues(6);

            contentValues.put("_id", object.id);
            contentValues.put("name", object.name);
            contentValues.put("unique_id", object.uniqueId);
            contentValues.put("status", object.status);
            contentValues.put("last_update", object.lastUpdate == null ? 0 : object.lastUpdate.getTime());
            contentValues.put("position_id", object.positionId);

            return contentValues;
        }
    }

    private class DeviceStorIOSQLiteGetResolver extends DefaultGetResolver<Device> {
        /**
         * {@inheritDoc}
         */
        @Override
        @NonNull
        public Device mapFromCursor(@NonNull Cursor cursor) {
            Device object = new Device();

            if(!cursor.isNull(cursor.getColumnIndex("_id"))) {
                object.id = cursor.getLong(cursor.getColumnIndex("_id"));
            }
            object.name = cursor.getString(cursor.getColumnIndex("name"));
            object.uniqueId = cursor.getString(cursor.getColumnIndex("unique_id"));
            object.status = cursor.getString(cursor.getColumnIndex("status"));
            object.lastUpdate = cursor.getInt(cursor.getColumnIndex("last_update")) == 0 ? null : new Date(cursor.getInt(cursor.getColumnIndex("last_update")));
            if(!cursor.isNull(cursor.getColumnIndex("position_id"))) {
                object.positionId = cursor.getLong(cursor.getColumnIndex("position_id"));
            }

            return object;
        }
    }

    public class DeviceStorIOSQLiteDeleteResolver extends DefaultDeleteResolver<Device> {
        /**
         * {@inheritDoc}
         */
        @Override
        @NonNull
        public DeleteQuery mapToDeleteQuery(@NonNull Device object) {
            return DeleteQuery.builder()
                    .table("devices")
                    .where("_id = ?")
                    .whereArgs(object.id)
                    .build();
        }
    }
}
