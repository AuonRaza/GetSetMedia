package com.example.getsetmedia.view;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.ActivityCompat.OnRequestPermissionsResultCallback;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.getsetmedia.R;
import com.example.getsetmedia.core.PermissionManager;
import com.example.getsetmedia.core.StorageManager;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {
    private Button btnImportImage, btnCaptureImage, btnRecordAudio, btnRecordVideo;

    //  2.2. define request codes for your permissions to uniquely identify them
    private final int REQUEST_CODE_ALL_PERMISSIONS = 100;
    private final int REQUEST_CAMERA = 110;
    private final int REQUEST_VIDEO = 120;
    private final int REQUEST_AUDIO = 130;
    private String cameraPerm = Manifest.permission.CAMERA;
    private String xStoragePerm = Manifest.permission.WRITE_EXTERNAL_STORAGE;
    private String recordPerm = Manifest.permission.RECORD_AUDIO;

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // 2.4. Now I will set the flags according to state of results for each permission
        switch(requestCode){
//            case REQUEST_CODE_CAMERA:
//                permCamera = true;
//            case REQUEST_CODE_WRITE_XSTORAGE:
//                permWXStorage = true;
//            case REQUEST_CODE_RECORD_AUDIO:
//                permRecAudio = true;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        PermissionManager permissionManager = new PermissionManager(this);
        StorageManager storageManager = new StorageManager();

        // 1. Bind the buttons with their IDs
        btnImportImage = findViewById(R.id.importImage);
        btnCaptureImage = findViewById(R.id.captureImage);
        btnRecordAudio = findViewById(R.id.recordAudio);
        btnRecordVideo = findViewById(R.id.recordVideo);

        List<String> permissionList = new ArrayList<>();

        if (!hasPermission(cameraPerm)){
            permissionList.add(cameraPerm);
        }
        if (!hasPermission(recordPerm)){
            permissionList.add(recordPerm);
        }
        if (!hasPermission(xStoragePerm)){
            permissionList.add(xStoragePerm);
        }
        if (!permissionList.isEmpty()){
            String[] permissions = permissionList.toArray(new String[permissionList.size()]);
            ActivityCompat.requestPermissions(this,permissions,REQUEST_CODE_ALL_PERMISSIONS);
        }

        btnCaptureImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                permissionManager.checkPermission(MainActivity.this, cameraPerm, new PermissionManager.PermissionAskListener() {
                    @Override
                    public void onNeedPermission() {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA);
                    }

                    @Override
                    public void onPermissionPreviouslyDenied() {
                        showRational(REQUEST_CAMERA);
                    }

                    @Override
                    public void onPermissionPreviouslyDeniedWithNeverAskAgain() {
                        dialogForSettings("Permission Denied", "Now you must allow camera access from settings.");
                    }

                    @Override
                    public void onPermissionGranted() {
                        openCamera();
                    }
                });
            }
        });

        btnImportImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        btnRecordVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });
    }

    private void openCamera(){
//        Intent iCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        Uri imagePath = new StorageManager().storeImage(this);
//        iCamera.putExtra(MediaStore.EXTRA_OUTPUT,imagePath);
//        clickPictureLauncher.launch(iCamera);
        Toast.makeText(this, "Permission for Camera Granted!", Toast.LENGTH_LONG).show();

    }

    private boolean hasPermission(String permission){
        return ContextCompat.checkSelfPermission(getApplicationContext(),permission) == PackageManager.PERMISSION_GRANTED;
    }

    private void showRational(int type){
        new AlertDialog.Builder(this).setTitle("Permission Denied").setMessage("Without this permission this app is unable to open camera to take your photo. Are you sure you want to deny this permission.")
                .setCancelable(false)
                .setNegativeButton("I'M SURE", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{android.Manifest.permission.CAMERA}, REQUEST_CAMERA);
                        dialog.dismiss();
                    }
                }).show();
    }

    private void dialogForSettings(String title, String msg) {
        new AlertDialog.Builder(this).setTitle(title).setMessage(msg)
                .setCancelable(false)
                .setNegativeButton("NOT NOW", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .setPositiveButton("SETTINGS", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        goToSettings();
                        dialog.dismiss();
                    }
                }).show();
    }

    private void goToSettings() {
        Intent intent = new Intent();
        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.parse("package:" + getPackageName());
        intent.setData(uri);
        startActivity(intent);
    }
}