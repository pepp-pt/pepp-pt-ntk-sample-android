package org.pepppt.sample.ui.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;

public class AlertDialogBuilder {
    public static void showSimpleWithOk(Context context, String title, String message, DialogInterface.OnClickListener onClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(android.R.string.ok, onClickListener);
        builder.create().show();
    }

    public static void showConfirmationWithYesAndNo(Context context, String title, String message, DialogInterface.OnClickListener actionPositive, DialogInterface.OnClickListener actionNegative) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setInverseBackgroundForced(true);
        builder.setPositiveButton("Yes", actionPositive);
        builder.setNegativeButton("No", actionNegative);
        AlertDialog alert = builder.create();
        alert.show();
    }
}
