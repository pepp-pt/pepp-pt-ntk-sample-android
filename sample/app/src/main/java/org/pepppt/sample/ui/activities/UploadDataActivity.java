package org.pepppt.sample.ui.activities;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Paint;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.pepppt.core.ProximityTracingService;
import org.pepppt.core.events.CoreEvent;
import org.pepppt.core.events.NewTanEventArgs;
import org.pepppt.core.events.UploadEventArgs;
import org.pepppt.core.exceptions.PackageNotFoundException;
import org.pepppt.core.tan.Tan;
import org.pepppt.sample.R;
import org.pepppt.sample.SampleApplication;
import org.pepppt.sample.ui.utils.AlertDialogBuilder;
import org.pepppt.sample.ui.utils.CallbackInfos;

import java.sql.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class UploadDataActivity extends AppCompatActivity {

    private Button button_start_upload;
    private CheckBox checkBox_agree;
    private TextView textView_yourTan;
    private TextView textView_expiry_date;
    private TextView textView_tanState;
    private ProgressBar progressBar_tan;
    private ProgressBar progressBar_upload;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EventBus.getDefault().register(this);
        setContentView(R.layout.activity_upload_data);
        checkBox_agree = findViewById(R.id.upload_data_checkBox_agree);
        button_start_upload = findViewById(R.id.upload_data_button_start);
        textView_yourTan = findViewById(R.id.upload_data_textView_yourTan);
        progressBar_tan = findViewById(R.id.upload_data_progressBar_tan);
        progressBar_upload = findViewById(R.id.upload_data_progressBar_upload);
        textView_expiry_date = findViewById(R.id.upload_data_textView_expiry_date);
        textView_tanState = findViewById(R.id.upload_data_textView_tanState);

        checkBox_agree.setEnabled(false);
        button_start_upload.setEnabled(false);
        progressBar_tan.setVisibility(View.VISIBLE);
        textView_tanState.setText(getString(R.string.upload_data_tan_state_generating));
        progressBar_upload.setVisibility(View.INVISIBLE);
        textView_expiry_date.setText("");

        ProximityTracingService.getInstance().requestTan();
        final Observer<Tan> tanObserver = new Observer<Tan>() {
            @Override
            public void onChanged(@Nullable final Tan tan) {
                if (tan.getExpiration() > 0 && tan.getTanData() != null && tan.getTanData().length() > 0) {   // Update the UI, in this case, a TextView.
                    textView_yourTan.setText(tan.getTanData());
                    DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                    String exp_date = df.format(new Date(tan.getExpiration()));
                    textView_expiry_date.setText(exp_date);
                    textView_tanState.setText(getString(R.string.upload_data_tan_state_waiting_for_activating));
                }
            }
        };
        //Observe the LiveData, passing in this activity as the LifecycleOwner and the observer.
        ProximityTracingService.getInstance().getTan().observe(this, tanObserver);

        checkBox_agree.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View checkbox) {
                button_start_upload.setEnabled(((CheckBox) checkbox).isChecked());
            }
        });

        button_start_upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View checkbox) {
                button_start_upload.setEnabled(false);
                SampleApplication.getApplication().getProximityService().startExport();
            }
        });
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
            case NEW_TAN: {
//                if (callbackInfos.getArgs().getClass() == NewTanEventArgs.class)
//                {
//                    textView_yourTan.setText(((NewTanEventArgs) callbackInfos.getArgs()).getTan().getTanData());
//                    DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
//                    String exp_date =  df.format(new Date(((NewTanEventArgs) callbackInfos.getArgs()).getTan().getExpiration()));
//                    textView_expiry_date.setText(exp_date);
//                    textView_tanState.setText(getString(R.string.upload_data_tan_state_waiting_for_activating));
//                }
                break;
            }

            case TAN_ACTIVATION_SUCCEDED: {
                checkBox_agree.setEnabled(true);
                progressBar_tan.setVisibility(View.INVISIBLE);
                textView_tanState.setVisibility(View.INVISIBLE);
                break;
            }
            case TAN_ACTIVATION_FAILED: {
                progressBar_tan.setVisibility(View.INVISIBLE);
                textView_tanState.setVisibility(View.INVISIBLE);
                textView_yourTan.setPaintFlags(textView_yourTan.getPaintFlags() | Paint.STRIKE_THRU_TEXT_FLAG);
                //TODO: getMessage from EventArgs?
                AlertDialogBuilder.showSimpleWithOk(UploadDataActivity.this, getString(R.string.general_error), getString(R.string.upload_data_tan_activation_failed), null);
                break;
            }
            case TAN_REQUEST_FAILED: {
                //TODO: getMessage from EventArgs?
                progressBar_tan.setVisibility(View.INVISIBLE);
                textView_tanState.setVisibility(View.INVISIBLE);
                AlertDialogBuilder.showSimpleWithOk(UploadDataActivity.this, getString(R.string.general_error), getString(R.string.upload_data_tan_request_failed), null);
                break;
            }
            case UPLOAD_STARTED: {
                progressBar_upload.setVisibility(View.VISIBLE);
                break;
            }
            case UPLOAD_PAUSED:
            case UPLOAD_PROGRESS: {
                progressBar_upload.setVisibility(View.VISIBLE);
                int progress = 0;
                if (callbackInfos.getArgs().getClass() == UploadEventArgs.class) {
                    progress = ((UploadEventArgs) callbackInfos.getArgs()).getProgress();
                }
                progressBar_upload.setProgress(progress);
                break;
            }
            case UPLOAD_FINISHED: {
                progressBar_upload.setVisibility(View.INVISIBLE);
                Intent intent = new Intent(UploadDataActivity.this, UploadFinishedActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                break;
            }
            case UPLOAD_FAILED: {
                progressBar_upload.setVisibility(View.INVISIBLE);
                String add_mess = "";
                if (callbackInfos.getArgs().getClass() == UploadEventArgs.class) {
                    add_mess = ((UploadEventArgs) callbackInfos.getArgs()).getMessage();
                }
                AlertDialogBuilder.showSimpleWithOk(UploadDataActivity.this, getString(R.string.general_error), getString(R.string.upload_data_upload_failed) + " " + add_mess, null);
                break;
            }
        }
    }

    public void dataButtonOnClick(View view) {
        Intent intent = new Intent(UploadDataActivity.this, MainDataActivity.class);
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
        } catch (PackageNotFoundException e) {
            e.printStackTrace();
        }
    }
}

