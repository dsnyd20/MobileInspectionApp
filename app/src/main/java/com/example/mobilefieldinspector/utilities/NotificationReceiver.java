package com.example.mobilefieldinspector.utilities;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.TaskStackBuilder;


import com.example.mobilefieldinspector.InspectionDetailActivity;
import com.example.mobilefieldinspector.MainActivity;
import com.example.mobilefieldinspector.R;
import com.example.mobilefieldinspector.database.DateConverter;

import static android.content.Context.NOTIFICATION_SERVICE;
import static com.example.mobilefieldinspector.utilities.Constants.CHANNEL_ID;
import static com.example.mobilefieldinspector.utilities.Constants.INSPECTION_ID_KEY;

public class NotificationReceiver extends BroadcastReceiver {

    public static final String alarmFile = "alarmFile";
    public static final String nextAlarmField = "nextAlarmID";
    static int notificationID;
    @Override
    public void onReceive(Context context, Intent intent) {

        //Inputs
        String destination = intent.getStringExtra("destination");
        if (destination == null || destination.isEmpty()) {
            destination = "";
        }
        int id = intent.getIntExtra("id", 0);
        String alarmTitle = intent.getStringExtra("title");
        String alarmTime = intent.getStringExtra("time");
        String alarmVerb = intent.getStringExtra("verb");
        String alarmObject = intent.getStringExtra("alarmObject");
        String alarmText = alarmTitle + " " + alarmVerb + " " + alarmTime + "!";
        int nextAlarmId = intent.getIntExtra("nextAlarmId", getAndIncrementNextAlarmId(context));


        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications)
                .setContentText(alarmTitle)
                .setContentTitle(alarmText);

        //Target intent to launch
        Intent resultIntent;
        Uri uri;
        SharedPreferences sharedPreferences;
        switch (alarmObject) {

            case "inspection":
                resultIntent = new Intent(context, InspectionDetailActivity.class);
                resultIntent.putExtra(INSPECTION_ID_KEY, id);
                break;

            default:
                resultIntent = new Intent(context, MainActivity.class);
                break;
        }

        //Task navigation for target intent.
        TaskStackBuilder stackBuilder = TaskStackBuilder.create(context);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(resultIntent);
        PendingIntent resultPendingIntent = stackBuilder.getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(resultPendingIntent).setAutoCancel(true);

        NotificationManager notificationManager=(NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(nextAlarmId, builder.build());
    }

    public static void scheduleAlert(Context context, String time, String title, int id, String verb, String object) {
        long now = DateConverter.nowDate();
        long alertTime = DateConverter.toTimestamp(time);
        if (now <= DateConverter.toTimestamp(time)) {
            int nextAlarmId = getNextAlarmId(context);
            Intent intent = new Intent(context, NotificationReceiver.class);
            intent.putExtra("title", title);
            intent.putExtra("time", time);
            intent.putExtra("nextAlarmId", nextAlarmId);
            intent.putExtra("id", id);
            intent.putExtra("verb", verb);
            intent.putExtra("alarmObject", object);
            PendingIntent sender = PendingIntent.getBroadcast(context, nextAlarmId, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            alarmManager.set(AlarmManager.RTC_WAKEUP, alertTime, sender);
            Toast toast = Toast.makeText(context, "Notification for " + title + " created for " + time + ".", Toast.LENGTH_SHORT);
            toast.show();

            SharedPreferences sp = context.getSharedPreferences(alarmFile, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putInt(Integer.toString(id), nextAlarmId);
            editor.commit();

            incrementNextAlarmId(context);
        }
    }
    private static int getNextAlarmId(Context context) {
        SharedPreferences alarmPrefs;
        alarmPrefs = context.getSharedPreferences(alarmFile, Context.MODE_PRIVATE);
        int nextAlarmId = alarmPrefs.getInt(nextAlarmField, 1);
        return nextAlarmId;
    }

    private static void incrementNextAlarmId(Context context) {
        SharedPreferences alarmPrefs;
        alarmPrefs = context.getSharedPreferences(alarmFile, Context.MODE_PRIVATE);
        int nextAlarmId = alarmPrefs.getInt(nextAlarmField, 1);
        SharedPreferences.Editor alarmEditor = alarmPrefs.edit();
        alarmEditor.putInt(nextAlarmField, nextAlarmId + 1);
        alarmEditor.commit();
    }

    private static int getAndIncrementNextAlarmId(Context context) {
        int nextAlarmId = getNextAlarmId(context);
        incrementNextAlarmId(context);
        return nextAlarmId;
    }
}