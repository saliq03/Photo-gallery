package com.csit.photoeditor;

import static android.Manifest.permission.READ_EXTERNAL_STORAGE;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static android.os.Environment.MEDIA_MOUNTED;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 100;
    RecyclerView recyclerView;
    ArrayList<String> images;
    CustomAdapter adapter;
    TextView totalimages;

    FloatingActionButton cam;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        recyclerView=findViewById(R.id.recyclerview);
        images=new ArrayList<>();
        recyclerView.setLayoutManager(new GridLayoutManager(this,3));


       totalimages=findViewById(R.id.textView4);
        // after permission granted;
        adapter=new CustomAdapter(images,this,this);
        recyclerView.setAdapter(adapter);
        checkpermission();


                 //OPENING CAMERA
        cam=findViewById(R.id.floatingActionButton);
        cam.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(checkcamerahardware(MainActivity.this)){
                    Intent intentcamera=new Intent(MainActivity.this, CameraActivity.class);
                    startActivity(intentcamera);
                }
                else{
                    Toast.makeText(MainActivity.this, "THIS BUTTON IS IN PROGRESS\nIT WILL WORK SOON", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    public void checkpermission(){
        int result= ContextCompat.checkSelfPermission(getApplicationContext(),READ_EXTERNAL_STORAGE);
        if(result== PackageManager.PERMISSION_GRANTED){
            LoadImages();

        }
        else {
            ActivityCompat.requestPermissions(this,new String[]{READ_EXTERNAL_STORAGE},1);
        }
        if (ContextCompat.checkSelfPermission(this, WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{WRITE_EXTERNAL_STORAGE}, 1);
        }
    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 1) {
            if (grantResults.length > 0) {
                boolean accepted = grantResults[0] == PackageManager.PERMISSION_GRANTED;
                if (accepted) {
                    LoadImages();
                } else {
                    Toast.makeText(this, "You have denied the storage permissions.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
    public void LoadImages(){
        Boolean sdcard= Environment.getExternalStorageState().equals(MEDIA_MOUNTED);
        if(sdcard){

            // these are the arguments of the cursor
            final String[] coloumn={MediaStore.Images.Media.DATA, MediaStore.Images.Media._ID};
            final String order=MediaStore.Images.Media.DATE_TAKEN + " DESC";

            Cursor cursor=getContentResolver().query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,coloumn,null,null,order);
           int totalimg=cursor.getCount();
             totalimages.setText("Total item: "+totalimg);
           for (int i=0;i<totalimg;i++){
               cursor.moveToPosition(i);
               int coloumnindex=cursor.getColumnIndex(MediaStore.Images.Media.DATA);
               images.add(cursor.getString(coloumnindex));
           }
           recyclerView.getAdapter().notifyDataSetChanged();
           cursor.close();
        }
    }

                // METHODS FOR CAMERA
    private  boolean checkcamerahardware(Context context){
        if(context.getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            return true;
        }
        return false;
    }
}

