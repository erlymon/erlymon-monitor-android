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
package org.erlymon.monitor

import com.chibatching.kotpref.KotprefModel
/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 3/24/16.
 */

object MainPref : KotprefModel() {
    var email: String by stringPrefVar(default = "")
    var password: String by stringPrefVar(default = "")

    var dns: String by stringPrefVar(default = "web.erlymon.org")
    var sslOrTls: Boolean by booleanPrefVar(default = false)
    var protocolVersion: Float by floatPrefVar(default = 3.4F)
}
