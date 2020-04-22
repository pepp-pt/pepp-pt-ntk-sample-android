package org.pepppt.sample.ui.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import org.pepppt.sample.R;
import org.pepppt.sample.ui.activities.MainActivity;

public class MaintenanceNotification {
    private PendingIntent notificationPendingIntent;
    private String channelid;
    private String channelname;
    private String channeldesc;

    public MaintenanceNotification(String _channelid, String _channelname, String _channeldesc) {
        channelid = _channelid;
        channelname = _channelname;
        channeldesc = _channeldesc;
    }

    // Since OREO its harder to create a notification. For that to work we need to create a
    // notification channel:
    // https://www.youtube.com/watch?v=tTbd1Mfi-Sk
    // and
    // https://developer.android.com/training/notify-user/channels
    public Notification createNotification(Context context, String title, String text, int icon) {
        if (notificationPendingIntent == null) {
            Intent notificationIntent = new Intent(context, MainActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            notificationPendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        }

        // OREO
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            return createNotificationWithOREO(context, title, text, icon);
        else
            return createNotificationWithLollipop(context, title, text, icon);
    }

    private Notification createNotificationWithLollipop(Context context, String title,
                                                        String text, int icon) {
        return new NotificationCompat.Builder(context, channelid)
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(text)
                .setPriority(Notification.PRIORITY_MAX)
                .setContentIntent(notificationPendingIntent).build();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private Notification createNotificationWithOREO(Context context, String title,
                                                    String text, int icon) {
        NotificationChannel channel = new NotificationChannel(channelid,
                channelname, NotificationManager.IMPORTANCE_HIGH);
        channel.setDescription(channeldesc);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, channelid);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.createNotificationChannel(channel);
        }

        return notificationBuilder
                .setSmallIcon(icon)
                .setColor(ContextCompat.getColor(context, R.color.colorAccent))
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(notificationPendingIntent)
                .setAutoCancel(true)
                .build();
    }
}
