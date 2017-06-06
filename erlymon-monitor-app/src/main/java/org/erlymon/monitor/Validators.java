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

import java.util.regex.Pattern;

/**
 * Created by sergey on 03.04.17.
 */

public class Validators {
    public static Pattern NAME_PATTERN = Pattern.compile("^[\\w]{3,15}$");
    public static Pattern IDENTIFIER_PATTERN = Pattern.compile("^[a-z0-9_-]{1,32}$");
    public static Pattern PASSWORD_PATTERN = Pattern.compile("^[\\w]{3,15}$");
    public static Pattern BING_KEY_PATTERN = Pattern.compile("^[a-z0-9_-]{3,15}$");
    public static Pattern LATITUDE_PATTERN = Pattern.compile("^(\\+|-)?(?:90(?:(?:\\.0{1,6})?)|(?:[0-9]|[1-8][0-9])(?:(?:\\.[0-9]{1,6})?))$");
    public static Pattern LONGITUDE_PATTERN = Pattern.compile("^(\\+|-)?(?:180(?:(?:\\.0{1,6})?)|(?:[0-9]|[1-9][0-9]|1[0-7][0-9])(?:(?:\\.[0-9]{1,6})?))$");
    public static Pattern ZOOM_PATTERN = Pattern.compile("^[0-9]{1,2}$");
}
