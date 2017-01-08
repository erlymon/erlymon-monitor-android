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
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_devices.*
import org.erlymon.core.model.data.Device
import org.erlymon.core.presenter.DevicesListPresenter
import org.erlymon.core.presenter.DevicesListPresenterImpl
import org.erlymon.core.view.DevicesListView
import org.erlymon.monitor.R
import org.erlymon.monitor.view.adapter.DevicesAdapter
import org.slf4j.LoggerFactory

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 4/7/16.
 */
class DevicesFragment : BaseFragment<DevicesListPresenter>(), DevicesListView {

    interface OnActionDeviceListener {
        fun onEditDevice(device: Device)
        fun onRemoveDevice(device: Device)
        fun onLoadPositions(device: Device)
        fun onShowOnMap(device: Device)
        fun onSendCommand(device: Device)
    }

    private var listener: OnActionDeviceListener? = null


    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface

        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = context as OnActionDeviceListener?
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException(context!!.toString() + " must implement OnActionDeviceListener")
        }

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_devices, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = DevicesListPresenterImpl(context, this)

        rv_devices.layoutManager = LinearLayoutManager(context)
        rv_devices.setHasFixedSize(true)

        rv_devices.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
    }

    override fun onResume() {
        super.onResume()
        presenter?.onLoadDevicesCache()
    }

    override fun showData(devices: List<Device>) {
        rv_devices.adapter = DevicesAdapter(context, devices)
    }

    override fun showError(error: String) {
        makeToast(rv_devices, error)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(DevicesFragment::class.java)

        fun newIntent(devices: Array<out Device>?) : DevicesFragment {
            val fragment = DevicesFragment()
            val args = Bundle()
            args.putParcelableArray("devices", devices)
            fragment.arguments = args
            return fragment;
        }
    }
}