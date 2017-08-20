package se.lowkhaiwynn.slas;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.ExecutionException;

/**
 * Created by Calvin Low on 5/16/2016.
 */
public class ScheduleFragment extends Fragment {

    private GridView gridView;

    private String lectID;
    private String lectName;
    private String lectVenue;
    private int[] blockArray = new int[100];
    static ArrayList<Appointment> appList = new ArrayList<Appointment>();
    private SwipeRefreshLayout swipeRefreshLayout;
    private Boolean spam;
    private Boolean editAppointment;
    private String[] appDetails;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        lectID = getArguments().getString("id");
        editAppointment = getArguments().getBoolean("editAppointment", false);
        appDetails = getArguments().getStringArray("app");
        View view = inflater.inflate(R.layout.schedule, container, false);

        if(editAppointment) {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.darkGreen)));
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(false);
            ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowTitleEnabled(true);
        }
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setDate();

        swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swipe);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorPrimary,R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                if(!DatabaseTask.quitIfNoConnection(getActivity())) {
                    new ImportAppointment(getActivity()).execute();
                }

                swipeRefreshLayout.setRefreshing(false);

            }
        });

        if(!DatabaseTask.quitIfNoConnection(getActivity())) {
            new ImportAppointment(getActivity()).execute();
        }


        gridView = (GridView) getView().findViewById(R.id.gridView1);

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
            }
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                boolean enable = false;
                if(gridView != null && gridView.getChildCount() > 0){
                    // check if the first item of the list is visible
                    boolean firstItemVisible = gridView.getFirstVisiblePosition() == 0;
                    // check if the top of the first item is visible
                    boolean topOfFirstItemVisible = gridView.getChildAt(0).getTop() == 0;
                    // enabling or disabling the refresh layout
                    enable = firstItemVisible && topOfFirstItemVisible;
                }
                swipeRefreshLayout.setEnabled(enable);
            }
        });

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                final Date position_date = new Date();
                int day = Calendar.getInstance().getTime().getDay();

                switch (position / 10) {
                    case 0: position_date.setHours(8);break;
                    case 1: position_date.setHours(9);break;
                    case 2: position_date.setHours(10);break;
                    case 3: position_date.setHours(11);break;
                    case 4: position_date.setHours(12);break;
                    case 5: position_date.setHours(13);break;
                    case 6: position_date.setHours(14);break;
                    case 7: position_date.setHours(15);break;
                    case 8: position_date.setHours(16);break;
                    case 9: position_date.setHours(17);break;
                }

                switch (position % 10) {
                    case 0: position_date.setMinutes(0);break;
                    case 1: position_date.setMinutes(0);break;
                    case 2: position_date.setMinutes(0);break;
                    case 3: position_date.setMinutes(0);break;
                    case 4: position_date.setMinutes(0);break;
                    case 5: position_date.setMinutes(30);break;
                    case 6: position_date.setMinutes(30);break;
                    case 7: position_date.setMinutes(30);break;
                    case 8: position_date.setMinutes(30);break;
                    case 9: position_date.setMinutes(30);break;
                }

                Calendar cal = Calendar.getInstance();
                if(day>=0 && day<=5) {
                    int add = (position % 5 + 1) - day;
                    cal.add(Calendar.DATE, add);
                } else {
                    cal.add(Calendar.DATE, ((position % 5 + 1) - day +7));
                }


                String hour = (position_date.getHours() > 12) ? String.valueOf(position_date.getHours()-12) : String.valueOf(position_date.getHours());
                hour = (Integer.parseInt(hour)/10 == 0) ? ("0" + hour) : hour;

                String minute = (position_date.getMinutes()/10 == 0) ? ("0" + position_date.getMinutes()) : String.valueOf(position_date.getMinutes());

                final String date = (cal.getTime().getYear()+1900) + "-" + (cal.getTime().getMonth()+1) + "-" + cal.getTime().getDate();
                final String time = hour + ":" + minute;


                for(Lecturer x : DatabaseTask.lecturerlist) {
                    if(x.findName(lectID) != null) {
                        lectName = x.getName();
                        lectVenue = x.getLectOffice();
                        break;
                    }
                }

                if(DatabaseTask.user instanceof Student) {
                    final CheckSpam checkSpam = new CheckSpam(date, getActivity());
                    if(!DatabaseTask.quitIfNoConnection(getActivity())) {
                        try {
                            checkSpam.execute().get();
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }

                    if(editAppointment) {
                            promptConfirmation(date, time, position_date.getHours(), position_date.getMinutes());
                    } else {
                        if(spam) {
                            Toast.makeText(getActivity(), "Please make appointment in other time. You made an appointment two days ago.", Toast.LENGTH_LONG).show();
                        } else {
                            promptConfirmation(date, time, position_date.getHours(), position_date.getMinutes());
                        }
                    }

                } else {
                    promptConfirmation(date, time, position_date.getHours(), position_date.getMinutes());
                }

            }
        });
    }

    public void promptConfirmation(final String date, String time, final int pHour, final int pMin) {
        if(DatabaseTask.isNetworkAvailable(getActivity())) {
            final EditText input = new EditText(getActivity());
            if(DatabaseTask.user instanceof Lecturer) {
                input.setHint("Booking Description");
            } else {
                input.setHint("Appointment Description");
            }

            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.MATCH_PARENT);
            input.setLayoutParams(lp);
            AlertDialog.Builder adb = new AlertDialog.Builder(getActivity());
            adb.setView(input)
                    .setCancelable(true);
            CheckSpam checkSpam = new CheckSpam(date, getActivity());
            try {
                checkSpam.execute().get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

            if(editAppointment && !spam) {
                adb.setPositiveButton("Confirm",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Thread thread = new Thread(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                String urlStr;
                                try
                                {
                                    URL url;
                                    if(DatabaseTask.user instanceof Lecturer) {
                                        urlStr = "http://smartkidsedu.tk/Android/editAppointment.php?lecturer=" + appDetails[0] + "&student=" + appDetails[0] + "&starttime=" + appDetails[3] + ":00&date=" + appDetails[2] + "&newstarttime=" + pHour + ":" + pMin + ":00&newdate=" + date + "&newdesc=" + input.getText().toString() + "&role=lecturer";
                                    } else {
                                        urlStr = "http://smartkidsedu.tk/Android/editAppointment.php?lecturer=" + appDetails[0] + "&student=" + appDetails[1] + "&starttime=" + appDetails[3] + ":00&date=" + appDetails[2] + "&newstarttime=" + pHour + ":" + pMin + ":00&newdate=" + date + "&newdesc=" + input.getText().toString() + "&role=student";
                                    }
                                    try {
                                        url = new URL(urlStr);
                                        HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                                        httpURLConnection.connect();
                                        InputStream inputStream = httpURLConnection.getInputStream();
                                        inputStream.close();
                                        httpURLConnection.disconnect();
                                    } catch (MalformedURLException e) {
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
                        }

                        );
                        thread.start();
                        getActivity().finish();

                    }
                });
            } else {

                adb.setPositiveButton("Confirm",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                            Thread thread = new Thread(new Runnable()
                            {
                                @Override
                                public void run()
                                {
                                    String urlStr;
                                    try
                                    {
                                        URL url;
                                        urlStr = "http://smartkidsedu.tk/Android/makeAppointment.php?lecturer=" + lectID + "&student=" + DatabaseTask.user.getID() + "&starttime=" + pHour + ":" + pMin + ":00" + "&date=" + date + "&desc=" + input.getText().toString();
                                        try {
                                            url = new URL(urlStr);
                                            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                                            httpURLConnection.connect();
                                            InputStream inputStream = httpURLConnection.getInputStream();
                                            inputStream.close();
                                            httpURLConnection.disconnect();
                                        } catch (MalformedURLException e) {
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
                            new ImportAppointment(getActivity()).execute();
                        }

                });

            }
            if(DatabaseTask.user instanceof Student) {
                adb.setMessage("Lecturer : " + lectName + "\n" + "Date : " + date + "\n" + "Time : " + time + ((pHour>=12) ? " P.M." :" A.M.") + "\nVenue : " + lectVenue + "\n\n**Please contact the lecturer if you want to cancel this appointment after the lecturer had approved\n\n");
            } else {
                adb.setMessage("Date : " + date + "\n" + "Time : " + time + ((pHour>=12) ? " P.M." :" A.M.") + "\n\n");
            }
            AlertDialog alert = adb.create();
            if(editAppointment && !spam) {
                if(DatabaseTask.user instanceof Student) {
                    alert.setTitle("Edit Appointment");
                } else {
                    alert.setTitle("Edit Booked Time");
                }

            } else {
                alert.setTitle("Comfirmation");
            }
            alert.show();
        } else {
            DatabaseTask.quitDialog(getActivity());
        }

    }

    public void setDate() {

        TextView[] date = {(TextView) getView().findViewById(R.id.dateMon), (TextView) getView().findViewById(R.id.dateTues), (TextView) getView().findViewById(R.id.dateWed), (TextView) getView().findViewById(R.id.dateThu), (TextView) getView().findViewById(R.id.dateFri)};
        int start = 0;
        Calendar cal = Calendar.getInstance();
        int cur = cal.getTime().getDay();
        if(cur>=0 && cur<=5)
            start = 1-cur;
        else if(cur == 6)
            start = 2;

        cal.add(Calendar.DATE, start);

        for (TextView i : date) {
            i.setText(String.valueOf(cal.getTime().getDate()+"/"+(cal.getTime().getMonth()+1)+"/"+(cal.getTime().getYear()+1900)));
            cal.add(Calendar.DATE, 1);
        }

    }

    private void setSpam(Boolean spam) {
        this.spam = spam;
    }

    public void refresh() {
        new ImportAppointment(getActivity()).execute();
    }

    class CheckSpam extends AsyncTask {

        ProgressDialog progressDialog;
        String result = "";
        String date;
        Activity activity;

        public CheckSpam(String date, Activity activity) {
            this.date = date;
            this.activity = activity;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            progressDialog = new ProgressDialog(activity, ProgressDialog.STYLE_SPINNER);
            progressDialog.setMessage("Checking... ...");
            progressDialog.setCancelable(false);
            progressDialog.show();
        }

        @Override
        protected String doInBackground(Object[] params) {
            if(DatabaseTask.isNetworkAvailable(activity)) {
                String get;
                String urlStr;
                if(editAppointment) {
                    urlStr = "http://smartkidsedu.tk/Android/checkSpam.php?student=" + DatabaseTask.user.getID() + "&lecturer=" + lectID + "&date=" + date;
                } else {
                    urlStr = "http://smartkidsedu.tk/Android/checkSpam.php?student=" + DatabaseTask.user.getID() + "&lecturer=" + lectID + "&date=" + date;
                }
                try {
                    URL url = new URL(urlStr);
                    HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                    InputStream inputStream = httpURLConnection.getInputStream();
                    BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                    while((get = bufferedReader.readLine()) != null){
                        result += get;
                    }
                    bufferedReader.close();
                    inputStream.close();
                    httpURLConnection.disconnect();
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                setSpam(result.contains("true") ? true : false);
            } else {
                DatabaseTask.quitDialog(getActivity());
            }
            return result;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);
            if(progressDialog.isShowing()) {
                progressDialog.dismiss();
            }
        }
    }


    class ImportAppointment extends AsyncTask<Void, Void, Void> {
        ProgressDialog progress;
        Activity activity;
        public ImportAppointment(Activity activity) {
            this.activity = activity;
        }

        @Override
        protected Void doInBackground(Void... params) {
            String result = null;
            String JSON_STRING;

            String urlString;
            if(DatabaseTask.user instanceof Student) {
                urlString = "http://www.smartkidsedu.tk/Android/lecturerAppointmentStudentView.php?lecturer=" + lectID;
            } else {
                urlString = "http://www.smartkidsedu.tk/Android/lecturerAppointment.php?lecturer=" + lectID;
            }
            try {
                URL url = new URL(urlString);
                HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = httpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
                StringBuilder stringBuilder = new StringBuilder();
                while((JSON_STRING = bufferedReader.readLine()) != null){
                    stringBuilder.append(JSON_STRING + "\n");
                }
                bufferedReader.close();
                inputStream.close();
                httpURLConnection.disconnect();
                result = stringBuilder.toString().trim();
            } catch (MalformedURLException e) {
                e.printStackTrace();
                Toast.makeText(activity, "Unknown Host Exception", Toast.LENGTH_SHORT).show();
            } catch (IOException e) {
                e.printStackTrace();
            }

            JSONObject jsonObject;
            JSONArray jsonArray;
            if (result != null) {
                try {
                    jsonObject = new JSONObject(result);
                    jsonArray = jsonObject.optJSONArray("server_response");
                    for (int i = 0; i<jsonArray.length(); i++) {
                        JSONObject JO = jsonArray.getJSONObject(i);
                        int startHour = JO.optInt("start_hour");
                        int startMin = JO.optInt("start_min");
                        int day = JO.optInt("day");
                        String date = JO.optString("date");
                        String student = JO.optString("student");
                        String lecturer = JO.optString("lecturer");
                        int status = JO.optInt("status");
                        String desc = JO.optString("desc");
                        Appointment appointment = new Appointment(startHour, startMin, day, date, student, lecturer, status, desc);
                        appList.add(appointment);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            appList.clear();
            progress = new ProgressDialog(activity);
            progress.setProgressStyle(progress.STYLE_SPINNER);
            progress.setCancelable(false);
            progress.setMessage("Downloading schedule...");
            progress.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            for(int j = 0; j< blockArray.length; j++) {
                blockArray[j] = 0;
            }

            for(Appointment i : appList) {
                if(DatabaseTask.user instanceof Lecturer) {
                    if(i.getStatus() == 3) {
                        continue;
                    } else {
                        blockArray[i.position()] = i.getStatus();
                    }
                } else {
                    if(i.getStatus() == 3) {
                        if(i.getStudent().equals(DatabaseTask.user.getID())) {
                            blockArray[i.position()] = i.getStatus();
                        }
                    } else {
                        blockArray[i.position()] = i.getStatus();
                    }
                }
            }

            if(progress.isShowing()) {
                progress.hide();
            }

            gridView.setAdapter(new AppointmentGridAdapter(activity.getApplicationContext(), blockArray));

            lectID = getArguments().getString("id");
            for(Lecturer x : DatabaseTask.lecturerlist) {
                if(x.findName(lectID) != null) {
                    lectName = x.getName();
                    lectVenue = x.getLectOffice();
                    break;
                }
            }
        }

    }
}
