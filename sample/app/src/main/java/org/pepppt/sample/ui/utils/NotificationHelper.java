package org.pepppt.sample.ui.utils;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import org.pepppt.sample.SampleApplication;
import org.pepppt.sample.R;
import org.pepppt.sample.ui.activities.MessagesActivity;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationHelper {
    public static void cancelNotification(int notificationId) {
        Context context = SampleApplication.getContext();
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(context.NOTIFICATION_SERVICE);
        notificationManager.cancel(notificationId);
    }

    public static void showBluetoothTurnedOffNotification() {
        Context context = SampleApplication.getContext();

        Notification.Builder notificationBuilder =
                new NotificationGenerator(
                        SampleApplication.getContext().getString(R.string.appchannelid),
                        SampleApplication.getContext().getString(R.string.appchannelname),
                        SampleApplication.getContext().getString(R.string.appchanneldesc),
                        NotificationManager.IMPORTANCE_HIGH,
                        Notification.PRIORITY_MAX)
                        .createNotificationBuilder(context,
                                context.getString(R.string.keep_bt_on_title),
                                context.getString(R.string.keep_bt_on_text),
                                R.mipmap.ic_launcher);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder.setAutoCancel(true);
        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(0, notificationBuilder.build());
        }
    }

    public static final int MISSING_PERMISSIONS_NOTIFICATION_ID = 0;

    public static void showMissingPermissionsNotification() {
        Context context = SampleApplication.getContext();

        Notification.Builder notificationBuilder =
                new NotificationGenerator(
                        SampleApplication.getContext().getString(R.string.appchannelid),
                        SampleApplication.getContext().getString(R.string.appchannelname),
                        SampleApplication.getContext().getString(R.string.appchanneldesc),
                        NotificationManager.IMPORTANCE_HIGH,
                        Notification.PRIORITY_MAX)
                        .createNotificationBuilder(context,
                                context.getString(R.string.missing_permission_title),
                                context.getString(R.string.missing_permission_text),
                                R.mipmap.ic_launcher);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder = notificationBuilder.setAutoCancel(true);
        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(MISSING_PERMISSIONS_NOTIFICATION_ID, notificationBuilder.build());
        }
    }

    public static Notification getServiceNotification() {
        Context context = SampleApplication.getContext();

        Notification.Builder notificationBuilder =
                new NotificationGenerator(
                        SampleApplication.getContext().getString(R.string.appchannelid),
                        SampleApplication.getContext().getString(R.string.appchannelname),
                        SampleApplication.getContext().getString(R.string.appchanneldesc),
                        NotificationManager.IMPORTANCE_LOW,
                        Notification.PRIORITY_MIN)
                        .createNotificationBuilder(context,
                                context.getString(R.string.service_notification_title),
                                context.getString(R.string.service_notification_text),
                                R.mipmap.ic_launcher);

        return notificationBuilder.build();
    }

    public static Notification.Builder getUploadNotificationBuilder() {
        Context context = SampleApplication.getContext();

        Notification.Builder notificationBuilder =
                new NotificationGenerator(
                        SampleApplication.getContext().getString(R.string.appchannelid),
                        SampleApplication.getContext().getString(R.string.appchannelname),
                        SampleApplication.getContext().getString(R.string.appchanneldesc),
                        NotificationManager.IMPORTANCE_DEFAULT,
                        Notification.PRIORITY_DEFAULT)
                        .createNotificationBuilder(context,
                                context.getString(R.string.notification_dataupload_title),
                                context.getString(R.string.notification_dataupload_description),
                                R.mipmap.ic_launcher);

        notificationBuilder = notificationBuilder.setOngoing(true);
        return notificationBuilder;
    }

    public static final int UPLOAD_NOTIFICATION_ID = 1701;

    public static void showUploadNotificationProgress(Notification.Builder notificationBuilder, int progress, String title, String text) {
        Context context = SampleApplication.getContext();

        notificationBuilder = notificationBuilder.setProgress(100, progress, false);

        if (text != null) {
            notificationBuilder = notificationBuilder.setContentText(text);
        }
        if (title != null) {
            notificationBuilder = notificationBuilder.setContentTitle(title);
        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(UPLOAD_NOTIFICATION_ID, notificationBuilder.build());
        }
    }

    public static final int DATA_UPLOAD_FINISHED_NOTIFICATION_ID = 74656;

    public static void showDataUploadFinishedNotification(boolean succesfully, String text) {
        Context context = SampleApplication.getContext();
        String title = context.getString(R.string.notification_dataupload_finished_success_title);
        if (!succesfully) {
            title = context.getString(R.string.notification_dataupload_finished_faild_title);
        }

        Notification.Builder notificationBuilder =
                new NotificationGenerator(
                        SampleApplication.getContext().getString(R.string.appchannelid),
                        SampleApplication.getContext().getString(R.string.appchannelname),
                        SampleApplication.getContext().getString(R.string.appchanneldesc),
                        NotificationManager.IMPORTANCE_HIGH,
                        Notification.PRIORITY_MAX)
                        .createNotificationBuilder(context,
                                title,
                                text,
                                R.mipmap.ic_launcher);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder = notificationBuilder.setAutoCancel(true);
        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(DATA_UPLOAD_FINISHED_NOTIFICATION_ID, notificationBuilder.build());
        }
    }

    public static final int NEW_MESSAGES_NOTIFICATION_ID = 2893;

    public static void showNewMessagesNotification() {
        Context context = SampleApplication.getContext();
        Intent notificationIntent = new Intent(context, MessagesActivity.class);
        PendingIntent contentIntent = PendingIntent.getActivity(context, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        Notification.Builder notificationBuilder =
                new NotificationGenerator(
                        SampleApplication.getContext().getString(R.string.appchannelid),
                        SampleApplication.getContext().getString(R.string.appchannelname),
                        SampleApplication.getContext().getString(R.string.appchanneldesc),
                        NotificationManager.IMPORTANCE_HIGH,
                        Notification.PRIORITY_MAX)
                        .createNotificationBuilder(context,
                                context.getString(R.string.notification_new_notification),
                                context.getString(R.string.notification_new_notification_subtitle),
                                R.mipmap.ic_launcher)
                        .setContentIntent(contentIntent);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            notificationBuilder = notificationBuilder.setAutoCancel(true);
        }
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(NEW_MESSAGES_NOTIFICATION_ID, notificationBuilder.build());
        }
    }
}
