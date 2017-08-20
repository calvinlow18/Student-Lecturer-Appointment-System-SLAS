package se.lowkhaiwynn.slas;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ShowAppointment extends AppCompatActivity {

    int lect;
    int stu;
    int appointment;
    Boolean future;
    Boolean onResumeInvoke;
    String deleteDisapprove;

    public ShowAppointment() {
        deleteDisapprove = "disapprove";
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_appointment);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Appointment");

        onResumeInvoke = false;

        if(DatabaseTask.user instanceof Lecturer) {
            stu = getIntent().getIntExtra("student", 0);
        } else {
            lect = getIntent().getIntExtra("lecturer", 0);
        }
        appointment = getIntent().getIntExtra("appointment", 0);
        future = getIntent().getBooleanExtra("future", false);

        TextView tvlect = (TextView) findViewById(R.id.lecturershowapp);
        TextView tvstu = (TextView) findViewById(R.id.studentshowapp);
        TextView tvdate = (TextView) findViewById(R.id.dateshowapp);
        TextView tvstart = (TextView) findViewById(R.id.starttimeshowapp);
        Button descBtn = (Button) findViewById(R.id.descBtn);
        TextView tvstatus = (TextView) findViewById(R.id.status);
        RelativeLayout relativeLayout = (RelativeLayout) findViewById(R.id.appointmentDetails);

        final Appointment currentApp = DatabaseTask.appList.get(appointment);

        switch(currentApp.getStatus()) {
            case 1:
                relativeLayout.setBackground(getResources().getDrawable(R.drawable.pendingappointment));
                break;
            case 2:
                relativeLayout.setBackground(getResources().getDrawable(R.drawable.approveappointment));
                break;
            case 3:
                relativeLayout.setBackground(getResources().getDrawable(R.drawable.disapproveappointment));
                break;
        }

        if(currentApp.getDesc().equals("")) {
            descBtn.setVisibility(View.GONE);
        }

        if(DatabaseTask.user instanceof Lecturer) {
            tvlect.setText(DatabaseTask.user.getName());
            tvstu.setText(stu == -1 ? " - " : DatabaseTask.studentlist.get(stu).getName());
        } else {
            tvlect.setText(DatabaseTask.lecturerlist.get(lect).getName());
            tvstu.setText(DatabaseTask.user.getName());
        }

        View view = this.getWindow().getDecorView();

        switch(currentApp.getStatus()) {
            case 1:
                tvstatus.setText("Pending");
                view.setBackgroundColor(view.getResources().getColor(R.color.lightYellow));
                break;
            case 2:
                tvstatus.setText("Approved");
                view.setBackgroundColor(view.getResources().getColor(R.color.mediumSeaGreen));
                break;
            case 3:
                tvstatus.setText("Disapproved");
                view.setBackgroundColor(view.getResources().getColor(R.color.tomato));
                break;
        }


        tvdate.setText(currentApp.getDate());
        tvstart.setText(currentApp.getTime());
        descBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Animation animFadein = AnimationUtils.loadAnimation(getApplicationContext(),R.anim.fade_in);
                view.startAnimation(animFadein);
                showDesc(currentApp);
            }
        });

        ImageView approve = (ImageView) findViewById(R.id.approveBtn);
        ImageView edit = (ImageView) findViewById(R.id.editBtn);
        final ImageView delete = (ImageView) findViewById(R.id.deleteBtn);

        if(DatabaseTask.user instanceof Student) {
            approve.setVisibility(View.GONE);
            delete.setVisibility(View.GONE);
            if(currentApp.getStatus() == 2) {
                edit.setVisibility(View.GONE);
            }
        } else {
            if(currentApp.getStatus() == 2) {
                approve.setVisibility(View.GONE);
                if(future) {
                    edit.setVisibility(View.GONE);
                }
                if(tvstu.getText().equals(" - ")) {
                    delete.setImageResource(R.drawable.delete);
                    deleteDisapprove = "delete";
                } else{
                    edit.setVisibility(View.GONE);
                }
            } else {
                delete.setImageResource(R.drawable.disapprove);
                deleteDisapprove = "disapprove";
                edit.setVisibility(View.GONE);
            }
        }

        approve.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DatabaseTask.isNetworkAvailable(ShowAppointment.this)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {

                            String urlStr = "http://www.smartkidsedu.tk/Android/approveAppointment.php?start=" + currentApp.getStart_hour() + ":" + currentApp.getStart_min() + ":00&date=" + currentApp.getDate() + "&student=" + DatabaseTask.studentlist.get(stu).getID() + "&lecturer=" + DatabaseTask.user.getID();
                            try{
                                URL url;
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
                    }).start();
                    Toast.makeText(getBaseContext(), "Approval Success", Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    DatabaseTask.quitDialog(ShowAppointment.this);
                }
            }

        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), AppointmentActivity.class);
                String[] appArr = {currentApp.getLecturer(), currentApp.getStudent(), currentApp.getDate(), currentApp.getStart_hour() + ":" + currentApp.getStart_min()};
                intent.putExtra("app", appArr);
                intent.putExtra("position", lect);
                intent.putExtra("edit", true);
                startActivity(intent);
            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(DatabaseTask.isNetworkAvailable(ShowAppointment.this)) {
                    new Thread(new Runnable() {
                        @Override
                        public void run() {
                            String urlStr;
                            if(deleteDisapprove.equals("delete")/*delete.getText().equals("Delete Appointment")*/) {
                                urlStr = "http://www.smartkidsedu.tk/Android/deleteAppointment.php?start=" + currentApp.getStart_hour() + ":" + currentApp.getStart_min() + ":00&date=" + currentApp.getDate() + "&lecturer=" + currentApp.getLecturer() + "&student=" + currentApp.getStudent();
                            } else {
                                urlStr = "http://www.smartkidsedu.tk/Android/disapproveAppointment.php?start=" + currentApp.getStart_hour() + ":" + currentApp.getStart_min() + ":00&date=" + currentApp.getDate() + "&lecturer=" + DatabaseTask.user.getID() + "&student=" + currentApp.getStudent();
                            }
                            urlStr = urlStr.replaceAll(" ", "%20");
                            try
                            {
                                URL url;
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
                    }).start();
                    if(/*delete.getText().equals("Delete Appointment")*/deleteDisapprove.equals("delete")) {
                        Toast.makeText(getBaseContext(), "Appointment Deleted", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getBaseContext(), "Appointment Disapproved", Toast.LENGTH_LONG).show();
                    }
                    finish();
                } else {
                    DatabaseTask.quitDialog(ShowAppointment.this);
                }
            }
        });


    }

    public void showDesc(Appointment appointment) {
        AlertDialog.Builder adb = new AlertDialog.Builder(this);
        adb.setMessage(appointment.getDesc())
                .setCancelable(true)
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
        AlertDialog alert = adb.create();
        alert.setTitle("Description");
        alert.show();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(onResumeInvoke) {
            finish();
        } else {
            onResumeInvoke = true;
        }
    }

}
