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
package org.erlymon.monitor.ui.fragment;

import android.app.Dialog;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.AppCompatSpinner;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AutoCompleteTextView;

import com.arellomobile.mvp.MvpAppCompatDialogFragment;
import com.arellomobile.mvp.presenter.InjectPresenter;

import org.erlymon.monitor.R;
import org.erlymon.monitor.mvp.model.Command;
import org.erlymon.monitor.mvp.presenter.SendCommandPresenter;
import org.erlymon.monitor.mvp.view.SendCommandView;

import java.util.HashMap;
import java.util.Map;

import butterknife.BindArray;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnItemSelected;
import timber.log.Timber;


/**
 * Created by Sergey Penkovsky <sergey.penkovsky@gmail.com> on 09.03.16.
 */
public class SendCommandDialogFragment extends MvpAppCompatDialogFragment implements SendCommandView {
    @BindView(R.id.s_type)
    AppCompatSpinner type;
    @BindView(R.id.tv_frequency)
    AutoCompleteTextView frequency;
    @BindView(R.id.tv_unit)
    AutoCompleteTextView unit;

    @BindArray(R.array.send_command_value)
    String[] types;

    @InjectPresenter
    SendCommandPresenter mSendCommandPresenter;

    private AlertDialog mErrorDialog;

    public static SendCommandDialogFragment newInstance(long deviceId) {
        SendCommandDialogFragment newFragment = new SendCommandDialogFragment();
        Bundle args = new Bundle();
        args.putLong("deviceId", deviceId);
        newFragment.setArguments(args);
        return newFragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Timber.d("onCreateDialog");

        LayoutInflater factory = LayoutInflater.from(getContext());
        final View view = factory.inflate(R.layout.fragment_send_command, null);
        ButterKnife.bind(this, view);

        Dialog dialog = new AlertDialog.Builder(getActivity(), R.style.AppTheme_Dialog)
                .setTitle(R.string.commandTitle)
                .setView(view)
                .setPositiveButton(android.R.string.ok,
                        (dialog1, whichButton) -> {
                            String cmdType = types[type.getSelectedItemPosition()];
                            long deviceId = getArguments().getLong("deviceId");
                            Command command = new Command();
                            command.setDeviceId(deviceId);
                            command.setType(cmdType);
                            try {
                                if (cmdType.equals("positionPeriodic")) {
                                    int freq = Integer.parseInt(String.valueOf(frequency.getText()));
                                    freq *= Integer.parseInt(String.valueOf(unit.getText()));
                                    Map<String, Object> attrs = new HashMap<>();
                                    attrs.put("frequency", freq);
                                    command.setAttributes(attrs);
                                }
                            } catch (NumberFormatException e) {
                                Timber.tag("SendCommandDialogFragment").w(e);
                            }
                            mSendCommandPresenter.send(command);
                        }
                )
                .create();

        return dialog;
    }

    @OnItemSelected(R.id.s_type)
    void onItemSelected(int position) {
        String cmdType = types[position];
        frequency.setVisibility(cmdType.equals("positionPeriodic") ? View.VISIBLE : View.GONE);
        unit.setVisibility(cmdType.equals("positionPeriodic") ? View.VISIBLE : View.GONE);
    }

    @Override
    public void startSendCommand() {

    }

    @Override
    public void finishSendCommand() {

    }

    @Override
    public void failedSendCommand(String message) {
        mErrorDialog = new AlertDialog.Builder(getContext())
                .setTitle(R.string.app_name)
                .setMessage(message)
                .setOnCancelListener(dialog -> mSendCommandPresenter.onErrorCancel())
                .show();
    }

    @Override
    public void hideError() {
        if (mErrorDialog != null && mErrorDialog.isShowing()) {
            mErrorDialog.cancel();
        }
    }

    @Override
    public void successSendCommand() {
        dismiss();
    }
}
