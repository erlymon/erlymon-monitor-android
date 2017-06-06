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
package org.erlymon.monitor.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import org.erlymon.monitor.MainApp;
import org.erlymon.monitor.R;
import org.erlymon.monitor.mvp.model.Device;
import org.erlymon.monitor.mvp.presenter.CacheDevicePresenter;
import org.erlymon.monitor.mvp.presenter.CreateDevicePresenter;
import org.erlymon.monitor.mvp.presenter.UpdateDevicePresenter;
import org.erlymon.monitor.mvp.view.CreateDeviceView;
import org.erlymon.monitor.mvp.view.GetDeviceView;
import org.erlymon.monitor.mvp.view.UpdateDeviceView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.terrakok.cicerone.Router;
import rx.Observable;
import timber.log.Timber;

import static org.erlymon.monitor.Validators.NAME_PATTERN;
import static org.erlymon.monitor.Validators.IDENTIFIER_PATTERN;

/**
 * Created by sergey on 31.03.17.
 */

public class DeviceFragment extends MvpAppCompatFragment implements GetDeviceView, CreateDeviceView, UpdateDeviceView {
    @BindView(R.id.form_layout)
    RelativeLayout mForm;

    @BindView(R.id.name_input_layout)
    TextInputLayout mNameInputLayout;
    @BindView(R.id.name_input)
    EditText mNameInput;

    @BindView(R.id.identifier_input_layout)
    TextInputLayout mIdentifierInputLayout;
    @BindView(R.id.identifier_input)
    EditText mIdentifierInput;

    @BindView(R.id.submit_input)
    FloatingActionButton mSubmitInput;

    @BindView(R.id.progress)
    ProgressBar mProgress;

    @InjectPresenter
    CacheDevicePresenter mCacheDevicePresenter;

    @InjectPresenter
    CreateDevicePresenter mCreateDevicePresenter;

    @InjectPresenter
    UpdateDevicePresenter mUpdateDevicePresenter;

    @Inject
    Router router;

    private AlertDialog mErrorDialog;

    public static DeviceFragment getNewInstance(Bundle args) {
        // Required empty public constructor
        DeviceFragment  instance = new DeviceFragment();
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        MainApp.getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_device, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Observable<Boolean> nameObservable = RxTextView.textChanges(mNameInput)
                .map(inputText -> !TextUtils.isEmpty(inputText) && NAME_PATTERN.matcher(inputText).matches())
                .distinctUntilChanged();

        nameObservable.subscribe(isValid -> {
            mNameInputLayout.setError("Invalid Name");
            mNameInputLayout.setErrorEnabled(!isValid);
        });

        Observable<Boolean> identifierObservable = RxTextView.textChanges(mIdentifierInput)
                .map(inputText -> !TextUtils.isEmpty(inputText) && IDENTIFIER_PATTERN.matcher(inputText).matches())
                .distinctUntilChanged();

        identifierObservable.subscribe(isValid -> {
            mIdentifierInputLayout.setError("Invalid Identifier");
            mIdentifierInputLayout.setErrorEnabled(!isValid);
        });

        Observable.combineLatest(
                nameObservable,
                identifierObservable,
                (nameValid, identifierValid) -> nameValid && identifierValid)
                .distinctUntilChanged()
                .subscribe(valid -> mSubmitInput.setEnabled(valid));

        RxView.clicks(mSubmitInput)
                .subscribe(aVoid -> {
                    Device device = getArguments().getParcelable("device");
                    device.setName(mNameInput.getText().toString());
                    device.setUniqueId(mIdentifierInput.getText().toString());
                    if (device.getId() == 0) {
                        mCreateDevicePresenter.save(device);
                    } else {
                        mUpdateDevicePresenter.save(device);
                    }
                });

        if (getArguments().getLong("deviceId", 0) == 0) {
            successGetDevice(new Device());
        } else {
            mCacheDevicePresenter.load(getArguments().getLong("deviceId", 0));
        }
    }

    private void toggleProgressVisibility(final boolean show) {
        mProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        mForm.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public void startGetDevice() {
        toggleProgressVisibility(true);
    }

    @Override
    public void finishGetDevice() {
        toggleProgressVisibility(false);
    }

    @Override
    public void failedGetDevice(String message) {
        mErrorDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.app_name)
                .setMessage(message)
                .setOnCancelListener(dialog -> mCacheDevicePresenter.onErrorCancel())
                .show();
    }

    @Override
    public void startUpdateDevice() {
        toggleProgressVisibility(true);
    }

    @Override
    public void finishUpdateDevice() {
        toggleProgressVisibility(false);
    }

    @Override
    public void failedUpdateDevice(String message) {
        mErrorDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.app_name)
                .setMessage(message)
                .setOnCancelListener(dialog -> mUpdateDevicePresenter.onErrorCancel())
                .show();
    }

    @Override
    public void startCreateDevice() {
        toggleProgressVisibility(true);
    }

    @Override
    public void finishCreateDevice() {
        toggleProgressVisibility(false);
    }

    @Override
    public void failedCreateDevice(String message) {
        mErrorDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.app_name)
                .setMessage(message)
                .setOnCancelListener(dialog -> mCacheDevicePresenter.onErrorCancel())
                .show();
    }

    @Override
    public void hideError() {
        if (mErrorDialog != null && mErrorDialog.isShowing()) {
            mErrorDialog.cancel();
        }
    }

    @Override
    public void successCreateDevice() {
        router.backTo(null);
    }

    @Override
    public void successUpdateDevice() {
        router.backTo(null);
    }

    @Override
    public void successGetDevice(Device device) {
        try {
            Timber.d("Device: " + device);
            getArguments().putParcelable("device", device);
            mNameInput.setText(device.getName() != null ? device.getName() : "");
            mIdentifierInput.setText(device.getUniqueId() != null ? device.getUniqueId() : "");
        } catch (Exception e) {
            e.printStackTrace();
            getArguments().putParcelable("device", new Device());
        }
    }
}
