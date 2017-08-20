package se.lowkhaiwynn.slas;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.PowerManager;
import android.support.v7.app.NotificationCompat;

public class ReminderReceiver extends BroadcastReceiver {
    public ReminderReceiver() {
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.SCREEN_BRIGHT_WAKE_LOCK | PowerManager.ACQUIRE_CAUSES_WAKEUP, "Wake Up");
        wl.acquire();
        Intent resultIntent = new Intent(context, AfterLogin.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, resultIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mBuilder = (NotificationCompat.Builder) new NotificationCompat.Builder(context)
                .setSmallIcon(R.drawable.slas);
        if(DatabaseTask.user instanceof Lecturer) {
            mBuilder.setContentTitle("Lecturer Reminder");
            mBuilder.setContentText("Please check your schedule in SLAS. There might be new appointments made by students.");
        } else {
            mBuilder.setContentTitle("Student Reminder");
            mBuilder.setContentText("Please check your appointments made.");

        }

        mBuilder.setContentIntent(pendingIntent);
        mBuilder.setVibrate(new long[] {1000, 1000});
        mBuilder.setAutoCancel(true);
        NotificationManager mNotificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.notify(0, mBuilder.build());
    }
}
