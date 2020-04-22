package org.pepppt.sample.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Switch;

import org.pepppt.core.ProximityTracingService;
import org.pepppt.core.exceptions.PackageNotFoundException;
import org.pepppt.sample.R;
import org.pepppt.sample.ui.adapter.LogItemAdapter;
import org.pepppt.sample.ui.utils.DisplayLogger;

public class LogActivity extends AppCompatActivity {
    private static LogActivity instance;
    private RecyclerView rvLogView;
    private LogItemAdapter adapter;
    private Switch swSync;

    public boolean syncEnabled() {
        return swSync.isChecked();
    }

    public static LogActivity getInstance() {
        return instance;
    }

    public RecyclerView getLogView() {
        return rvLogView;
    }

    public LogItemAdapter getAdapter() {
        return adapter;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        instance = this;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log);
        rvLogView = findViewById(R.id.rvLogElements);
        swSync = findViewById(R.id.swSync);
        adapter = new LogItemAdapter(DisplayLogger.loglist);
        getLogView().setAdapter(adapter);
        rvLogView.setLayoutManager(new LinearLayoutManager(this));
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
            Log.e("LogActivity", ex.toString());
        }
    }
}
