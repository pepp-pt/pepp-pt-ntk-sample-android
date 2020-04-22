package org.pepppt.sample.ui.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.pepppt.core.ProximityTracingService;
import org.pepppt.core.events.CoreEvent;
import org.pepppt.sample.R;
import org.pepppt.sample.SampleApplication;
import org.pepppt.sample.ui.utils.AlertDialogBuilder;
import org.pepppt.sample.ui.utils.CallbackInfos;

public class UploadFinishedActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_finished);
        ProximityTracingService.getInstance().checkForNewMessages(true);


    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CallbackInfos callbackInfos) {
        CoreEvent event = callbackInfos.getEvent();

        DialogInterface.OnClickListener reCheckMessages = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                ProximityTracingService.getInstance().checkForNewMessages(true);
            }
        };
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    public void dataButtonOnClick(View view) {
        try {
            ProximityTracingService.getInstance().decommission();
            Uri packageUri = Uri.parse("package:" + SampleApplication.getApplication().getPackageName());
            Intent uninstallIntent = new Intent(Intent.ACTION_UNINSTALL_PACKAGE, packageUri);
            startActivity(uninstallIntent);
        } catch (Exception ex) {
            AlertDialogBuilder.showSimpleWithOk(this, getString(R.string.general_error), ex.getMessage(), null);
            Log.e("UploadFinishedActivity", ex.getMessage(), ex);
        }

    }
}
