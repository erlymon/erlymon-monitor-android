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

import android.support.annotation.NonNull;

import com.pushtorefresh.storio.sqlite.queries.Query;

/**
 * Created by sergey on 1/7/17.
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
            .build();

    // This is just class with Meta Data, we don't need instances
    private DevicesTable() {
        throw new IllegalStateException("No instances please");
    }

    @NonNull
    public static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE + "("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY, "
                + COLUMN_NAME + " STRING NOT NULL, "
                + COLUMN_UNIQUE_ID + " STRING NOT NULL, "
                + COLUMN_STATUS + " STRING NOT NULL, "
                + COLUMN_LAST_UPDATE + " INTEGER NOT NULL, "
                + COLUMN_POSITION_ID + " INTEGER DEFAULT NULL"
                + ");";
    }
}
