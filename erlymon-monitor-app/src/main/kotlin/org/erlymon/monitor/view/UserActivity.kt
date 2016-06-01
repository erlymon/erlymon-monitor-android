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
import android.util.Log
import android.view.MenuItem
import android.view.View
import kotlinx.android.synthetic.main.activity_user.*
import kotlinx.android.synthetic.main.content_user.*
import org.erlymon.core.model.data.User
import org.erlymon.core.presenter.UserPresenter
import org.erlymon.core.presenter.UserPresenterImpl
import org.erlymon.core.view.UserView
import org.erlymon.monitor.R
import org.slf4j.LoggerFactory

class UserActivity : BaseActivity<UserPresenter>(), UserView {
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
        setContentView(R.layout.activity_user)

        setSupportActionBar(toolbar)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        presenter = UserPresenterImpl(this, this)

        val session = intent.getParcelableExtra<User>("session")
        val user = intent.getParcelableExtra<User>("user")
        logger.debug("USER ID: " + user?.id + " USER: " + user?.toString())
        name.setText(user?.name)
        email.setText(user?.email)
        password.setText(user?.password)
        admin.setChecked(if (user?.admin != null) (user?.admin as Boolean) else false)
        admin.setEnabled(session?.admin as Boolean)
        map.setText(user?.map)
        distanceUnit.setText(user?.distanceUnit)
        speedUnit.setText(user?.speedUnit)
        latitude.setText(if (user?.latitude != null) user?.latitude.toString() else 0.0.toString())
        longitude.setText(if (user?.longitude != null) user?.longitude.toString() else 0.0.toString())
        zoom.setText(if (user?.zoom != null) user?.zoom.toString() else 0.toString())
        twelveHourFormat.setChecked(if (user?.twelveHourFormat != null) (user?.twelveHourFormat as Boolean) else false)

        fab_account_save.setOnClickListener {
            presenter?.onSaveButtonClick()
        }
    }

    override fun showData(data: User) {
        logger.debug(user.toString())
        val intent = Intent()
        intent.putExtra("user", user)
        setResult(RESULT_OK, intent)
        finish()
    }

    override fun showError(error: String) {
        makeToast(toolbar, error)
    }

    override fun getUserId(): Long {
        val user = intent.getParcelableExtra<User>("user")
        return if (user != null) user.id else 0
    }

    override fun getUser(): User {
        var user = intent.getParcelableExtra<User>("user")
        if (user == null) {
            user = User()
        }
        user.name = name.text.toString()
        user.email = email.text.toString()
        user.password = password.text.toString()
        user.admin = admin.isChecked
        user.map = map.text.toString()
        user.distanceUnit = distanceUnit.text.toString()
        user.speedUnit = speedUnit.text.toString()
        user.latitude = if (latitude.text.length > 0) latitude.text.toString().toDouble() else 0.0
        user.longitude = if (longitude.text.length > 0) longitude.text.toString().toDouble() else 0.0
        user.zoom = if (zoom.text.length > 0) zoom.text.toString().toInt() else 0
        user.twelveHourFormat = twelveHourFormat.isChecked
        return user
    }

    companion object {
        private val logger = LoggerFactory.getLogger(UserActivity::class.java)
    }
}
