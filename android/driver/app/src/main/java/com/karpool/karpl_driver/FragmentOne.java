package com.karpool.karpl_driver;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;

import cz.msebera.android.httpclient.Header;

public class FragmentOne extends Fragment {

    private Button tripTimeButton, tripDateButton, createButton;
    protected static TextView dateLabel, timeLabel;
    private static String date, time; // FOR DATABASE
    private EditText newDestination, newPrice, newSeats, newOrigin;


    private static String userID;
    private final static String KEY_USER = "userID";



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_one, container, false);
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
        userID = prefs.getString(KEY_USER, null);


        tripTimeButton = rootView.findViewById(R.id.newTime);
        tripDateButton = rootView.findViewById(R.id.newDate);
        createButton = rootView.findViewById(R.id.createButton);
        dateLabel = rootView.findViewById(R.id.dateLabel);
        timeLabel = rootView.findViewById(R.id.timeLabel);
        newPrice = rootView.findViewById(R.id.newPrice);
        newDestination = rootView.findViewById(R.id.newDestination);
        newSeats = rootView.findViewById(R.id.newSeats);
        newOrigin = rootView.findViewById(R.id.newOrigin);


        createButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                createTripTask(userID, newOrigin.getText().toString(), newDestination.getText().toString(), Integer.parseInt(
                        newSeats.getText().toString()), time, date, Integer.parseInt(newPrice.getText().toString()));


            }
        });

        tripDateButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DialogFragment newFragment = new DatePickerFragment();
                newFragment.show(getFragmentManager(), "datePicker");

            }
        });


        tripTimeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DialogFragment newFragment = new TimePickerFragment();
                newFragment.show(getFragmentManager(), "timePicker");


            }
        });


        return rootView;
    }

    /**
     * Represents an asynchronous create trip task
     */

    public void createTripTask(String mUser, String mOrigin, String mDestination, int mSeats, String mTime, String mDate, int mPrice) {


        HttpUtils.post("trips/" + mUser + "/" + mOrigin + "/" + mDestination + "/" + mSeats + "/" + mTime + "/" + mDate + "/" + mPrice, new RequestParams(), new JsonHttpResponseHandler() {

            @Override
            public void onFinish() {
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (!response.getString("destination").isEmpty()) {
                        clearFields();


                    } else {


                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        });


    }


    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            return new TimePickerDialog(getActivity(), this, hour, minute, DateFormat.is24HourFormat(getActivity()));

        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            time = convertDate(hourOfDay) + convertDate(minute);
            timeLabel.setText(convertDate(hourOfDay) + ":" + convertDate(minute));
            timeLabel.setVisibility(View.VISIBLE);
        }


    }

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);


            // new instance of datePickerDialog
            Dialog datePickerDialog = new DatePickerDialog(getActivity(), this, year, month, day);

            //setting minimum time
            ((DatePickerDialog) datePickerDialog).getDatePicker().setMinDate(System.currentTimeMillis() - 1000);

            return datePickerDialog;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            date = Integer.toString(year) + convertDate(day) + convertDate(month);
            dateLabel.setText(Integer.toString(year) + "-" + convertDate(day) + "-" + convertDate(month));
            dateLabel.setVisibility(View.VISIBLE);

        }
    }

    protected static String convertDate(int input) {
        if (input >= 10) {
            return String.valueOf(input);
        } else {
            return "0" + String.valueOf(input);
        }
    }

    protected void clearFields() {
        newOrigin.setText("");
        newDestination.setText("");
        newPrice.setText("");
        newSeats.setText("");
        newOrigin.clearFocus();
        newDestination.clearFocus();
        newPrice.clearFocus();
        newSeats.clearFocus();
        timeLabel.setVisibility(View.INVISIBLE);
        dateLabel.setVisibility(View.INVISIBLE);


    }


}

