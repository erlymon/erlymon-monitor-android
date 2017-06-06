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
import android.webkit.URLUtil;
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
import org.erlymon.monitor.mvp.model.Server;
import org.erlymon.monitor.mvp.presenter.CacheServerPresenter;
import org.erlymon.monitor.mvp.presenter.UpdateServerPresenter;
import org.erlymon.monitor.mvp.view.GetServerView;
import org.erlymon.monitor.mvp.view.UpdateServerView;

import java.util.Arrays;

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
import static org.erlymon.monitor.Validators.ZOOM_PATTERN;
import static org.erlymon.monitor.Validators.BING_KEY_PATTERN;

/**
 * Created by sergey on 31.03.17.
 */

public class ServerFragment extends MvpAppCompatFragment implements GetServerView, UpdateServerView {
    @BindView(R.id.form_layout)
    RelativeLayout mForm;

    @BindView(R.id.registration_input_layout)
    TextInputLayout mRegistrationInputLayout;
    @BindView(R.id.registration_input)
    CheckBox mRegistrationInput;

    @BindView(R.id.readonly_input_layout)
    TextInputLayout mReadonlyInputLayer;
    @BindView(R.id.readonly_input)
    CheckBox mReadonlyInput;

    @BindView(R.id.map_input)
    MaterialSpinner mMapInput;

    @BindView(R.id.bing_key_input_layout)
    TextInputLayout mBingKeyInputLayer;
    @BindView(R.id.bing_key_input)
    EditText mBingKeyInput;

    @BindView(R.id.map_url_input_layout)
    TextInputLayout mMapUrlInputLayer;
    @BindView(R.id.map_url_input)
    EditText mMapUrlInput;

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
    CacheServerPresenter mCacheServerPresenter;

    @InjectPresenter
    UpdateServerPresenter mUpdateServerPresenter;

    @Inject
    Router router;

    private AlertDialog mErrorDialog;

    public static ServerFragment getNewInstance(Bundle args) {
        // Required empty public constructor
        ServerFragment  instance = new ServerFragment();
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
        View view = inflater.inflate(R.layout.fragment_server, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Observable<Boolean> bingKeyObservable = RxTextView.textChanges(mBingKeyInput)
                .map(inputText -> TextUtils.isEmpty(inputText) || BING_KEY_PATTERN.matcher(inputText).matches())
                .distinctUntilChanged();

        bingKeyObservable.subscribe(isValid -> {
            mBingKeyInputLayer.setError("Invalid Bing Key");
            mBingKeyInputLayer.setErrorEnabled(!isValid);
        });

        Observable<Boolean> mapUrlObservable = RxTextView.textChanges(mMapUrlInput)
                .map(inputText -> TextUtils.isEmpty(inputText) || URLUtil.isValidUrl(inputText.toString()))
                .distinctUntilChanged();

        mapUrlObservable.subscribe(isValid -> {
            mMapUrlInputLayer.setError("Invalid Map Url");
            mMapUrlInputLayer.setErrorEnabled(!isValid);
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
                bingKeyObservable,
                mapUrlObservable,
                latitudeObservable,
                longitudeObservable,
                zoomObservable,
                (bingKeyValid, mapUrlValid, latitudeValid, longitudeValid, zoomValid) -> bingKeyValid && mapUrlValid && latitudeValid && longitudeValid && zoomValid)
                .distinctUntilChanged()
                .subscribe(valid -> mSubmitInput.setEnabled(valid));

        RxView.clicks(mSubmitInput)
                .subscribe(aVoid -> {
                    Server server = getArguments().getParcelable("server");
                    server.setRegistration(mRegistrationInput.isChecked());
                    server.setReadonly(mReadonlyInput.isChecked());
                    server.setMap(mapType[mMapInput.getSelectedItemPosition()]);
                    server.setBingKey(mBingKeyInput.getText().toString());
                    server.setMapUrl(mMapUrlInput.getText().toString());
                    server.setDistanceUnit(distanceUnit[mDistanceUnitInput.getSelectedItemPosition()]);
                    server.setSpeedUnit(speedUnit[mSpeedUnitInput.getSelectedItemPosition()]);
                    server.setLatitude(Double.parseDouble(mLatitudeInput.getText().toString()));
                    server.setLongitude(Double.parseDouble(mLongitudeInput.getText().toString()));
                    server.setZoom(Integer.parseInt(mZoomInput.getText().toString()));
                    server.setTwelveHourFormat(mTwelveHourFormatInput.isChecked());
                    mUpdateServerPresenter.save(server);
                });

        mCacheServerPresenter.load();
    }

    private void toggleProgressVisibility(final boolean show) {
        mProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        mForm.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public void startUpdateServer() {
        toggleProgressVisibility(true);
    }

    @Override
    public void finishUpdateServer() {
        toggleProgressVisibility(false);
    }

    @Override
    public void failedUpdateServer(String message) {
        mErrorDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.app_name)
                .setMessage(message)
                .setOnCancelListener(dialog -> mUpdateServerPresenter.onErrorCancel())
                .show();
    }

    @Override
    public void startGetServer() {
        toggleProgressVisibility(true);
    }

    @Override
    public void finishGetServer() {
        toggleProgressVisibility(false);
    }

    @Override
    public void failedGetServer(String message) {
        mErrorDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.app_name)
                .setMessage(message)
                .setOnCancelListener(dialog -> mCacheServerPresenter.onErrorCancel())
                .show();
    }

    @Override
    public void hideError() {
        if (mErrorDialog != null && mErrorDialog.isShowing()) {
            mErrorDialog.cancel();
        }
    }

    @Override
    public void successGetServer(Server server) {
        try {
            Timber.d("Server: " + server);
            getArguments().putParcelable("server", server);
            mRegistrationInput.setChecked(server.getRegistration());
            mReadonlyInput.setChecked(server.getReadonly());

            int mapTypeIndex = Arrays.asList(mapType).indexOf(server.getMap());
            int distanceUnitIndex = Arrays.asList(distanceUnit).indexOf(server.getDistanceUnit());
            int speedUnitIndex = Arrays.asList(speedUnit).indexOf(server.getSpeedUnit());

            mMapInput.setSelection(mapTypeIndex != -1 ? mapTypeIndex : 0);
            mDistanceUnitInput.setSelection(distanceUnitIndex != -1 ? distanceUnitIndex : 0);
            mSpeedUnitInput.setSelection(speedUnitIndex != -1 ? speedUnitIndex : 0);

            mBingKeyInput.setText(server.getBingKey());
            mMapUrlInput.setText(server.getMapUrl());
            mLatitudeInput.setText(server.getLatitude() != null ? String.valueOf(server.getLatitude()) : "");
            mLongitudeInput.setText(server.getLongitude() != null ? String.valueOf(server.getLongitude()) : "");
            mZoomInput.setText(server.getZoom() != null ? String.valueOf(server.getZoom()) : "");
            mTwelveHourFormatInput.setChecked(server.getTwelveHourFormat());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void successUpdateServer() {
        router.backTo(null);
    }
}
