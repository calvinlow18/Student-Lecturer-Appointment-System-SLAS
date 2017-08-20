package se.lowkhaiwynn.slas;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.NavigationView;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

public class AfterLogin extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private Boolean doublePressed;
    private Menu menu;
    public static boolean check;

    public AfterLogin() {
        doublePressed = false;
        check = false;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_after_login);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        if(DatabaseTask.isNetworkAvailable(getApplicationContext())) {
            DatabaseTask.checkRemember(this);
            if(DatabaseTask.remember) {
                DatabaseTask.runAlarm(getApplicationContext());
            }
            Boolean pass = getIntent().getBooleanExtra("Pass Login", false);
            if (DatabaseTask.remember || pass) {
                if (DatabaseTask.getRole(getApplicationContext()).equals("lecturer")) {
                    DatabaseTask.retrieveLecturer();
                } else if (DatabaseTask.getRole(getApplicationContext()).equals("student")) {
                    DatabaseTask.retrieveStudent();
                }



                NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
                navigationView.setNavigationItemSelectedListener(this);

                if (DatabaseTask.user instanceof Student) {
                    MenuItem future_info_mi = navigationView.getMenu().findItem(R.id.future_app);
                    future_info_mi.setVisible(false);
                    future_info_mi.setEnabled(false);
                }

                View header = navigationView.getHeaderView(0);
                TextView id = (TextView) header.findViewById(R.id.navIdentity);
                TextView name = (TextView) header.findViewById(R.id.navName);
                ImageView imageView = (ImageView) header.findViewById(R.id.imageView);
                id.setText(DatabaseTask.user.ID);
                name.setText(DatabaseTask.user.Name);
                ColorGenerator mColorGenerator = ColorGenerator.MATERIAL;
                imageView.setImageDrawable(TextDrawable.builder().buildRound(String.valueOf(DatabaseTask.user.getName().charAt(0)).toUpperCase(), mColorGenerator.getRandomColor()));

                ProgressDialog progressDialog = new ProgressDialog(this);

                progressDialog.setMessage("Loading data ... ...");
                progressDialog.setProgressStyle(progressDialog.STYLE_SPINNER);
                progressDialog.setCancelable(false);
                progressDialog.show();
                new BackgroundTask().execute();
                progressDialog.dismiss();
            }else{
                startActivity(new Intent(this, RoleSelectionActivity.class));
            }
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_content, new AdsActivity());
            fragmentTransaction.commit();
        } else {
            AlertDialog.Builder adb = new AlertDialog.Builder(this);
            adb.setMessage("Please Check Your Connection !!!")
                    .setCancelable(false)
                    .setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                            startActivity(getIntent());
                        }
                    })
                    .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    });
            AlertDialog alert = adb.create();
            alert.setTitle("Alert...");
            alert.show();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        if(getSupportActionBar() != null) {
            getSupportActionBar().setTitle("SLAS");
        }
        if(check) {
            check = false;
        } else {
            if(DatabaseTask.user instanceof Lecturer) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_content, new AdsActivity());
                fragmentTransaction.commit();
            }
        }

    }

    @Override
    public void onBackPressed() {

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            if(doublePressed) {
                finishAffinity();
                super.onBackPressed();
            } else {
                doublePressed = true;
                Toast.makeText(this, "Please click BACK again to exit", Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doublePressed=false;
                    }
                }, 2000);
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        this.menu = menu;
        return true;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == R.id.make_appointment) {
            if(DatabaseTask.user instanceof Student) {
                FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
                fragmentTransaction.replace(R.id.frame_content, new LecturerList());
                fragmentTransaction.addToBackStack("After Login");
                fragmentTransaction.commit();
            } else {
                startActivity(new Intent(this, AppointmentActivity.class));
            }
        } else if (id == R.id.future_app) {
            startActivity(new Intent(this, FutureAppointmentActivity.class));
        } else if (id == R.id.lecturer_info) {
            if(menu!=null && DatabaseTask.user instanceof Student) {
                MenuItem menuItem = menu.findItem(R.id.action_search);
                if(menuItem != null) {
                    menuItem.setVisible(false);
                }
            }
            FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
            fragmentTransaction.replace(R.id.frame_content, new LecturerInfoList());
            fragmentTransaction.addToBackStack("Lect Info List");
            fragmentTransaction.commit();
        } else if (id == R.id.log_out) {
            DatabaseTask.clearRemember();
            DatabaseTask.clearAlarm();
            Intent intent = new Intent(this, RoleSelectionActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    class BackgroundTask extends AsyncTask {

        @Override
        protected Object doInBackground(Object[] params) {

            try {
                URL url = new URL("http://www.smartkidsedu.tk/Android/lecturerlist.php");
                URLConnection connection = url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader
                        = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String response = "";
                String line = "";
                while((line = bufferedReader.readLine()) != null)
                {
                    response+=line;
                }
                bufferedReader.close();
                inputStream.close();
                JSONObject jsonObject;
                JSONArray jsonArray;
                if (response != null) {
                    DatabaseTask.lecturerlist.clear();
                    try {
                        jsonObject = new JSONObject(response);
                        jsonArray = jsonObject.optJSONArray("server_response");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject JO = jsonArray.getJSONObject(i);
                            String lectID = JO.optString("LectID");
                            String lectName = JO.optString("LectName");
                            String lectEmail = JO.optString("LectEmail");
                            String lectDept = JO.optString("LectDept");
                            String lectOffice = JO.optString("LectOffice");
                            String lectNo = JO.optString("LectNo");
                            DatabaseTask.lecturerlist.add(new Lecturer(lectID, lectName, lectEmail, lectDept, lectOffice, lectNo));
                        }
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            try {
                URL url = new URL("http://www.smartkidsedu.tk/Android/studentlist.php");
                URLConnection connection = url.openConnection();
                connection.connect();
                InputStream inputStream = connection.getInputStream();
                BufferedReader bufferedReader
                        = new BufferedReader(new InputStreamReader(inputStream, "iso-8859-1"));
                String response = "";
                String line = "";
                while((line = bufferedReader.readLine()) != null)
                {
                    response+=line;
                }
                bufferedReader.close();
                inputStream.close();
                JSONObject jsonObject;
                JSONArray jsonArray;
                if (response != null) {
                    DatabaseTask.studentlist.clear();
                    try {
                        jsonObject = new JSONObject(response);
                        jsonArray = jsonObject.optJSONArray("server_response");
                        for (int i = 0; i < jsonArray.length(); i++) {
                            JSONObject JO = jsonArray.getJSONObject(i);
                            String stuID = JO.optString("stuID");
                            String stuName = JO.optString("stuName");
                            String stuEmail = JO.optString("stuEmail");
                            String stuNo = JO.optString("stuNo");
                            String stuSem = JO.optString("stuSem");
                            int stuIntake = JO.optInt("stuIntake");
                            String stuDept = JO.optString("stuDept");
                            DatabaseTask.studentlist.add(new Student(stuID, stuName, stuEmail, stuNo, stuSem, stuIntake, stuDept));
                        }
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

    }



}
