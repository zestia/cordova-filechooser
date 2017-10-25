package com.megster.cordova;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.OpenableColumns;
import android.util.Log;

import org.apache.cordova.CordovaArgs;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONException;
import org.json.JSONObject;

public class FileChooser extends CordovaPlugin {
    private static final String ACTION_OPEN = "open";

    private static final String TAG = "FileChooser";
    private static final int PICK_FILE_REQUEST = 1;

    CallbackContext callback;

    @Override
    public boolean execute(String action, CordovaArgs args, CallbackContext callbackContext) throws JSONException {
        if (action.equals(ACTION_OPEN)) {
            chooseFile(callbackContext);
            return true;
        }

        return false;
    }

    public void chooseFile(CallbackContext callbackContext) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("*/*");
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.putExtra(Intent.EXTRA_LOCAL_ONLY, true);

        Intent chooser = Intent.createChooser(intent, "Select File");
        cordova.startActivityForResult(this, chooser, PICK_FILE_REQUEST);

        PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
        pluginResult.setKeepCallback(true);
        callback = callbackContext;
        callbackContext.sendPluginResult(pluginResult);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PICK_FILE_REQUEST && callback != null) {
            if (resultCode == Activity.RESULT_OK) {
                Uri uri = data.getData();

                if (uri != null) {
                    Log.w(TAG, uri.toString());
                    Cursor cursor = this.cordova.getActivity().getContentResolver().query(uri, null, null, null, null);
                    int nameIdx = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME);
                    cursor.moveToFirst();

                    JSONObject json = new JSONObject();
                    try {
                        json.put("filePath", uri.toString());
                        json.put("fileName", cursor.getString(nameIdx));
                        callback.success(json);
                    } catch (JSONException e) {
                        callback.error("Unable to create JSON object");
                    }
                } else {
                    callback.error("File uri was null");
                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                PluginResult pluginResult = new PluginResult(PluginResult.Status.NO_RESULT);
                callback.sendPluginResult(pluginResult);
            } else {
                callback.error(resultCode);
            }
        }
    }
}
