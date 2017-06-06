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
package org.erlymon.monitor.dagger.module;


import android.content.SharedPreferences;

import com.appunite.websocket.rx.RxWebSockets;
import com.appunite.websocket.rx.object.GsonObjectSerializer;
import com.appunite.websocket.rx.object.ObjectSerializer;
import com.appunite.websocket.rx.object.RxObjectWebSockets;
import com.google.gson.Gson;

import org.erlymon.monitor.mvp.model.Event;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.Request;

/**
 * Created by sergey on 17.03.17.
 */

@Module(includes = {HttpClientModule.class, GsonModule.class})
public class WebSocketModule {
    @Provides
    @Singleton
    public RxObjectWebSockets provideRxObjectWebSockets(RxWebSockets rxWebSockets, ObjectSerializer serializer) {
        return new RxObjectWebSockets(rxWebSockets, serializer);
    }

    @Provides
    @Singleton
    public RxWebSockets provideRxWebSockets(OkHttpClient client, SharedPreferences sharedPreferences) {
        boolean sslOrTls = sharedPreferences.getBoolean("sslOrTls", false);
        String dns = sharedPreferences.getString("dns", "web.erlymon.org");
        return new RxWebSockets(client, new Request.Builder()
                .get()
                .url("{socket}://{dns}/api/socket".replace("{socket}", sslOrTls? "wss" : "ws").replace("{dns}", dns))
                //.url("{socket}://{dns}/api/socket".replace("{socket}", mSslOrTls ? "wss" : "ws").replace("{dns}", mDns))
                .addHeader("Sec-WebSocket-Protocol", "chat")
                .build());

    }

    @Provides
    @Singleton
    public ObjectSerializer provideObjectSerializer(Gson gson) {
        return new GsonObjectSerializer(gson, Event.class);
    }
}
