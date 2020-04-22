package org.pepppt.sample.ui.activities.onboarding;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import org.pepppt.core.ProximityTracingService;
import org.pepppt.core.exceptions.PackageNotFoundException;
import org.pepppt.sample.R;
import org.pepppt.sample.SampleApplication;
import org.pepppt.sample.ui.activities.UploadFinishedActivity;
import org.pepppt.sample.ui.utils.AlertDialogBuilder;
import org.pepppt.sample.ui.activities.MainActivity;
import org.pepppt.sample.ui.utils.NotificationHelper;
import org.pepppt.sample.ui.utils.PermissionInfo;

import java.util.ArrayList;
import java.util.List;

public class PermissionsActivity extends AppCompatActivity {

    List<PermissionInfo> permissionInfos;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("currentView", "Onboarding: PermissionsActivity");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_onboarding_permissions);

        permissionInfos = new ArrayList<>();
        permissionInfos.add(new PermissionInfo(Manifest.permission.BLUETOOTH, REQUEST_PERMISSION_BLUETHOOTH, this.getString(R.string.permission_explanation_bluethooth), Build.VERSION_CODES.LOLLIPOP, 1));
        permissionInfos.add(new PermissionInfo(Manifest.permission.ACCESS_COARSE_LOCATION, REQUEST_PERMISSION_ACCESS_COARSE_LOCATION, this.getString(R.string.permission_explanation_access_coarse_location), Build.VERSION_CODES.LOLLIPOP, 2));
        permissionInfos.add(new PermissionInfo(Manifest.permission.ACCESS_BACKGROUND_LOCATION, REQUEST_PERMISSION_ACCESS_BACKGROUND_LOCATION, this.getString(R.string.permission_explanation_access_background_location), Build.VERSION_CODES.Q, 2));
        permissionInfos.add(new PermissionInfo(Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, REQUEST_IGNORE_BATTERY_OPTIMIZATIONS, this.getString(R.string.permission_explanation_request_ignore_battery_optimizations), Build.VERSION_CODES.M, 3));
    }

    private final int REQUEST_PERMISSION_BLUETHOOTH = 4712;
    private final int REQUEST_PERMISSION_ACCESS_COARSE_LOCATION = 4714;
    private final int REQUEST_PERMISSION_ACCESS_BACKGROUND_LOCATION = 4715;
    private final int REQUEST_IGNORE_BATTERY_OPTIMIZATIONS = 4716;

    private Activity getActivity(Context context) {
        while (context instanceof ContextWrapper) {
            if (context instanceof Activity) {
                return (Activity) context;
            }
            context = ((ContextWrapper) context).getBaseContext();
        }
        return null;
    }

    private void showPermissionsRequestExplanation(String title, String message, final String permission, final int permissionRequestCode) {
        AlertDialogBuilder.showSimpleWithOk(this, title, message, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                showRequestPermissionDialog(permission, permissionRequestCode);
            }
        });
    }

    private void showRequestPermissionDialog(String permissionName, int permissionRequestCode) {
        ActivityCompat.requestPermissions(this, new String[]{permissionName}, permissionRequestCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case REQUEST_PERMISSION_BLUETHOOTH:
            case REQUEST_PERMISSION_ACCESS_COARSE_LOCATION:
            case REQUEST_PERMISSION_ACCESS_BACKGROUND_LOCATION:
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(this, "Permission Granted!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Permission Denied!", Toast.LENGTH_SHORT).show();
                }
        }
        request_for_permissions(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        try {
            if (ProximityTracingService.getInstance().hasBeenDecommissioned()) {
                Intent nextScreen = new Intent(this, UploadFinishedActivity.class);
                startActivity(nextScreen);
            }
        } catch (PackageNotFoundException e) {
            e.printStackTrace();
        }
        if (SampleApplication.getApplication().getProximityService().checkAllPermissions().size() == 0 &&
                !SampleApplication.getApplication().getProximityService().isBatteryOptimizationEnabled()) {
            SharedPreferences settings = this.getSharedPreferences("org.pepppt.sample.settings", this.MODE_PRIVATE);

            SharedPreferences.Editor editor = settings.edit();
            editor.putBoolean("onboarding_finished", true);
            editor.commit();

            NotificationHelper.cancelNotification(NotificationHelper.MISSING_PERMISSIONS_NOTIFICATION_ID);

            Intent openRegistrationIntent = new Intent(PermissionsActivity.this, MainActivity.class);
            openRegistrationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            openRegistrationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(openRegistrationIntent);
            finish();
        }
    }

    private void request_for_permissions(Context context) {
        for (PermissionInfo permissionInfo : permissionInfos) {
            if (Build.VERSION.SDK_INT >= permissionInfo.getVersion_code()) {
                if (permissionInfo.getRequestCode() == REQUEST_IGNORE_BATTERY_OPTIMIZATIONS) {
                    if (SampleApplication.getApplication().getProximityService().isBatteryOptimizationEnabled()) {
                        SampleApplication.getApplication().getProximityService().disableBatteryOptimization();
                        break;
                    }
                } else {
                    if (ContextCompat.checkSelfPermission(context, permissionInfo.getName()) == PackageManager.PERMISSION_DENIED) {
                        // Permission is not granted
                        // Should we show an explanation?
                        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(context), permissionInfo.getName())) {
                            // Show an explanation to the user *asynchronously* -- don't block
                            // this thread waiting for the user's response! After the user
                            // sees the explanation, try again to request the permission.
                            showPermissionsRequestExplanation(context.getString(R.string.permission_requestexplanation_title), permissionInfo.getExplanation(), permissionInfo.getName(), permissionInfo.getRequestCode());
                        } else {
                            // No explanation needed; request the permission
                            ActivityCompat.requestPermissions(getActivity(context), new String[]{permissionInfo.getName()}, permissionInfo.getRequestCode());
                        }
                        break;
                    }
                }
            }
        }
    }

    public void permissionsButtonOnClick(View view) {
        request_for_permissions(view.getContext());

    }
}
