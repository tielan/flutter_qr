package com.tielan.flutterqr;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.Toast;


import cn.bingoogolapple.qrcode.core.QRCodeView;
import cn.bingoogolapple.qrcode.zxing.ZXingView;

public class QRScanActivity extends Activity implements QRCodeView.Delegate{

    public static String EXTRA_RESULT = "extra_result";
    public static String ANIM_KEY = "anim_key";

    private boolean qrRead;

    private static final int MY_PERMISSIONS_REQUEST_CAMERA_PHONE = 777;
    private static final String TAG = QRScanActivity.class.getSimpleName();
    ZXingView mZXingView;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTranslucentStatus();
        setContentView(R.layout.activity_qr_read);
        mZXingView = findViewById(R.id.zxingview);
        mZXingView.setDelegate(this);
    }
    protected void setTranslucentStatus() {
        // 5.0以上系统状态栏透明
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Window window = getWindow();
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(Color.TRANSPARENT);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(requestCodeQRCodePermissions()){
            mZXingView.startCamera(); // 打开后置摄像头开始预览，但是并未开始识别
            mZXingView.startSpotAndShowRect(); // 显示扫描框，并且延迟0.1秒后开始识别
        }
    }
    //实现返回 无动画
    @Override
    protected void onPause() {
        boolean anim = getIntent().getBooleanExtra(ANIM_KEY,false);
        if(!anim){
            overridePendingTransition(0,0);
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        mZXingView.stopCamera(); // 关闭摄像头预览，并且隐藏扫描框
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mZXingView.onDestroy(); // 销毁二维码扫描控件
        super.onDestroy();
    }

    @Override
    public void onScanQRCodeSuccess(String result) {
        Log.i(TAG, "result:" + result);
        if (!qrRead) {
            synchronized (this) {
                qrRead = true;
                Intent data = new Intent();
                data.putExtra(EXTRA_RESULT, result);
                setResult(Activity.RESULT_OK, data);
                finish();
            }
        }
    }

    @Override
    public void onCameraAmbientBrightnessChanged(boolean isDark) {
        // 这里是通过修改提示文案来展示环境是否过暗的状态，接入方也可以根据 isDark 的值来实现其他交互效果
        String tipText = mZXingView.getScanBoxView().getTipText();
        if(tipText == null) return;
        String ambientBrightnessTip = "\n环境过暗，请打开闪光灯";
        if (isDark) {
            if (!tipText.contains(ambientBrightnessTip)) {
                mZXingView.getScanBoxView().setTipText(tipText + ambientBrightnessTip);
            }
        } else {
            if (tipText.contains(ambientBrightnessTip)) {
                tipText = tipText.substring(0, tipText.indexOf(ambientBrightnessTip));
                mZXingView.getScanBoxView().setTipText(tipText);
            }
        }
    }
    @Override
    public void onScanQRCodeOpenCameraError() {
        Log.e(TAG, "打开相机出错");
    }

    public void onBackClick(View v) {
        Intent data = new Intent();
        setResult(Activity.RESULT_CANCELED, data);
        finish();
        //  mZXingView.openFlashlight(); // 打开闪光灯
        //    mZXingView.closeFlashlight(); // 关闭闪光灯
    }
    private boolean isOpened = false;

    public void onLightClick(View imageView){
        if(isOpened){
            ((ImageView)imageView).setImageResource(R.drawable.ic_shoudiantongdakai);
            mZXingView.closeFlashlight();
            isOpened = false;
        }else{

            ((ImageView)imageView).setImageResource(R.drawable.ic_shoudiantongdianliang);
            mZXingView.openFlashlight();
            isOpened = true;
        }

    }

    public void onPermissionsGranted() {
        mZXingView.startCamera(); // 打开后置摄像头开始预览，但是并未开始识别
        mZXingView.startSpotAndShowRect(); // 显示扫描框，并且延迟0.1秒后开始识别
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA_PHONE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                onPermissionsGranted();
            } else {
                showToast("相机权限被拒绝");
            }
        }else if (requestCode == MY_PERMISSIONS_REQUEST_CAMERA_PHONE){
            for (int i = 0; i < grantResults.length; i++) {
                if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                    //判断是否勾选禁止后不再询问
                    boolean showRequestPermission = ActivityCompat.shouldShowRequestPermissionRationale(QRScanActivity.this, permissions[i]);
                    if (showRequestPermission) {
                         showToast("相机权限被拒绝");
                    }
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private boolean requestCodeQRCodePermissions() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},  MY_PERMISSIONS_REQUEST_CAMERA_PHONE);
                return false;
            }
        }
        return true;
    }

    private void showToast(String msg){
        Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
    }
}