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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import org.erlymon.monitor.MainApp;
import org.erlymon.monitor.R;
import org.erlymon.monitor.mvp.model.User;
import org.erlymon.monitor.mvp.presenter.CacheUserPresenter;
import org.erlymon.monitor.mvp.presenter.CreateUserPresenter;
import org.erlymon.monitor.mvp.presenter.UpdateUserPresenter;
import org.erlymon.monitor.mvp.view.CreateUserView;
import org.erlymon.monitor.mvp.view.GetUserView;
import org.erlymon.monitor.mvp.view.UpdateUserView;

import java.util.Arrays;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import fr.ganfra.materialspinner.MaterialSpinner;
import ru.terrakok.cicerone.Router;
import rx.Observable;
import timber.log.Timber;

import static org.erlymon.monitor.Validators.LATITUDE_PATTERN;
import static org.erlymon.monitor.Validators.LONGITUDE_PATTERN;
import static org.erlymon.monitor.Validators.PASSWORD_PATTERN;
import static org.erlymon.monitor.Validators.NAME_PATTERN;
import static org.erlymon.monitor.Validators.ZOOM_PATTERN;

/**
 * Created by sergey on 31.03.17.
 */

public class UserFragment  extends MvpAppCompatFragment implements GetUserView, CreateUserView, UpdateUserView {
    @BindView(R.id.form_layout)
    RelativeLayout mForm;

    @BindView(R.id.name_input_layout)
    TextInputLayout mNameInputLayout;
    @BindView(R.id.name_input)
    EditText mNameInput;

    @BindView(R.id.email_input_layout)
    TextInputLayout mEmailInputLayout;
    @BindView(R.id.email_input)
    EditText mEmailInput;

    @BindView(R.id.password_input_layout)
    TextInputLayout mPasswordInputLayout;
    @BindView(R.id.password_input)
    EditText mPasswordInput;

    @BindView(R.id.admin_input)
    CheckBox mAdminInput;

    @BindView(R.id.map_input)
    MaterialSpinner mMapInput;

    @BindView(R.id.distance_unit_input)
    MaterialSpinner mDistanceUnitInput;

    @BindView(R.id.speed_unit_input)
    MaterialSpinner mSpeedUnitInput;

    @BindView(R.id.latitude_input_layout)
    TextInputLayout mLatitudeInputLayout;
    @BindView(R.id.latitude_input)
    EditText mLatitudeInput;

    @BindView(R.id.longitude_input_layout)
    TextInputLayout mLongitudeInputLayout;
    @BindView(R.id.longitude_input)
    EditText mLongitudeInput;

    @BindView(R.id.zoom_input_layout)
    TextInputLayout mZoomInputLayout;
    @BindView(R.id.zoom_input)
    EditText mZoomInput;

    @BindView(R.id.twelve_hour_format_input)
    CheckBox mTwelveHourFormatInput;

    @BindView(R.id.submit_input)
    FloatingActionButton mSubmitInput;

    @BindView(R.id.progress)
    ProgressBar mProgress;

    @BindArray(R.array.mapType)
    String[] mapType;

    @BindArray(R.array.distanceUnit)
    String[] distanceUnit;

    @BindArray(R.array.speedUnit)
    String[] speedUnit;


    @InjectPresenter
    CacheUserPresenter mCacheUserPresenter;

    @InjectPresenter
    CreateUserPresenter mCreateUserPresenter;

    @InjectPresenter
    UpdateUserPresenter mUpdateUserPresenter;

    @Inject
    Router router;

    private AlertDialog mErrorDialog;

    public static UserFragment getNewInstance(Bundle args) {
        // Required empty public constructor
        UserFragment  instance = new UserFragment();
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
        View view = inflater.inflate(R.layout.fragment_user, container, false);
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

        Observable<Boolean> emailObservable = RxTextView.textChanges(mEmailInput)
                .map(inputText -> {
                    if (getArguments().getBoolean("isAdmin", false)) {
                        return true;
                    } else {
                        return !TextUtils.isEmpty(inputText) && android.util.Patterns.EMAIL_ADDRESS.matcher(inputText).matches();
                    }
                })
                .distinctUntilChanged();

        emailObservable.subscribe(isValid -> {
            mEmailInputLayout.setError("Invalid Email");
            mEmailInputLayout.setErrorEnabled(!isValid);
        });

        Observable<Boolean> passwordObservable = RxTextView.textChanges(mPasswordInput)
                .map(inputText -> {
                    if (getArguments().getLong("userId", -1) == -1) {
                        return !TextUtils.isEmpty(inputText) && PASSWORD_PATTERN.matcher(inputText).matches();
                    } else {
                        return TextUtils.isEmpty(inputText) || PASSWORD_PATTERN.matcher(inputText).matches();
                    }
                })
                .distinctUntilChanged();

        passwordObservable.subscribe(isValid -> {
            mPasswordInputLayout.setError("Invalid Password");
            mPasswordInputLayout.setErrorEnabled(!isValid);
        });


        Observable<Boolean> latitudeObservable = RxTextView.textChanges(mLatitudeInput)
                .map(inputText -> TextUtils.isEmpty(inputText) || LATITUDE_PATTERN.matcher(inputText).matches())
                .distinctUntilChanged();

        latitudeObservable.subscribe(isValid -> {
            mLatitudeInputLayout.setError("Invalid Latitude");
            mLatitudeInputLayout.setErrorEnabled(!isValid);
        });

        Observable<Boolean> longitudeObservable = RxTextView.textChanges(mLongitudeInput)
                .map(inputText -> TextUtils.isEmpty(inputText) || LONGITUDE_PATTERN.matcher(inputText).matches())
                .distinctUntilChanged();

        longitudeObservable.subscribe(isValid -> {
            mLongitudeInputLayout.setError("Invalid Longitude");
            mLongitudeInputLayout.setErrorEnabled(!isValid);
        });

        Observable<Boolean> zoomObservable = RxTextView.textChanges(mZoomInput)
                .map(inputText -> TextUtils.isEmpty(inputText) || ZOOM_PATTERN.matcher(inputText).matches())
                .distinctUntilChanged();

        zoomObservable.subscribe(isValid -> {
            mZoomInputLayout.setError("Invalid Zoom");
            mZoomInputLayout.setErrorEnabled(!isValid);
        });

        Observable.combineLatest(
                nameObservable,
                emailObservable,
                passwordObservable,
                latitudeObservable,
                longitudeObservable,
                zoomObservable,
                (nameValid, emailValid, passwordValid, latitudeValid, longitudeValid, zoomValid) -> nameValid && emailValid && passwordValid && latitudeValid && longitudeValid && zoomValid)
                .distinctUntilChanged()
                .subscribe(valid -> mSubmitInput.setEnabled(valid));

        RxView.clicks(mSubmitInput)
                .subscribe(aVoid -> {
                    User user = getArguments().getParcelable("user");
                    user.setName(mNameInput.getText().toString());
                    user.setEmail(mEmailInput.getText().toString());
                    user.setPassword(mPasswordInput.getText().length() > 0 ? mPasswordInput.getText().toString() : "");
                    user.setAdmin(mAdminInput.isChecked());
                    user.setLanguage(Locale.getDefault().getLanguage());
                    //user.setReadonly(mReadonlyInput.isChecked());
                    user.setMap(mapType[mMapInput.getSelectedItemPosition()]);
                    user.setDistanceUnit(distanceUnit[mDistanceUnitInput.getSelectedItemPosition()]);
                    user.setSpeedUnit(speedUnit[mSpeedUnitInput.getSelectedItemPosition()]);
                    user.setLatitude(mLatitudeInput.getText().length() > 0 ? Double.parseDouble(mLatitudeInput.getText().toString()) : null);
                    user.setLongitude(mLongitudeInput.getText().length() > 0 ? Double.parseDouble(mLongitudeInput.getText().toString()) : null);
                    user.setZoom(mZoomInput.getText().length() > 0 ? Integer.parseInt(mZoomInput.getText().toString()) : null);
                    user.setTwelveHourFormat(mTwelveHourFormatInput.isChecked()); // version > 3.4
                    if (user.getId() == 0) {
                        mCreateUserPresenter.save(user);
                    } else {
                        mUpdateUserPresenter.save(user);
                    }
                });

        User session = getArguments().getParcelable("session");
        if (session != null) {
            mAdminInput.setChecked(session.getAdmin());
            mAdminInput.setEnabled(session.getAdmin());
        }

        if (getArguments().getLong("userId", -1) == -1) {
            successGetUser(new User());
        } else {
            mCacheUserPresenter.load(getArguments().getLong("userId", -1));
        }
    }

    private void toggleProgressVisibility(final boolean show) {
        mProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        mForm.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public void startGetUser() {
        toggleProgressVisibility(true);
    }

    @Override
    public void finishGetUser() {
        toggleProgressVisibility(false);
    }

    @Override
    public void failedGetUser(String message) {
        mErrorDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.app_name)
                .setMessage(message)
                .setOnCancelListener(dialog -> mCacheUserPresenter.onErrorCancel())
                .show();
    }

    @Override
    public void startUpdateUser() {
        toggleProgressVisibility(true);
    }

    @Override
    public void finishUpdateUser() {
        toggleProgressVisibility(false);
    }

    @Override
    public void failedUpdateUser(String message) {
        mErrorDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.app_name)
                .setMessage(message)
                .setOnCancelListener(dialog -> mUpdateUserPresenter.onErrorCancel())
                .show();
    }

    @Override
    public void startCreateUser() {
        toggleProgressVisibility(true);
    }

    @Override
    public void finishCreateUser() {
        toggleProgressVisibility(false);
    }

    @Override
    public void failedCreateUser(String message) {
        mErrorDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.app_name)
                .setMessage(message)
                .setOnCancelListener(dialog -> mCacheUserPresenter.onErrorCancel())
                .show();
    }

    @Override
    public void hideError() {
        if (mErrorDialog != null && mErrorDialog.isShowing()) {
            mErrorDialog.cancel();
        }
    }

    @Override
    public void successCreateUser() {
        router.backTo(null);
    }

    @Override
    public void successUpdateUser() {
        router.backTo(null);
    }

    @Override
    public void successGetUser(User user) {
        try {
            Timber.d("User: " + user);
            getArguments().putParcelable("user", user);
            mNameInput.setText(user.getName() != null ? user.getName() : "");
            mEmailInput.setText(user.getEmail() != null ? user.getEmail() : "");
            mAdminInput.setChecked(user.getAdmin() != null ? user.getAdmin() : false);

            int mapTypeIndex = Arrays.asList(mapType).indexOf(user.getMap());
            int distanceUnitIndex = Arrays.asList(distanceUnit).indexOf(user.getDistanceUnit());
            int speedUnitIndex = Arrays.asList(speedUnit).indexOf(user.getSpeedUnit());

            mMapInput.setSelection(mapTypeIndex != -1 ? mapTypeIndex : 0);
            mDistanceUnitInput.setSelection(distanceUnitIndex != -1 ? distanceUnitIndex : 0);
            mSpeedUnitInput.setSelection(speedUnitIndex != -1 ? speedUnitIndex : 0);
            mLatitudeInput.setText(user.getLatitude() != null ? String.valueOf(user.getLatitude()) : "");
            mLongitudeInput.setText(user.getLongitude() != null ? String.valueOf(user.getLongitude()) : "");
            mZoomInput.setText(user.getZoom() != null ? String.valueOf(user.getZoom()) : "");
            mTwelveHourFormatInput.setChecked(user.getTwelveHourFormat() != null ? user.getTwelveHourFormat() : false);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
