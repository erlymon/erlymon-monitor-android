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
import org.erlymon.monitor.mvp.model.User;
import org.erlymon.monitor.mvp.model.UsersTable;


/**
 * Created by sergey on 04.04.17.
 */

public class UsersCursorAdapter extends CursorRecyclerAdapter<UsersCursorAdapter.MyViewHolder>  {
    private OnUsersClickListener mListener;

    public UsersCursorAdapter(Cursor cursor) {
        super(cursor);
    }


/*
    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (OnUsersClickListener) recyclerView.getContext();
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(recyclerView.getContext().toString() + " must implement UsersCursorAdapter.OnUsersClickListener");
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
                .inflate(R.layout.list_user, parent, false);
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
        private TextView email;
        private User data;


        MyViewHolder(View view) {
            super(view);
            layout = (GridLayout) view.findViewById(R.id.layout);
            name = (TextView) view.findViewById(R.id.name);
            email = (TextView) view.findViewById(R.id.email);
            layout.setOnClickListener(this);
        }

        void bind(Cursor cursor) {
            data = fromCursor(cursor);
            name.setText(data.getName());
            email.setText(data.getEmail());
        }

        @Override
        public void onClick(View v) {
            if (mListener != null) {
                mListener.onUserClick(v, data);
            }
        }
    }

    private User fromCursor(Cursor cursor) {
        User user = new User();
        user.setId(cursor.getLong(cursor.getColumnIndex(UsersTable.COLUMN_ID)));
        user.setName(cursor.getString(cursor.getColumnIndex(UsersTable.COLUMN_NAME)));
        user.setEmail(cursor.getString(cursor.getColumnIndex(UsersTable.COLUMN_EMAIL)));
        user.setPassword(cursor.getString(cursor.getColumnIndex(UsersTable.COLUMN_PASSWORD)));
        if (!cursor.isNull(cursor.getColumnIndex(UsersTable.COLUMN_ADMIN))) {
            user.setAdmin(cursor.getInt(cursor.getColumnIndex(UsersTable.COLUMN_ADMIN)) == 1);
        }
        user.setMap(cursor.getString(cursor.getColumnIndex(UsersTable.COLUMN_MAP)));
        user.setLanguage(cursor.getString(cursor.getColumnIndex(UsersTable.COLUMN_LANGUAGE)));
        user.setDistanceUnit(cursor.getString(cursor.getColumnIndex(UsersTable.COLUMN_DISTANCE_UNIT)));
        user.setSpeedUnit(cursor.getString(cursor.getColumnIndex(UsersTable.COLUMN_SPEED_UNIT)));
        user.setLatitude(cursor.getDouble(cursor.getColumnIndex(UsersTable.COLUMN_LATITUDE)));
        user.setLongitude(cursor.getDouble(cursor.getColumnIndex(UsersTable.COLUMN_LONGITUDE)));
        user.setZoom(cursor.getInt(cursor.getColumnIndex(UsersTable.COLUMN_ZOOM)));
        if (!cursor.isNull(cursor.getColumnIndex(UsersTable.COLUMN_READONLY))) {
            user.setReadonly(cursor.getInt(cursor.getColumnIndex(UsersTable.COLUMN_READONLY)) == 1);
        }
        if (!cursor.isNull(cursor.getColumnIndex(UsersTable.COLUMN_TWELVE_HOUR_FORMAT))) {
            user.setTwelveHourFormat(cursor.getInt(cursor.getColumnIndex(UsersTable.COLUMN_TWELVE_HOUR_FORMAT)) == 1);
        }
        return user;
    }

    public interface OnUsersClickListener {
        void onUserClick(View view, User user);
    }

}
