package org.pepppt.sample.ui.network;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.PhoneAuthProvider;

import org.pepppt.sample.SampleApplication;

import java.util.concurrent.TimeUnit;
import java.util.Arrays;
import java.util.List;

/**
 * <h1>FirebbaseHelper</h1>
 * Class for Google Firebase
 * related task such as
 * sign up and token validation
 */

public class FirebaseHelper {
    private String userFirebaseToken;

    private static String LOG_TAG = FirebaseHelper.class.getSimpleName();

    public FirebaseHelper() {
    }

    public FirebaseUser getCurrentFirebaseUser() {

        FirebaseAuth auth = FirebaseAuth.getInstance();
        return auth.getCurrentUser();
    }

    public FirebaseAuth getAuth() {
        return FirebaseAuth.getInstance();
    }

    /**
     * This method is for validating telephone numbers
     * String[] regTPs = first possible segment of mobile numbers
     *
     * @param number the number to check
     * @return boolean number is correct or not
     */
    public boolean numberIsValid(String number) {
        String[] regTPs = {"151", "152", "157", "159", "160", "162", "163", "170", "171", "172", "173", "174", "175", "176", "177", "178", "179", "123"};

        // remove spaces
        number = number.replace(" ", "");

        Log.d("register-checkNumber", number);

        if (number.isEmpty() || number.length() <= 10) {
            return false;
        }

        // remove leading zero
        if (number.charAt(3) == '0') {
            StringBuffer buf = new StringBuffer(number.length() - 1);
            buf.append(number.substring(0, 3)).append(number.substring(4));
            number = buf.toString();
        }

        // check if is valid regTP
        String tpToCheck = number.substring(3, 6);
        List<String> list = Arrays.asList(regTPs);
        if (!list.contains(tpToCheck)) {
            return false;
        }

        return true;
    }

    /**
     * Firebase Telephone Number Verification
     * / Telephone number login helper
     *
     * @param completeNumber    the user's number
     * @param durationInSeconds timeout wait for entering code / auto retrieving code
     * @param activity          the activity you're calling from
     * @param callbacks         the callback function gets triggered after the verification
     */
    public void verifyNumber(String completeNumber, int durationInSeconds, AppCompatActivity activity, PhoneAuthProvider.OnVerificationStateChangedCallbacks callbacks) {
        Log.d("firebase", "Number to verify: " + completeNumber);
        PhoneAuthProvider.getInstance().verifyPhoneNumber(completeNumber, 60, TimeUnit.SECONDS, activity, callbacks);
    }


    /**
     * Sign out the user at Firebase
     * and tell core lib to purge user
     */
    public void unregisterDevice() {
        //TODO tell Core lib to delete user
        FirebaseAuth.getInstance().signOut();
        // TODO set user has not done onBoarding flag
    }

    /**
     * Signing up the device/user at Google Firebase
     * if successfull: Send Data to core lib to
     * perform device sign up (such as IID_Token, Firebase Instance ID)
     */
    public void registerDevice() {
        if (this.getCurrentFirebaseUser() != null) {
            // Verifying Google Firebase Token:
            this.getCurrentFirebaseUser().getIdToken(true).
                    addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        @Override
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if (task.isSuccessful()) {
                                userFirebaseToken = task.getResult().getToken();

                                // REGISTER DEVICE AT BACKEND
                                SampleApplication.getApplication().getProximityService().signUpUser(userFirebaseToken);
                            } else {
                                // Google firbase token verification error:
                                Log.e(LOG_TAG, "Error getting user token! " + task.getException().getMessage(), task.getException());
                            }
                        }
                    });
        }
    }

}
