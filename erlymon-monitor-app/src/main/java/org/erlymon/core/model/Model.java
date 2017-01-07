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


import com.appunite.websocket.rx.messages.RxEvent;
import com.appunite.websocket.rx.object.messages.RxObjectEvent;

import org.erlymon.core.model.data.Command;
import org.erlymon.core.model.data.Device;
import org.erlymon.core.model.data.Permission;
import org.erlymon.core.model.data.Position;
import org.erlymon.core.model.data.Server;
import org.erlymon.core.model.data.User;

import java.util.Date;

import rx.Observable;

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 5/4/16.
 */
public interface Model {
    Observable<Server> getServer();
    Observable<Server> updateServer(Server server);

    Observable<User> getSession();
    Observable<User> createSession(String email, String password);
    Observable<Void> deleteSession();

    Observable<User[]> getUsers();

    Observable<User> createUser(User user);

    Observable<User> updateUser(long id, User user);

    Observable<Void> deleteUser(long id);

    Observable<Device[]> getDevices();

    Observable<Device[]> getDevices(boolean all);

    Observable<Device[]> getDevices(long userId);

    Observable<Device> createDevice(Device device);

    Observable<Device> updateDevice(long id, Device device);

    Observable<Void> deleteDevice(long id);

    Observable<Position[]> getPositions(long deviceId, Date from, Date to);

    Observable<Void> createCommand(Command command);

    Observable<RxObjectEvent> openWebSocket();

    Observable<Void> createPermission(Permission permission);

    Observable<Void> deletePermission(Permission permission);
}
