package com.bignerdranch.android.undebt;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TimePicker;

import java.util.Calendar;
import java.util.Date;

public class TimePickerFragment extends DialogFragment {

    public static final String EXTRA_TIME = "com.bignerdranch.android.undebt.time";
    private static final String ARG_TIME = "time";

    private Date mTime;
    private TimePicker mTimePicker;

    public static TimePickerFragment newInstance(Date time) {
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME, time);

        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        mTime = (Date) getArguments().getSerializable(ARG_TIME);
        Calendar c = Calendar.getInstance();
        c.setTime(mTime);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);

            View v = LayoutInflater.from(getActivity())
                    .inflate(R.layout.dialog_time, null);

            mTimePicker = (TimePicker) v.findViewById(R.id.dialog_time_picker);
            mTimePicker.setCurrentHour(hour);
            mTimePicker.setCurrentMinute(minute);
            mTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
                @Override
                public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                    //set the callback tha indicates the time has been adjusted
                    Calendar c = Calendar.getInstance();
                    c.setTime(mTime);
                    c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    c.set(Calendar.MINUTE, minute);

                    mTime = c.getTime();
                    getArguments().putSerializable(ARG_TIME, mTime);
                }
            });

        return new AlertDialog.Builder(getActivity())
                .setView(v)
                .setTitle(R.string.time_picker_title)
                .setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendResult(Activity.RESULT_OK);
                    }
                })
                .create();
    }

    public static TimePickerFragment timeInstance(Date time){
        Bundle args = new Bundle();
        args.putSerializable(ARG_TIME, time);

        TimePickerFragment fragment = new TimePickerFragment();
        fragment.setArguments(args);
        return fragment;
    }
    public void sendResult(int resultCode){
        if(getTargetFragment()==null){
            return;
        }
        Intent timeintent = new Intent();
        timeintent.putExtra(EXTRA_TIME, mTime);

        getTargetFragment().onActivityResult(getTargetRequestCode(), resultCode, timeintent);
    }

}
