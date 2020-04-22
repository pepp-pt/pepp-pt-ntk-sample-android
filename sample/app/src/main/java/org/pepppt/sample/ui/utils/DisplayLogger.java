package org.pepppt.sample.ui.utils;

import org.pepppt.sample.ui.activities.LogActivity;
import org.pepppt.sample.ui.adapter.LogItem;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class DisplayLogger {
    public static ArrayList<LogItem> loglist = new ArrayList<>();

    public static void printLine(String line) {
        String currentTime = new SimpleDateFormat("HH:mm:ss", Locale.getDefault()).format(new Date());
        String dattime = currentTime + ": ";
        String print = dattime + line + "\n";
        if (LogActivity.getInstance() != null) {
            if (LogActivity.getInstance().getAdapter() != null) {
                LogActivity.getInstance().getAdapter().addScanItem(new LogItem(print));
            }
        } else {
            loglist.add(new LogItem(print));
        }
    }
}
