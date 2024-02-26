
package com.csit.photoeditor;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.DrawableRes;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.camera.core.AspectRatio;
import androidx.camera.core.Camera;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;

public class CameraActivity  extends AppCompatActivity {
    ImageButton capture, flipCamera, flash;
    PreviewView previewView;
    private int camerafacing = CameraSelector.LENS_FACING_BACK;
    private final ActivityResultLauncher<String> activityResultLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), new ActivityResultCallback<Boolean>() {
        @Override
        public void onActivityResult(Boolean result) {
            if (result) {
                StartCamera(camerafacing);
            }

        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera2);
        capture = findViewById(R.id.capture);
        flipCamera = findViewById(R.id.flipcamera);
        flash = findViewById(R.id.flash);
        previewView =findViewById(R.id.mycamera);
        if (ContextCompat.checkSelfPermission(CameraActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            activityResultLauncher.launch(Manifest.permission.CAMERA);
        } else {
            StartCamera(camerafacing);
        }

        flipCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (camerafacing == CameraSelector.LENS_FACING_BACK) {
                    camerafacing = CameraSelector.LENS_FACING_FRONT;
                } else {
                    camerafacing = CameraSelector.LENS_FACING_BACK;
                }
                StartCamera(camerafacing);
            }
        });

    }


    public void StartCamera(int camerafacing) {
        int aspectRatio = aspectRatio(previewView.getWidth(), previewView.getHeight());
        ListenableFuture<ProcessCameraProvider> listenableFuture = ProcessCameraProvider.getInstance(this);
        listenableFuture.addListener(()->{
            try {
                ProcessCameraProvider CameraProvider = (ProcessCameraProvider) listenableFuture.get();
                Preview preview = new Preview.Builder().setTargetAspectRatio(aspectRatio).build();

                ImageCapture imageCapture=new ImageCapture.Builder().setCaptureMode(ImageCapture.CAPTURE_MODE_MINIMIZE_LATENCY)
                        .setTargetRotation(getWindowManager().getDefaultDisplay().getRotation()).build();

                CameraSelector cameraSelector=new CameraSelector.Builder().requireLensFacing(camerafacing).build();

                CameraProvider.unbindAll();

                Camera camera =CameraProvider.bindToLifecycle(this,cameraSelector,preview,imageCapture);

                capture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(ContextCompat.checkSelfPermission(CameraActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)!=PackageManager.PERMISSION_GRANTED){
                            activityResultLauncher.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE);
                        }else {
                            takePicture(imageCapture);
                        }
                    }
                });

                flash.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        setFlash(camera);
                    }
                });
                preview.setSurfaceProvider(previewView.getSurfaceProvider());
            }catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }
        },ContextCompat.getMainExecutor(this));
    }

    public int aspectRatio(int width,int height){
        double previewRatio=(double)Math.max(width,height)/Math.min(width,height);
        if(Math.abs(previewRatio-4.0/3.0)<=Math.abs(previewRatio-16.0/9.0)){
            return AspectRatio.RATIO_4_3;
        }
        return AspectRatio.RATIO_16_9;
    }

    private void setFlash(Camera camera){
        if(camera.getCameraInfo().hasFlashUnit()){
            if(camera.getCameraInfo().getTorchState().getValue()==0){
                camera.getCameraControl().enableTorch(true);
                flash.setImageResource(R.drawable.flash_off_);
            }
            else{
                camera.getCameraControl().enableTorch(false);
                flash.setImageResource(R.drawable.lash_on);
            }
        }
        else {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(CameraActivity.this, "Flash is Unavailable currently", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    public void takePicture(ImageCapture imageCapture){
        final File file=new File(getExternalFilesDir(null),System.currentTimeMillis()+".jpg");
        ImageCapture.OutputFileOptions outputFileOptions=new ImageCapture.OutputFileOptions.Builder(file).build();
        imageCapture.takePicture(outputFileOptions, Executors.newCachedThreadPool(), new ImageCapture.OnImageSavedCallback() {
            @Override
            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CameraActivity.this, "image saved at "+file.getPath(), Toast.LENGTH_SHORT).show();
                    }
                });
                StartCamera(camerafacing);
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CameraActivity.this, "image not saved", Toast.LENGTH_SHORT).show();
                    }
                });
                StartCamera(camerafacing);
            }
        });
    }
}