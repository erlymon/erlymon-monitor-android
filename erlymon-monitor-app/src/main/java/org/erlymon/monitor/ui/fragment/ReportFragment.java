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

import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.ProgressBar;
import android.widget.TabHost;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.jakewharton.rxbinding.view.RxView;

import org.erlymon.monitor.MainApp;
import org.erlymon.monitor.R;
import org.erlymon.monitor.mvp.model.Position;
import org.erlymon.monitor.mvp.presenter.GetPositionsPresenter;
import org.erlymon.monitor.mvp.view.GetPositionsView;
import org.erlymon.monitor.ui.adapter.PositionsExpandableListAdapter;
import org.erlymon.monitor.ui.widget.DateTimePicker;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.BoundingBoxE6;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Polyline;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;

/**
 * Created by sergey on 31.03.17.
 */

public class ReportFragment extends MvpAppCompatFragment implements GetPositionsView {
    @BindView(R.id.tabHost)
    TabHost mTabHost;

    @BindView(R.id.mapview)
    MapView mMapView;

    @BindView(R.id.lv_positions)
    ExpandableListView mList;

    @BindView(R.id.create_input)
    FloatingActionButton mCreateInput;

    @BindView(R.id.progress)
    ProgressBar mProgress;

    @InjectPresenter
    GetPositionsPresenter mGetPositionsPresenter;

    Polyline pathOverlay;

    private AlertDialog mErrorDialog;
    private Dialog mDateTimeDialog;

    public static ReportFragment getNewInstance(Bundle args) {
        // Required empty public constructor
        ReportFragment instance = new ReportFragment();
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
        View view = inflater.inflate(R.layout.fragment_report, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Timber.d("onViewCreated");

        mTabHost.setup();
        addTabHost(R.id.ll_map_positions, R.string.mapTitle, "map_position");
        addTabHost(R.id.ll_list_positions, R.string.listTitle, "list_positions");

        RxView.clicks(mCreateInput)
                .subscribe(aVoid -> {
                    final View dialogView = LayoutInflater.from(getContext())
                            .inflate(R.layout.fragment_date_time_interval, null);

                    mDateTimeDialog = new AlertDialog.Builder(getContext(), R.style.AppTheme_Dialog)
                            .setTitle(R.string.sharedDateTimeInterval)
                            .setView(dialogView)
                            .setPositiveButton(android.R.string.ok,
                                    (dialog, whichButton) -> {
                                        DateTimePicker from = ButterKnife.findById(mDateTimeDialog, R.id.date_time_from);
                                        DateTimePicker to = ButterKnife.findById(mDateTimeDialog, R.id.date_time_to);
                                        Timber.d("Interval time: " + from.getTime() + " to: " + to.getTime());
                                        mGetPositionsPresenter.load(
                                                getArguments().getLong("deviceId", -1),
                                                from.getTime(),
                                                to.getTime()
                                        );
                                    }
                            )
                            .setNegativeButton(android.R.string.cancel, null)
                            .create();
                    mDateTimeDialog.show();

                });
    }
    @Override
    public void onResume() {
        super.onResume();
        pathOverlay = new Polyline(getContext());
        pathOverlay.setColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        pathOverlay.getPaint().setStrokeWidth(10.0f);
        mMapView.getOverlays().add(pathOverlay);
    }

    @Override
    public void onPause() {
        mMapView.getOverlays().remove(pathOverlay);
        super.onPause();
    }

    private void addTabHost(int contentId, int indicatorId, String tag) {
        TabHost.TabSpec spec = mTabHost.newTabSpec(tag);
        spec.setContent(contentId);
        spec.setIndicator(getString(indicatorId));
        mTabHost.addTab(spec);
    }
    private void toggleProgressVisibility(final boolean show) {
        mProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        mTabHost.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public void startGetPositions() {
        toggleProgressVisibility(true);
    }

    @Override
    public void finishGetPositions() {
        toggleProgressVisibility(false);
    }

    @Override
    public void failedGetPositions(String message) {
        mErrorDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.app_name)
                .setMessage(message)
                .setOnCancelListener(dialog -> mGetPositionsPresenter.onErrorCancel())
                .show();
    }

    @Override
    public void hideError() {
        if (mErrorDialog != null && mErrorDialog.isShowing()) {
            mErrorDialog.cancel();
        }
    }

    @Override
    public void successGetPositions(List<Position> positions) {
        Timber.d("Interval positions: " + positions);

        ArrayList<GeoPoint> points = new ArrayList<>();
        for (Position position: positions) {
            points.add(new GeoPoint(position.getLatitude(), position.getLongitude()));
        }
        pathOverlay.setPoints(points);
        mMapView.zoomToBoundingBox(BoundingBox.fromGeoPoints(points), true);
        mMapView.postInvalidate();

        mList.setAdapter(new PositionsExpandableListAdapter(getContext(), 0, positions));
    }
}

