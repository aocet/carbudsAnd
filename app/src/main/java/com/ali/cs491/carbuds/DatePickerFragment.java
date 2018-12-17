package com.ali.cs491.carbuds;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.text.format.DateFormat;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.Calendar;


public class DatePickerFragment extends DialogFragment {
    public static final int DATE = 0;
    public static final int TIME = 1;
    private int type;
    public void setType(int type){
        this.type = type;
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        int hour = c.get(Calendar.HOUR_OF_DAY);
        int minute = c.get(Calendar.MINUTE);
        if(type == DATE) {
            return new DatePickerDialog(getActivity(), dateSetListener, year, month, day);
        } else {
            return new TimePickerDialog(getActivity(), timeSetListener, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }
    }

    private DatePickerDialog.OnDateSetListener dateSetListener =
            new DatePickerDialog.OnDateSetListener() {
                public void onDateSet(DatePicker view, int year, int month, int day) {

                    Toast.makeText(getActivity(), "selected date is " + view.getYear() +
                            " / " + (view.getMonth()+1) +
                            " / " + view.getDayOfMonth(), Toast.LENGTH_SHORT).show();
                }
            };
    private TimePickerDialog.OnTimeSetListener timeSetListener =
            new TimePickerDialog.OnTimeSetListener() {
                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                    Toast.makeText(getActivity(), "selected time is "
                                    + view.getHour() +
                                    " / " + view.getMinute()
                            , Toast.LENGTH_SHORT).show();
                }
            };
}