package se.lowkhaiwynn.slas;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.LinkedList;

/**
 * Created by Calvin Low on 5/14/2016.
 */
public class DatabaseTask {
    public static String role;
    public static User user;
    public static boolean remember = false;
    public static Context c;
    private static SharedPreferences preferences;
    public static LinkedList<Lecturer> lecturerlist = new LinkedList<Lecturer>();
    public static LinkedList<Student> studentlist = new LinkedList<>();
    public static ArrayList<Appointment> appList = new ArrayList<Appointment>();

    private static AlarmManager alarmManager;
    private static PendingIntent rPendingIntent;

    public static String getRole(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        role = preferences.getString("Role", "");
        return role;
    }

    public static void runAlarm(Context context) {
        Calendar calendar = Calendar.getInstance();

        Intent rIntent = new Intent(context, ReminderReceiver.class);
        rPendingIntent = PendingIntent.getBroadcast(context, 0, rIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        alarmManager = (AlarmManager)context.getSystemService(context.ALARM_SERVICE);

        if(user instanceof Student) {
            calendar.set(Calendar.HOUR_OF_DAY, 20);
        } else {
            calendar.set(Calendar.HOUR_OF_DAY, 18);
        }
        calendar.add(Calendar.DATE, 1);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, rPendingIntent);

    }

    public static void clearAlarm() {
        if(alarmManager != null && rPendingIntent != null) {
            alarmManager.cancel(rPendingIntent);
        }
    }

    public static void storeLecturer(String ID, String name, String email, String department, String office, String no, String role) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("remember", true);
        editor.putString("lectID", ID);
        editor.putString("lectName", name);
        editor.putString("lectEmail", email);
        editor.putString("lectDept", department);
        editor.putString("lectOffice", office);
        editor.putString("lectNo", no);
        editor.putString("Role", role);
        editor.apply();
    }

    public static void retrieveLecturer() {
        if(remember == true) {
            String id = preferences.getString("lectID", "");
            String name = preferences.getString("lectName", "");
            String email = preferences.getString("lectEmail", "");
            String dept = preferences.getString("lectDept", "");
            String office = preferences.getString("lectOffice", "");
            String no = preferences.getString("lectNo", "");
            user = new Lecturer(id, name, email, dept, office, no);
        }
    }

    public static void storeStudent(String ID, String name, String email, String no, String stuIntake, int semester, String dept, String role) {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("remember", true);
        editor.putString("stuID", ID);
        editor.putString("stuName", name);
        editor.putString("stuEmail", email);
        editor.putString("stuNo", no);
        editor.putString("stuIntake", stuIntake);
        editor.putInt("stuSemester", semester);
        editor.putString("stuDept", dept);
        editor.putString("Role", role);
        editor.apply();
    }

    public static void retrieveStudent() {
        if(remember == true) {
            String id = preferences.getString("stuID", "");
            String name = preferences.getString("stuName", "");
            String email = preferences.getString("stuEmail", "");
            String no = preferences.getString("stuNo", "");
            String intake = preferences.getString("stuIntake", "");
            int semester = preferences.getInt("stuSemester", 0);
            String dept = preferences.getString("stuDept", "");
            user = new Student(id, name, email, no, intake, semester, dept);
        }
    }

    public static void clearRemember() {
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("remember", false);
        editor.apply();
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void checkRemember(Context context) {
        preferences = PreferenceManager.getDefaultSharedPreferences(context);
        remember = preferences.getBoolean("remember", false);
    }

    public static boolean quitIfNoConnection(final Activity activity) {
        if(!DatabaseTask.isNetworkAvailable(activity)) {
            AlertDialog.Builder adb = new AlertDialog.Builder(activity);
            adb.setMessage("Please Check Your Connection !!!")
                    .setCancelable(false)
                    .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            activity.finishAffinity();
                        }
                    });
            AlertDialog alert = adb.create();
            alert.setTitle("Alert...");
            alert.show();
            return true;
        }
        return false;
    }

    public static void quitDialog(final Activity activity) {
        AlertDialog.Builder adb = new AlertDialog.Builder(activity, R.style.AppTheme_Dialog);
        adb.setMessage("Please check your connection and restart the app!!!")
                .setCancelable(false)
                .setNegativeButton("Close", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        activity.finishAffinity();
                    }
                });
        final AlertDialog alert = adb.create();
        alert.setTitle("Connection Error");
        alert.show();
    }

}
