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

import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.jakewharton.rxbinding.view.RxView;

import org.erlymon.monitor.MainApp;
import org.erlymon.monitor.R;
import org.erlymon.monitor.Screens;
import org.erlymon.monitor.mvp.presenter.CacheDevicesPresenter;
import org.erlymon.monitor.mvp.presenter.DeleteDevicePresenter;
import org.erlymon.monitor.mvp.view.CacheDevicesView;
import org.erlymon.monitor.mvp.view.DeleteDeviceView;
import org.erlymon.monitor.ui.adapter.DevicesCursorAdapter;
import org.erlymon.monitor.ui.adapter.RecyclerItemClickListener;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.terrakok.cicerone.Router;
import timber.log.Timber;

/**
 * Created by sergey on 31.03.17.
 */

public class DevicesFragment  extends MvpAppCompatFragment implements CacheDevicesView, DeleteDeviceView {
    @BindView(R.id.devices)
    RecyclerView mDevices;
    @BindView(R.id.create_input)
    FloatingActionButton mCreateInput;

    @InjectPresenter
    CacheDevicesPresenter mCacheDevicesPresenter;

    @InjectPresenter
    DeleteDevicePresenter mDeleteDevicePresenter;

    @Inject
    Router router;

    private DevicesCursorAdapter mAdapter;
    private AlertDialog mConfirmDialog;
    private AlertDialog mErrorDialog;

    public static DevicesFragment getNewInstance(Bundle args) {
        // Required empty public constructor
        DevicesFragment  instance = new DevicesFragment();
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
        View view = inflater.inflate(R.layout.fragment_devices, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Timber.d("onViewCreated");
        mDevices.setLayoutManager(new LinearLayoutManager(getContext()));
        mDevices.setHasFixedSize(true);
        mDevices.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        mDevices.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), (view1, position) -> {
            Timber.d("Device position: " + position + " id: " + mAdapter.getItemId(position));
            PopupMenu popupMenu = new PopupMenu(getContext(), view1);
            popupMenu.inflate(R.menu.fragment_devices_popupmenu);
            popupMenu.setOnMenuItemClickListener(new OnExecDevicePopupMenu(mAdapter.getItemId(position)));
            popupMenu.show();
        }));

        RxView.clicks(mCreateInput)
                .subscribe(aVoid -> {
                    Bundle args = new Bundle();
                    args.putLong("deviceId", -1);
                    router.navigateTo(Screens.DEVICE_SCREEN, args);
                });

        mCacheDevicesPresenter.load();
    }

    @Override
    public void onDestroyView() {
        mAdapter = null;
        super.onDestroyView();
    }

    private void toggleProgressVisibility(boolean b) {
    }

    @Override
    public void startCacheDevices() {
        toggleProgressVisibility(true);
    }

    @Override
    public void finishCacheDevices() {
        toggleProgressVisibility(false);
    }

    @Override
    public void failedCacheDevices(String message) {

    }

    @Override
    public void startDeleteDevice() {

    }

    @Override
    public void finishDeleteDevice() {

    }

    @Override
    public void failedDeleteDevice(String message) {
        mErrorDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.app_name)
                .setMessage(message)
                .setOnCancelListener(dialog -> mDeleteDevicePresenter.onErrorCancel())
                .show();
    }

    @Override
    public void hideError() {
        if (mErrorDialog != null && mErrorDialog.isShowing()) {
            mErrorDialog.cancel();
        }
    }

    @Override
    public void successDeleteDevice() {

    }

    @Override
    public void successCacheDevices(Cursor cursor) {
        Timber.d("Count devices: " + cursor.getCount());
        if (mAdapter == null) {
            mAdapter = new DevicesCursorAdapter(cursor);
            mDevices.setAdapter(mAdapter);
        } else {
            mAdapter.changeCursor(cursor);
        }
    }


    private class OnExecDevicePopupMenu implements PopupMenu.OnMenuItemClickListener {
        long mDeviceId;

        public OnExecDevicePopupMenu(long deviceId) {
            this.mDeviceId = deviceId;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            Bundle args = new Bundle();
            switch (item.getItemId()) {
                case R.id.action_device_edit:
                    //mListener.editDevice(mDeviceId);
                    args.putLong("deviceId", mDeviceId);
                    router.navigateTo(Screens.DEVICE_SCREEN, args);
                    break;
                case R.id.action_device_remove:
                    //mListener.deleteDevice(mDeviceId);
                    mConfirmDialog = new AlertDialog.Builder(getContext())
                            .setTitle(R.string.deviceTitle)
                            .setMessage(R.string.sharedRemoveConfirm)
                            .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                                mDeleteDevicePresenter.delete(mDeviceId);
                            })
                            .setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> mConfirmDialog.dismiss())
                            .show();
                    break;
                case R.id.action_device_positions:
                    //mListener.showReport(mDeviceId);
                    args.putLong("deviceId", mDeviceId);
                    router.navigateTo(Screens.REPORT_SCREEN, args);
                    break;
                case R.id.action_show_on_map:
                    //mListener.showDeviceOnMap(mDeviceId);
                    args.putLong("deviceId", mDeviceId);
                    router.newRootScreen(Screens.MAP_SCREEN, args);
                    break;
                case R.id.action_send_command:
                    //mListener.sendCommandToDevice(mDeviceId);
                    SendCommandDialogFragment dialogFragment = SendCommandDialogFragment.newInstance(mDeviceId);
                    dialogFragment.show(getActivity().getSupportFragmentManager(), "send_command_dialog");
                    break;
            }
            return false;
        }
    }
}
