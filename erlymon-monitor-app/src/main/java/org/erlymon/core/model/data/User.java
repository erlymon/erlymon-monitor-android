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

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.google.gson.annotations.Since;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 5/4/16.
 */
public class User extends RealmObject implements Parcelable {
    @PrimaryKey
    @SerializedName("id")
    @Expose
    private long id;

    @Since(3.0)
    @SerializedName("name")
    @Expose
    private String name;

    @Since(3.0)
    @SerializedName("email")
    @Expose
    private String email;

    @Since(3.0)
    @SerializedName("password")
    @Expose
    private String password;

    @Since(3.0)
    @SerializedName("admin")
    @Expose
    private Boolean admin;

    @Since(3.1)
    @SerializedName("map")
    @Expose
    private String map;

    @Since(3.1)
    @SerializedName("language")
    @Expose
    private String language;

    @Since(3.1)
    @SerializedName("distanceUnit")
    @Expose
    private String distanceUnit;

    @Since(3.1)
    @SerializedName("speedUnit")
    @Expose
    private String speedUnit;

    @Since(3.1)
    @SerializedName("latitude")
    @Expose
    private Double latitude;

    @Since(3.1)
    @SerializedName("longitude")
    @Expose
    private Double longitude;

    @Since(3.1)
    @SerializedName("zoom")
    @Expose
    private Integer zoom;

    @Since(3.4)
    @SerializedName("readonly")
    @Expose
    private Boolean readonly;

    @Since(3.5)
    @SerializedName("twelveHourFormat")
    @Expose
    private Boolean twelveHourFormat;

    public User() {}
    /**
     *
     * @return
     * The id
     */
    public long getId() {
        return id;
    }

    /**
     *
     * @param id
     * The id
     */
    public void setId(long id) {
        this.id = id;
    }

    /**
     *
     * @return
     * The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name
     * The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return
     * The email
     */
    public String getEmail() {
        return email;
    }

    /**
     *
     * @param email
     * The email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     *
     * @return
     * The readonly
     */
    public Boolean getReadonly() {
        return readonly;
    }

    /**
     *
     * @param readonly
     * The readonly
     */
    public void setReadonly(Boolean readonly) {
        this.readonly = readonly;
    }

    /**
     *
     * @return
     * The admin
     */
    public Boolean getAdmin() {
        return admin;
    }

    /**
     *
     * @param admin
     * The admin
     */
    public void setAdmin(Boolean admin) {
        this.admin = admin;
    }

    /**
     *
     * @return
     * The map
     */
    public String getMap() {
        return map;
    }

    /**
     *
     * @param map
     * The map
     */
    public void setMap(String map) {
        this.map = map;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    /**
     *
     * @return
     * The distanceUnit
     */
    public String getDistanceUnit() {
        return distanceUnit;
    }

    /**
     *
     * @param distanceUnit
     * The distanceUnit
     */
    public void setDistanceUnit(String distanceUnit) {
        this.distanceUnit = distanceUnit;
    }

    /**
     *
     * @return
     * The speedUnit
     */
    public String getSpeedUnit() {
        return speedUnit;
    }

    /**
     *
     * @param speedUnit
     * The speedUnit
     */
    public void setSpeedUnit(String speedUnit) {
        this.speedUnit = speedUnit;
    }

    /**
     *
     * @return
     * The latitude
     */
    public Double getLatitude() {
        return latitude;
    }

    /**
     *
     * @param latitude
     * The latitude
     */
    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    /**
     *
     * @return
     * The longitude
     */
    public Double getLongitude() {
        return longitude;
    }

    /**
     *
     * @param longitude
     * The longitude
     */
    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    /**
     *
     * @return
     * The zoom
     */
    public Integer getZoom() {
        return zoom;
    }

    /**
     *
     * @param zoom
     * The zoom
     */
    public void setZoom(Integer zoom) {
        this.zoom = zoom;
    }

    /**
     *
     * @return
     * The twelveHourFormat
     */
    public Boolean getTwelveHourFormat() {
        return twelveHourFormat;
    }

    /**
     *
     * @param twelveHourFormat
     * The twelveHourFormat
     */
    public void setTwelveHourFormat(Boolean twelveHourFormat) {
        this.twelveHourFormat = twelveHourFormat;
    }

    /**
     *
     * @return
     * The password
     */
    public String getPassword() {
        return password;
    }

    /**
     *
     * @param password
     * The password
     */
    public void setPassword(String password) {
        this.password = password;
    }

    protected User(Parcel in) {
        id = in.readLong();
        name = in.readString();
        email = in.readString();
        byte readonlyVal = in.readByte();
        readonly = readonlyVal == 0x02 ? null : readonlyVal != 0x00;
        byte adminVal = in.readByte();
        admin = adminVal == 0x02 ? null : adminVal != 0x00;
        map = in.readString();
        language = in.readString();
        distanceUnit = in.readString();
        speedUnit = in.readString();
        latitude = in.readByte() == 0x00 ? null : in.readDouble();
        longitude = in.readByte() == 0x00 ? null : in.readDouble();
        zoom = in.readByte() == 0x00 ? null : in.readInt();
        byte twelveHourFormatVal = in.readByte();
        twelveHourFormat = twelveHourFormatVal == 0x02 ? null : twelveHourFormatVal != 0x00;
        password = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(id);
        dest.writeString(name);
        dest.writeString(email);
        if (readonly == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (readonly ? 0x01 : 0x00));
        }
        if (admin == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (admin ? 0x01 : 0x00));
        }
        dest.writeString(map);
        dest.writeString(language);
        dest.writeString(distanceUnit);
        dest.writeString(speedUnit);
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
        if (zoom == null) {
            dest.writeByte((byte) (0x00));
        } else {
            dest.writeByte((byte) (0x01));
            dest.writeInt(zoom);
        }
        if (twelveHourFormat == null) {
            dest.writeByte((byte) (0x02));
        } else {
            dest.writeByte((byte) (twelveHourFormat ? 0x01 : 0x00));
        }
        dest.writeString(password);
    }

    @SuppressWarnings("unused")
    public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}