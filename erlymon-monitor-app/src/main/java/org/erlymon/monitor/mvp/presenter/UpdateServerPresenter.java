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
package org.erlymon.monitor.mvp.presenter;


import com.arellomobile.mvp.InjectViewState;

import org.erlymon.monitor.MainApp;
import org.erlymon.monitor.mvp.MainService;
import org.erlymon.monitor.mvp.model.Server;
import org.erlymon.monitor.mvp.view.UpdateServerView;

import javax.inject.Inject;

import rx.Subscription;

/**
 * Created by sergey on 17.03.17.
 */

@InjectViewState
public class UpdateServerPresenter extends BasePresenter<UpdateServerView> {

    @Inject
    MainService mMainService;

    public UpdateServerPresenter() {
        MainApp.getAppComponent().inject(this);
    }

    public void save(Server server) {
        getViewState().startUpdateServer();

        // save session id
        Subscription subscription = mMainService.updateServer(server)
                .subscribe(result -> {
                    getViewState().finishUpdateServer();
                    getViewState().successUpdateServer();
                }, exception -> {
                    getViewState().finishUpdateServer();
                    getViewState().failedUpdateServer(exception.getMessage());
                });

        unsubscribeOnDestroy(subscription);
    }

    public void onErrorCancel() {
        getViewState().hideError();
    }
}
