package org.pepppt.sample.ui.activities.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import org.pepppt.core.ProximityTracingService;
import org.pepppt.core.exceptions.PackageNotFoundException;
import org.pepppt.sample.R;
import org.pepppt.sample.ui.activities.UploadFinishedActivity;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("currentView", "Onboarding: WelcomeActivity");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_onboarding_welcome);
    }

    public void welcomeButtonOnClick(View view) {
        Intent openRegistrationIntent = new Intent(WelcomeActivity.this, RegistrationActivity.class);
        openRegistrationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        openRegistrationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(openRegistrationIntent);
        finish();
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
    }
}
