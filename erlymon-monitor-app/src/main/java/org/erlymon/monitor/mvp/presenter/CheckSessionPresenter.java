package org.erlymon.monitor.mvp.presenter;


import com.arellomobile.mvp.InjectViewState;

import org.erlymon.monitor.MainApp;
import org.erlymon.monitor.mvp.MainService;
import org.erlymon.monitor.mvp.view.CheckSessionView;

import javax.inject.Inject;

import rx.Subscription;

/**
 * Created by sergey on 17.03.17.
 */

@InjectViewState
public class CheckSessionPresenter extends BasePresenter<CheckSessionView> {

    @Inject
    MainService mMainService;

    public CheckSessionPresenter() {
        MainApp.getAppComponent().inject(this);
    }

    public void check() {
        getViewState().startCheckSession();

        // save session id
        Subscription subscription = mMainService.checkSession()
                .subscribe(user -> {
                    getViewState().finishCheckSession();
                    getViewState().successCheckSession(user);
                }, exception -> {
                    getViewState().finishCheckSession();
                    getViewState().failedCheckSession(exception.getMessage());
                });

        unsubscribeOnDestroy(subscription);
    }

    public void onErrorCancel() {
        getViewState().hideError();
    }
}
