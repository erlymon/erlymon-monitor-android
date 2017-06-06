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

import java.util.Arrays;
import java.util.List;

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 5/19/16.
 */
public class Event {
    private List<Device> devices;
    private List<Position> positions;

    public Event() {}

    public Event(List<Device> devices, List<Position> positions) {
        this.devices = devices;
        this.positions = positions;
    }
    public List<Device> getDevices() {
        return devices;
    }

    public List<Position> getPositions() {
        return positions;
    }

    @Override
    public String toString() {
        return "Event{" +
                "devices=" + devices +
                ", positions=" + positions +
                '}';
    }
}
