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
package org.erlymon.monitor.ui.activity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.arellomobile.mvp.MvpAppCompatActivity;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.squareup.otto.Bus;

import org.erlymon.monitor.MainApp;
import org.erlymon.monitor.R;
import org.erlymon.monitor.Screens;
import org.erlymon.monitor.mvp.model.Server;
import org.erlymon.monitor.mvp.model.User;
import org.erlymon.monitor.mvp.presenter.CheckSessionPresenter;
import org.erlymon.monitor.mvp.presenter.GetServerPresenter;
import org.erlymon.monitor.mvp.presenter.SignOutPresenter;
import org.erlymon.monitor.mvp.view.CheckSessionView;
import org.erlymon.monitor.mvp.view.GetServerView;
import org.erlymon.monitor.mvp.view.SignOutView;
import org.erlymon.monitor.ui.fragment.AboutFragment;
import org.erlymon.monitor.ui.fragment.DeviceFragment;
import org.erlymon.monitor.ui.fragment.DevicesFragment;
import org.erlymon.monitor.ui.fragment.MapFragment;
import org.erlymon.monitor.ui.fragment.PermissionsFragment;
import org.erlymon.monitor.ui.fragment.ReportFragment;
import org.erlymon.monitor.ui.fragment.ServerFragment;
import org.erlymon.monitor.ui.fragment.SignInFragment;
import org.erlymon.monitor.ui.fragment.SignUpFragment;
import org.erlymon.monitor.ui.fragment.UserFragment;
import org.erlymon.monitor.ui.fragment.UsersFragment;
import org.osmdroid.tileprovider.tilesource.bing.BingMapTileSource;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.terrakok.cicerone.Navigator;
import ru.terrakok.cicerone.NavigatorHolder;
import ru.terrakok.cicerone.Router;
import ru.terrakok.cicerone.android.SupportFragmentNavigator;
import timber.log.Timber;

public class MainActivity extends MvpAppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        SignInFragment.OnViewListener,
        GetServerView,
        CheckSessionView,
        SignOutView {
    @BindView(R.id.toolbar)
    Toolbar mToolbar;

    @BindView(R.id.progress)
    ProgressBar mProgress;

    @BindView(R.id.container)
    FrameLayout mContainer;

    @BindView(R.id.drawer_layout)
    DrawerLayout drawer;

    @BindView(R.id.nav_view)
    NavigationView navigationView;

    TextView mLabelName;

    TextView mLabelEmail;

    @InjectPresenter
    GetServerPresenter mGetServerPresenter;

    @InjectPresenter
    CheckSessionPresenter mCheckSessionPresenter;

    @InjectPresenter
    SignOutPresenter mSignOutPresenter;

    @Inject
    NavigatorHolder navigatorHolder;

    @Inject
    Router router;

    @Inject
    SharedPreferences sharedPreferences;

    @Inject
    Bus bus;

    private AlertDialog mErrorDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        MainApp.getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        LinearLayout linearLayout = (LinearLayout) navigationView.getHeaderView(0);
        mLabelName = (TextView) linearLayout.getChildAt(1);
        mLabelEmail = (TextView) linearLayout.getChildAt(2);



        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, mToolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView.setNavigationItemSelectedListener(this);

        navigatorHolder.setNavigator(navigator);
        mGetServerPresenter.getServer();

        Timber.d("CURRENT USER: " + getIntent().getParcelableExtra("session"));
    }

    @Override
    public void onResume() {
        super.onResume();
        bus.register(this);
    }

    @Override
    public void onPause() {
        bus.unregister(this);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        navigatorHolder.removeNavigator();
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        User session = getIntent().getParcelableExtra("session");
        Server server = getIntent().getParcelableExtra("server");
        Bundle args = new Bundle();
        args.putParcelable("session", session);
        args.putParcelable("server", server);
        switch (item.getItemId()) {
            case R.id.nav_map:
                router.newRootScreen(Screens.MAP_SCREEN, args);
                break;
            case R.id.nav_devices:
                router.newRootScreen(Screens.DEVICES_SCREEN, args);
                break;
            case R.id.nav_users:
                router.newRootScreen(Screens.USERS_SCREEN, args);
                break;
            case R.id.nav_server:
                router.navigateTo(Screens.SERVER_SCREEN, args);
                break;
            case R.id.nav_account:
                args.putLong("userId", session.getId());
                router.navigateTo(Screens.USER_SCREEN, args);
                break;
            case R.id.nav_about:
                router.navigateTo(Screens.ABOUT_SCREEN);
                break;
            case R.id.nav_sign_out:
                mSignOutPresenter.signOut();
                break;
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private Navigator navigator = new SupportFragmentNavigator(getSupportFragmentManager(),
            R.id.container) {
        @Override
        protected Fragment createFragment(String screenKey, Object data) {
            switch(screenKey) {
                case Screens.SIGN_IN_SCREEN:
                    return SignInFragment.getNewInstance(data);
                case Screens.SIGN_UP_SCREEN:
                    return SignUpFragment.getNewInstance(data);
                case Screens.MAP_SCREEN:
                    return MapFragment.getNewInstance((Bundle) data);
                case Screens.DEVICES_SCREEN:
                    return DevicesFragment.getNewInstance((Bundle) data);
                case Screens.USERS_SCREEN:
                    return UsersFragment.getNewInstance((Bundle) data);
                case Screens.SERVER_SCREEN:
                    return ServerFragment.getNewInstance((Bundle) data);
                case Screens.USER_SCREEN:
                    return UserFragment.getNewInstance((Bundle) data);
                case Screens.DEVICE_SCREEN:
                    return DeviceFragment.getNewInstance((Bundle) data);
                case Screens.PERMISSIONS_SCREEN:
                    return PermissionsFragment.getNewInstance((Bundle) data);
                case Screens.REPORT_SCREEN:
                    return ReportFragment.getNewInstance((Bundle) data);
                case Screens.ABOUT_SCREEN:
                    return AboutFragment.getNewInstance(data);
                default:
                    throw new RuntimeException("Unknown screen key!");
            }
        }

        @Override
        protected void showSystemMessage(String message) {
            Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
        }

        @Override
        protected void exit() {
            finish();
        }
    };

    private void toggleProgressVisibility(final boolean show) {
        mProgress.setVisibility(show ? View.VISIBLE : View.GONE);
        mContainer.setVisibility(show ? View.GONE : View.VISIBLE);
    }

    @Override
    public void startCheckSession() {
        toggleProgressVisibility(true);
    }

    @Override
    public void finishCheckSession() {
        toggleProgressVisibility(false);
    }

    @Override
    public void failedCheckSession(String message) {
        mToolbar.setVisibility(View.GONE);
        router.newRootScreen(Screens.SIGN_IN_SCREEN);
    }

    @Override
    public void startSignOut() {
        toggleProgressVisibility(true);
    }

    @Override
    public void finishSignOut() {
        toggleProgressVisibility(false);
    }

    @Override
    public void startGetServer() {
        toggleProgressVisibility(true);
    }

    @Override
    public void finishGetServer() {
        toggleProgressVisibility(false);
    }

    @Override
    public void failedGetServer(String message) {
        mErrorDialog = new AlertDialog.Builder(this)
                .setTitle(R.string.app_name)
                .setMessage(message)
                .setOnCancelListener(dialog -> mGetServerPresenter.onErrorCancel())
                .show();

        router.newRootScreen(Screens.SIGN_IN_SCREEN);
    }

    @Override
    public void hideError() {
        if (mErrorDialog != null && mErrorDialog.isShowing()) {
            mErrorDialog.cancel();
        }
    }


    @Override
    public void successGetServer(Server server) {
        getIntent().putExtra("server", server);
        BingMapTileSource.setBingKey(server.getBingKey());
        mCheckSessionPresenter.check();
    }

    @Override
    public void successSignOut() {
        sharedPreferences.edit()
                .putLong("userId", -1)
                .putBoolean("save", false)
                .putString("email", "")
                .putString("password", "")
                .apply();
        mToolbar.setVisibility(View.GONE);
        router.newRootScreen(Screens.SIGN_IN_SCREEN);
    }

    @Override
    public void successCheckSession(User user) {
        Timber.d("successCheckSession");
        successSignIn(user);
    }


    @Override
    public void successSignIn(User user) {
        getIntent().putExtra("session", user);

        mToolbar.setVisibility(View.VISIBLE);
        mLabelName.setText(user.getName());
        mLabelEmail.setText(user.getEmail());

        navigationView.getMenu().findItem(R.id.nav_users).setVisible(user.getAdmin());
        navigationView.getMenu().findItem(R.id.nav_server).setVisible(user.getAdmin());

        router.newRootScreen(Screens.MAP_SCREEN);
    }
}
