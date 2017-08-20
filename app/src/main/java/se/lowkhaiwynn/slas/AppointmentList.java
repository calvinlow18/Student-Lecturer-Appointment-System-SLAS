package se.lowkhaiwynn.slas;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
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

/**
 * Created by Calvin Low on 5/31/2016.
 */
public class AppointmentList extends Fragment {

    private SwipeRefreshLayout swipeRefreshLayout;
    private ListView listView;
    private String url;
    private Boolean edit;
    private Boolean future;

    public AppointmentList(String url) {
        this.url = url;
    }


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.appointment_list, container, false);
        edit = getArguments().getBoolean("editAppointment", false);
        future = getArguments().getBoolean("future", false);
        swipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipeAppointment);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimaryDark, R.color.colorPrimary,R.color.colorAccent);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                new InputAppointment(getActivity(), listView).execute();
                swipeRefreshLayout.setRefreshing(false);

            }
        });

        listView = (ListView) view.findViewById(R.id.appointmentList);
        new InputAppointment(getActivity(), listView).execute();
        if(!edit) {
            listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent intent = new Intent(getActivity(), ShowAppointment.class);
                    if(DatabaseTask.user instanceof Lecturer) {
                        Student student = null;
                        for(Student s : DatabaseTask.studentlist) {
                            if(s.getID().equals(DatabaseTask.appList.get(position).getStudent())) {
                                student = s;
                                break;
                            }
                        }
                        int sNo = DatabaseTask.studentlist.indexOf(student);
                        intent.putExtra("student", sNo);
                        intent.putExtra("appointment", position);
                    } else {

                        Lecturer lecturer = null;
                        for(Lecturer l : DatabaseTask.lecturerlist) {
                            if(l.getID().equals(DatabaseTask.appList.get(position).getLecturer())) {
                                lecturer = l;
                                break;
                            }
                        }
                        int lNo = DatabaseTask.lecturerlist.indexOf(lecturer);

                        intent.putExtra("lecturer", lNo);
                        intent.putExtra("appointment", position);

                    }

                    intent.putExtra("date", DatabaseTask.appList.get(position).getDate());
                    intent.putExtra("start", DatabaseTask.appList.get(position).getTime().substring(0, 4));
                    intent.putExtra("description", DatabaseTask.appList.get(position).getDesc());
                    intent.putExtra("status", DatabaseTask.appList.get(position).getStatus());
                    intent.putExtra("future", future);
                    startActivity(intent);
                }
            });

        }

        return view;
    }

    public void refresh() {
        new InputAppointment(getActivity(), listView).execute();
    }



    class InputAppointment extends AsyncTask<Void, Void, Void> {
        ProgressDialog progress;
        Activity activity;
        ListView lv;
        public InputAppointment(Activity activity, ListView lv) {
            this.lv = lv;
            this.activity = activity;
        }

        @Override
        protected Void doInBackground(Void... params) {
            String result = null;
            String JSON_STRING;
            String urlString = null;
            if(DatabaseTask.isNetworkAvailable(getContext())) {
                if(DatabaseTask.user instanceof Lecturer) {
                    if(future) {
                        urlString = "http://www.smartkidsedu.tk/Android/futurelecturerappointment.php?lecturer=" + DatabaseTask.user.getID();
                    } else {
                        urlString = url;
                    }

                } else {
                    urlString = "http://www.smartkidsedu.tk/Android/studentAppointment.php?student=" + DatabaseTask.user.getID();
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
                    Toast.makeText(activity.getApplicationContext(), "Unknown Host Exception", Toast.LENGTH_SHORT).show();
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
                            DatabaseTask.appList.add(appointment);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            DatabaseTask.appList.clear();
            progress = new ProgressDialog(activity);
            progress.setProgressStyle(progress.STYLE_SPINNER);
            progress.setCancelable(false);
            progress.setMessage("Downloading schedule...");
            progress.show();
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);

            if(DatabaseTask.isNetworkAvailable(activity.getApplicationContext())) {
                lv.setAdapter( new AppointmentListAdapter(activity.getApplicationContext()));
            } else {
                DatabaseTask.quitDialog(getActivity());
            }

            if(progress.isShowing()) {
                progress.dismiss();
            }
        }
    }


}
