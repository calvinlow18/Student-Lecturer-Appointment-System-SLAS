package se.lowkhaiwynn.slas;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import org.apache.http.conn.ConnectTimeoutException;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.Calendar;

/**
 * Created by Calvin Low on 5/25/2016.
 */
public class FutureAppointment extends Fragment{
    static Calendar c;
    static int yearS;
    static int monthS;
    static int dayS;
    static int hourS;
    static int minS;
    static View view;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.future_booking, container, false);
        final ImageButton pickDate = (ImageButton) view.findViewById(R.id.pickDateButton);
        c = Calendar.getInstance();
        switch(c.getTime().getDay() + 1) {
            case Calendar.MONDAY:
                c.add(Calendar.DATE, 7);
                break;
            case Calendar.TUESDAY:
                c.add(Calendar.DATE, 6);
                break;
            case Calendar.WEDNESDAY:
                c.add(Calendar.DATE, 5);
                break;
            case Calendar.THURSDAY:
                c.add(Calendar.DATE, 4);
                break;
            case Calendar.FRIDAY:
                c.add(Calendar.DATE, 3);
                break;
            case Calendar.SATURDAY:
                c.add(Calendar.DATE, 9);
                break;
            case Calendar.SUNDAY:
                c.add(Calendar.DATE, 8);
                break;
        }
        yearS = c.getTime().getYear() + 1900;
        monthS = c.getTime().getMonth() + 1;
        dayS = c.getTime().getDate();
        displayDate();
        pickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animFadein = AnimationUtils.loadAnimation(getContext(),R.anim.fade_in);
                pickDate.startAnimation(animFadein);
                DatePickerFragment newFragment = new DatePickerFragment(view);
                newFragment.show(getActivity().getSupportFragmentManager(), "datePicker");
            }
        });
        final ImageButton pickTime = (ImageButton) view.findViewById(R.id.pickTimeBtn);
        hourS = 8;
        minS = 0;
        displayTime();
        pickTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animFadein = AnimationUtils.loadAnimation(getContext(),R.anim.fade_in);
                pickTime.startAnimation(animFadein);
                TimePickerFragment timePickerFragment = new TimePickerFragment(view);
                timePickerFragment.show(getActivity().getSupportFragmentManager(), "timePicker");
            }
        });

        final ImageButton submitBtn = (ImageButton) view.findViewById(R.id.future_confirm);
        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Animation animFadein = AnimationUtils.loadAnimation(getContext(),R.anim.fade_in);
                submitBtn.startAnimation(animFadein);
                final EditText input = new EditText(getActivity());
                input.setHint("Booking Description");
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                android.support.v7.app.AlertDialog.Builder adb = new android.support.v7.app.AlertDialog.Builder(getActivity());
                adb.setMessage("Date : " + dayS + "-" + monthS + "-" + yearS + "\n" + "Time : " + getTime() + "\n")
                        .setView(input)
                        .setCancelable(true)
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(DatabaseTask.isNetworkAvailable(getContext())) {
                                    ProgressDialog pd = new ProgressDialog(getActivity());
                                    pd.setMessage("Submitting your request... ...");
                                    pd.setProgressStyle(pd.STYLE_HORIZONTAL);
                                    pd.show();
                                    Thread thread = new Thread(new Runnable()
                                    {
                                        @Override
                                        public void run()
                                        {
                                        try
                                        {
                                            URL url;
                                            String urlStr = "http://smartkidsedu.tk/Android/makeAppointment.php?lecturer=" + DatabaseTask.user.getID() + "&student=" + DatabaseTask.user.getID() + "&starttime=" + hourS + ":" + minS + ":00" + "&date=" + yearS + "-" + monthS + "-" + dayS + "&desc=" + input.getText().toString();
                                            System.out.println(urlStr);
                                            try {
                                                url = new URL(urlStr);
                                                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                                                httpURLConnection.setConnectTimeout(30000);
                                                httpURLConnection.connect();
                                                InputStream inputStream = httpURLConnection.getInputStream();
                                                inputStream.close();
                                                httpURLConnection.disconnect();
                                            } catch (MalformedURLException e) {
                                                e.printStackTrace();
                                            } catch (ConnectTimeoutException e) {
                                                e.printStackTrace();
                                            }catch (SocketTimeoutException e) {
                                                e.printStackTrace();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                        catch (Exception e)
                                        {
                                            e.printStackTrace();
                                        }

                                        }
                                    });
                                    thread.start();
                                    pd.hide();
                                    Toast.makeText(getActivity(), "Congratulation, you booked your time successfully !!!", Toast.LENGTH_SHORT).show();
                                } else {
                                    DatabaseTask.quitDialog(getActivity());
                                }
                            }
                        });
                android.support.v7.app.AlertDialog alert = adb.create();
                alert.setTitle("Comfirmation");
                alert.show();
            }
        });


        return view;
    }

    public static void displayDate() {
        TextView futureDay = (TextView) view.findViewById(R.id.future_day);
        TextView futureMonth = (TextView) view.findViewById(R.id.future_month);
        TextView futureYear = (TextView) view.findViewById(R.id.future_year);
        if(futureDay!=null) {
            futureDay.setText(String.valueOf(dayS));
            String month = null;
            switch(monthS) {
                case 1:
                    month = "January";
                    break;
                case 2:
                    month = "February";
                    break;
                case 3:
                    month = "March";
                    break;
                case 4:
                    month = "April";
                    break;
                case 5:
                    month = "May";
                    break;
                case 6:
                    month = "June";
                    break;
                case 7:
                    month = "July";
                    break;
                case 8:
                    month = "August";
                    break;
                case 9:
                    month = "September";
                    break;
                case 10:
                    month = "October";
                    break;
                case 11:
                    month = "November";
                    break;
                case 12:
                    month = "December";
                    break;
            }
            futureMonth.setText(month);
            futureYear.setText(String.valueOf(yearS));
        }

    }

    public static void displayTime() {
        TextView futureTime = (TextView) view.findViewById(R.id.future_time);
        if(futureTime!=null) {
            futureTime.setText(getTime());
        }

    }

    public static String getTime() {
        Boolean ampm = true;
        int hourO = hourS;
        int minO = minS;
        String hourStr = String.valueOf(hourO);
        String minStr = String.valueOf(minO);
        if(hourS>11) {
            ampm = false;
            if(hourS>12) {
                hourO = hourS-12;
            }
        }

        if(hourO/10 == 0) {
            hourStr = "0" + hourO;
        }

        if(minO/10 == 0) {
            minStr = "0" + minO;
        }

        return hourStr + ":" + minStr + ((ampm == true) ? " A.M." : " P.M.");
    }


    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        View v;
        int counter = 0;

        public DatePickerFragment(View v) {
            this.v = v;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            DatePickerDialog dpd = new DatePickerDialog(getActivity(),AlertDialog.THEME_DEVICE_DEFAULT_LIGHT, this, yearS, monthS-1, dayS);
            dpd.getDatePicker().setMinDate(c.getTimeInMillis());

            return dpd;
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            Calendar calendar = Calendar.getInstance();
            calendar.set(year, month, day);
            if(calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY || calendar.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
                if(counter == 0) {
                    Toast.makeText(getActivity(), "Please don't choose Saturday and Sunday :)", Toast.LENGTH_SHORT).show();
                    ++counter;
                }
            } else {
                yearS = year;
                monthS = month+1;
                dayS = day;
                displayDate();
            }
        }


    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        View v;
        TimePickerDialog tpd;
        int counter = 0;

        public TimePickerFragment(View v) {
            this.v = v;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            tpd = new TimePickerDialog(getActivity(), this, hourS, minS, true);
            tpd.updateTime(hourS, minS);
            return tpd;
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            if(hourOfDay<8 || hourOfDay>=18) {
                if(counter == 0) {
                    Toast.makeText(getActivity(), "Please enter a valid time (hours between 8 and 18). Time is refreshed to 8:00 A.M.", Toast.LENGTH_SHORT).show();
                    ++counter;
                }
                hourS = 8;
                minS = 0;
            } else {
                hourS = hourOfDay;
                if(minute>0 && minute<30) {
                    minS = 0;
                    if(counter == 0) {
                        Toast.makeText(getActivity(), "Your booking time is adjusted to " + getTime(), Toast.LENGTH_SHORT).show();
                        ++counter;
                    }
                } else if(minute>30 && minute<=59) {
                    minS = 30;
                    if(counter == 0) {
                        Toast.makeText(getActivity(), "Your booking time is adjusted to " + getTime(), Toast.LENGTH_SHORT).show();
                        ++counter;
                    }
                } else {
                    minS = minute;
                }

            }
            displayTime();
        }
    }

}
