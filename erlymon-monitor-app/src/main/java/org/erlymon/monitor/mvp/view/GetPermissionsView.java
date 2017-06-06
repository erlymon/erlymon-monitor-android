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
package org.erlymon.monitor.mvp.view;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.AddToEndSingleStrategy;
import com.arellomobile.mvp.viewstate.strategy.SkipStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;

import org.erlymon.monitor.mvp.model.Device;
import org.erlymon.monitor.mvp.model.Server;

import java.util.List;

/**
 * Created by sergey on 17.03.17.
 */

@StateStrategyType(AddToEndSingleStrategy.class)
public interface GetPermissionsView extends MvpView {
    void startGetPermissions();

    void finishGetPermissions();

    void failedGetPermissions(String message);

    void hideError();
/*
    void hideFormError();

    void showFormError(Integer tokenError, Integer operateAsError);
*/
    @StateStrategyType(SkipStrategy.class)
    void successGetPermissions(List<Device> allDevices, List<Device> accessDevices);
}
