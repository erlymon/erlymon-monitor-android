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

import com.appunite.websocket.rx.object.MoreObservables;
import com.appunite.websocket.rx.object.RxObjectWebSockets;
import com.appunite.websocket.rx.object.messages.RxObjectEventMessage;
import com.fernandocejas.frodo.annotation.RxLogObservable;
import com.google.gson.Gson;

import org.erlymon.monitor.api.RestApi;
import org.erlymon.monitor.api.util.QueryDate;
import org.erlymon.monitor.mvp.model.Command;
import org.erlymon.monitor.mvp.model.Device;
import org.erlymon.monitor.mvp.model.Event;
import org.erlymon.monitor.mvp.model.Permission;
import org.erlymon.monitor.mvp.model.Position;
import org.erlymon.monitor.mvp.model.Server;
import org.erlymon.monitor.mvp.model.User;

import java.util.Date;
import java.util.List;

import rx.Observable;

/**
 * Created by sergey on 21.03.17.
 */

public class NetworkService {
    private RestApi mRestApi;
    private RxObjectWebSockets mRxObjectWebSockets;
    private Gson mGson;

    public NetworkService(RestApi restApi, RxObjectWebSockets rxObjectWebSockets, Gson gson) {
        mRestApi = restApi;
        mRxObjectWebSockets = rxObjectWebSockets;
        mGson = gson;
    }

    @RxLogObservable
    public Observable<Server> getServer() {
        return mRestApi.getServer();
    }

    @RxLogObservable
    public Observable<Server> updateServer(Server server) {
        return mRestApi.updateServer(server);
    }

    @RxLogObservable
    public Observable<User> createSession(String email, String password) {
        return mRestApi.createSession(email, password);
    }

    @RxLogObservable
    public Observable<Void> deleteSession() {
        return mRestApi.deleteSession()
                .onErrorReturn(throwable -> null);
    }

    @RxLogObservable
    public Observable<User> getSession() {
        return mRestApi.getSession();
    }

    @RxLogObservable
    public Observable<User> createUser(User user) {
        return mRestApi.createUser(user);
    }

    @RxLogObservable
    public Observable<User> updateUser(User user) {
        return mRestApi.updateUser(user.getId(), user);
    }

    @RxLogObservable
    public Observable<Void> deleteUser(long id) {
        return mRestApi.deleteUser(id);
    }

    @RxLogObservable
    public Observable<List<Device>> getDevices() {
        return mRestApi.getDevices();
    }

    @RxLogObservable
    public Observable<List<Device>> getDevices(boolean all) {
        return mRestApi.getDevices(all);
    }

    @RxLogObservable
    public Observable<List<Device>> getDevices(long userId) {
        return mRestApi.getDevices(userId);
    }

    @RxLogObservable
    public Observable<List<User>> getUsers() {
        return mRestApi.getUsers();
    }

    @RxLogObservable
    public Observable<Device> createDevice(Device device) {
        return mRestApi.createDevice(device);
    }

    @RxLogObservable
    public Observable<Device> updateDevice(Device device) {
        return mRestApi.updateDevice(device.getId(), device);
    }

    @RxLogObservable
    public Observable<Void> deleteDevice(long id) {
        return mRestApi.deleteDevice(id);
    }

    @RxLogObservable
    public Observable<Void> createPermission(Permission permission) {
        return mRestApi.createPermission(permission);
    }

    @RxLogObservable
    public Observable<Void> deletePermission(Permission permission) {
        return mRestApi.deletePermission(permission);
    }

    @RxLogObservable
    public Observable<Void> createCommand(Command command) {
        return mRestApi.createCommand(command);
    }

    @RxLogObservable
    public Observable<List<Position>> getPositions(long deviceId, Date from, Date to) {
        return mRestApi.getPositions(deviceId, new QueryDate(from), new QueryDate(to));
    }

    @RxLogObservable
    public Observable<Event> openWebSocket() {
        return mRxObjectWebSockets
                .webSocketObservable()
                .compose(MoreObservables.filterAndMap(RxObjectEventMessage.class))
                .compose(RxObjectEventMessage.filterAndMap(Event.class));
    }
}
