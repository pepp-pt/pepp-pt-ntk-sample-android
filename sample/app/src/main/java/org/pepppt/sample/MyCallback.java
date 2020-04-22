package org.pepppt.sample;

import android.app.Notification;
import android.content.Intent;
import android.util.Log;

import org.greenrobot.eventbus.EventBus;
import org.pepppt.core.CoreCallback;
import org.pepppt.core.ProximityTracingService;
import org.pepppt.core.events.CoreEvent;
import org.pepppt.core.events.EventArgs;
import org.pepppt.core.events.UploadEventArgs;
import org.pepppt.core.exceptions.PackageNotFoundException;
import org.pepppt.sample.ui.utils.CallbackInfos;
import org.pepppt.sample.ui.utils.NotificationHelper;

class MyCallback extends CoreCallback {
    private static String TAG = MyCallback.class.getSimpleName();

    @Override
    public void onEvent(CoreEvent event, EventArgs args) {
        super.onEvent(event, args);
        Log.i(TAG, event.toString());

        EventBus.getDefault().post(new CallbackInfos(event, args));

        switch (event) {
            case INSUFFICIENT_PERMISSIONS:
                handleInsufficientPermissions(args);
                break;
            case UPLOAD_STARTED:
            case UPLOAD_FAILED:
            case UPLOAD_PAUSED:
            case UPLOAD_FINISHED:
            case UPLOAD_PROGRESS:
                handleUploadEvent(event, args);
                break;
            case REGISTRATION_SUCCESS:
                Intent openPermissionsIntent = new Intent(SampleApplication.getContext(), org.pepppt.sample.ui.activities.onboarding.PermissionsActivity.class);
                openPermissionsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                openPermissionsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                openPermissionsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                SampleApplication.getContext().startActivity(openPermissionsIntent);

            case CORE_IS_READY:
//                EventBus.getDefault().post("test");
                break;

            case NEW_TAN:

//                if (args instanceof NewTanEventArgs) {
//                    NewTanEventArgs tanargs = (NewTanEventArgs)args;
//                    EventBus.getDefault().post(tanargs);
//                }
                break;

            case TAN_ACTIVATION_SUCCEDED:
                Log.i(TAG, "TAN_ACTIVATION_SUCCEDED");
//                EventBus.getDefault().post(new CallbackInfos(event, args));
                break;

            case TAN_ACTIVATION_FAILED:
                Log.i(TAG, "TAN_ACTIVATION_FAILED");
//                EventBus.getDefault().post(new CallbackInfos(event, args));
                break;

            case TAN_REQUEST_FAILED:
                Log.i(TAG, "TAN_REQUEST_FAILED");
//                EventBus.getDefault().post(new CallbackInfos(event, args));
                break;
            case NEW_MESSAGES_ARRIVED: {
                NotificationHelper.showNewMessagesNotification();
                break;
            }
            default:
                break;
        }
    }

    private void handleInsufficientPermissions(EventArgs args) {
        //MissingPermissionsNotificationHelper.show();
    }

    private void handleUploadEvent(CoreEvent event, EventArgs args) {
        UploadEventArgs uploadEventArgs = null;
        if (args != null && args.getClass() == UploadEventArgs.class) {
            uploadEventArgs = (UploadEventArgs) args;
        }
        Notification.Builder notificationBuilder = NotificationHelper.getUploadNotificationBuilder();

        switch (event) {

            case UPLOAD_STARTED:
                NotificationHelper.showUploadNotificationProgress(
                        notificationBuilder,
                        0,
                        "Start upload...",
                        "");
                break;

            case UPLOAD_PROGRESS:
                NotificationHelper.showUploadNotificationProgress(
                        notificationBuilder,
                        (uploadEventArgs != null) ? uploadEventArgs.getProgress() : 0,
                        "Upload in progress",
                        "...");
                break;

            case UPLOAD_PAUSED:
                NotificationHelper.showUploadNotificationProgress(
                        notificationBuilder,
                        (uploadEventArgs != null) ? uploadEventArgs.getProgress() : 0,
                        "Upload paused...",
                        (uploadEventArgs != null) ? uploadEventArgs.getMessage() : "");
                break;

            case UPLOAD_FAILED:
                NotificationHelper.cancelNotification(NotificationHelper.UPLOAD_NOTIFICATION_ID);
                NotificationHelper.showDataUploadFinishedNotification(
                        false,
                        (uploadEventArgs != null) ? uploadEventArgs.getMessage() : null);
                break;

            case UPLOAD_FINISHED:
                NotificationHelper.cancelNotification(NotificationHelper.UPLOAD_NOTIFICATION_ID);
                NotificationHelper.showDataUploadFinishedNotification(
                        true,
                        "");
                try {
                    ProximityTracingService.getInstance().decommission();
                } catch (PackageNotFoundException e) {
                    e.printStackTrace();
                }
                break;

            default:
                break;
        }

    }
}
