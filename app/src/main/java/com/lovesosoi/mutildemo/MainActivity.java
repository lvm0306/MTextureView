package com.lovesosoi.mutildemo;

import android.Manifest;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.media.ImageReader;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.lovesosoi.mtextureview.CameraTextureView;

import java.io.File;
import java.nio.ByteBuffer;


public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    private CameraTextureView mCameraTextureView;
    private Button btnPaizhao;
    private ImageView iv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mCameraTextureView = findViewById(R.id.ctv);
        btnPaizhao = findViewById(R.id.paizhao);
        btnPaizhao.setOnClickListener(this);
        iv = findViewById(R.id.iv);
        File mFile = new File(getExternalFilesDir(null), "pic.jpg");

        //To configure CameraTexture
        mCameraTextureView.setActivity(this); //Must be written to pass the necessary parameters
        mCameraTextureView.setPicSaveFile(mFile);//Setting the save address of the picture picture
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestCameraPermission();
            return;
        }
        mCameraTextureView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mCameraTextureView.onPause();
    }


    private void requestCameraPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
//            ConfirmationDialog();
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA},
                    1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length != 1 || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "permisson", Toast.LENGTH_SHORT).show();
            } else {
                //Executing the camera initialization operation
                mCameraTextureView.onResume();
            }
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.paizhao: {
                mCameraTextureView.takePicture(new CameraTextureView.TackPhotoCallback() {
                    @Override
                    public void tackPhotoSuccess(String photoPath) {
//                    showToast(photoPath);
                    }

                    @Override
                    public void takebyBitmap(ImageReader reader) {
                        Image image = reader.acquireNextImage();
                        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
                        byte[] bytes = new byte[buffer.remaining()];
                        buffer.get(bytes);//Deposit a byte array from a buffer
                        //todo This can be converted to Bitmap
                        final Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        if (bitmap != null) {
                            MainActivity.this.runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    iv.setImageBitmap(bitmap);
                                }
                            });
                            image.close();
                        }
                    }


                    @Override
                    public void tackPhotoError(Exception e) {
//                        showToast(e.getMessage());
                    }
                });
                break;
            }
        }
    }
}