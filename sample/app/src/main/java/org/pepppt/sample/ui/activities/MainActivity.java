package org.pepppt.sample.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.security.ProviderInstaller;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.pepppt.core.CoreContext;
import org.pepppt.core.ProximityTracingService;
import org.pepppt.core.exceptions.PackageNotFoundException;
import org.pepppt.sample.R;
import org.pepppt.sample.SampleApplication;
import org.pepppt.sample.ui.activities.onboarding.PermissionsActivity;
import org.pepppt.sample.ui.activities.onboarding.WelcomeActivity;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;

import javax.net.ssl.SSLContext;


public class MainActivity extends AppCompatActivity {
    private static String TAG = MainActivity.class.getSimpleName();

    private SharedPreferences settings;

    private static Handler handler;
    private static Runnable runnable;
    private static TextView adtx;
    private static TextView adrx;
    private static TextView uptime;
    private static TextView timeSinceInstall;
    private static TextView uniqueEncounters;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("currentView", "MainActivity");
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);

        setContentView(R.layout.activity_main);

        // Solve secure connection issues with older devices (javax.net.ssl.SSLHandshakeException: Handshake failed)
        // See https://guides.codepath.com/android/Using-OkHttp#enabling-tls-v1-2-on-older-devices
        try {
            // Google Play will install latest OpenSSL
            ProviderInstaller.installIfNeeded(getApplicationContext());
            SSLContext sslContext;
            sslContext = SSLContext.getInstance("TLSv1.2");
            sslContext.init(null, null, null);
            sslContext.createSSLEngine();
        } catch (GooglePlayServicesRepairableException | GooglePlayServicesNotAvailableException
                | NoSuchAlgorithmException | KeyManagementException e) {
            e.printStackTrace();
        }

        // Start onboarding process, if the user did not finished yet

        settings = this.getSharedPreferences("org.pepppt.sample.settings", this.MODE_PRIVATE);

        if (!settings.getBoolean("onboarding_finished", false)) {
            Intent openOnboardingIntent = new Intent(MainActivity.this, WelcomeActivity.class);
            openOnboardingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            openOnboardingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(openOnboardingIntent);
            finish();
        }

        // Update scanner measurement values on screen

        adrx = findViewById(R.id.tvScannedAds);
        adtx = findViewById(R.id.tvSendAdvertisements);
        uptime = findViewById(R.id.tvUptime);
        uniqueEncounters = findViewById(R.id.tvUniqueEncounter);

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    long now = System.currentTimeMillis();
                    adtx.setText("" + String.valueOf(CoreContext.getInstance().getMetrics().NumberOfAdvertisementsBroadcasted));
                    adrx.setText("" + String.valueOf(CoreContext.getInstance().getMetrics().NumberOfAdvertisementsReceived));

                    long diff = now - SampleApplication.started;
                    long seconds = (diff / 1000) % 60;
                    long minutes = ((diff / 1000) / 60) % 60;
                    long hours = (diff / 1000) / 3600;
                    uptime.setText("" + hours + "h " + minutes + "m " + seconds + "s");

                    uniqueEncounters.setText(String.valueOf(CoreContext.getInstance().getMetrics().getUniqueIdsCount()));

                    try {
                        PackageManager pm = getPackageManager();
                        long installed = pm.getPackageInfo("org.pepppt.sample.ui", 0).firstInstallTime;
                        diff = now - installed;
                        seconds = (diff / 1000) % 60;
                        minutes = ((diff / 1000) / 60) % 60;
                        hours = (diff / 1000) / 3600;
                        timeSinceInstall.setText("using since: " + hours + "h " + minutes + "m " + seconds + "s");
                    } catch (Exception ex) {

                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                handler.postDelayed(runnable, 1000);
            }
        };
        runnable.run();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(String event) {
        Log.i("MainActivity", "onMessageEvent: " + event.toString());
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case 4712: {
                // If request is cancelled, the result arrays are empty
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    Log.i(TAG, "granted");
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Log.i(TAG, "denied");
                }

                return;
            }

            // Check here other permissions this app might request..
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        try {
            if (ProximityTracingService.getInstance().hasBeenDecommissioned()) {
                Intent nextScreen = new Intent(this, UploadFinishedActivity.class);
                startActivity(nextScreen);
            }
        } catch (PackageNotFoundException ex) {
            ex.printStackTrace();
            Log.e(TAG, ex.toString());
        }

        boolean onboarding_finished = settings.getBoolean("onboarding_finished", false);

        if (onboarding_finished) {
            // After onboarding, check all permissions to be granted permanently
            if (SampleApplication.getApplication().getProximityService().checkAllPermissions().size() > 0 ||
                    SampleApplication.getApplication().getProximityService().isBatteryOptimizationEnabled()) {
                Intent nextScreen = new Intent(this, PermissionsActivity.class);
                startActivity(nextScreen);
            }
        }
    }

    public void mainButtonOnClick(View view) {
        Intent openOnboardingIntent = new Intent(MainActivity.this, MainDataActivity.class);
        openOnboardingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        openOnboardingIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(openOnboardingIntent);
    }
}
