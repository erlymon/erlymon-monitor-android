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

import android.database.Cursor;
import android.support.v4.util.Pair;

import com.appunite.websocket.rx.object.RxObjectWebSockets;
import com.fernandocejas.frodo.annotation.RxLogObservable;

import org.erlymon.monitor.mvp.common.Utils;
import org.erlymon.monitor.mvp.model.Command;
import org.erlymon.monitor.mvp.model.Device;
import org.erlymon.monitor.mvp.model.Event;
import org.erlymon.monitor.mvp.model.Permission;
import org.erlymon.monitor.mvp.model.Position;
import org.erlymon.monitor.mvp.model.Server;
import org.erlymon.monitor.mvp.model.User;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import timber.log.Timber;

/**
 * Created by sergey on 30.03.17.
 */

public class MainService {
    private NetworkService mNetworkService;
    private RxObjectWebSockets mRxObjectWebSockets;
    private StorageService mStorageService;

    public MainService(NetworkService networkService, RxObjectWebSockets rxObjectWebSockets, StorageService storageService) {
        mNetworkService = networkService;
        mRxObjectWebSockets = rxObjectWebSockets;
        mStorageService = storageService;
    }

    @RxLogObservable
    public Observable<User> signIn(String email, String password) {
        return mNetworkService.createSession(email, password)
                .flatMap(new Func1<User, Observable<User>>() {
                    @Override
                    public Observable<User> call(User user) {
                        if (user.getAdmin()) {
                            return Observable.zip(
                                    mNetworkService.getDevices().flatMap(new Func1<List<Device>, Observable<List<Device>>>() {
                                        @Override
                                        public Observable<List<Device>> call(List<Device> devices) {
                                            return mStorageService.saveDevices(devices);
                                        }
                                    }),
                                    mNetworkService.getUsers().flatMap(new Func1<List<User>, Observable<List<User>>>() {
                                        @Override
                                        public Observable<List<User>> call(List<User> users) {
                                            return mStorageService.saveUsers(users);
                                        }
                                    }),
                                    (devices, users) -> user
                            );
                        } else {
                            return mNetworkService.getDevices()
                                    .flatMap(new Func1<List<Device>, Observable<User>>() {
                                        @Override
                                        public Observable<User> call(List<Device> devices) {
                                            return mStorageService.saveDevices(devices).map(devices1 -> user);
                                        }
                                    });
                        }
                    }
                })
                .flatMap(new Func1<User, Observable<User>>() {
                    @Override
                    public Observable<User> call(User user) {
                        return mStorageService.saveUser(user);
                    }
                })
                .compose(Utils.applySchedulers())
                .doOnError(Timber::w);
    }

    @RxLogObservable
    public Observable<Void> signOut() {
        return mNetworkService.deleteSession()
                .flatMap(new Func1<Void, Observable<Void>>() {
                    @Override
                    public Observable<Void> call(Void aVoid) {
                        return mStorageService.drop();
                    }
                })
                .compose(Utils.applySchedulers())
                .doOnError(Timber::w);
    }

    @RxLogObservable
    public Observable<User> signUp(String name, String email, String password) {
        return mNetworkService.createUser(User.newUser(name, email, password))
                .compose(Utils.applySchedulers())
                .doOnError(Timber::w);
    }

    @RxLogObservable
    public Observable<User> checkSession() {
        return mNetworkService.getSession()
                .compose(Utils.applySchedulers())
                .doOnError(Timber::w);
    }

    @RxLogObservable
    public Observable<Server> getServer() {
        return mNetworkService.getServer()
                .flatMap(new Func1<Server, Observable<Server>>() {
                    @Override
                    public Observable<Server> call(Server server) {
                        return mStorageService.saveServer(server);
                    }
                })
                .compose(Utils.applySchedulers())
                .doOnError(Timber::w);
    }

    @RxLogObservable
    public Observable<Server> getServerByCache() {
        return mStorageService.getServer()
                .compose(Utils.applySchedulers())
                .doOnError(Timber::w);
    }

    @RxLogObservable
    public Observable<Server> updateServer(Server server) {
        return mNetworkService.updateServer(server)
                .flatMap(new Func1<Server, Observable<Server>>() {
                    @Override
                    public Observable<Server> call(Server server) {
                        return mStorageService.saveServer(server);
                    }
                })
                .compose(Utils.applySchedulers())
                .doOnError(Timber::w);
    }

    @RxLogObservable
    public Observable<Cursor> getDevicesCursor() {
        return mStorageService.getDevicesCursor()
                .compose(Utils.applySchedulers())
                .doOnError(Timber::w);
    }

    @RxLogObservable
    public Observable<List<Device>> getDevicesList() {
        return mStorageService.getDevicesList()
                .compose(Utils.applySchedulers())
                .doOnError(Timber::w);
    }

    @RxLogObservable
    public Observable<Pair<List<Device>, List<Position>>> getDevicesAndPositions() {
        return mStorageService.getDevicesList()
                .flatMap(new Func1<List<Device>, Observable<Pair<List<Device>, List<Position>>>>() {
                    @Override
                    public Observable<Pair<List<Device>, List<Position>>> call(List<Device> devices) {
                        List<Long> ids = new ArrayList<>();
                        for (Device device: devices) {
                            if (device.getPositionId() != null && device.getPositionId() > 0) {
                                ids.add(device.getPositionId());
                            }
                        }
                        return mStorageService.getPositions(ids)
                               .map(positions -> Pair.create(devices, positions));
                    }
                })
                .compose(Utils.applySchedulers())
                .doOnError(Timber::w);
    }

    @RxLogObservable
    public Observable<Pair<List<Device>, List<Device>>> getDevices(boolean admin, long userId) {
        return Observable.zip(
                mNetworkService.getDevices(admin),
                mNetworkService.getDevices(userId),
                Pair::new)
                .compose(Utils.applySchedulers())
                .doOnError(Timber::w);
    }

    @RxLogObservable
    public Observable<User> getUserByCache(long id) {
        return mStorageService.getUser(id)
                .compose(Utils.applySchedulers())
                .doOnError(Timber::w);
    }

    @RxLogObservable
    public Observable<Cursor> getUsers() {
        return mStorageService.getUsers()
                .compose(Utils.applySchedulers())
                .doOnError(Timber::w);
    }

    @RxLogObservable
    public Observable<User> updateUser(User user) {
        return mNetworkService.updateUser(user)
                .flatMap(new Func1<User, Observable<User>>() {
                    @Override
                    public Observable<User> call(User user) {
                        return mStorageService.saveUser(user);
                    }
                })
                .compose(Utils.applySchedulers())
                .doOnError(Timber::w);
    }

    @RxLogObservable
    public Observable<User> createUser(User user) {
        return mNetworkService.createUser(user)
                .flatMap(new Func1<User, Observable<User>>() {
                    @Override
                    public Observable<User> call(User user) {
                        return mStorageService.saveUser(user);
                    }
                })
                .compose(Utils.applySchedulers())
                .doOnError(Timber::w);
    }

    @RxLogObservable
    public Observable<Void> deleteUser(long id) {
        return mNetworkService.deleteUser(id)
                .flatMap(new Func1<Void, Observable<Void>>() {
                    @Override
                    public Observable<Void> call(Void avoid) {
                        return mStorageService.deleteUser(id);
                    }
                })
                .compose(Utils.applySchedulers())
                .doOnError(Timber::w);
    }

    @RxLogObservable
    public Observable<Device> getDeviceByCache(long id) {
        return mStorageService.getDevice(id)
                .compose(Utils.applySchedulers())
                .doOnError(Timber::w);
    }

    @RxLogObservable
    public Observable<Device> createDevice(Device device) {
        return mNetworkService.createDevice(device)
                .flatMap(new Func1<Device, Observable<Device>>() {
                    @Override
                    public Observable<Device> call(Device result) {
                        return mStorageService.saveDevice(result);
                    }
                })
                .compose(Utils.applySchedulers())
                .doOnError(Timber::w);
    }

    @RxLogObservable
    public Observable<Device> updateDevice(Device device) {
        return mNetworkService.updateDevice(device)
                .flatMap(new Func1<Device, Observable<Device>>() {
                    @Override
                    public Observable<Device> call(Device result) {
                        return mStorageService.saveDevice(result);
                    }
                })
                .compose(Utils.applySchedulers())
                .doOnError(Timber::w);
    }

    @RxLogObservable
    public Observable<Void> deleteDevice(long id) {
        return mNetworkService.deleteDevice(id)
                .flatMap(new Func1<Void, Observable<Void>>() {
                    @Override
                    public Observable<Void> call(Void avoid) {
                        return mStorageService.deleteDevice(id);
                    }
                })
                .compose(Utils.applySchedulers())
                .doOnError(Timber::w);
    }

    @RxLogObservable
    public Observable<Void> createPermission(Permission permission) {
        return mNetworkService.createPermission(permission)
                .compose(Utils.applySchedulers())
                .doOnError(Timber::w);
    }

    @RxLogObservable
    public Observable<Void> deletePermission(Permission permission) {
        return mNetworkService.deletePermission(permission)
                .compose(Utils.applySchedulers())
                .doOnError(Timber::w);
    }

    @RxLogObservable
    public Observable<Void> sendCommand(Command command) {
        return mNetworkService.createCommand(command)
                .compose(Utils.applySchedulers())
                .doOnError(Timber::w);
    }

    @RxLogObservable
    public Observable<List<Position>> getPositions(long deviceId, Date from, Date to) {
        return mNetworkService.getPositions(deviceId, from, to)
                .compose(Utils.applySchedulers())
                .doOnError(Timber::w);
    }

    @RxLogObservable
    public Observable<Event> openWebSocket() {
        return mNetworkService.openWebSocket()
                .retryWhen(observable -> observable.flatMap(new Func1<Throwable, Observable<?>>() {
                    @Override
                    public Observable<?> call(Throwable throwable) {
                        Timber.tag("ModelImpl").w(throwable);
                        return Observable.timer(5, TimeUnit.SECONDS);
                    }
                }))
                .flatMap(new Func1<Event, Observable<Event>>() {
                    @Override
                    public Observable<Event> call(final Event event) {
                        Timber.d("EVENT: " + event);
                        if (event.getDevices() != null && event.getPositions() != null) {

                            return Observable.zip(
                                    mStorageService.saveDevices(event.getDevices()),
                                    mStorageService.savePositions(event.getPositions()),
                                    (devicePutResults, userPutResults) -> event
                            );
                        }

                        if (event.getDevices() != null) {
                            return mStorageService.saveDevices(event.getDevices())
                                    .map(devicePutResults -> event);
                        }

                        if (event.getPositions() != null) {
                            return mStorageService.savePositions(event.getPositions())
                                    .map(devicePutResults -> event);
                        }

                        return Observable.just(event);
                    }
                })
                .doOnError(Timber::w)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .unsubscribeOn(Schedulers.io());
    }
}
