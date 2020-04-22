package org.pepppt.sample.ui.activities.onboarding;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import org.pepppt.core.CoreCallback;
import org.pepppt.core.ProximityTracingService;
import org.pepppt.core.exceptions.PackageNotFoundException;
import org.pepppt.sample.R;
import org.pepppt.sample.ui.activities.UploadFinishedActivity;
import org.pepppt.sample.ui.network.FirebaseHelper;

public class RegistrationActivity extends AppCompatActivity {

    private CheckBox termsCheckbox;
    private Button registrationButton;
    private static final String TAG = "RegistrationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("currentView", "Onboarding: RegistrationActivity");
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_onboarding_registration);

        this.termsCheckbox = findViewById(R.id.termsCheckbox);
        this.registrationButton = findViewById(R.id.registrationButton);

        //
        // JUST FOR TESTING!!!
        //
        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();
                        Log.d(TAG, "onComplete instance ID token: " + token);

                        // Log and toast
                        String msg = token;
                        Toast.makeText(RegistrationActivity.this, msg, Toast.LENGTH_SHORT).show();
                    }
                });
    }

    public void termsLinkOnClick(View textlink) {

    }

    public void termsCheckboxOnClick(View checkbox) {
        this.registrationButton.setEnabled(((CheckBox) checkbox).isChecked());
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

    public void registrationButtonOnClick(View button) {
        if (button.isEnabled()) {
            FirebaseAuth.getInstance().signInAnonymously()
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("firebase", "signInAnonymously:success");
                                        FirebaseHelper fbh = new FirebaseHelper();
                                        fbh.registerDevice();
                                    } else {
                                        // Toast.makeText(this, "There was a problem signing up your device.", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            }

                    );


            /*Intent openPermissionsIntent = new Intent(RegistrationActivity.this, PermissionsActivity.class);
            openPermissionsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            openPermissionsIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(openPermissionsIntent);
            finish();*/
        }
    }


}
