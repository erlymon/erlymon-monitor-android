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

import android.content.DialogInterface
import android.content.Intent
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v7.app.ActionBarDrawerToggle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import io.realm.RealmResults
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.erlymon.core.model.data.*
import org.erlymon.core.presenter.MainPresenter
import org.erlymon.core.presenter.MainPresenterImpl
import org.erlymon.core.view.MainView
import org.erlymon.monitor.R
import org.erlymon.monitor.view.adapter.CustomFragmentPagerAdapter
import org.erlymon.monitor.view.adapter.DevicesAdapter
import org.erlymon.monitor.view.adapter.UsersAdapter
import org.erlymon.monitor.view.fragment.ConfirmDialogFragment
import org.erlymon.monitor.view.fragment.SendCommandDialogFragment
import org.osmdroid.util.GeoPoint

import org.slf4j.LoggerFactory

class MainActivity : BaseActivity<MainPresenter>(),
        MainView,
        NavigationView.OnNavigationItemSelectedListener,
        DevicesFragment.OnActionDeviceListener,
        UsersFragment.OnActionUserListener,
        ConfirmDialogFragment.ConfirmDialogListener,
        SendCommandDialogFragment.SendCommandDialogListener {

    private var pagerAdapter: CustomFragmentPagerAdapter? = null

    private var mAccountNameView: TextView? = null
    private var mAccountEmailView: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setSupportActionBar(toolbar)

        presenter = MainPresenterImpl(this, this)

        val linearLayout = nav_view.getHeaderView(0) as LinearLayout
        mAccountNameView = linearLayout.getChildAt(1) as TextView
        mAccountEmailView = linearLayout.getChildAt(2) as TextView

        val session = intent.getParcelableExtra<User>("session")
        mAccountNameView?.text = session?.name
        mAccountEmailView?.text = session?.email

        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        nav_view.setNavigationItemSelectedListener(this)

        (nav_view.menu.findItem(R.id.nav_users) as MenuItem).isVisible = session?.admin!!
        (nav_view.menu.findItem(R.id.nav_server) as MenuItem).isVisible = session?.admin!!


        pagerAdapter = CustomFragmentPagerAdapter(supportFragmentManager)
        pagerAdapter?.addPage(MapFragment())
        pagerAdapter?.addPage(DevicesFragment())
        pagerAdapter?.addPage(UsersFragment())
        view_pager.setAdapter(pagerAdapter)

        view_pager.addOnPageChangeListener(object : ViewPager.OnPageChangeListener {

            override fun onPageSelected(position: Int) {
                logger.debug("onPageSelected, position = " + position)
                when (position) {
                    0 -> {
                        fab.visibility = View.GONE
                        supportActionBar?.setTitle(R.string.mapTitle)
                    }
                    1 -> {
                        fab.visibility = View.VISIBLE
                        supportActionBar?.setTitle(R.string.settingsDevices)
                    }
                    2 -> {
                        fab.visibility = View.VISIBLE
                        supportActionBar?.setTitle(R.string.settingsUsers)
                    }
                }
            }

            override fun onPageScrolled(position: Int, positionOffset: Float,
                                        positionOffsetPixels: Int) {
            }

            override fun onPageScrollStateChanged(state: Int) {
            }
        })

        fab.setOnClickListener {
            when (view_pager.currentItem) {
                1 -> {
                    logger.debug("Start DeviceActivity")
                    val intent = Intent(this@MainActivity, DeviceActivity::class.java)
                            .putExtra("session", intent.getParcelableExtra<User>("session"))
                    startActivity(intent)
                }
                2 -> {
                    logger.debug("Start UserActivity")
                    val intent = Intent(this@MainActivity, UserActivity::class.java)
                            .putExtra("session", intent.getParcelableExtra<User>("session"))
                    startActivity(intent)
                }
            }
        }
    }

    override fun onBackPressed() {
        if (drawer_layout.isDrawerOpen(GravityCompat.START)) {
            drawer_layout.closeDrawer(GravityCompat.START)
        } else {
            //super.onBackPressed();
            if (backPressed + 2000 > System.currentTimeMillis()) {
                presenter?.onDeleteSessionButtonClick()
            } else {
                Toast.makeText(baseContext, getString(R.string.sharedBackPressed), Toast.LENGTH_SHORT).show()
                backPressed = System.currentTimeMillis()
            }
        }
    }

    override fun showPosition(position: Position) {
        try {
            (pagerAdapter?.getItem(0) as MapFragment).animateTo(GeoPoint(position.latitude, position.longitude), 15)
            view_pager.setCurrentItem(0)
            nav_view.setCheckedItem(R.id.nav_map)
        } catch (e: Exception) {
            logger.warn(Log.getStackTraceString(e))
        }
    }

    override fun showCompleted() {
        finish()
    }

    override fun showRemoveDeviceCompleted() {
        intent.putExtra("deviceId", 0)
    }

    override fun showRemoveUserCompleted() {
        intent.putExtra("userId", 0)
    }

    override fun getUserId(): Long {
        return intent.getLongExtra("userId", 0)
    }

    override fun getDeviceId(): Long {
        return intent.getLongExtra("deviceId", 0)
    }

    override fun getPositionId(): Long {
        return intent.getLongExtra("positionId", 0)
    }

    override fun getCommand(): Command {
        val command = intent.getParcelableExtra<Command>("command")
        command.deviceId = deviceId
        return command
    }

    override fun showError(error: String) {
        makeToast(toolbar, error)
    }

    @SuppressWarnings("StatementWithEmptyBody")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        when (item.itemId) {
            R.id.nav_map -> {
                view_pager.setCurrentItem(0)
                val center = calculateMapCenter()
                (pagerAdapter?.getItem(0) as MapFragment).animateTo(center.first, center.second)
            }
            R.id.nav_devices -> {
                view_pager.setCurrentItem(1)
            }
            R.id.nav_users -> {
                view_pager.setCurrentItem(2)
            }
            R.id.nav_server -> {
                val intent = Intent(this@MainActivity, ServerActivity::class.java)
                        .putExtra("session", intent.getParcelableExtra<User>("session"))
                        .putExtra("server", intent.getParcelableExtra<Server>("server"))
                startActivityForResult(intent, REQUEST_CODE_UPDATE_SERVER)
            }
            R.id.nav_account -> {
                val intent = Intent(this@MainActivity, UserActivity::class.java)
                        .putExtra("session", intent.getParcelableExtra<User>("session"))
                        .putExtra("user", intent.getParcelableExtra<User>("session"))
                startActivityForResult(intent, REQUEST_CODE_UPDATE_ACCOUNT)
            }
            R.id.nav_about -> {
                startActivity(Intent(this@MainActivity, AboutActivity::class.java))
            }
            R.id.nav_sign_out -> {
                presenter?.onDeleteSessionButtonClick()
            }
        }

        drawer_layout.closeDrawer(GravityCompat.START)
        return true
    }

    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_CODE_UPDATE_SERVER ->
                if (resultCode == RESULT_OK) {
                    val server = data?.getParcelableExtra<Server>("server")
                    intent.putExtra("server", server)
                }
            REQUEST_CODE_UPDATE_ACCOUNT ->
                if (resultCode == RESULT_OK) {
                    val user = data?.getParcelableExtra<User>("user")
                    intent.putExtra("session", user)
                    mAccountNameView?.text = user?.name
                    mAccountEmailView?.text = user?.email
                }
            REQUEST_CODE_CREATE_OR_UPDATE_DEVICE ->
                if (resultCode == RESULT_OK) {
                }
        }
    }

    override fun onEditDevice(device: Device) {
        val intent = Intent(this@MainActivity, DeviceActivity::class.java)
                .putExtra("session", intent.getParcelableExtra<User>("session"))
                .putExtra("device", device)
        startActivity(intent)
    }

    override fun onRemoveDevice(device: Device) {
        intent.putExtra("deviceId", device.id)
        val dialogFragment = ConfirmDialogFragment.newInstance(R.string.deviceTitle, R.string.sharedRemoveConfirm)
        dialogFragment.show(supportFragmentManager, "remove_item_dialog")
    }

    override fun onLoadPositions(device: Device) {
        val intent = Intent(this@MainActivity, PositionsActivity::class.java)
                .putExtra("session", intent.getParcelableExtra<User>("session"))
                .putExtra("device", device)
        startActivity(intent)
    }

    override fun onShowOnMap(device: Device) {
        intent.putExtra("positionId", device.positionId)
        presenter?.onGetPostionByCache()
    }

    override fun onSendCommand(device: Device) {
        intent.putExtra("deviceId", device.id)
        val dialogFragment = SendCommandDialogFragment.newInstance(device.id)
        dialogFragment.show(supportFragmentManager, "send_command_dialog")
    }

    override fun onEditUser(user: User) {
        val intent = Intent(this@MainActivity, UserActivity::class.java)
                .putExtra("session", intent.getParcelableExtra<User>("session"))
                .putExtra("user", user)
        startActivity(intent)
    }

    override fun onRemoveUser(user: User) {
        intent.putExtra("userId", user.id)
        val dialogFragment = ConfirmDialogFragment.newInstance(R.string.deviceTitle, R.string.sharedRemoveConfirm)
        dialogFragment.show(supportFragmentManager, "remove_item_dialog")
    }

    override fun onPermissionForUser(user: User) {
        val intent = Intent(this@MainActivity, DevicesActivity::class.java)
                .putExtra("session", intent.getParcelableExtra<User>("session"))
                .putExtra("user", user)
        startActivity(intent)
    }

    override fun onPositiveClick(dialog: DialogInterface, which: Int) {
        if (deviceId > 0) {
            presenter?.onDeleteDeviceButtonClick()
        }
        if (userId > 0) {
            presenter?.onDeleteUserButtonClick()
        }
    }

    override fun onSendCommand(command: Command?) {
        intent.putExtra("command", command)
        presenter?.onSendCommandButtonClick()
    }

    private fun calculateMapCenter() :Pair<GeoPoint, Int> {
        val user = intent.getParcelableExtra<User>("session")
        if (user.latitude === 0.0 && user.longitude === 0.0 && user.zoom === 0) {
            val server = intent.getParcelableExtra<Server>("server")
            if (server.latitude === 0.0 && server.longitude === 0.0 && server.zoom === 0) {
                return Pair(GeoPoint(server.latitude, server.longitude), server.zoom)
            }
        } else {
            return Pair(GeoPoint(user.latitude, user.longitude), user.zoom)
        }
        return Pair(GeoPoint(0, 0), 0)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(MainActivity::class.java)
        private var backPressed: Long = 0

        val REQUEST_CODE_UPDATE_SERVER = 1
        val REQUEST_CODE_UPDATE_ACCOUNT = 2
        val REQUEST_CODE_CREATE_OR_UPDATE_DEVICE = 3
    }
}
