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
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.squareup.otto.Bus;

import org.erlymon.monitor.MainApp;
import org.erlymon.monitor.R;
import org.erlymon.monitor.Screens;
import org.erlymon.monitor.mvp.presenter.SignUpPresenter;
import org.erlymon.monitor.mvp.view.SignUpView;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.terrakok.cicerone.Router;
import rx.Observable;

import static org.erlymon.monitor.Validators.PASSWORD_PATTERN;
import static org.erlymon.monitor.Validators.NAME_PATTERN;

/**
 * Created by sergey on 31.03.17.
 */

public class SignUpFragment extends MvpAppCompatFragment implements SignUpView {
    @BindView(R.id.form_layout)
    LinearLayout mForm;
    @BindView(R.id.name_input_layout)
    TextInputLayout mNameInputLayer;
    @BindView(R.id.name_input)
    EditText mNameInput;
    @BindView(R.id.email_input_layout)
    TextInputLayout mEmailInputLayer;
    @BindView(R.id.email_input)
    EditText mEmailInput;
    @BindView(R.id.password_input_layout)
    TextInputLayout mPasswordInputLayer;
    @BindView(R.id.password_input)
    EditText mPasswordInput;
    @BindView(R.id.fab_account_save)
    FloatingActionButton mFabAccountSave;
    @BindView(R.id.progress)
    ProgressBar mProgress;

    @InjectPresenter
    SignUpPresenter mSignUpPresenter;

    @Inject
    Router router;

    private AlertDialog mErrorDialog;

    public static SignUpFragment getNewInstance(Object data) {
        // Required empty public constructor
        SignUpFragment  instance = new SignUpFragment();

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
        View view = inflater.inflate(R.layout.fragment_signup, container, false);
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
            mNameInputLayer.setError("Invalid Name");
            mNameInputLayer.setErrorEnabled(!isValid);
        });

        Observable<Boolean> emailObservable = RxTextView.textChanges(mEmailInput)
                .map(inputText -> !TextUtils.isEmpty(inputText) && android.util.Patterns.EMAIL_ADDRESS.matcher(inputText).matches())
                .distinctUntilChanged();

        emailObservable.subscribe(isValid -> {
            mEmailInputLayer.setError("Invalid Email");
            mEmailInputLayer.setErrorEnabled(!isValid);
        });

        Observable<Boolean> passwordObservable = RxTextView.textChanges(mPasswordInput)
                .map(inputText -> !TextUtils.isEmpty(inputText) || PASSWORD_PATTERN.matcher(inputText).matches())
                .distinctUntilChanged();

        passwordObservable.subscribe(isValid -> {
            mPasswordInputLayer.setError("Invalid Password");
            mPasswordInputLayer.setErrorEnabled(!isValid);
        });

        Observable.combineLatest(
                nameObservable,
                emailObservable,
                passwordObservable,
                (nameValid, emailValid, passwordValid) -> nameValid && emailValid && passwordValid)
                .distinctUntilChanged()
                .subscribe(valid -> mFabAccountSave.setEnabled(valid));

        RxView.clicks(mFabAccountSave)
                .subscribe(aVoid -> {
                    mSignUpPresenter.signUp(
                            mNameInput.getText().toString(),
                            mEmailInput.getText().toString(),
                            mPasswordInput.getText().toString()
                            );
                });
    }

    private void toggleProgressVisibility(final boolean show) {
        mProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        mForm.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public void startSignUp() {
        toggleProgressVisibility(true);
    }

    @Override
    public void finishSignUp() {
        toggleProgressVisibility(false);
    }

    @Override
    public void failedSignUp(String message) {
        mErrorDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.app_name)
                .setMessage(message)
                .setOnCancelListener(dialog -> mSignUpPresenter.onErrorCancel())
                .show();
    }

    @Override
    public void hideError() {
        if (mErrorDialog != null && mErrorDialog.isShowing()) {
            mErrorDialog.cancel();
        }
    }

    @Override
    public void successSignUp() {
        router.backTo(null);
    }
}
