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

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Since;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;

import java.util.Date;

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 5/4/16.
 */
@StorIOSQLiteType(table = DevicesTable.TABLE)
public class Device implements Parcelable {
    @StorIOSQLiteColumn(name = DevicesTable.COLUMN_ID, key = true)
    long id;

    @StorIOSQLiteColumn(name = DevicesTable.COLUMN_NAME)
    @Since(3.0)
    @SerializedName("name")
    @Expose
    String name;

    @StorIOSQLiteColumn(name = DevicesTable.COLUMN_UNIQUE_ID)
    @Since(3.0)
    @SerializedName("uniqueId")
    @Expose
    String uniqueId;

    @StorIOSQLiteColumn(name = DevicesTable.COLUMN_STATUS)
    @Since(3.3)
    @SerializedName("status")
    @Expose
    String status;

    //@StorIOSQLiteColumn(name = DevicesTable.COLUMN_LAST_UPDATE)
    @Since(3.3)
    @SerializedName("lastUpdate")
    @Expose
    Date lastUpdate;


    @StorIOSQLiteColumn(name = DevicesTable.COLUMN_POSITION_ID)
    @Since(3.4)
    @SerializedName("positionId")
    @Expose
    Long positionId;

    public Device() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUniqueId() {
        return uniqueId;
    }

    public void setUniqueId(String uniqueId) {
        this.uniqueId = uniqueId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    protected Device(Parcel in) {
        id = in.readLong();
        name = in.readString();
        uniqueId = in.readString();
        status = in.readString();
        long tmpLastUpdate = in.readLong();
        lastUpdate = tmpLastUpdate != -1 ? new Date(tmpLastUpdate) : null;
        positionId = in.readByte() == 0x00 ? null : in.readLong();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(uniqueId);
        dest.writeString(status);
        dest.writeLong(lastUpdate != null ? lastUpdate.getTime() : -1L);
        if (positionId == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(positionId);
        }
    }

    @SuppressWarnings("unused")
    public static final Creator<Device> CREATOR = new Creator<Device>() {
        @Override
        public Device createFromParcel(Parcel in) {
            return new Device(in);
        }

        @Override
        public Device[] newArray(int size) {
            return new Device[size];
        }
    };

    @Override
    public String toString() {
        return "Device{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", uniqueId='" + uniqueId + '\'' +
                ", status='" + status + '\'' +
                ", lastUpdate=" + lastUpdate +
                ", positionId=" + positionId +
                '}';
    }
}
