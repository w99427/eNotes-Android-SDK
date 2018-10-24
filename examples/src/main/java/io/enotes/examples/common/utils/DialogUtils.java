package io.enotes.examples.common.utils;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import io.enotes.examples.R;


public class DialogUtils {





    public static AlertDialog showWithdrawDialog(Context context, String address, String amount, String fee) {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(context, R.style.alertdialog);
        alertDialog.setCancelable(false);
        View view = LayoutInflater.from(context).inflate(R.layout.dialog_withdraw, null);
        alertDialog.setTitle(R.string.withdraw_confirmed);
        alertDialog.setView(view);
        ((TextView) view.findViewById(R.id.tv_to_address)).setText(address);
        ((TextView) view.findViewById(R.id.tv_amount)).setText(amount);
        ((TextView) view.findViewById(R.id.tv_fee)).setText(fee);
        alertDialog.setNegativeButton(R.string.dialog_cancel, ((dialog, which) -> {
        }));
        alertDialog.setPositiveButton(R.string.dialog_sure, null);
        AlertDialog alertDialog1 = alertDialog.create();
        alertDialog1.show();
        return alertDialog1;
    }

}
