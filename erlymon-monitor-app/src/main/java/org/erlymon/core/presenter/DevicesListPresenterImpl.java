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

import org.erlymon.core.model.Model;
import org.erlymon.core.model.ModelImpl;
import org.erlymon.core.model.data.Device;
import org.erlymon.core.model.data.DevicesTable;
import org.erlymon.core.model.data.StorageModule;
import org.erlymon.core.view.DevicesListView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.Subscriptions;

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 5/4/16.
 */
public class DevicesListPresenterImpl implements DevicesListPresenter {
    private static final Logger logger = LoggerFactory.getLogger(DevicesListPresenterImpl.class);
    private Model model;

    private DevicesListView view;
    private Subscription subscription = Subscriptions.empty();

    public DevicesListPresenterImpl(Context context, DevicesListView view) {
        this.view = view;
        this.model = new ModelImpl(context);
    }

    @Override
    public void onLoadDevicesCache() {
        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }

        subscription = StorageModule.getInstance().getStorage()
                .get()
                .listOfObjects(Device.class)
                .withQuery(DevicesTable.QUERY_ALL)
                .prepare()
                .asRxObservable()
                .doOnError(throwable -> logger.error(Log.getStackTraceString(throwable)))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<List<Device>>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        view.showError(e.getMessage());
                    }

                    @Override
                    public void onNext(List<Device> devices) {
                        view.showData(devices);
                    }
                });
    }

    @Override
    public void onStop() {
        if (!subscription.isUnsubscribed()) {
            subscription.unsubscribe();
        }
    }
}
