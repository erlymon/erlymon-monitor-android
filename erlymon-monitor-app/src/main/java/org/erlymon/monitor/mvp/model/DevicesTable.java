/*
 * Copyright (c) 2016, Sergey Penkovsky <sergey.penkovsky@gmail.com>
 *
 * This file is part of Erlymon Monitor.
 *
 * Erlymon Monitor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Erlymon Monitor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Erlymon Monitor.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.erlymon.monitor.mvp.model;

import android.content.ContentValues;
import android.database.Cursor;
import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.SQLiteTypeMapping;
import com.pushtorefresh.storio.sqlite.operations.delete.DefaultDeleteResolver;
import com.pushtorefresh.storio.sqlite.operations.get.DefaultGetResolver;
import com.pushtorefresh.storio.sqlite.operations.put.DefaultPutResolver;
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.InsertQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;
import com.pushtorefresh.storio.sqlite.queries.RawQuery;
import com.pushtorefresh.storio.sqlite.queries.UpdateQuery;


import java.util.Date;

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 1/7/17.
 */
public class DevicesTable {

    @NonNull
    public static final String TABLE = "devices";

    @NonNull
    public static final String COLUMN_ID = "_id";

    @NonNull
    public static final String COLUMN_NAME = "name";

    @NonNull
    public static final String COLUMN_UNIQUE_ID = "unique_id";

    @NonNull
    public static final String COLUMN_STATUS = "status";

    @NonNull
    public static final String COLUMN_LAST_UPDATE = "last_update";

    @NonNull
    public static final String COLUMN_POSITION_ID = "position_id";


    // Yep, with StorIO you can safely store queries as objects and reuse them, they are immutable
    @NonNull
    public static final Query QUERY_ALL = Query.builder()
            .table(TABLE)
            .orderBy("name")
            .build();

    @NonNull
    public static final RawQuery QUERY_DROP = RawQuery.builder()
            .query("DELETE FROM " + TABLE + ";")
            .build();

    // This is just class with Meta Data, we don't need instances
    private DevicesTable() {
        throw new IllegalStateException("No instances please");
    }

    @NonNull
    public static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE + "("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY, "
                + COLUMN_NAME + " TEXT NOT NULL, "
                + COLUMN_UNIQUE_ID + " TEXT NOT NULL, "
                + COLUMN_STATUS + " TEXT DEFAULT NULL, "
                + COLUMN_LAST_UPDATE + " NUMERIC DEFAULT NULL, "
                + COLUMN_POSITION_ID + " INTEGER DEFAULT NULL"
                + ");";
    }

    public static class DeviceSQLiteTypeMapping extends SQLiteTypeMapping<Device> {
        public DeviceSQLiteTypeMapping() {
            super(new DeviceStorIOSQLitePutResolver(),
                    new DeviceStorIOSQLiteGetResolver(),
                    new DeviceStorIOSQLiteDeleteResolver());
        }
    }

    private static class DeviceStorIOSQLitePutResolver extends DefaultPutResolver<Device> {
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
            contentValues.put("last_update", object.lastUpdate == null ? null : object.lastUpdate.getTime());
            contentValues.put("position_id", object.positionId);

            return contentValues;
        }
    }

    private static  class DeviceStorIOSQLiteGetResolver extends DefaultGetResolver<Device> {
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
            if(!cursor.isNull(cursor.getColumnIndex("last_update"))) {
                object.lastUpdate = new Date(cursor.getLong(cursor.getColumnIndex("last_update")));
            }
            if(!cursor.isNull(cursor.getColumnIndex("position_id"))) {
                object.positionId = cursor.getLong(cursor.getColumnIndex("position_id"));
            }

            return object;
        }
    }

    private static class DeviceStorIOSQLiteDeleteResolver extends DefaultDeleteResolver<Device> {
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
