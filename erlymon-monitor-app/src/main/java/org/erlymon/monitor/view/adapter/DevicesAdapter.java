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
package org.erlymon.monitor.view.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridLayout;
import android.widget.TextView;

import org.erlymon.core.model.data.Device;
import org.erlymon.monitor.R;

import java.util.List;

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 1/7/16.
 */
public class DevicesAdapter extends BaseAdapter<Device, DevicesAdapter.MyViewHolder> {
    private OnDevicesClickListener mListener;

    public DevicesAdapter(Context context, List<Device> data) {
        super(context, data);
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (OnDevicesClickListener) recyclerView.getContext();
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(recyclerView.getContext().toString() + " must implement DevicesAdapter.OnUsersClickListener");
        }
    }

    @Override
    public void onDetachedFromRecyclerView(RecyclerView recyclerView) {
        mListener = null;
        super.onDetachedFromRecyclerView(recyclerView);
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = inflater.inflate(R.layout.list_device, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Device obj = getData().get(position);
        holder.bind(obj);
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

        void bind(Device data) {
            this.data = data;

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
        switch (status) {
            case "online": return R.color.colorOnlineStatus;
            case "offline": return R.color.colorOfflineStatus;
            case "unknown": return R.color.colorUnknownStatus;
            default: return R.color.colorUnknownStatus;
        }
    }

    public interface OnDevicesClickListener {
        void onDeviceClick(View view, Device device);
    }
}