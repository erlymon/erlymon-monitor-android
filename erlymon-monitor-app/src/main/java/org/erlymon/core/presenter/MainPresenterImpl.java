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

import com.pushtorefresh.storio.sqlite.operations.delete.DeleteResult;
import com.pushtorefresh.storio.sqlite.queries.DeleteQuery;
import com.pushtorefresh.storio.sqlite.queries.Query;

import org.erlymon.core.model.Model;
import org.erlymon.core.model.ModelImpl;
import org.erlymon.core.model.data.DevicesTable;
import org.erlymon.core.model.data.Position;
import org.erlymon.core.model.data.ServersTable;
import org.erlymon.core.model.data.StorageModule;
import org.erlymon.core.model.data.UsersTable;
import org.erlymon.core.view.MainView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observable;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Func1;
import rx.functions.Func3;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 5/4/16.
 */
public class MainPresenterImpl implements MainPresenter {
    private static final Logger logger = LoggerFactory.getLogger(MainPresenterImpl.class);
    private Model model;

    private ProgressDialog progressDialog;
    private MainView view;
    private Subscription subscription = Subscriptions.empty();

    public MainPresenterImpl(Context context, MainView view, int progressMessageId) {
        this.view = view;
        this.model = new ModelImpl(context);
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
                .flatMap(new Func1<Void, Observable<Void>>() {
                    @Override
                    public Observable<Void> call(Void aVoid) {
                        Observable<Object> serverObserver = StorageModule.getInstance().getStorage()
                                .executeSQL()
                                .withQuery(ServersTable.QUERY_DROP)
                                .prepare()
                                .asRxObservable();

                        Observable<Object> usersObserver = StorageModule.getInstance().getStorage()
                                .executeSQL()
                                .withQuery(UsersTable.QUERY_DROP)
                                .prepare()
                                .asRxObservable();

                        Observable<Object> devicesObserver = StorageModule.getInstance().getStorage()
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
                        );
                    }
                })
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
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
                .flatMap(new Func1<Void, Observable<Void>>() {
                    @Override
                    public Observable<Void> call(Void aVoid) {
                        return StorageModule.getInstance().getStorage()
                                .delete()
                                .byQuery(DeleteQuery.builder()
                                        .table(DevicesTable.TABLE)
                                        .where("id = ?")
                                        .whereArgs(view.getDeviceId()) // No need to write String.valueOf()
                                        .build())
                                .prepare()
                                .asRxObservable()
                                .map(new Func1<DeleteResult, Void>() {
                                    @Override
                                    public Void call(DeleteResult deleteResult) {
                                        return null;
                                    }
                                });
                    }
                })
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
    public void onDeleteUserButtonClick() {
        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }

        subscription = model.deleteUser(view.getUserId())
                .flatMap(new Func1<Void, Observable<Void>>() {
                    @Override
                    public Observable<Void> call(Void aVoid) {
                        return StorageModule.getInstance().getStorage()
                                .delete()
                                .byQuery(DeleteQuery.builder()
                                        .table(UsersTable.TABLE)
                                        .where("id = ?")
                                        .whereArgs(view.getUserId()) // No need to write String.valueOf()
                                        .build())
                                .prepare()
                                .asRxObservable()
                                .map(new Func1<DeleteResult, Void>() {
                                    @Override
                                    public Void call(DeleteResult deleteResult) {
                                        return null;
                                    }
                                });
                    }
                })
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
        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }

        subscription = StorageModule.getInstance().getStorage()
                .get()
                .object(Position.class)
                .withQuery(Query.builder() // Query builder
                        .table("positions")
                        .where("id = ?")
                        .whereArgs(view.getPositionId())
                        .build())
                .prepare()
                .asRxObservable()
                .doOnError(throwable -> logger.error(Log.getStackTraceString(throwable)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Position>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        view.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(Position position) {
                        view.showPosition(position);
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
