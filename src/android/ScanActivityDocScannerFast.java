package com.neutrinos.plugin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

import com.krobys.documentscanner.ScanActivity;
import com.krobys.documentscanner.model.DocumentScannerErrorModel;
import com.krobys.documentscanner.model.ScannerResults;

public class ScanActivityDocScannerFast extends ScanActivity {

    private static final String TAG = ScanActivityDocScannerFast.class.getSimpleName();

    public static void start(Context context) {
        Intent intent = new Intent(context, ScanActivityDocScannerFast.class);
        context.startActivity(intent);
    }

    private AlertDialog.Builder alertDialogBuilder;
    private AlertDialog alertDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.app_scan_activity_layout);
        addFragmentContentLayout();
    }



    @Override
    public void onError(DocumentScannerErrorModel error) {
        //TODO return Error
        //showAlertDialog("Error", error.getErrorMessage().getError(), "ok");
        //showAlertDialog(getString(R.string.error_label), error.getErrorMessage().getError(), getString(R.string.ok_label));
    }

    @Override
    public void onSuccess(ScannerResults scannerResults) {
        String file = Uri.fromFile(scannerResults.getImageFile()).toString();
        Intent returnIntent = new Intent();
        returnIntent.putExtra(ScanConstants.SCANNED_RESULT,file);
        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onClose() {
        Log.d(TAG, "onClose");
        finish();
    }

    private void showAlertDialog(String title, String message, String buttonMessage) {
        alertDialogBuilder = new AlertDialog.Builder(this)
                .setTitle(title)
                .setMessage(message)
                .setPositiveButton(buttonMessage, (dialog, which) -> {

                });
        if (alertDialog != null) {
            alertDialog.dismiss();
        }
        alertDialog = alertDialogBuilder.create();
        alertDialog.setCanceledOnTouchOutside(false);
        alertDialog.show();
    }

}