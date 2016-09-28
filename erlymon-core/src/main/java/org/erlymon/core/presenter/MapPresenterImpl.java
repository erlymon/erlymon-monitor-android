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

import com.appunite.websocket.rx.object.messages.RxObjectEvent;
import com.appunite.websocket.rx.object.messages.RxObjectEventMessage;

import org.erlymon.core.model.Model;
import org.erlymon.core.model.ModelImpl;
import org.erlymon.core.model.api.util.MoreObservables;
import org.erlymon.core.model.data.Device;
import org.erlymon.core.model.data.Event;
import org.erlymon.core.view.MapView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.concurrent.TimeUnit;

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
public class MapPresenterImpl implements MapPresenter {
    private static final Logger logger = LoggerFactory.getLogger(MapPresenterImpl.class);

    private Model model;
    private Realm realmdb;

    private MapView view;
    private Subscription subscription = Subscriptions.empty();

    public MapPresenterImpl(Context context, MapView view) {
        this.view = view;
        this.model = new ModelImpl(context);
        this.realmdb = Realm.getDefaultInstance();
    }

    @Override
    public void onOpenWebSocket() {
        logger.debug("onStart");
        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }

        subscription = model.openWebSocket()
                .subscribeOn(Schedulers.io())
                .doOnNext(rxObjectEvent -> logger.error("DUMP RxObjectEvent => " + rxObjectEvent))
                .compose(MoreObservables.filterAndMap(RxObjectEventMessage.class))
                .doOnNext(rxObjectEventMessage -> logger.error("DUMP RxObjectEventMessage => " + rxObjectEventMessage))
                .compose(RxObjectEventMessage.filterAndMap(Event.class))
                .doOnNext(event -> logger.error("DUMP Event => " + event))
                .retryWhen(observable -> observable.flatMap(new Func1<Throwable, Observable<?>>() {
                    @Override
                    public Observable<?> call(Throwable throwable) {
                        logger.warn(Log.getStackTraceString(throwable));
                        return Observable.timer(1, TimeUnit.SECONDS);
                    }
                }))
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext(event -> realmdb.executeTransactionAsync(realm -> {
                    if (event.getPositions() != null) {
                        realm.copyToRealmOrUpdate(Arrays.asList(event.getPositions()));
                    }
                    if (event.getDevices() != null) {
                        realm.copyToRealmOrUpdate(Arrays.asList(event.getDevices()));
                    }
                }))
                .flatMap(event -> {
                    if ((event.getDevices() == null || event.getDevices().length == 0) && event.getPositions() != null) {
                        return realmdb.where(Device.class).findAllAsync().asObservable()
                                .flatMap(devices -> Observable.just(new Event(devices.toArray(new Device[devices.size()]), event.getPositions())));
                    } else {
                        return Observable.just(event);
                    }
                })
                .doOnNext(event -> view.showEvent(event))
                //.retry()
                .subscribe();
    }

    @Override
    public void onStop() {
        logger.debug("onStop");
        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }

        if (!realmdb.isClosed()) {
            realmdb.close();
        }
    }
}
