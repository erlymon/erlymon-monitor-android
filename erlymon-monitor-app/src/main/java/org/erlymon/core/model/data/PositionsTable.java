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
package org.erlymon.core.model.data;

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

import org.jetbrains.annotations.NotNull;

import java.util.Date;

/**
 * Created by sergey on 1/7/17.
 */
/*
{
"address":"Rue de Hellen, Lanvéoc, Châteaulin, Finistère, Brittany, 29160, France",
"altitude":65.0,
"attributes":{
"battery":0.0,
"hdop":0.0
},
"course":0.0,
"deviceId":975311244721490,
"deviceTime":"2017-01-03T18:28:29.000+0000",
"fixTime":"2017-01-03T18:28:29.000+0000",
"id":3815366961392973,
"latitude":48.279862,
"longitude":-4.463134,
"outdated":false,
"protocol":"osmand",
"serverTime":"2017-01-03T18:28:29.000+0000",
"speed":0.0,
"type":"",
"valid":false
}
 */
public class PositionsTable {

    @NonNull
    public static final String TABLE = "positions";

    @NonNull
    public static final String COLUMN_ID = "_id";

    @NonNull
    public static final String COLUMN_PROTOCOL = "protocol";

    @NonNull
    public static final String COLUMN_DEVICE_ID = "device_id";

    @NonNull
    public static final String COLUMN_SERVER_TIME = "server_time";

    @NonNull
    public static final String COLUMN_DEVICE_TIME = "device_time";

    @NonNull
    public static final String COLUMN_FIX_TIME = "fix_time";

    @NonNull
    public static final String COLUMN_OUTDATED = "outdated";

    @NonNull
    public static final String COLUMN_VALID = "valid";

    @NonNull
    public static final String COLUMN_LATITUDE = "latitude";

    @NonNull
    public static final String COLUMN_LONGITUDE = "longitude";

    @NonNull
    public static final String COLUMN_ALTITUDE = "altitude";

    @NonNull
    public static final String COLUMN_SPEED = "speed";

    @NonNull
    public static final String COLUMN_COURSE = "course";

    @NonNull
    public static final String COLUMN_ADDRESS = "address";

    @NonNull
    public static final String COLUMN_ATTRIBUTES = "attributes";

    @NonNull
    public static final Query QUERY_ALL = Query.builder()
            .table(TABLE)
            .build();

    @NotNull
    public static final RawQuery QUERY_DROP = RawQuery.builder()
            .query("DELETE FROM " + TABLE + ";")
            .build();

    // This is just class with Meta Data, we don't need instances
    private PositionsTable() {
        throw new IllegalStateException("No instances please");
    }

    @NonNull
    public static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE + "("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY, "
                + COLUMN_PROTOCOL + " STRING NOT NULL, "
                + COLUMN_DEVICE_ID + " INTEGER NOT NULL, "
                + COLUMN_SERVER_TIME + " STRING DEFAULT NULL, "
                + COLUMN_DEVICE_TIME + " INTEGER DEFAULT NULL, "
                + COLUMN_FIX_TIME + " INTEGER DEFAULT NULL, "
                + COLUMN_VALID + " INTEGER DEFAULT NULL, "
                + COLUMN_LATITUDE + " REAL DEFAULT NULL, "
                + COLUMN_LONGITUDE + " REAL DEFAULT NULL, "
                + COLUMN_ALTITUDE + " REAL DEFAULT NULL, "
                + COLUMN_SPEED + " REAL DEFAULT NULL, "
                + COLUMN_COURSE + " REAL DEFAULT NULL, "
                + COLUMN_ADDRESS + " STRING DEFAULT NULL, "
                + COLUMN_ATTRIBUTES + " STRING DEFAULT NULL"
                + ");";
    }

    public static class PositionSQLiteTypeMapping extends SQLiteTypeMapping<Position> {
        public PositionSQLiteTypeMapping() {
            super(new PositionsTable.PositionStorIOSQLitePutResolver(),
                    new PositionsTable.PositionStorIOSQLiteGetResolver(),
                    new PositionsTable.PositionStorIOSQLiteDeleteResolver());
        }
    }

    private static class PositionStorIOSQLitePutResolver extends DefaultPutResolver<Position> {
        /**
         * {@inheritDoc}
         */
        @Override
        @NonNull
        public InsertQuery mapToInsertQuery(@NonNull Position object) {
            return InsertQuery.builder()
                    .table("positions")
                    .build();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @NonNull
        public UpdateQuery mapToUpdateQuery(@NonNull Position object) {
            return UpdateQuery.builder()
                    .table("positions")
                    .where("_id = ?")
                    .whereArgs(object.id)
                    .build();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @NonNull
        public ContentValues mapToContentValues(@NonNull Position object) {
            ContentValues contentValues = new ContentValues(14);

            contentValues.put("_id", object.id);
            contentValues.put("protocol", object.protocol);
            contentValues.put("device_id", object.deviceId);
            contentValues.put("server_time", object.serverTime.getTime());
            contentValues.put("device_time", object.deviceTime.getTime());
            contentValues.put("fix_time", object.fixTime.getTime());
            contentValues.put("outdated", object.outdated);
            contentValues.put("valid", object.valid);
            contentValues.put("latitude", object.latitude);
            contentValues.put("longitude", object.longitude);
            contentValues.put("altitude", object.altitude);
            contentValues.put("speed", object.speed);
            contentValues.put("course", object.course);
            contentValues.put("address", object.address);

            return contentValues;
        }
    }

    private static class PositionStorIOSQLiteGetResolver extends DefaultGetResolver<Position> {
        /**
         * {@inheritDoc}
         */
        @Override
        @NonNull
        public Position mapFromCursor(@NonNull Cursor cursor) {
            Position object = new Position();

            if(!cursor.isNull(cursor.getColumnIndex("_id"))) {
                object.id = cursor.getLong(cursor.getColumnIndex("_id"));
            }
            object.protocol = cursor.getString(cursor.getColumnIndex("protocol"));
            if(!cursor.isNull(cursor.getColumnIndex("device_id"))) {
                object.deviceId = cursor.getLong(cursor.getColumnIndex("device_id"));
            }
            if(!cursor.isNull(cursor.getColumnIndex("server_time"))) {
                object.serverTime = new Date(cursor.getLong(cursor.getColumnIndex("server_time")));
            }
            if(!cursor.isNull(cursor.getColumnIndex("device_time"))) {
                object.deviceTime = new Date(cursor.getLong(cursor.getColumnIndex("device_time")));
            }
            if(!cursor.isNull(cursor.getColumnIndex("fix_time"))) {
                object.fixTime = new Date(cursor.getLong(cursor.getColumnIndex("fix_time")));
            }
            if(!cursor.isNull(cursor.getColumnIndex("outdated"))) {
                object.outdated = cursor.getInt(cursor.getColumnIndex("outdated")) == 1;
            }
            if(!cursor.isNull(cursor.getColumnIndex("valid"))) {
                object.valid = cursor.getInt(cursor.getColumnIndex("valid")) == 1;
            }
            if(!cursor.isNull(cursor.getColumnIndex("latitude"))) {
                object.latitude = cursor.getDouble(cursor.getColumnIndex("latitude"));
            }
            if(!cursor.isNull(cursor.getColumnIndex("longitude"))) {
                object.longitude = cursor.getDouble(cursor.getColumnIndex("longitude"));
            }
            if(!cursor.isNull(cursor.getColumnIndex("altitude"))) {
                object.altitude = cursor.getDouble(cursor.getColumnIndex("altitude"));
            }
            if(!cursor.isNull(cursor.getColumnIndex("speed"))) {
                object.speed = cursor.getFloat(cursor.getColumnIndex("speed"));
            }
            if(!cursor.isNull(cursor.getColumnIndex("course"))) {
                object.course = cursor.getFloat(cursor.getColumnIndex("course"));
            }
            object.address = cursor.getString(cursor.getColumnIndex("address"));

            return object;
        }
    }

    private static class PositionStorIOSQLiteDeleteResolver extends DefaultDeleteResolver<Position> {
        /**
         * {@inheritDoc}
         */
        @Override
        @NonNull
        public DeleteQuery mapToDeleteQuery(@NonNull Position object) {
            return DeleteQuery.builder()
                    .table("positions")
                    .where("_id = ?")
                    .whereArgs(object.id)
                    .build();
        }
    }

}
