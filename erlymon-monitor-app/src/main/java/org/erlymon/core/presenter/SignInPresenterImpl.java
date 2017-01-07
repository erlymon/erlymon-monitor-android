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
package org.erlymon.core.presenter;


import android.app.ProgressDialog;
import android.content.Context;
import android.util.Log;

import com.pushtorefresh.storio.sqlite.operations.put.PutResult;
import com.pushtorefresh.storio.sqlite.operations.put.PutResults;

import org.erlymon.core.model.Model;
import org.erlymon.core.model.ModelImpl;
import org.erlymon.core.model.api.util.tuple.Triple;
import org.erlymon.core.model.data.Device;
import org.erlymon.core.model.data.Server;
import org.erlymon.core.model.data.StorageModule;
import org.erlymon.core.model.data.User;
import org.erlymon.core.view.SignInView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;

import io.realm.Realm;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 5/4/16.
 */
public class SignInPresenterImpl implements SignInPresenter {
    private static final Logger logger = LoggerFactory.getLogger(SignInPresenterImpl.class);
    private Model model;

    private ProgressDialog progressDialog;
    private SignInView view;
    private Subscription subscription = Subscriptions.empty();

    public SignInPresenterImpl(Context context, SignInView view, int progressMessageId) {
        this.view = view;
        this.model = new ModelImpl(context);
        this.progressDialog = new ProgressDialog(context);
        this.progressDialog.setMessage(context.getString(progressMessageId));
    }

    @Override
    public void onGetServer() {
        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }

        progressDialog.show();
        subscription = model.getServer()
                .flatMap(new Func1<Server, Observable<Server>>() {
                    @Override
                    public Observable<Server> call(Server server) {
                        return StorageModule.getInstance().getStorage()
                                .put()
                                .object(server)
                                .prepare()
                                .asRxObservable()
                                .map(putResult -> server);
                    }
                })
                .doOnError(throwable -> logger.error(Log.getStackTraceString(throwable)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Server>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        progressDialog.hide();
                        view.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(Server data) {
                        progressDialog.hide();
                        view.showServer(data);
                    }
                });
    }

    @Override
    public void onGetSession() {
        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }

        progressDialog.show();
        subscription = model.getSession()
                .flatMap(user -> {
                    if (user.getAdmin()) {
                        return Observable.zip(model.getDevices(), model.getUsers(), (devices, users) -> new Triple<>(user, devices, users)).asObservable();
                    } else {
                        return model.getDevices().flatMap(devices -> {
                            User[] users = new User[] { user };
                            return Observable.just(new Triple<>(user, devices, users));
                        });
                    }
                })
                .flatMap(new Func1<Triple<User, Device[], User[]>, Observable<User>>() {
                    @Override
                    public Observable<User> call(final Triple<User, Device[], User[]> data) {
                        Observable<PutResults<Device>> devicesObserver = StorageModule.getInstance().getStorage()
                                .put()
                                .objects(Arrays.asList(data.second))
                                .prepare()
                                .asRxObservable();

                        Observable<PutResults<User>> usersObserver = StorageModule.getInstance().getStorage()
                                .put()
                                .objects(Arrays.asList(data.third))
                                .prepare()
                                .asRxObservable();

                        return Observable.zip(
                                devicesObserver,
                                usersObserver,
                                (devicePutResults, userPutResults) -> data.first
                        );
                    }
                })
                .doOnError(throwable -> logger.error(Log.getStackTraceString(throwable)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<User>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        progressDialog.hide();
                        logger.error(Log.getStackTraceString(e));
                        view.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(User data) {
                        progressDialog.hide();
                        view.showSession(data);
                    }
                });
    }

    @Override
    public void onCreateSession() {

        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }

        progressDialog.show();
        subscription = model.createSession(view.getEmail(), view.getPassword())
                .subscribeOn(Schedulers.io())
                .flatMap(user -> {
                    if (user.getAdmin()) {
                        return Observable.zip(model.getDevices(), model.getUsers(), (devices, users) -> new Triple<>(user, devices, users)).asObservable();
                    } else {
                        return model.getDevices().flatMap(devices -> {
                            User[] users = new User[] { user };
                            return Observable.just(new Triple<>(user, devices, users));
                        });
                    }
                })
                .flatMap(new Func1<Triple<User, Device[], User[]>, Observable<User>>() {
                    @Override
                    public Observable<User> call(final Triple<User, Device[], User[]> data) {
                        Observable<PutResults<Device>> devicesObserver = StorageModule.getInstance().getStorage()
                                .put()
                                .objects(Arrays.asList(data.second))
                                .prepare()
                                .asRxObservable();

                        Observable<PutResults<User>> usersObserver = StorageModule.getInstance().getStorage()
                                .put()
                                .objects(Arrays.asList(data.third))
                                .prepare()
                                .asRxObservable();

                        return Observable.zip(
                                devicesObserver,
                                usersObserver,
                                (devicePutResults, userPutResults) -> data.first
                        );
                    }
                })
                .doOnError(throwable -> logger.error(Log.getStackTraceString(throwable)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<User>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        progressDialog.hide();
                        logger.error(Log.getStackTraceString(e));
                        view.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(User data) {
                        progressDialog.hide();
                        view.showSession(data);
                    }
                });
    }

    @Override
    public void onStop() {
        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }

        if (progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }
}
