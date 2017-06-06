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
package org.erlymon.monitor.dagger;

import org.erlymon.monitor.dagger.module.BusModule;
import org.erlymon.monitor.dagger.module.ContextModule;
import org.erlymon.monitor.dagger.module.GsonModule;
import org.erlymon.monitor.dagger.module.HttpClientModule;
import org.erlymon.monitor.dagger.module.MainModule;
import org.erlymon.monitor.dagger.module.NavigationModule;
import org.erlymon.monitor.dagger.module.PreferencesModule;
import org.erlymon.monitor.dagger.module.RestApiModule;
import org.erlymon.monitor.dagger.module.NetworkModule;
import org.erlymon.monitor.dagger.module.RetrofitModule;
import org.erlymon.monitor.dagger.module.StorageModule;
import org.erlymon.monitor.dagger.module.WebSocketModule;
import org.erlymon.monitor.mvp.presenter.CacheDevicePresenter;
import org.erlymon.monitor.mvp.presenter.CacheDevicesAndPositionsPresenter;
import org.erlymon.monitor.mvp.presenter.CacheDevicesPresenter;
import org.erlymon.monitor.mvp.presenter.CacheServerPresenter;
import org.erlymon.monitor.mvp.presenter.CacheUserPresenter;
import org.erlymon.monitor.mvp.presenter.CacheUsersPresenter;
import org.erlymon.monitor.mvp.presenter.CheckSessionPresenter;
import org.erlymon.monitor.mvp.presenter.CreateDevicePresenter;
import org.erlymon.monitor.mvp.presenter.CreatePermissionPresenter;
import org.erlymon.monitor.mvp.presenter.CreateUserPresenter;
import org.erlymon.monitor.mvp.presenter.DeleteDevicePresenter;
import org.erlymon.monitor.mvp.presenter.DeletePermissionPresenter;
import org.erlymon.monitor.mvp.presenter.DeleteUserPresenter;
import org.erlymon.monitor.mvp.presenter.GetPermissionsPresenter;
import org.erlymon.monitor.mvp.presenter.GetPositionsPresenter;
import org.erlymon.monitor.mvp.presenter.GetServerPresenter;
import org.erlymon.monitor.mvp.presenter.OpenWebSocketPresenter;
import org.erlymon.monitor.mvp.presenter.SendCommandPresenter;
import org.erlymon.monitor.mvp.presenter.SignInPresenter;
import org.erlymon.monitor.mvp.presenter.SignOutPresenter;
import org.erlymon.monitor.mvp.presenter.SignUpPresenter;
import org.erlymon.monitor.mvp.presenter.UpdateDevicePresenter;
import org.erlymon.monitor.mvp.presenter.UpdateServerPresenter;
import org.erlymon.monitor.mvp.presenter.UpdateUserPresenter;
import org.erlymon.monitor.ui.activity.MainActivity;
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

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by sergey on 30.03.17.
 */
@Singleton
@Component(modules = {
        BusModule.class,
        NavigationModule.class,
        ContextModule.class,
        PreferencesModule.class,
        HttpClientModule.class,
        GsonModule.class,
        RetrofitModule.class,
        RestApiModule.class,
        WebSocketModule.class,
        StorageModule.class,
        NetworkModule.class,
        MainModule.class
})
public interface AppComponent {
    void inject(MainActivity activity);

    void inject(SignInFragment fragment);
    void inject(SignUpFragment fragment);
    void inject(MapFragment fragment);
    void inject(DevicesFragment fragment);
    void inject(UsersFragment fragment);
    void inject(DeviceFragment fragment);
    void inject(UserFragment fragment);
    void inject(ServerFragment fragment);
    void inject(ReportFragment fragment);
    void inject(PermissionsFragment fragment);

    void inject(GetServerPresenter presenter);
    void inject(CacheServerPresenter presenter);
    void inject(UpdateServerPresenter presenter);
    void inject(CheckSessionPresenter presenter);
    void inject(SignInPresenter presenter);
    void inject(SignOutPresenter presenter);
    void inject(SignUpPresenter presenter);
    void inject(CacheDevicePresenter presenter);
    void inject(CacheDevicesPresenter presenter);
    void inject(CacheUsersPresenter presenter);
    void inject(CacheUserPresenter presenter);
    void inject(CreateUserPresenter presenter);
    void inject(CreateDevicePresenter presenter);
    void inject(UpdateUserPresenter presenter);
    void inject(UpdateDevicePresenter presenter);
    void inject(DeleteUserPresenter presenter);
    void inject(DeleteDevicePresenter presenter);
    void inject(GetPermissionsPresenter presenter);
    void inject(CreatePermissionPresenter presenter);
    void inject(DeletePermissionPresenter presenter);
    void inject(SendCommandPresenter presenter);
    void inject(GetPositionsPresenter presenter);
    void inject(OpenWebSocketPresenter presenter);
    void inject(CacheDevicesAndPositionsPresenter presenter);
}
