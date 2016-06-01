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

import android.os.Bundle
import android.support.v4.util.Pair
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_devices.*
import kotlinx.android.synthetic.main.content_devices.*
import org.erlymon.core.model.data.Device
import org.erlymon.core.model.data.Permission
import org.erlymon.core.model.data.User
import org.erlymon.core.presenter.PermissionsPresenter
import org.erlymon.core.presenter.PermissionsPresenterImpl
import org.erlymon.core.view.PermissionsView
import org.erlymon.monitor.R
import org.erlymon.monitor.view.adapter.DevicesPermissionsAdapter
import org.slf4j.LoggerFactory

class DevicesActivity : BaseActivity<PermissionsPresenter>(), PermissionsView {
    private var permission: Permission? = null

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        val id = item.itemId
        when (id) {
            android.R.id.home -> {
                onBackPressed()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_devices)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        presenter = PermissionsPresenterImpl(this, this)
        presenter?.onLoadDevices()
    }

    override fun showData(data: Pair<Array<Device>, Array<Device>>) {
        logger.debug("showData => " + data.first.size + " : " + data.second.size)
        devices.adapter = DevicesPermissionsAdapter(this, data,  object : DevicesPermissionsAdapter.PermissionListener{
            override fun createPermission(device: Device) {
                permission = Permission(intent.getParcelableExtra<User>("user").id, device.id)
                presenter?.onCreatePermissionButtonClick()
            }

            override fun deletePermission(device: Device) {
                permission = Permission(intent.getParcelableExtra<User>("user").id, device.id)
                presenter?.onDeletePermissionButtonClick()
            }
        })
    }

    override fun showCreatePermissionCompleted() {
        permission = null
    }

    override fun showRemovePermissionCompleted() {
        permission = null
    }

    override fun showError(error: String) {
        makeToast(toolbar, error)
    }

    override fun getPermission(): Permission? {
        return permission
    }

    override fun getUserId(): Long {
        return  intent.getParcelableExtra<User>("user").id
    }

    companion object {
        private val logger = LoggerFactory.getLogger(DevicesActivity::class.java)
    }
}
