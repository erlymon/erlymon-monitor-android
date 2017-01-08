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
package org.erlymon.core.model.data;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Since;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteColumn;
import com.pushtorefresh.storio.sqlite.annotations.StorIOSQLiteType;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 5/4/16.
 */
@StorIOSQLiteType(table = PositionsTable.TABLE)
public class Position implements Parcelable {
    @StorIOSQLiteColumn(name = PositionsTable.COLUMN_ID, key = true)
    Long id;

    @StorIOSQLiteColumn(name = PositionsTable.COLUMN_PROTOCOL)
    @Since(3.0)
    String protocol;

    @StorIOSQLiteColumn(name = PositionsTable.COLUMN_DEVICE_ID)
    @Since(3.0)
    Long deviceId;

    @StorIOSQLiteColumn(name = PositionsTable.COLUMN_SERVER_TIME)
    @Since(3.0)
    Date serverTime;

    @StorIOSQLiteColumn(name = PositionsTable.COLUMN_DEVICE_TIME)
    @Since(3.0)
    Date deviceTime;

    @StorIOSQLiteColumn(name = PositionsTable.COLUMN_FIX_TIME)
    @Since(3.0)
    Date fixTime;

    @StorIOSQLiteColumn(name = PositionsTable.COLUMN_OUTDATED)
    Boolean outdated;

    @StorIOSQLiteColumn(name = PositionsTable.COLUMN_VALID)
    @Since(3.0)
    Boolean valid;

    @StorIOSQLiteColumn(name = PositionsTable.COLUMN_LATITUDE)
    @Since(3.0)
    Double latitude;

    @StorIOSQLiteColumn(name = PositionsTable.COLUMN_LONGITUDE)
    @Since(3.0)
    Double longitude;

    @StorIOSQLiteColumn(name = PositionsTable.COLUMN_ALTITUDE)
    @Since(3.0)
    Double altitude;

    @StorIOSQLiteColumn(name = PositionsTable.COLUMN_SPEED)
    @Since(3.0)
    Float speed;

    @StorIOSQLiteColumn(name = PositionsTable.COLUMN_COURSE)
    @Since(3.0)
    Float course;

    @StorIOSQLiteColumn(name = PositionsTable.COLUMN_ADDRESS)
    @Since(3.0)
    String address;

    //@StorIOSQLiteColumn(name = PositionsTable.COLUMN_ATTRIBUTES)
    @Since(3.2)
    Map<String, Object> attributes;

    public Position() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getProtocol() {
        return protocol;
    }

    public void setProtocol(String protocol) {
        this.protocol = protocol;
    }

    public Long getDeviceId() {
        return deviceId;
    }

    public void setDeviceId(Long deviceId) {
        this.deviceId = deviceId;
    }

    public Date getServerTime() {
        return serverTime;
    }

    public void setServerTime(Date serverTime) {
        this.serverTime = serverTime;
    }

    public Date getDeviceTime() {
        return deviceTime;
    }

    public void setDeviceTime(Date deviceTime) {
        this.deviceTime = deviceTime;
    }

    public Date getFixTime() {
        return fixTime;
    }

    public void setFixTime(Date fixTime) {
        this.fixTime = fixTime;
    }

    public Boolean getOutdated() {
        return outdated;
    }

    public void setOutdated(Boolean outdated) {
        this.outdated = outdated;
    }

    public Boolean getValid() {
        return valid;
    }

    public void setValid(Boolean valid) {
        this.valid = valid;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public Double getAltitude() {
        return altitude;
    }

    public void setAltitude(Double altitude) {
        this.altitude = altitude;
    }

    public Float getSpeed() {
        return speed;
    }

    public void setSpeed(Float speed) {
        this.speed = speed;
    }

    public Float getCourse() {
        return course;
    }

    public void setCourse(Float course) {
        this.course = course;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public void setAttributes(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    protected Position(Parcel in) {
        id = in.readByte() == 0x00 ? null : in.readLong();
        protocol = in.readString();
        deviceId = in.readByte() == 0x00 ? null : in.readLong();
        long tmpServerTime = in.readLong();
        serverTime = tmpServerTime != -1 ? new Date(tmpServerTime) : null;
        long tmpDeviceTime = in.readLong();
        deviceTime = tmpDeviceTime != -1 ? new Date(tmpDeviceTime) : null;
        long tmpFixTime = in.readLong();
        fixTime = tmpFixTime != -1 ? new Date(tmpFixTime) : null;
        byte outdatedVal = in.readByte();
        outdated = outdatedVal == 0x02 ? null : outdatedVal != 0x00;
        byte realVal = in.readByte();
        valid = realVal == 0x02 ? null : realVal != 0x00;
        latitude = in.readByte() == 0x00 ? null : in.readDouble();
        longitude = in.readByte() == 0x00 ? null : in.readDouble();
        altitude = in.readByte() == 0x00 ? null : in.readDouble();
        speed = in.readByte() == 0x00 ? null : in.readFloat();
        course = in.readByte() == 0x00 ? null : in.readFloat();
        address = in.readString();
        //attributes = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (id == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(id);
        }
        dest.writeString(protocol);
        if (deviceId == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeLong(deviceId);
        }
        dest.writeLong(serverTime != null ? serverTime.getTime() : -1L);
        dest.writeLong(deviceTime != null ? deviceTime.getTime() : -1L);
        dest.writeLong(fixTime != null ? fixTime.getTime() : -1L);
        if (outdated == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (outdated ? 0x01 : 0x00));
        }
        if (valid == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (valid ? 0x01 : 0x00));
        }
        if (latitude == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(latitude);
        }
        if (longitude == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(longitude);
        }
        if (altitude == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeDouble(altitude);
        }
        if (speed == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeFloat(speed);
        }
        if (course == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeFloat(course);
        }
        dest.writeString(address);
        //dest.writeString(attributes);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<Position> CREATOR = new Parcelable.Creator<Position>() {
        @Override
        public Position createFromParcel(Parcel in) {
            return new Position(in);
        }

        @Override
        public Position[] newArray(int size) {
            return new Position[size];
        }
    };

    public static List<String> createList(Position position) {
        List<String> array = new ArrayList<>();
        array.add("" + position.getValid());
        array.add("" + position.getFixTime().toString());
        array.add("" + position.getLatitude());
        array.add("" + position.getLongitude());
        array.add("" + position.getAltitude());
        array.add("" + position.getSpeed());
        array.add("" + position.getCourse());
        return array;
    }

    @Override
    public String toString() {
        return "Position{" +
                "id=" + id +
                ", protocol='" + protocol + '\'' +
                ", deviceId=" + deviceId +
                ", serverTime=" + serverTime +
                ", deviceTime=" + deviceTime +
                ", fixTime=" + fixTime +
                ", outdated=" + outdated +
                ", valid=" + valid +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                ", altitude=" + altitude +
                ", speed=" + speed +
                ", course=" + course +
                ", address='" + address + '\'' +
                '}';
    }
}
