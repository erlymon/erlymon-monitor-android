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
package org.erlymon.monitor.api;

import org.erlymon.monitor.api.util.QueryDate;
import org.erlymon.monitor.mvp.model.Command;
import org.erlymon.monitor.mvp.model.Device;
import org.erlymon.monitor.mvp.model.Permission;
import org.erlymon.monitor.mvp.model.Position;
import org.erlymon.monitor.mvp.model.Server;
import org.erlymon.monitor.mvp.model.User;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.HTTP;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;
import rx.Observable;

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 5/4/16.
 */

public interface RestApi {
    @GET("server")
    Observable<Server> getServer();

    @PUT("server")
    Observable<Server> updateServer(@Body Server server);

    //http://web.erlymon.org/api/session
    @GET("session")
    Observable<User> getSession();

    @FormUrlEncoded
    @POST("session")
    Observable<User> createSession(@Field("email") String email, @Field("password") String password);

    //@Headers("Content-Type: application/json")
    @DELETE("session")
    Observable<Void> deleteSession();

    /**
     * Unrecognized field "twelveHourFormat" (class org.traccar.model.User), not marked as ignorable (13 known properties: "readonly", "name", "speedUnit", "latitude", "admin", "longitude", "zoom", "language", "distanceUnit", "id", "password", "email", "map"])
     */
    @POST("users")
    Observable<User> createUser(@Body User user);

    /**
     * {"details":"Column \"LANGUAGE\" not found; SQL statement:\nUPDATE users SET\n        name = ?,\n        email = ?,\n        admin = ?,\n        map = ?,\n        language = ?,\n        distanceUnit = ?,\n        speedUnit = ?,\n        latitude = ?,\n        longitude = ?,\n        zoom = ?\n        WHERE id = ?; [42122-191] - JdbcSQLException (... < QueryBuilder:61 < *:131 < DataManager:201 < UserResource:66 < ...)","message":"Column \"LANGUAGE\" not found; SQL statement:\nUPDATE users SET\n        name = ?,\n        email = ?,\n        admin = ?,\n        map = ?,\n        language = ?,\n        distanceUnit = ?,\n        speedUnit = ?,\n        latitude = ?,\n        longitude = ?,\n        zoom = ?\n        WHERE id = ?; [42122-191]"}
     */
    @PUT("users/{id}")
    Observable<User> updateUser(@Path("id") long id, @Body User user);

    @GET("users")
    Observable<List<User>> getUsers();

    @Headers("Content-Type: application/json")
    @DELETE("users/{id}")
    Observable<Void> deleteUser(@Path("id") long id);

    @POST("devices")
    Observable<Device> createDevice(@Body Device device);

    @PUT("devices/{id}")
    Observable<Device> updateDevice(@Path("id") long id, @Body Device device);

    //http://web.erlymon.org/api/devices/10246?_dc=1452209536829
    //http://web.erlymon.org/api/devices/10246
    @Headers("Content-Type: application/json")
    @DELETE("devices/{id}")
    Observable<Void> deleteDevice(@Path("id") long id);

    @GET("devices")
    Observable<List<Device>> getDevices();

    @GET("devices")
    Observable<List<Device>> getDevices(@Query("all") boolean all);

    @GET("devices")
    Observable<List<Device>> getDevices(@Query("userId") long userId);

    // http://web.erlymon.org/api/positions?_dc=1452345778504&deviceId=10290&from=2016-01-06T21%3A00%3A00.000Z&to=2016-01-09T20%3A45%3A00.000Z&page=1&start=0&limit=25
    @GET("positions")
    Observable<List<Position>> getPositions(@Query("deviceId") long deviceId, @Query("from") QueryDate from, @Query("to") QueryDate to);

    // {"deviceId":10995,"type":"engineResume","id":-1}
    // {"deviceId":10995,"type":"positionPeriodic","id":-1,"attributes":{"frequency":1}}
    @POST("commands")
    Observable<Void> createCommand(@Body Command command);

    @POST("permissions")
    Observable<Void> createPermission(@Body Permission permission);

    //@DELETE("permissions")
    @HTTP(method = "DELETE", path = "permissions", hasBody = true)
    Observable<Void> deletePermission(@Body Permission permission);
}