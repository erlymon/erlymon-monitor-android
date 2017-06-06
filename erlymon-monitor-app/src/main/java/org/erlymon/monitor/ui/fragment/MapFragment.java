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
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.jakewharton.rxbinding.view.RxView;
import com.tbruyelle.rxpermissions.RxPermissions;

import org.erlymon.monitor.MainApp;
import org.erlymon.monitor.R;
import org.erlymon.monitor.map.DevicesMarkerClusterer;
import org.erlymon.monitor.map.MarkerWithLabel;
import org.erlymon.monitor.mvp.model.Device;
import org.erlymon.monitor.mvp.model.Event;
import org.erlymon.monitor.mvp.model.Position;
import org.erlymon.monitor.mvp.model.User;
import org.erlymon.monitor.mvp.presenter.CacheDevicesAndPositionsPresenter;
import org.erlymon.monitor.mvp.presenter.CacheDevicesPresenter;
import org.erlymon.monitor.mvp.presenter.CacheUserPresenter;
import org.erlymon.monitor.mvp.presenter.OpenWebSocketPresenter;
import org.erlymon.monitor.mvp.view.CacheDevicesAndPositionsView;
import org.erlymon.monitor.mvp.view.CacheDevicesView;
import org.erlymon.monitor.mvp.view.CacheUsersView;
import org.erlymon.monitor.mvp.view.GetUserView;
import org.erlymon.monitor.mvp.view.OpenWebSocketView;
import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.tileprovider.tilesource.bing.BingMapTileSource;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.Marker;
import org.osmdroid.views.overlay.TilesOverlay;
import org.osmdroid.views.overlay.mylocation.GpsMyLocationProvider;
import org.osmdroid.views.overlay.mylocation.MyLocationNewOverlay;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import butterknife.BindDrawable;
import butterknife.BindView;
import butterknife.ButterKnife;
import ru.terrakok.cicerone.Router;
import rx.functions.Action1;
import timber.log.Timber;

/**
 * Created by sergey on 31.03.17.
 */

public class MapFragment extends MvpAppCompatFragment implements GetUserView, CacheDevicesAndPositionsView, OpenWebSocketView {
    @BindView(R.id.mapview)
    MapView mMapView;
    @BindView(R.id.myPlace)
    CheckBox myPlace;

    @BindDrawable(R.drawable.ic_arrow_offline)
    Drawable arrowOfflineDrawable;

    @BindDrawable(R.drawable.ic_arrow_online)
    Drawable arrowOnlineDrawable;

    @BindDrawable(R.drawable.ic_arrow_unknown)
    Drawable arrowUnknownDrawable;

    @Inject
    Router router;

    @Inject
    SharedPreferences preferences;

    @InjectPresenter
    CacheDevicesAndPositionsPresenter mCacheDevicesAndPositionsPresenter;

    @InjectPresenter
    OpenWebSocketPresenter mOpenWebSocketPresenter;

    @InjectPresenter
    CacheUserPresenter mCacheUserPresenter;

    BingMapTileSource bing;
    private AlertDialog mErrorDialog;


    private DevicesMarkerClusterer mRadiusMarkerClusterer;
    private Map<Long, MarkerWithLabel> mMarkers = new HashMap<>();
    private MyLocationNewOverlay mLocationOverlay;

    public static MapFragment getNewInstance(Bundle args) {
        // Required empty public constructor
        MapFragment  instance = new MapFragment();
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
        View view = inflater.inflate(R.layout.fragment_map, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mMapView.setTilesScaledToDpi(true);
        mMapView.setMultiTouchControls(true);

        bing = new BingMapTileSource(null);

        RxView.clicks(myPlace)
                .compose(RxPermissions.getInstance(getContext()).ensure(Manifest.permission.ACCESS_COARSE_LOCATION))
                .subscribe(granted -> {
                    if (granted) {
                        if (myPlace.isChecked()) {
                            mLocationOverlay.enableFollowLocation();
                            mLocationOverlay.enableMyLocation();
                            mLocationOverlay.runOnFirstFix(() -> mMapView.post(() -> {
                                try {
                                    mMapView.getController().setZoom(15);
                                    mMapView.getController().animateTo(new GeoPoint(
                                            mLocationOverlay.getLastFix().getLatitude(),
                                            mLocationOverlay.getLastFix().getLongitude()
                                    ));
                                    mMapView.postInvalidate();
                                } catch (Exception e) {

                                }
                            }));
                        } else {
                            mLocationOverlay.disableFollowLocation();
                            mLocationOverlay.disableMyLocation();
                        }
                    } else {
                        myPlace.setChecked(false);
                        //makeToast(myPlace, getString(R.string.errorPermissionCoarseLocation))
                    }
                });

        mCacheUserPresenter.load(preferences.getLong("userId", -1));
        mCacheDevicesAndPositionsPresenter.load();
        mOpenWebSocketPresenter.open();
    }

    @Override
    public void onResume() {
        super.onResume();

        mRadiusMarkerClusterer = new DevicesMarkerClusterer(getContext());
        mRadiusMarkerClusterer.setIcon(BitmapFactory.decodeResource(getResources(), R.drawable.marker_cluster));
        mMapView.getOverlays().add(mRadiusMarkerClusterer);

        mLocationOverlay = new MyLocationNewOverlay(new GpsMyLocationProvider(getActivity()), mMapView);
        mLocationOverlay.disableFollowLocation();
        mLocationOverlay.disableMyLocation();
        mMapView.getOverlays().add(mLocationOverlay);
    }

    @Override
    public void onPause() {
        mLocationOverlay.disableFollowLocation();
        mLocationOverlay.disableMyLocation();

        mMapView.getOverlays().remove(mLocationOverlay);
        mMapView.getOverlays().remove(mRadiusMarkerClusterer);
        mMarkers.clear();
        super.onPause();
    }


    public void updateUnitMarker(Device device, Position position) {
        try {
            Timber.d("UPDATE MARKER: device: $device position: $position");
            MarkerWithLabel marker = mMarkers.get(device.getId());
            marker.setTitle(device.getName());

            if (position.getFixTime() != null) {
                marker.setSnippet(position.getFixTime().toString());
            }

            marker.setIcon(getStatusArrow(device.getStatus()));
            marker.setLabelAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM + 2.f);
            if (position.getCourse() != null) {
                marker.setRotation(position.getCourse());
            }
            marker.setPosition(new GeoPoint(position.getLatitude(), position.getLongitude()));
        } catch (Exception e) {
            Timber.w(e);
        }
    }

    public void addUnitMarker(Device device) {
        try {
            Timber.tag("MapFragment").d("ADD MARKER: device: $device");
            MarkerWithLabel marker = mMarkers.get(device.getId());
            if (marker == null) {
                marker = new MarkerWithLabel(mMapView);
                mRadiusMarkerClusterer.add(marker);
                mMarkers.put(device.getId(), marker);
            }
            marker.setIcon(getStatusArrow(device.getStatus()));
            marker.setLabelAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM + 2.f);
            marker.setRelatedObject(device);
            marker.setTitle(device.getName());
        } catch (Exception e) {
            Timber.w(e);
        }
    }

    private Drawable getStatusArrow(String status) {
        if (status == null) {
            return arrowUnknownDrawable;
        }
        switch (status) {
            case "online": return arrowOnlineDrawable;
            case "offline": return arrowOfflineDrawable;
            case "unknown": return arrowUnknownDrawable;
            default: return arrowUnknownDrawable;
        }
    }

    @Override
    public void startOpenWebSocket() {

    }

    @Override
    public void finishOpenWebSocket() {

    }

    @Override
    public void failedOpenWebSocket(String message) {

    }

    @Override
    public void startCacheDevicesAndPositions() {

    }

    @Override
    public void finishCacheDevicesAndPositions() {

    }

    @Override
    public void failedCacheDevicesAndPositions(String message) {

    }

    @Override
    public void startGetUser() {

    }

    @Override
    public void finishGetUser() {

    }

    @Override
    public void failedGetUser(String message) {

    }

    @Override
    public void hideError() {

    }

    @Override
    public void successGetUser(User user) {
        Timber.d("MAP TYPE: %s", user.getMap());
        // init map
        switch (user.getMap()) {
            case "bingAerial":
                bing.setStyle(BingMapTileSource.IMAGERYSET_AERIAL);
                mMapView.setTileSource(bing);
                break;
            case "bingRoad":
                bing.setStyle(BingMapTileSource.IMAGERYSET_ROAD);
                mMapView.setTileSource(bing);
                break;
            case "custom":
            default:
                mMapView.setTileSource(TileSourceFactory.DEFAULT_TILE_SOURCE);
        }
        mMapView.invalidate();
    }


    @Override
    public void successCacheDevicesAndPositions(List<Device> devices, List<Position> positions) {
        Timber.tag("MapFragment").d("successCacheDevicesAndPositions => devices: " + devices + " positions: " + positions);
        long deviceId = getArguments().getLong("deviceId", -1);
        Timber.tag("MapFragment").d("renderMap => deviceId: " + deviceId);
        renderMarkers(devices, positions);
    }

    @Override
    public void successOpenWebSocket(Event event) {
        Timber.tag("MapFragment").d("successOpenWebSocket => Event: " + event);
        renderMarkers(event.getDevices(), event.getPositions());
    }


    private void renderMarkers(List<Device> devices, List<Position> positions) {
        if (devices != null) {
            for (Device device : devices) {
                //Timber.tag("MapFragment").d("showEvent: device: $device");
                addUnitMarker(device);
            }
        }

        if (positions != null) {
            for (Position position : positions) {
                //Timber.tag("MapFragment").d("showEvent: position: $position");
                MarkerWithLabel marker = mMarkers.get(position.getDeviceId());
                if (marker != null) {
                    updateUnitMarker((Device) marker.getRelatedObject(), position);
                }
            }
        }

        long deviceId = getArguments().getLong("deviceId", -1);
        if (deviceId != -1 && mMarkers.get(deviceId) != null) {
            mMapView.getController().animateTo(mMarkers.get(deviceId).getPosition());
        }

        mRadiusMarkerClusterer.invalidate();
        mMapView.postInvalidate();
    }
}
