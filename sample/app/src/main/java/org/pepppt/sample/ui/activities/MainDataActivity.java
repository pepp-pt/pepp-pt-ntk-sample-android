package org.pepppt.sample.ui.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.pepppt.core.ProximityTracingService;
import org.pepppt.core.events.CoreEvent;
import org.pepppt.core.events.NewTanEventArgs;
import org.pepppt.core.exceptions.PackageNotFoundException;
import org.pepppt.sample.R;
import org.pepppt.sample.SampleApplication;
import org.pepppt.sample.ui.utils.AlertDialogBuilder;
import org.pepppt.sample.ui.utils.CallbackInfos;

public class MainDataActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_main_data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(CallbackInfos callbackInfos) {
        CoreEvent event = callbackInfos.getEvent();
        switch (event) {
            case LABTEST_RESULT_AVAILABLE:
                ProximityTracingService.getInstance().checkForNewMessages(false);
                break;
            case LABTEST_REQUEST_FAILED:
                AlertDialogBuilder.showSimpleWithOk(MainDataActivity.this, getString(R.string.general_error), "LABTEST_REQUEST_FAILED", null);
                break;
        }
    }

    public void dataButtonOnClick(View view) {
        Intent openRegistrationIntent = new Intent(MainDataActivity.this, MainActivity.class);
        openRegistrationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        openRegistrationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(openRegistrationIntent);
        finish();
    }

    public void labTestButtonOnClick(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Request LabTest Result");
        builder.setMessage("Please enter your lab-code here. You will find the code on the test lab's form.");

        final LinearLayout layout = new LinearLayout(this);
        layout.setPadding(60, 0, 60, 0);
        layout.setOrientation(LinearLayout.VERTICAL);

        final LinearLayout layoutLabId = new LinearLayout(this);
        layoutLabId.setOrientation(LinearLayout.HORIZONTAL);
        layout.addView(layoutLabId);

        final TextView textViewLabId = new TextView(this);
        textViewLabId.setText("Lab Id:");
        layoutLabId.addView(textViewLabId);

        final EditText inputLabId = new EditText(this);
        inputLabId.setInputType(InputType.TYPE_CLASS_TEXT);
        inputLabId.setText("200");
        layoutLabId.addView(inputLabId);

        final LinearLayout layoutOrderId = new LinearLayout(this);
        layoutOrderId.setOrientation(LinearLayout.HORIZONTAL);
        layout.addView(layoutOrderId);

        final TextView textViewOrderId = new TextView(this);
        textViewOrderId.setText("Order Id:");
        layoutOrderId.addView(textViewOrderId);

        final EditText inputOrderId = new EditText(this);
        inputOrderId.setInputType(InputType.TYPE_CLASS_TEXT);
        inputOrderId.setText("negativ");
        layoutOrderId.addView(inputOrderId);

        final TextView textViewDesc = new TextView(this);
        textViewDesc.setText("if a result is available, you will be notified");
        layout.addView(textViewDesc);

        builder.setView(layout);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Button labTestButton = findViewById(R.id.labTestButton);
                labTestButton.setEnabled(false);
                String labId = inputLabId.getText().toString();
                String orderId = inputOrderId.getText().toString();
                SampleApplication.getApplication().getProximityService().requestLabTestResult(labId, orderId);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        final AlertDialog dialog = builder.create();
        //builder.show(); //only one of this
        dialog.show(); //only after this, getButton works
    }

    public void uploadButtonOnClick(View view) {

        Intent intent = new Intent(MainDataActivity.this, UploadDataActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
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
        } catch (PackageNotFoundException ex) {
            ex.printStackTrace();
            Log.e("MainDataActivity", ex.toString());
        }
    }
}