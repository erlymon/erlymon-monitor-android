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
package org.erlymon.monitor.mvp.model;

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 5/26/16.
 */
public class Permission {
    private long userId;
    private long deviceId;

    public Permission() {}

    public void setUserId(long userId) {
        this.userId = userId;
    }

    public void setDeviceId(long deviceId) {
        this.deviceId = deviceId;
    }

    public static Permission newPermission(long userId, long deviceId) {
        Permission permission = new Permission();
        permission.setUserId(userId);
        permission.setDeviceId(deviceId);
        return  permission;
    }
}
