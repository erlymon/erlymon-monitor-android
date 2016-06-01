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


import android.content.Context;
import android.util.Log;

import com.appunite.websocket.rx.messages.RxEvent;
import com.appunite.websocket.rx.messages.RxEventStringMessage;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.erlymon.core.model.Model;
import org.erlymon.core.model.ModelImpl;
import org.erlymon.core.model.api.ApiModule;
import org.erlymon.core.model.data.Device;
import org.erlymon.core.model.data.Event;
import org.erlymon.core.model.data.User;
import org.erlymon.core.view.MainView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 5/4/16.
 */
public class MainPresenterImpl implements MainPresenter {
    private static final Logger logger = LoggerFactory.getLogger(MainPresenterImpl.class);
    private Model model;

    private MainView view;
    private Subscription subscription = Subscriptions.empty();
    private Subscription subscriptionWS = Subscriptions.empty();

    public MainPresenterImpl(Context context, MainView view) {
        this.view = view;
        this.model = new ModelImpl(context);
    }

    @Override
    public void onDeleteSessionButtonClick() {

        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }

        subscription = model.deleteSession()
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
    public void onLoadDevices() {
        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }

        subscription = model.getDevices()
                .subscribe(new Observer<Device[]>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        logger.error(Log.getStackTraceString(e));
                        view.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(Device[] data) {
                        logger.debug(data.toString());
                        view.showDevices(data);
                    }
                });
    }

    @Override
    public void onLoadUsers() {
        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }

        subscription = model.getUsers()
                .subscribe(new Observer<User[]>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        logger.error(Log.getStackTraceString(e));
                        view.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(User[] data) {
                        logger.debug(data.toString());
                        view.showUsers(data);
                    }
                });
    }

    @Override
    public void onOpenWebSocket() {
        if (!subscriptionWS.isUnsubscribed()) {
            subscriptionWS.unsubscribe();
        }

        subscriptionWS = model.openWebSocket()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnError(new Action1<Throwable>() {
                    @Override
                    public void call(Throwable e) {
                        logger.error("doOnError => " + Log.getStackTraceString(e));
                        view.showError(e.getMessage());
                    }
                })
                .doOnNext(new Action1<RxEvent>() {
                    @Override
                    public void call(RxEvent rxEvent) {
                        logger.debug("doOnNext => Event: " + rxEvent.toString());
                        if (rxEvent instanceof RxEventStringMessage) {
                            String msg = ((RxEventStringMessage) rxEvent).message();
                            logger.debug(msg);
                            view.showEvent(ApiModule.getInstance().getGson().fromJson(msg, Event.class));

                        }
                    }
                })
                .subscribe();
    }

    @Override
    public void onStop() {
        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }

        if (!subscriptionWS.isUnsubscribed()) {
            subscriptionWS.unsubscribe();
        }
    }
}
