package com.neutrinos.plugin;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.StrictMode;
import android.util.Base64;

import com.krobys.documentscanner.ui.DocumentScanner;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import java.io.ByteArrayOutputStream;
import java.net.URL;

public class Scan extends CordovaPlugin {

    private static final int REQUEST_CODE = 99;
    public CallbackContext callbackContext;

    public static final int PERMISSION_DENIED_ERROR = 20;
    public static final int TAKE_PIC_SEC = 0;
    public static final int SAVE_TO_ALBUM_SEC = 1;

    protected final static String[] permissions = {Manifest.permission.CAMERA,
            Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};

    @Override
    protected void pluginInitialize() {
        super.pluginInitialize();
        DocumentScanner.Configuration config = new DocumentScanner.Configuration(1000, -1, Bitmap.CompressFormat.JPEG, false, true, true, true, true, true);
        DocumentScanner.INSTANCE.init(this.cordova.getContext(), config);

        //DocumentScanner.Configuration config = new DocumentScanner.Configuration(1000, -1, Bitmap.CompressFormat.JPEG);
        //DocumentScanner.INSTANCE.init(this.cordova.getContext(), config);
    }

    @Override
    public boolean execute(String action, JSONArray args, CallbackContext callbackContext) throws JSONException {

        if (action.equals("scanDoc")) {
            //Take the values from the arguments if they're not already defined (this is tricky)
            this.callbackContext = callbackContext;

            cordova.setActivityResultCallback(this);

            try {
                Intent intent = new Intent(cordova.getActivity().getApplicationContext(), ScanActivityDocScannerFast.class);
                cordova.getActivity().startActivityForResult(intent, REQUEST_CODE);
            } catch (IllegalArgumentException e) {
                this.callbackContext.error("Illegal Argument Exception");
                PluginResult r = new PluginResult(PluginResult.Status.ERROR);
                this.callbackContext.sendPluginResult(r);
            } catch (Exception e) {
                this.callbackContext.error("Something went wrong! Try reducing the quality option.");
                PluginResult r = new PluginResult(PluginResult.Status.ERROR);
                this.callbackContext.sendPluginResult(r);
            }

            return true;

        } else {

            return false;

        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE && resultCode == cordova.getActivity().RESULT_OK) {
            String uri = data.getExtras().getString(ScanConstants.SCANNED_RESULT);
            if (uri != null) {
                this.callbackContext.success(uri);
            } else {
                this.callbackContext.error("null data from scan libary");
            }
        } else {
            this.callbackContext.error("Incorrect result or user canceled the action.");
        }
    }

    /**
     * @param url - web url
     * @return - Base64 String
     * Method used to Convert URL to Base64 String
     */
    public String convertUrlToBase64(String url) {
        URL newurl;
        Bitmap bitmap;
        String base64 = "";
        try {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
            newurl = new URL(url);
            bitmap = BitmapFactory.decodeStream(newurl.openConnection().getInputStream());
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            base64 = Base64.encodeToString(outputStream.toByteArray(), Base64.NO_WRAP);
        } catch (Exception e) {
            this.callbackContext.error(e.getMessage());
        }
        return base64;
    }
}
