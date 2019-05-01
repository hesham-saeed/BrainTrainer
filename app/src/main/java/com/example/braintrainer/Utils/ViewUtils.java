package com.example.braintrainer.Utils;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.example.braintrainer.R;

public class ViewUtils {
    public static AlertDialog createAlertDialog(Context context, String dialogMessage) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);

        View view = LayoutInflater.from(context).inflate(R.layout.loading_dialog, null);
        TextView message = view.findViewById(R.id.alert_dialog_message_text_view);
        message.setText(dialogMessage);

        builder.setView(view);
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        return dialog;
    }
}
