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
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;

import org.erlymon.monitor.MainApp;
import org.erlymon.monitor.R;
import org.erlymon.monitor.mvp.model.Device;
import org.erlymon.monitor.mvp.model.Permission;
import org.erlymon.monitor.mvp.presenter.CreatePermissionPresenter;
import org.erlymon.monitor.mvp.presenter.DeletePermissionPresenter;
import org.erlymon.monitor.mvp.presenter.GetPermissionsPresenter;
import org.erlymon.monitor.mvp.view.CreatePermissionView;
import org.erlymon.monitor.mvp.view.DeletePermissionView;
import org.erlymon.monitor.mvp.view.GetPermissionsView;
import org.erlymon.monitor.ui.adapter.DevicesPermissionsAdapter;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by sergey on 31.03.17.
 */

public class PermissionsFragment extends MvpAppCompatFragment implements
        GetPermissionsView,
        CreatePermissionView,
        DeletePermissionView {
    @BindView(R.id.devices)
    ListView mDevices;

    @BindView(R.id.progress)
    ProgressBar mProgress;

    @InjectPresenter
    GetPermissionsPresenter mGetPermissionsPresenter;

    @InjectPresenter
    CreatePermissionPresenter mCreatePermissionPresenter;

    @InjectPresenter
    DeletePermissionPresenter mDeletePermissionPresenter;

    private AlertDialog mErrorDialog;


    public static PermissionsFragment getNewInstance(Bundle args) {
        // Required empty public constructor
        PermissionsFragment instance = new PermissionsFragment();
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        MainApp.getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
        Timber.d("onCreate");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Timber.d("onCreateView");
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_permissions, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Timber.d("onViewCreated");
        mGetPermissionsPresenter.getDevices(getArguments().getLong("userId", -1));
    }

    private void toggleProgressVisibility(final boolean show) {
        mProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        mDevices.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public void startGetPermissions() {
        toggleProgressVisibility(true);
    }

    @Override
    public void finishGetPermissions() {
        toggleProgressVisibility(false);
    }

    @Override
    public void failedGetPermissions(String message) {
        mErrorDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.app_name)
                .setMessage(message)
                .setOnCancelListener(dialog -> mGetPermissionsPresenter.onErrorCancel())
                .show();
    }

    @Override
    public void startCreatePermission() {
        toggleProgressVisibility(true);
    }

    @Override
    public void finishCreatePermission() {
        toggleProgressVisibility(false);
    }

    @Override
    public void failedCreatePermission(String message) {
        mErrorDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.app_name)
                .setMessage(message)
                .setOnCancelListener(dialog -> mCreatePermissionPresenter.onErrorCancel())
                .show();
    }

    @Override
    public void startDeletePermission() {
        toggleProgressVisibility(true);
    }

    @Override
    public void finishDeletePermission() {
        toggleProgressVisibility(false);
    }

    @Override
    public void failedDeletePermission(String message) {
        mErrorDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.app_name)
                .setMessage(message)
                .setOnCancelListener(dialog -> mDeletePermissionPresenter.onErrorCancel())
                .show();
    }

    @Override
    public void hideError() {
        if (mErrorDialog != null && mErrorDialog.isShowing()) {
            mErrorDialog.cancel();
        }
    }

    @Override
    public void successDeletePermission() {

    }

    @Override
    public void successCreatePermission() {

    }

    @Override
    public void successGetPermissions(List<Device> allDevices, List<Device> accessDevices) {
        mDevices.setAdapter(new DevicesPermissionsAdapter(getContext(), allDevices, accessDevices, new DevicesPermissionsAdapter.PermissionListener() {
            @Override
            public void createPermission(Device device) {
                mCreatePermissionPresenter.save(Permission.newPermission(
                        getArguments().getLong("userId", -1),
                        device.getId()
                ));
            }

            @Override
            public void deletePermission(Device device) {
                mDeletePermissionPresenter.delete(Permission.newPermission(
                        getArguments().getLong("userId", -1),
                        device.getId()
                ));
            }
        }));
    }
}
