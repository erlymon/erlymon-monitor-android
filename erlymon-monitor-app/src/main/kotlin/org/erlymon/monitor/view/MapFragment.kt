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
package org.erlymon.monitor.view

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import io.realm.RealmChangeListener

import org.osmdroid.bonuspack.clustering.RadiusMarkerClusterer
import org.osmdroid.bonuspack.overlays.Marker
import org.osmdroid.util.GeoPoint
import org.slf4j.LoggerFactory
import java.util.*

import kotlinx.android.synthetic.main.fragment_map.*
import org.erlymon.core.model.data.Device
import org.erlymon.monitor.R
import org.osmdroid.util.BoundingBoxE6

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 4/7/16.
 */
class MapFragment : BaseFragment() {

    private inner class DevicesMarkerClusterer(ctx: Context) : RadiusMarkerClusterer(ctx) {

        fun remove(marker: Marker) {
            mItems.remove(marker)
        }
    }

    private var mRadiusMarkerClusterer: DevicesMarkerClusterer? = null
    private var markers: MutableMap<Long, Marker> = HashMap()


    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_map, container, false)
    }

    private var arrowDrawable: Drawable? = null

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        arrowDrawable = resources.getDrawable(R.drawable.ic_arrow)

        mapview.isTilesScaledToDpi = true
        mapview.setMultiTouchControls(true)

        mapview.postDelayed({
            try {
                val user = storage.firstUser

                if (user.latitude === 0.0 && user.longitude === 0.0 && user.zoom === 0) {
                    val server = storage.firstServer
                    if (server.latitude === 0.0 && server.longitude === 0.0 && server.zoom === 0) {
                        animateTo(GeoPoint(server.latitude!!, server.longitude!!), server.zoom!!)
                    }
                } else {
                    animateTo(GeoPoint(user.latitude, user.longitude), user.zoom)
                }
            } catch (ignore: NullPointerException) {
                //logger.warn(Log.getStackTraceString(e))
            }
            mapview.postInvalidate()
        }, 1000)
    }

    override fun onResume() {
        super.onResume()

        mapview.postDelayed({
            try {
                val user = storage.firstUser

                if (user.latitude === 0.0 && user.longitude === 0.0 && user.zoom === 0) {
                    val server = storage.firstServer
                    if (server.latitude === 0.0 && server.longitude === 0.0 && server.zoom === 0) {
                        animateTo(GeoPoint(server.latitude!!, server.longitude!!), server.zoom!!)
                    }
                } else {
                    animateTo(GeoPoint(user.latitude, user.longitude), user.zoom)
                }
            } catch (ignore: NullPointerException) {
                //logger.warn(Log.getStackTraceString(e))
            }
            mapview.postInvalidate()
        }, 1000)

        mRadiusMarkerClusterer = DevicesMarkerClusterer(context)
        mRadiusMarkerClusterer?.setIcon(BitmapFactory.decodeResource(resources, R.drawable.marker_cluster))
        mapview.overlays.add(mRadiusMarkerClusterer)

        storage.getDevices()?.addChangeListener{ res ->
            res.forEach { device ->
                updateUnitMarker(device)
            }
            mRadiusMarkerClusterer?.invalidate()
            mapview?.postInvalidate()
        }
    }

    override fun onPause() {
        storage.getDevices()?.removeChangeListeners()
        mapview.overlays.remove(mRadiusMarkerClusterer)
        markers.clear()
        super.onPause()
    }

    fun animateTo(geoPoint: GeoPoint, zoom: Int) {
        mapview.controller.setZoom(zoom)
        mapview.controller.animateTo(geoPoint)
        mapview.postInvalidate()
    }

    fun zoomToBoundingBox(boundingBoxE6: BoundingBoxE6) {
        mapview.zoomToBoundingBox(boundingBoxE6)
        mapview.postInvalidate()
    }

    private fun removeUnitMarker(device: Device) {
        try {
            logger.debug("REMOVE MARKER: " + device)
            val marker = markers[device.id]
            logger.debug("REMOVE MARKER: " + marker)
            mRadiusMarkerClusterer?.remove(marker as Marker)
            markers.remove(device.id)
            marker?.remove(mapview)
        } catch (e: NullPointerException) {
            logger.warn(Log.getStackTraceString(e))
        }

    }

    private fun updateUnitMarker(device: Device) {
        try {
            logger.debug("UPDATE MARKER: " + device)
            val position = storage.getPositionById(device.positionId)
            if (position != null) {
                var marker: Marker? = markers[device.id]
                if (marker == null) {
                    marker = Marker(mapview)
                    mRadiusMarkerClusterer?.add(marker)
                    markers.put(device.id, marker)

                }
                marker.title = device.name
                marker.snippet = position.fixTime.toString()

                marker.setIcon(arrowDrawable)
                if (position.course != null) {
                    marker.rotation = position.course
                }
                marker.position = GeoPoint(position.latitude, position.longitude)
            }
        } catch (e: Exception) {
            logger.warn(Log.getStackTraceString(e))
        }
    }

    companion object {
        private val logger = LoggerFactory.getLogger(MapFragment::class.java)
    }
}