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
import org.erlymon.monitor.mvp.model.User;
import org.erlymon.monitor.mvp.view.CreateUserView;

import javax.inject.Inject;

import rx.Subscription;

/**
 * Created by sergey on 17.03.17.
 */

@InjectViewState
public class CreateUserPresenter extends BasePresenter<CreateUserView> {

    @Inject
    MainService mMainService;

    public CreateUserPresenter() {
        MainApp.getAppComponent().inject(this);
    }

    public void save(User user) {
        getViewState().startCreateUser();

        // save session id
        Subscription subscription = mMainService.createUser(user)
                .subscribe(result -> {
                    getViewState().finishCreateUser();
                    getViewState().successCreateUser();
                }, exception -> {
                    getViewState().finishCreateUser();
                    getViewState().failedCreateUser(exception.getMessage());
                });

        unsubscribeOnDestroy(subscription);
    }

    public void onErrorCancel() {
        getViewState().hideError();
    }
}
