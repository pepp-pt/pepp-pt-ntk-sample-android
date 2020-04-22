package org.pepppt.sample.ui.utils;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.content.ContextCompat;

import org.pepppt.sample.R;
import org.pepppt.sample.ui.activities.MainActivity;

public class NotificationGenerator {
    private PendingIntent notificationPendingIntent;
    private String channelid;
    private String channelname;
    private String channeldesc;
    private int importance;
    private int priority;

    public NotificationGenerator(String _channelid, String _channelname, String _channeldesc, int _importance, int _priority) {
        channelid = _channelid;
        channelname = _channelname;
        channeldesc = _channeldesc;
        importance = _importance;
        priority = _priority;
    }

    public Notification.Builder createNotificationBuilder(Context context, String title, String text, int icon) {
        if (notificationPendingIntent == null) {
            Intent notificationIntent = new Intent(context, MainActivity.class);
            notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            notificationPendingIntent = PendingIntent.getActivity(context, 0, notificationIntent, 0);
        }

        Notification.Builder notificationBuilder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {   //OREO
            NotificationChannel channel = new NotificationChannel(channelid, channelname, importance);
            if (channeldesc != null && channeldesc.length() > 0) {
                channel.setDescription(channeldesc);
            }

            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }

            notificationBuilder = new Notification.Builder(context, channelid);
            notificationBuilder.setColor(ContextCompat.getColor(context, R.color.colorAccent));
        } else {   //Lollipop
            notificationBuilder = new Notification.Builder(context);
            notificationBuilder.setPriority(priority);
        }

        return notificationBuilder
                .setSmallIcon(icon)
                .setContentTitle(title)
                .setContentText(text)
                .setContentIntent(notificationPendingIntent);
    }
}
