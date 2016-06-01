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

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import kotlinx.android.synthetic.main.activity_server.*
import kotlinx.android.synthetic.main.content_server.*
import org.erlymon.core.model.data.Server
import org.erlymon.core.presenter.ServerPresenter
import org.erlymon.core.presenter.ServerPresenterImpl
import org.erlymon.core.view.ServerView
import org.erlymon.monitor.R
import org.slf4j.LoggerFactory

class ServerActivity : BaseActivity<ServerPresenter>(), ServerView {
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
        setContentView(R.layout.activity_server)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        presenter = ServerPresenterImpl(this, this)

        val server = intent.getParcelableExtra<Server>("server")
        logger.debug("SERVER ID: " + server?.id + " SERVER: " + server?.toString())
        registration.setChecked(if (server?.registration != null) (server?.registration as Boolean) else false)
        readonly.setChecked(if (server?.readonly != null) (server?.readonly as Boolean) else false)
        map.setText(server?.map)
        bingKey.setText(server?.bingKey)
        mapUrl.setText(server?.mapUrl)
        distanceUnit.setText(server?.distanceUnit)
        speedUnit.setText(server?.speedUnit)
        latitude.setText(server?.latitude.toString())
        longitude.setText(server?.longitude.toString())
        zoom.setText(server?.zoom.toString())
        twelveHourFormat.setChecked(if (server?.twelveHourFormat != null) (server?.twelveHourFormat as Boolean) else false)

        fab_account_save.setOnClickListener {
            presenter?.onSaveButtonClick()
        }
    }

    override fun showData(server: Server) {
        logger.debug(server.toString())

        val intent = Intent()
        intent.putExtra("server", server)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun showError(error: String) {
        makeToast(toolbar, error)
    }

    override fun getServerId(): Long {
        val server = intent.getParcelableExtra<Server>("server")
        return if (server != null) server.id else 0
    }

    override fun getServer(): Server {
        val server = intent.getParcelableExtra<Server>("server")
        server.registration = registration.isChecked
        server.readonly = readonly.isChecked
        server.map = map.text.toString()
        server.bingKey = bingKey.text.toString()
        server.mapUrl = mapUrl.text.toString()
        server.distanceUnit = distanceUnit.text.toString()
        server.speedUnit = speedUnit.text.toString()
        server.latitude = latitude.text.toString().toDouble()
        server.longitude = longitude.text.toString().toDouble()
        server.zoom = zoom.text.toString().toInt()
        server.twelveHourFormat = twelveHourFormat.isChecked
        return server
    }

    companion object {
        private val logger = LoggerFactory.getLogger(ServerActivity::class.java)
    }
}
