package com.csit.photoeditor;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.jsibbold.zoomage.ZoomageView;

import java.io.File;

public class viewImage extends AppCompatActivity {

    ZoomageView image;
    String image_file;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image);

        image=findViewById(R.id.image);

        image_file=getIntent().getStringExtra("imageFile");
        File img=new File(image_file);
        if (img.exists()){
            Glide.with(this).load( img).into(image);
        }
    }
}