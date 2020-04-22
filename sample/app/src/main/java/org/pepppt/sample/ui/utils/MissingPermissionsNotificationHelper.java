package org.pepppt.sample.ui.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.content.Context;

import org.pepppt.sample.R;
import org.pepppt.sample.SampleApplication;
import org.pepppt.sample.ui.activities.MainActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

public class MissingPermissionsNotificationHelper {
    private static Notification notification;

    /*
    public static void show() {
        Context context = SampleApplication.getContext();
        Notification n = MainActivity.permissionnotification.createNotification(context,
          context.getString(R.string.missing_permission_title),
          context.getString(R.string.missing_permission_text), R.mipmap.ic_launcher);
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null)
            notificationManager.notify(0, n);
    }
    */
}
