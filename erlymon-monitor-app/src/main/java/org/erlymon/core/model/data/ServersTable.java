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
import com.pushtorefresh.storio.sqlite.queries.RawQuery;

import org.jetbrains.annotations.NotNull;

/**
 * Created by sergey on 1/7/17.
 */
public class ServersTable {

    @NonNull
    public static final String TABLE = "servers";

    @NonNull
    public static final String COLUMN_ID = "_id";

    @NonNull
    public static final String COLUMN_REGISTRATION = "registration";

    @NonNull
    public static final String COLUMN_LATITUDE = "latitude";

    @NonNull
    public static final String COLUMN_LONGITUDE = "longitude";

    @NonNull
    public static final String COLUMN_ZOOM = "zoom";

    @NonNull
    public static final String COLUMN_MAP = "map";

    @NonNull
    public static final String COLUMN_LANGUAGE = "language";

    @NonNull
    public static final String COLUMN_DISTANCE_UNIT = "distance_unit";

    @NonNull
    public static final String COLUMN_SPEED_UNIT = "speed_unit";

    @NonNull
    public static final String COLUMN_BING_KEY = "bing_key";

    @NonNull
    public static final String COLUMN_MAP_URL = "map_url";

    @NonNull
    public static final String COLUMN_READONLY = "readonly";

    @NonNull
    public static final String COLUMN_TWELVE_HOUR_FORMAT = "twelve_hour_format";


    // Yep, with StorIO you can safely store queries as objects and reuse them, they are immutable
    @NonNull
    public static final Query QUERY_ALL = Query.builder()
            .table(TABLE)
            .build();

    @NotNull
    public static final RawQuery QUERY_DROP = RawQuery.builder()
            .query("DELETE FROM " + TABLE + ";")
            .build();

    // This is just class with Meta Data, we don't need instances
    private ServersTable() {
        throw new IllegalStateException("No instances please");
    }

    @NonNull
    public static String getCreateTableQuery() {
        return "CREATE TABLE " + TABLE + "("
                + COLUMN_ID + " INTEGER NOT NULL PRIMARY KEY, "
                + COLUMN_REGISTRATION + " INTEGER NOT NULL, "
                + COLUMN_LATITUDE + " REAL NOT NULL, "
                + COLUMN_LONGITUDE + " REAL NOT NULL, "
                + COLUMN_ZOOM + " INTEGER NOT NULL, "
                + COLUMN_MAP + " STRING DEFAULT NULL, "
                + COLUMN_LANGUAGE + " STRING NOT NULL, "
                + COLUMN_DISTANCE_UNIT + " STRING DEFAULT NULL, "
                + COLUMN_SPEED_UNIT + " STRING DEFAULT NULL, "
                + COLUMN_BING_KEY + " STRING DEFAULT NULL, "
                + COLUMN_MAP_URL + " STRING NOT NULL, "
                + COLUMN_READONLY + " INTEGER NOT NULL, "
                + COLUMN_TWELVE_HOUR_FORMAT + " INTEGER DEFAULT NULL"
                + ");";
    }
}
