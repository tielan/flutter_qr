package com.tielan.flutterqr;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Process;

import java.util.Map;

import io.flutter.app.FlutterActivity;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/**
 * FlutterQrPlugin
 */
public class FlutterQrPlugin implements MethodCallHandler, PluginRegistry.ActivityResultListener {

    private static final String CHANNEL = "flutter_qr";

    private static final int REQUEST_CODE_SCAN_ACTIVITY = 2777;
    private static FlutterQrPlugin instance;

    private Activity activity;
    private Result pendingResult;

    public FlutterQrPlugin(Activity activity) {
        this.activity = activity;
    }

    /**
     * Plugin registration.
     */
    public static void registerWith(Registrar registrar) {
        if (instance == null) {
            final MethodChannel channel = new MethodChannel(registrar.messenger(), CHANNEL);
            instance = new FlutterQrPlugin(registrar.activity());
            registrar.addActivityResultListener(instance);
            channel.setMethodCallHandler(instance);
        }
    }


    @Override
    public void onMethodCall(MethodCall call, Result result) {
        if (pendingResult != null) {
            result.error("ALREADY_ACTIVE", "QR Code reader is already active", null);
            return;
        }
        pendingResult = result;
        if (call.method.equals("readQRCode")) {
                startView();
        } else {
            throw new IllegalArgumentException("Unknown method " + call.method);
        }
    }

    private void startView() {
        Intent intent = new Intent(activity, QRScanActivity.class);
        activity.startActivityForResult(intent, REQUEST_CODE_SCAN_ACTIVITY);
    }

    @Override
    public boolean onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CODE_SCAN_ACTIVITY) {
            if (resultCode == Activity.RESULT_OK) {
                String string = data.getStringExtra(QRScanActivity.EXTRA_RESULT);
                pendingResult.success(string);
            } else {
                pendingResult.success(null);
            }
            pendingResult = null;
            return true;
        }
        return false;
    }

}
