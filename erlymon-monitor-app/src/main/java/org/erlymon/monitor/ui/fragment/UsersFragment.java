package org.erlymon.monitor.ui.fragment;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;
import com.jakewharton.rxbinding.view.RxView;

import org.erlymon.monitor.MainApp;
import org.erlymon.monitor.R;
import org.erlymon.monitor.Screens;
import org.erlymon.monitor.mvp.model.User;
import org.erlymon.monitor.mvp.presenter.CacheUsersPresenter;
import org.erlymon.monitor.mvp.presenter.DeleteUserPresenter;
import org.erlymon.monitor.mvp.view.CacheUsersView;
import org.erlymon.monitor.mvp.view.DeleteUserView;
import org.erlymon.monitor.ui.adapter.DevicesCursorAdapter;
import org.erlymon.monitor.ui.adapter.RecyclerItemClickListener;
import org.erlymon.monitor.ui.adapter.UsersCursorAdapter;

import javax.inject.Inject;

import butterknife.BindView;
import butterknife.ButterKnife;
import ru.terrakok.cicerone.Router;
import timber.log.Timber;

/**
 * Created by sergey on 31.03.17.
 */

public class UsersFragment  extends MvpAppCompatFragment implements CacheUsersView, DeleteUserView {
    @BindView(R.id.users)
    RecyclerView mUsers;
    @BindView(R.id.create_input)
    FloatingActionButton mCreateInput;

    @InjectPresenter
    CacheUsersPresenter mCacheUsersPresenter;

    @InjectPresenter
    DeleteUserPresenter mDeleteUserPresenter;

    @Inject
    Router router;

    private UsersCursorAdapter mAdapter;

    private AlertDialog mConfirmDialog;
    private AlertDialog mErrorDialog;

    public static UsersFragment getNewInstance(Bundle args) {
        // Required empty public constructor
        UsersFragment  instance = new UsersFragment();
        instance.setArguments(args);
        return instance;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        MainApp.getAppComponent().inject(this);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_users, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        mUsers.setLayoutManager(new LinearLayoutManager(getContext()));
        mUsers.setHasFixedSize(true);
        mUsers.addItemDecoration(new DividerItemDecoration(getContext(), DividerItemDecoration.VERTICAL));
        mUsers.addOnItemTouchListener(new RecyclerItemClickListener(getContext(), (view1, position) -> {
            Timber.d("User position: " + position + " id: " + mAdapter.getItemId(position));
            PopupMenu popupMenu = new PopupMenu(getContext(), view1);
            popupMenu.inflate(R.menu.fragment_users_popupmenu);
            popupMenu.setOnMenuItemClickListener(new OnExecUserPopupMenu(mAdapter.getItemId(position)));
            popupMenu.show();
        }));
        RxView.clicks(mCreateInput)
                .subscribe(aVoid -> {
                    User session = getArguments().getParcelable("session");
                    Bundle args = new Bundle();
                    args.putLong("userId", -1);
                    args.putParcelable("session", session);
                    router.navigateTo(Screens.USER_SCREEN, args);
                });

        mCacheUsersPresenter.load();
    }

    @Override
    public void onDestroyView() {
        mAdapter = null;
        super.onDestroyView();
    }

    private void toggleProgressVisibility(boolean b) {
    }

    @Override
    public void startCacheUsers() {
        toggleProgressVisibility(true);
    }

    @Override
    public void finishCacheUsers() {
        toggleProgressVisibility(false);
    }

    @Override
    public void failedCacheUsers(String message) {
        mErrorDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.app_name)
                .setMessage(message)
                .setOnCancelListener(dialog -> mCacheUsersPresenter.onErrorCancel())
                .show();
    }

    @Override
    public void startDeleteUser() {
        toggleProgressVisibility(true);
    }

    @Override
    public void finishDeleteUser() {
        toggleProgressVisibility(false);
    }

    @Override
    public void failedDeleteUser(String message) {
        mErrorDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.app_name)
                .setMessage(message)
                .setOnCancelListener(dialog -> mDeleteUserPresenter.onErrorCancel())
                .show();
    }

    @Override
    public void hideError() {
        if (mErrorDialog != null && mErrorDialog.isShowing()) {
            mErrorDialog.cancel();
        }
    }

    @Override
    public void successDeleteUser() {

    }

    @Override
    public void successCacheUsers(Cursor cursor) {
        Timber.d("Count devices: " + cursor.getCount());
        if (mAdapter == null) {
            mAdapter = new UsersCursorAdapter(cursor);
            mUsers.setAdapter(mAdapter);
        } else {
            mAdapter.changeCursor(cursor);
        }
    }

    private class OnExecUserPopupMenu implements PopupMenu.OnMenuItemClickListener {
        long mUserId;

        public OnExecUserPopupMenu(long userId) {
            this.mUserId = userId;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            User session = getArguments().getParcelable("session");
            Bundle args = new Bundle();
            args.putParcelable("session", session);
            switch (item.getItemId()) {
                case R.id.action_user_edit:
                    args.putLong("userId", mUserId);
                    args.putParcelable("session", session);
                    router.navigateTo(Screens.USER_SCREEN, args);
                    break;
                case R.id.action_user_remove:
                    mConfirmDialog = new AlertDialog.Builder(getContext())
                            .setTitle(R.string.userTitle)
                            .setMessage(R.string.sharedRemoveConfirm)
                            .setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
                                mDeleteUserPresenter.delete(mUserId);
                            })
                            .setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> mConfirmDialog.dismiss())
                            .show();
                    break;
                case R.id.action_user_devices:
                    args.putLong("userId", mUserId);
                    router.navigateTo(Screens.PERMISSIONS_SCREEN, args);
                    break;
            }
            return false;
        }
    }
}
