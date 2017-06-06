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
package org.erlymon.monitor;

import android.content.Context;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.facebook.stetho.Stetho;

import org.erlymon.monitor.dagger.AppComponent;
import org.erlymon.monitor.dagger.DaggerAppComponent;
import org.erlymon.monitor.dagger.module.BusModule;
import org.erlymon.monitor.dagger.module.ContextModule;
import org.erlymon.monitor.dagger.module.GsonModule;
import org.erlymon.monitor.dagger.module.HttpClientModule;
import org.erlymon.monitor.dagger.module.MainModule;
import org.erlymon.monitor.dagger.module.NavigationModule;
import org.erlymon.monitor.dagger.module.NetworkModule;
import org.erlymon.monitor.dagger.module.PreferencesModule;
import org.erlymon.monitor.dagger.module.RestApiModule;
import org.erlymon.monitor.dagger.module.RetrofitModule;
import org.erlymon.monitor.dagger.module.StorageModule;
import org.erlymon.monitor.dagger.module.WebSocketModule;

import io.fabric.sdk.android.Fabric;
import timber.log.Timber;

/**
 * Created by sergey on 21.03.17.
 */

public class MainApp extends MultiDexApplication {
    private static AppComponent sAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();

        Stetho.initializeWithDefaults(this);

        CrashlyticsCore core = new CrashlyticsCore.Builder()
                .disabled(BuildConfig.DEBUG)
                .build();
        Fabric.with(this, new Crashlytics.Builder().core(core).build(), new Crashlytics());

        // ...

        if (BuildConfig.DEBUG) {
            Timber.plant(new Timber.DebugTree());
        }
        Timber.plant(new CrashlyticsTree());

        MainApp.init(getBaseContext());
    }

    public static void init(Context context) {
        sAppComponent = DaggerAppComponent
                .builder()
                .busModule(new BusModule())
                .navigationModule(new NavigationModule())
                .contextModule(new ContextModule(context))
                .preferencesModule(new PreferencesModule())
                .gsonModule(new GsonModule())
                .httpClientModule(new HttpClientModule())
                .retrofitModule(new RetrofitModule())
                .restApiModule(new RestApiModule())
                .webSocketModule(new WebSocketModule())
                .networkModule(new NetworkModule())
                .storageModule(new StorageModule())
                .mainModule(new MainModule())
                .build();
    }

    public static AppComponent getAppComponent() {
        return sAppComponent;
    }
}
