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
package org.erlymon.monitor.view

import android.content.Context
import android.os.Bundle
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.fragment_users.*
import org.erlymon.core.model.data.User
import org.erlymon.core.presenter.UsersListPresenter
import org.erlymon.core.presenter.UsersListPresenterImpl
import org.erlymon.core.view.UsersListView
import org.erlymon.monitor.R
import org.erlymon.monitor.view.adapter.UsersAdapter
import org.slf4j.LoggerFactory

/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 4/7/16.
 */
class UsersFragment : BaseFragment<UsersListPresenter>(), UsersListView {

    interface OnActionUserListener {
        fun onEditUser(user: User)
        fun onRemoveUser(user: User)
        fun onPermissionForUser(user: User)
    }

    private var listener: OnActionUserListener? = null


    // Override the Fragment.onAttach() method to instantiate the NoticeDialogListener
    override fun onAttach(context: Context?) {
        super.onAttach(context)
        // Verify that the host activity implements the callback interface

        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            listener = context as OnActionUserListener?
        } catch (e: ClassCastException) {
            // The activity doesn't implement the interface, throw exception
            throw ClassCastException(context!!.toString() + " must implement OnActionUserListener")
        }

    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater!!.inflate(R.layout.fragment_users, container, false)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter = UsersListPresenterImpl(context, this)

        rv_users.layoutManager = LinearLayoutManager(context)
        rv_users.setHasFixedSize(true)

        rv_users.addItemDecoration(DividerItemDecoration(context, DividerItemDecoration.VERTICAL))
    }

    override fun onResume() {
        super.onResume()
        presenter?.onLoadUsersCache()
    }

    override fun showData(data: List<User>) {
        rv_users.adapter = UsersAdapter(context, data)
    }

    override fun showError(error: String) {
       makeToast(rv_users, error)
    }

    companion object {
        private val logger = LoggerFactory.getLogger(UsersFragment::class.java)
    }
}