package com.csit.photoeditor;


import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;

import static androidx.core.content.PermissionChecker.PERMISSION_GRANTED;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

public class CustomAdapter extends RecyclerView.Adapter<CustomAdapter.ViewHolder> {

    private ArrayList<String> image_list;
    private Context context;
    private Activity myactivity;

    public CustomAdapter(ArrayList<String> image_list, Context context,Activity activity) {
        this.image_list = image_list;
        this.context = context;
        this.myactivity=activity;
    }

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        private final ImageView image;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View
            image=view.findViewById(R.id.itemid);
        }


    }




    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(context)
                .inflate(R.layout.imagelayout, viewGroup, false);

        return new ViewHolder(view);
    }



    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder,
                                 @SuppressLint("RecyclerView") final int position) {
    File imagefile=new File(image_list.get(position));
    if (imagefile.exists()){
        Glide.with(context).load(imagefile).into(viewHolder.image);
    }
    viewHolder.image.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent=new Intent(context, viewImage.class);
            intent.putExtra("imageFile",image_list.get(position));
            context.startActivity(intent);
        }
    });

    viewHolder.image.setOnLongClickListener(new View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            int check = ContextCompat.checkSelfPermission(context, WRITE_EXTERNAL_STORAGE);
            if(check==PERMISSION_GRANTED) {
            AlertDialog.Builder builder = new AlertDialog.Builder(context)
                    .setTitle("Delete Image")
                    .setMessage("Are you sure want to delete this image")
                    .setIcon(R.drawable.baseline_delete_24)
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    })
                    .setPositiveButton("yes", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            imagefile.delete();
                            notifyItemRemoved(position);
                        }
                    });
            builder.show();
        }
            else {
                ActivityCompat.requestPermissions(myactivity,
                        new String[]{WRITE_EXTERNAL_STORAGE},
                        1);
            }
            return true;
        }
    });
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return image_list.size();
    }
}

