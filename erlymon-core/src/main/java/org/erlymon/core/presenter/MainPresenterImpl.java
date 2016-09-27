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

import org.erlymon.core.model.Model;
import org.erlymon.core.model.ModelImpl;
import org.erlymon.core.model.data.Device;
import org.erlymon.core.model.data.Position;
import org.erlymon.core.model.data.User;
import org.erlymon.core.view.MainView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.realm.Realm;
import io.realm.RealmObject;
import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 5/4/16.
 */
public class MainPresenterImpl implements MainPresenter {
    private static final Logger logger = LoggerFactory.getLogger(MainPresenterImpl.class);
    private Model model;
    private Realm realmdb;

    private ProgressDialog progressDialog;
    private MainView view;
    private Subscription subscription = Subscriptions.empty();

    public MainPresenterImpl(Context context, MainView view, int progressMessageId) {
        this.view = view;
        this.model = new ModelImpl(context);
        this.realmdb = Realm.getDefaultInstance();
        this.progressDialog = new ProgressDialog(context);
        this.progressDialog.setMessage(context.getString(progressMessageId));
    }

    @Override
    public void onDeleteSessionButtonClick() {

        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }

        progressDialog.show();
        subscription = model.deleteSession()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(o -> realmdb.executeTransactionAsync(realm -> realm.deleteAll()))
                .subscribe(new Observer<Void>() {
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
                    public void onNext(Void data) {
                        progressDialog.hide();
                        view.showCompleted();
                    }
                });
    }

    @Override
    public void onDeleteDeviceButtonClick() {
        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }

        subscription = model.deleteDevice(view.getDeviceId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(o -> realmdb.executeTransaction(realm -> {
                        try {
                            realm.where(Device.class).equalTo("id", view.getDeviceId()).findFirst().deleteFromRealm();
                        } catch (Exception e) {
                            throw new RuntimeException(e);
                        }
                }))
                .subscribe(new Observer<Void>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        logger.error(Log.getStackTraceString(e));
                        view.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(Void data) {
                        view.showRemoveDeviceCompleted();
                    }
                });
    }

    @Override
    public void onDeleteUserButtonClick() {
        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }

        subscription = model.deleteUser(view.getUserId())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(o -> realmdb.executeTransaction(realm -> {
                    try {
                        realm.where(User.class).equalTo("id", view.getUserId()).findFirst().deleteFromRealm();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                }))
                .subscribe(new Observer<Void>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        logger.error(Log.getStackTraceString(e));
                        view.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(Void data) {
                        view.showRemoveUserCompleted();
                    }
                });
    }

    @Override
    public void onSendCommandButtonClick() {
        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }

        subscription = model.createCommand(view.getCommand())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Void>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        logger.error(Log.getStackTraceString(e));
                        view.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(Void data) {
                        view.showRemoveDeviceCompleted();
                    }
                });
    }

    @Override
    public void onGetPostionByCache() {
        try {
            if (!subscription.isUnsubscribed()) {
                subscription.unsubscribe();
            }

            subscription = realmdb.where(Position.class).equalTo("id", view.getPositionId()).findFirst().asObservable()
                    .flatMap((Func1<RealmObject, Observable<Position>>) realmObject -> realmObject.asObservable())
                    .subscribeOn(AndroidSchedulers.mainThread())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(new Observer<Position>() {
                        @Override
                        public void onCompleted() {

                        }

                        @Override
                        public void onError(Throwable e) {
                            logger.error(Log.getStackTraceString(e));
                            view.showError(e.getMessage());
                        }

                        @Override
                        public void onNext(Position data) {
                            logger.debug(data.toString());
                            view.showPosition(data);
                        }
                    });
        } catch (NullPointerException e) {
            logger.error(Log.getStackTraceString(e));
            view.showError(e.getMessage());
        }
    }

    @Override
    public void onStop() {
        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }

        if (!realmdb.isClosed()) {
            realmdb.close();
        }

        if (progressDialog.isShowing()) {
            progressDialog.hide();
        }
    }
}
