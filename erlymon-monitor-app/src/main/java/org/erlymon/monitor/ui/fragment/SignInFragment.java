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

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.jakewharton.rxbinding.view.RxView;
import com.squareup.otto.Bus;
import com.tbruyelle.rxpermissions.RxPermissions;

import org.erlymon.monitor.MainApp;
import org.erlymon.monitor.R;
import org.erlymon.monitor.Screens;
import org.erlymon.monitor.mvp.model.User;
import org.erlymon.monitor.mvp.presenter.SignInPresenter;
import org.erlymon.monitor.mvp.view.SignInView;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import ru.terrakok.cicerone.Router;
import timber.log.Timber;

/**
 * Created by sergey on 31.03.17.
 */

public class SignInFragment extends MvpAppCompatFragment implements SignInView {
    @BindView(R.id.form_layout)
    LinearLayout mForm;
    @BindView(R.id.email_input_layout)
    TextInputLayout mEmailInputLayer;
    @BindView(R.id.email_input)
    EditText mEmailInput;
    @BindView(R.id.password_input_layout)
    TextInputLayout mPasswordInputLayer;
    @BindView(R.id.password_input)
    EditText mPasswordInput;
    @BindView(R.id.save_input)
    AppCompatCheckBox mSaveInput;
    @BindView(R.id.sign_in_button)
    Button mSignIn;
    @BindView(R.id.sign_up_button)
    TextView mSignUp;
    @BindView(R.id.settings_button)
    ImageView mSettings;

    @BindArray(R.array.protocol_version)
    String[] protocolVersions;

    @BindArray(R.array.protocol_version_value)
    String[] protocolVersionValues;

    @Inject
    Router router;

    @Inject
    SharedPreferences sharedPreferences;

    @InjectPresenter
    SignInPresenter mSignInPresenter;

    private Dialog mSettingsDialog;
    private AlertDialog mErrorDialog;
    private OnViewListener mListener;

    public interface OnViewListener {
        void successSignIn(User user);
    }

    public static SignInFragment getNewInstance(Object data) {
        // Required empty public constructor
        SignInFragment  instance = new SignInFragment();
        instance.setArguments(new Bundle());
        return instance;
    }

    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        // Verify that the host activity implements the callback interface

        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (OnViewListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement SignInFragment.OnViewListener");
        }
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
        View view = inflater.inflate(R.layout.fragment_signin, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        RxView.clicks(mSettings)
                .subscribe(aVoid -> {
                    final View dialogView = LayoutInflater.from(getContext())
                            .inflate(R.layout.fragment_settings, null);

                    Timber.e("PREFS DNS: " + sharedPreferences.getString("dns", "web.erlymon.org"));

                    mSettingsDialog = new AlertDialog.Builder(getContext(), R.style.AppTheme_Dialog)
                            .setTitle(R.string.settingsTitle)
                            .setView(dialogView)
                            .setPositiveButton(android.R.string.ok,
                                    (dialog, whichButton) -> {
                                        AutoCompleteTextView dns = ButterKnife.findById((Dialog) dialog, R.id.dns);
                                        CheckBox sslOrTls = ButterKnife.findById((Dialog) dialog, R.id.sslOrTls);
                                        AppCompatSpinner protocolVersion  = ButterKnife.findById((Dialog) dialog, R.id.protocolVersion);
                                        sharedPreferences.edit()
                                                .putString("dns", dns.getText().toString())
                                                .putBoolean("sslOrTls", sslOrTls.isChecked())
                                                .putFloat("protocolVersion", Float.parseFloat(protocolVersionValues[protocolVersion.getSelectedItemPosition()]))
                                                .apply();
                                        MainApp.init(getContext());
                                    }
                            )
                            .setNegativeButton(android.R.string.cancel, null)
                            .create();


                    AutoCompleteTextView dns = ButterKnife.findById(dialogView, R.id.dns);
                    dns.setText(sharedPreferences.getString("dns", "web.erlymon.org"));

                    CheckBox sslOrTls = ButterKnife.findById(dialogView, R.id.sslOrTls);
                    sslOrTls.setChecked(sharedPreferences.getBoolean("sslOrTls", false));

                    List<String> pv = Arrays.asList(protocolVersionValues);
                    AppCompatSpinner protocolVersion  = ButterKnife.findById(dialogView, R.id.protocolVersion);
                    protocolVersion.setSelection(pv.indexOf(String.valueOf(sharedPreferences.getFloat("protocolVersion", 3.4f))));

                    mSettingsDialog.show();
                });

        RxView.clicks(mSignIn)
                .compose(RxPermissions.getInstance(getContext()).ensure(Manifest.permission.WRITE_EXTERNAL_STORAGE))
                .subscribe(granted -> {
                    if (granted) {
                        mSignInPresenter.signIn(mEmailInput.getText().toString(), mPasswordInput.getText().toString());
                    } else {
                        router.showSystemMessage(getString(R.string.errorPermissionWriteStorage));
                    }
                });

        RxView.clicks(mSignUp)
                .subscribe(aVoid -> router.navigateTo(Screens.SIGN_UP_SCREEN));

        if (sharedPreferences.getBoolean("save", false)) {
            mEmailInput.setText(sharedPreferences.getString("email", ""));
            mPasswordInput.setText(sharedPreferences.getString("password", ""));
            mSaveInput.setChecked(sharedPreferences.getBoolean("save", false));
            mSignInPresenter.signIn(sharedPreferences.getString("email", ""), sharedPreferences.getString("password", ""));
        }
    }

    private void toggleProgressVisibility(boolean b) {
    }

    @Override
    public void startSignIn() {
        toggleProgressVisibility(true);
    }

    @Override
    public void finishSignIn() {
        toggleProgressVisibility(false);
    }

    @Override
    public void successSignIn(User user) {
        Timber.d("successSignIn");
        //if (mSaveInput.isChecked()) {
            sharedPreferences.edit()
                    .putLong("userId", user.getId())
                    .putBoolean("save", mSaveInput.isChecked())
                    .putString("email", mEmailInput.getText().toString())
                    .putString("password", mPasswordInput.getText().toString())
                    .apply();
        //}
        //router.showSystemMessage("showToolbar");
        mListener.successSignIn(user);
    }


    @Override
    public void failedSignIn(String message) {
        mErrorDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.app_name)
                .setMessage(message)
                .setOnCancelListener(dialog -> mSignInPresenter.onErrorCancel())
                .show();
    }

    @Override
    public void hideError() {
        if (mErrorDialog != null && mErrorDialog.isShowing()) {
            mErrorDialog.cancel();
        }
    }
}
