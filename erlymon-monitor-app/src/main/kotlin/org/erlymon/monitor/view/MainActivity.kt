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
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.app_bar_main.*
import kotlinx.android.synthetic.main.content_main.*
import org.erlymon.core.model.data.*
import org.erlymon.core.presenter.MainPresenter
import org.erlymon.core.presenter.MainPresenterImpl
import org.erlymon.core.view.MainView
import org.erlymon.monitor.R
import org.erlymon.monitor.view.adapter.CustomFragmentPagerAdapter
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
    private var deviceId: Long = 0
    private var userId: Long = 0
    private var command: Command? = null

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
        drawer_layout.setDrawerListener(toggle)
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

        presenter?.onLoadDevices()
    }

    override fun onStart() {
        super.onStart()
        presenter?.onOpenWebSocket()
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

    override fun showEvent(event: Event?) {
        logger.debug("showEvent => " + event.toString())
        if (event?.devices != null) {
            storage?.createOrUpdateDevices(event?.devices)
        }
        if (event?.positions != null) {
            storage?.createOrUpdatePositions(event?.positions)
        }
    }

    override fun showUsers(users: Array<User>) {
        logger.debug("showUsers => " + users.size)
        storage?.createOrUpdateUsers(users)
    }

    override fun showDevices(devices: Array<Device>) {
        logger.debug("showDevices => " + devices.size)
        storage?.createOrUpdateDevices(devices)
        presenter?.onLoadUsers()
    }

    override fun showCompleted() {
        storage?.deleteAll()
        finish()
    }

    override fun showRemoveDeviceCompleted() {
        storage?.removeDevice(deviceId)
        deviceId = 0
    }

    override fun showRemoveUserCompleted() {
        storage?.removeUser(userId)
        userId = 0
    }

    override fun getUserId(): Long {
        return userId
    }

    override fun getDeviceId(): Long {
        return deviceId
    }

    override fun getCommand(): Command? {
        command?.deviceId = deviceId
        return command
    }

    override fun showError(error: String) {
        makeToast(toolbar, error)
    }

    @SuppressWarnings("StatementWithEmptyBody")
    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        // Handle navigation view item clicks here.
        val id = item.itemId
        when (item.itemId) {
            R.id.nav_map -> {
                view_pager.setCurrentItem(0)
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
                    storage?.createOrUpdateServer(server)
                }
            REQUEST_CODE_UPDATE_ACCOUNT ->
                if (resultCode == RESULT_OK) {
                    val user = data?.getParcelableExtra<User>("session")
                    intent.putExtra("session", user)
                    storage?.createOrUpdateUser(user)
                    mAccountNameView?.text = user?.name
                    mAccountEmailView?.text = user?.email
                }
            REQUEST_CODE_CREATE_OR_UPDATE_DEVICE ->
                if (resultCode == RESULT_OK) {
                    val device = data?.getParcelableExtra<Device>("device")
                    storage?.createOrUpdateDevice(device)
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
        deviceId = device.id
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
        try {
            val position = storage?.getPositionById(device.positionId)
            (pagerAdapter?.getItem(0) as MapFragment).animateTo(GeoPoint(position?.latitude as Double, position?.longitude as Double), 15)
            view_pager.setCurrentItem(0)
            nav_view.setCheckedItem(R.id.nav_map)
        } catch (e: NullPointerException) {
            logger.warn(Log.getStackTraceString(e))
        }
    }

    override fun onSendCommand(device: Device) {
        deviceId = device.getId();
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
        userId = user.id
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
        this.command = command;
        presenter?.onSendCommandButtonClick()
    }

    companion object {
        private val logger = LoggerFactory.getLogger(MainActivity::class.java)
        private var backPressed: Long = 0

        val REQUEST_CODE_UPDATE_SERVER = 1
        val REQUEST_CODE_UPDATE_ACCOUNT = 2
        val REQUEST_CODE_CREATE_OR_UPDATE_DEVICE = 3
    }
}
