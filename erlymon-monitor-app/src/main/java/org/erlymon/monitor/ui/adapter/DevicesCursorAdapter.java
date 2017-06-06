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

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import org.erlymon.monitor.R;
import org.erlymon.monitor.mvp.model.Device;
import org.erlymon.monitor.mvp.model.DevicesTable;

import java.util.Date;

/**
 * Created by sergey on 04.04.17.
 */

public class DevicesCursorAdapter extends CursorRecyclerAdapter<DevicesCursorAdapter.MyViewHolder>  {
    private OnDevicesClickListener mListener;


    public DevicesCursorAdapter(Cursor cursor) {
        super(cursor);
    }
/*
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (OnDevicesClickListener) recyclerView.getContext();
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(recyclerView.getContext().toString() + " must implement DevicesCursorAdapter.OnDevicesClickListener");
        }
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        mListener = null;
        super.onDetachedFromRecyclerView(recyclerView);
    }
*/
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_device, parent, false);
        MyViewHolder vh = new MyViewHolder(itemView);
        return vh;
    }

    @Override
    public void onBindViewHolderCursor(MyViewHolder holder, Cursor cursor) {
        holder.bind(cursor);
    }

    class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private GridLayout layout;
        private TextView name;
        private TextView identifier;
        private Device data;


        MyViewHolder(View view) {
            super(view);
            layout = (GridLayout) view.findViewById(R.id.layout);
            name = (TextView) view.findViewById(R.id.name);
            identifier = (TextView) view.findViewById(R.id.identifier);
            layout.setOnClickListener(this);
        }

        void bind(Cursor cursor) {
            data = fromCursor(cursor);
            name.setText(data.getName());
            identifier.setText(data.getUniqueId());
            layout.setBackgroundResource(getStatusColorId(data.getStatus()));
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onDeviceClick(v, data);
            }
        }
    }

    private int getStatusColorId(String status) {
        if (status == null) {
            return R.color.colorUnknownStatus;
        }
        switch (status) {
            case "online": return R.color.colorOnlineStatus;
            case "offline": return R.color.colorOfflineStatus;
            case "unknown": return R.color.colorUnknownStatus;
            default: return R.color.colorUnknownStatus;
        }
    }

    private Device fromCursor(Cursor cursor) {
        Device device = new Device();
        device.setId(cursor.getLong(cursor.getColumnIndex(DevicesTable.COLUMN_ID)));
        device.setName(cursor.getString(cursor.getColumnIndex(DevicesTable.COLUMN_NAME)));
        device.setUniqueId(cursor.getString(cursor.getColumnIndex(DevicesTable.COLUMN_UNIQUE_ID)));
        device.setStatus(cursor.getString(cursor.getColumnIndex(DevicesTable.COLUMN_STATUS)));
        if (!cursor.isNull(cursor.getColumnIndex(DevicesTable.COLUMN_LAST_UPDATE))) {
            device.setLastUpdate(new Date(cursor.getLong(cursor.getColumnIndex(DevicesTable.COLUMN_LAST_UPDATE))));
        }
        device.setPositionId(cursor.getLong(cursor.getColumnIndex(DevicesTable.COLUMN_POSITION_ID)));
        return device;
    }

    public interface OnDevicesClickListener {
        void onDeviceClick(View view, Device device);
    }
}
