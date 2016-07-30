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
package org.erlymon.core.model;

import android.content.Context;

import com.appunite.websocket.rx.messages.RxEvent;
import com.appunite.websocket.rx.object.messages.RxObjectEvent;

import org.erlymon.core.model.api.ApiModule;
import org.erlymon.core.model.api.util.QueryDate;
import org.erlymon.core.model.data.Command;
import org.erlymon.core.model.data.Device;
import org.erlymon.core.model.data.Permission;
import org.erlymon.core.model.data.Position;
import org.erlymon.core.model.data.Server;
import org.erlymon.core.model.data.User;

import java.util.Date;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 5/4/16.
 */
public class ModelImpl implements Model {
    ApiModule apiModule = ApiModule.getInstance();

    public ModelImpl(Context context) {
    }

    @Override
    public Observable<Server> getServer() {
        return apiModule.getApi().getServer();
    }

    @Override
    public Observable<Server> updateServer(Server server) {
        return apiModule.getApi().updateServer(server);
    }

    @Override
    public Observable<User> getSession() {
        return apiModule.getApi().getSession();
    }

    @Override
    public Observable<User> createSession(String email, String password) {
        return apiModule.getApi().createSession(email, password);
    }

    @Override
    public Observable<Void> deleteSession() {
        return apiModule.getApi().deleteSession();
    }

    @Override
    public Observable<User[]> getUsers() {
        return apiModule.getApi().getUsers();
    }

    @Override
    public Observable<User> createUser(User user) {
        return apiModule.getApi().createUser(user);
    }

    @Override
    public Observable<User> updateUser(long id, User user) {
        return apiModule.getApi().updateUser(id, user);
    }

    @Override
    public Observable<Void> deleteUser(long id) {
        return apiModule.getApi().deleteUser(id);
    }

    @Override
    public Observable<Device[]> getDevices() {
        return apiModule.getApi().getDevices();
    }

    @Override
    public Observable<Device[]> getDevices(boolean all) {
        return apiModule.getApi().getDevices(all);
    }

    @Override
    public Observable<Device[]> getDevices(long userId) {
        return apiModule.getApi().getDevices(userId);
    }

    @Override
    public Observable<Device> createDevice(Device device) {
        return apiModule.getApi().createDevice(device);
    }

    @Override
    public Observable<Device> updateDevice(long id, Device device) {
        return apiModule.getApi().updateDevice(id, device);
    }

    @Override
    public Observable<Void> deleteDevice(long id) {
        return apiModule.getApi().deleteDevice(id);
    }

    @Override
    public Observable<Position[]> getPositions(long deviceId, Date from, Date to) {
        return apiModule.getApi().getPositions(deviceId, new QueryDate(from), new QueryDate(to))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread());
    }

    @Override
    public Observable<Void> createCommand(Command command) {
        return apiModule.getApi().createCommand(command);
    }

    @Override
    public Observable<RxObjectEvent> openWebSocket() {
        return apiModule.createWebSocket()
                .webSocketObservable();
    }

    @Override
    public Observable<Void> createPermission(Permission permission) {
        return apiModule.getApi().createPermission(permission);
    }

    @Override
    public Observable<Void> deletePermission(Permission permission) {
        return apiModule.getApi().deletePermission(permission);
    }
}
