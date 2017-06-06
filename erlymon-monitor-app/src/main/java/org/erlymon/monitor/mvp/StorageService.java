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
package org.erlymon.monitor.mvp;

import android.content.Context;
import android.database.Cursor;

import com.fernandocejas.frodo.annotation.RxLogObservable;
import com.pushtorefresh.storio.sqlite.StorIOSQLite;
import com.pushtorefresh.storio.sqlite.impl.DefaultStorIOSQLite;
import com.pushtorefresh.storio.sqlite.operations.put.PutResults;
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;

import org.erlymon.monitor.mvp.model.DbOpenHelper;
import org.erlymon.monitor.mvp.model.Device;
import org.erlymon.monitor.mvp.model.DevicesTable;
import org.erlymon.monitor.mvp.model.Position;
import org.erlymon.monitor.mvp.model.PositionsTable;
import org.erlymon.monitor.mvp.model.Server;
import org.erlymon.monitor.mvp.model.ServerSQLiteTypeMapping;
import org.erlymon.monitor.mvp.model.ServersTable;
import org.erlymon.monitor.mvp.model.User;
import org.erlymon.monitor.mvp.model.UserSQLiteTypeMapping;
import org.erlymon.monitor.mvp.model.UsersTable;

import java.util.Arrays;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func3;

/**
 * Created by sergey on 21.03.17.
 */

public class StorageService {
    private StorIOSQLite storIOSQLite;

    public StorageService(Context context) {
        storIOSQLite = DefaultStorIOSQLite.builder()
                .sqliteOpenHelper(new DbOpenHelper(context))
                .addTypeMapping(Server.class, new ServerSQLiteTypeMapping())
                .addTypeMapping(User.class, new UserSQLiteTypeMapping())
                .addTypeMapping(Device.class, new DevicesTable.DeviceSQLiteTypeMapping())
                .addTypeMapping(Position.class, new PositionsTable.PositionSQLiteTypeMapping())
                .build();
    }

    @RxLogObservable
    public Observable<Server> getServer() {
        return storIOSQLite
                .get()
                .object(Server.class)
                .withQuery(ServersTable.QUERY_ALL)
                .prepare()
                .asRxObservable();
    }

    @RxLogObservable
    public Observable<Server> saveServer(Server server) {
        return storIOSQLite
                .put()
                .object(server)
                .prepare()
                .asRxObservable()
                .map(devicePutResult -> server);
    }

    @RxLogObservable
    public Observable<Cursor> getDevicesCursor() {
        return storIOSQLite
                .get()
                .cursor()
                .withQuery(DevicesTable.QUERY_ALL)
                .prepare()
                .asRxObservable();
    }

    @RxLogObservable
    public Observable<List<Device>> getDevicesList() {
        return storIOSQLite
                .get()
                .listOfObjects(Device.class)
                .withQuery(DevicesTable.QUERY_ALL)
                .prepare()
                .asRxObservable();
    }


    @RxLogObservable
    public Observable<List<Position>> getPositions(List<Long> ids) {
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < ids.size(); i ++) {
            builder.append("_id = ?");
            if (i != ids.size() - 1) {
                builder.append(" OR ");
            }
        }

        return storIOSQLite
                .get()
                .listOfObjects(Position.class)
                .withQuery(Query.builder().table(PositionsTable.TABLE)
                        .where(builder.toString())
                        .whereArgs(ids)
                        .build()
                )
                .prepare()
                .asRxObservable();
    }

    @RxLogObservable
    public Observable<List<Device>> saveDevices(List<Device> devices) {
        return storIOSQLite
                .put()
                .objects(devices)
                .prepare()
                .asRxObservable()
                .map(devicePutResults -> devices);
    }

    @RxLogObservable
    public Observable<Device> saveDevice(Device device) {
        return storIOSQLite
                .put()
                .object(device)
                .prepare()
                .asRxObservable()
                .map(devicePutResult -> device);
    }

    @RxLogObservable
    public Observable<User> getUser(long id) {
        return storIOSQLite
                .get()
                .object(User.class)
                .withQuery(Query.builder()
                        .table(UsersTable.TABLE)
                        .where("_id = ?")
                        .whereArgs(id)
                        .build()
                )
                .prepare()
                .asRxObservable();
    }

    @RxLogObservable
    public Observable<Cursor> getUsers() {
        return storIOSQLite
                .get()
                .cursor()
                .withQuery(UsersTable.QUERY_ALL)
                .prepare()
                .asRxObservable();
    }

    @RxLogObservable
    public Observable<List<User>> saveUsers(List<User> users) {
        return storIOSQLite
                .put()
                .objects(users)
                .prepare()
                .asRxObservable()
                .map(devicePutResults -> users);
    }

    @RxLogObservable
    public Observable<User> saveUser(User user) {
        return storIOSQLite
                .put()
                .object(user)
                .prepare()
                .asRxObservable()
                .map(devicePutResult -> user);
    }

    @RxLogObservable
    public Observable<Void> deleteUser(long id) {
        return storIOSQLite
                .delete()
                .byQuery(DeleteQuery.builder()
                        .table(UsersTable.TABLE)
                        .where("_id = ?")
                        .whereArgs(id)
                        .build()
                )
                .prepare()
                .asRxObservable()
                .map(deleteResult -> null);
    }

    @RxLogObservable
    public Observable<Device> getDevice(long id) {
        return storIOSQLite
                .get()
                .object(Device.class)
                .withQuery(Query.builder()
                        .table(DevicesTable.TABLE)
                        .where("_id = ?")
                        .whereArgs(id)
                        .build()
                )
                .prepare()
                .asRxObservable();
    }

    @RxLogObservable
    public Observable<Void> deleteDevice(long id) {
        return storIOSQLite
                .delete()
                .byQuery(DeleteQuery.builder()
                        .table(DevicesTable.TABLE)
                        .where("_id = ?")
                        .whereArgs(id)
                        .build()
                )
                .prepare()
                .asRxObservable()
                .map(deleteResult -> null);
    }

    @RxLogObservable
    public Observable<List<Position>> savePositions(List<Position> positions) {
        return storIOSQLite
                .put()
                .objects(positions)
                .prepare()
                .asRxObservable()
                .map(positionsPutResults -> positions);
    }

    @RxLogObservable
    public Observable<Void> drop() {
        Observable<Object> serverObserver = storIOSQLite
                .executeSQL()
                .withQuery(ServersTable.QUERY_DROP)
                .prepare()
                .asRxObservable();

        Observable<Object> usersObserver = storIOSQLite
                .executeSQL()
                .withQuery(UsersTable.QUERY_DROP)
                .prepare()
                .asRxObservable();

        Observable<Object> devicesObserver = storIOSQLite
                .executeSQL()
                .withQuery(DevicesTable.QUERY_DROP)
                .prepare()
                .asRxObservable();

        return Observable.zip(
                serverObserver,
                usersObserver,
                devicesObserver,
                new Func3<Object, Object, Object, Void>() {
                    @Override
                    public Void call(Object o, Object o2, Object o3) {
                        return null;
                    }
                }
        )
                .onErrorReturn(throwable -> null);
    }
}
