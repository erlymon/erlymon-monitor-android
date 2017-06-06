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
package org.erlymon.monitor.ui.adapter;


import android.content.Context;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import org.erlymon.monitor.R;
import org.erlymon.monitor.mvp.model.Device;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 1/7/16.
 */
public class DevicesPermissionsAdapter extends ArrayAdapter<Device> {
    private Map<Long, Device> permissionsMap = new HashMap<>();
    private PermissionListener permissionListener;

    public interface PermissionListener {
        void createPermission(Device device);
        void deletePermission(Device device);
    }
    public DevicesPermissionsAdapter(Context context, List<Device> allDevices, List<Device> accessDevices, PermissionListener permissionListener) {
        super(context, R.layout.list_device_permission, allDevices);
        for (Device device: accessDevices) {
            permissionsMap.put(device.getId(), device);
        }
        this.permissionListener = permissionListener;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (convertView == null) {
            convertView =  LayoutInflater.from(parent.getContext()).inflate(R.layout.list_device_permission,  parent, false);
            viewHolder = new ViewHolder(convertView);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        Device item = getItem(position);
        viewHolder.permission.setChecked(permissionsMap.containsKey(item.getId()));
        viewHolder.permission.setOnClickListener(new PermissionClick(viewHolder, item));
        viewHolder.name.setText(item.getName());
        viewHolder.identifier.setText(item.getUniqueId());
        return convertView;
    }

    /**
     * Реализация класса ViewHolder, хранящего ссылки на виджеты.
     */
    class ViewHolder {
        private AppCompatCheckBox permission;
        private TextView name;
        private TextView identifier;

        public ViewHolder(View itemView) {
            permission = (AppCompatCheckBox) itemView.findViewById(R.id.permission);
            name = (TextView) itemView.findViewById(R.id.name);
            identifier = (TextView) itemView.findViewById(R.id.identifier);
        }
    }

    class PermissionClick implements View.OnClickListener {
        ViewHolder viewHolder;
        Device device;

        public PermissionClick(ViewHolder viewHolder, Device device) {
            this.viewHolder = viewHolder;
            this.device = device;
        }

        @Override
        public void onClick(View v) {
            try {
                if (viewHolder.permission.isChecked()) {
                    permissionListener.createPermission(device);
                } else {
                    permissionListener.deletePermission(device);
                }
            } catch (NullPointerException ignore) {

            }
        }
    }
}