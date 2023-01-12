package com.fooding.images_test;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.squareup.picasso.Picasso;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {
    String imgUrl = "https://tesla-cdn.thron.com/delivery/public/image/tesla/d75beae1-60b5-4bdd-b08a-ddb865f01ed8/bvlatuR/std/2878x1800/roadster-hero-desktop?quality=auto-medium&format=auto";
    ImageView img;
    ImageView img2;
    ProgressBar progressBar;

    String photoFilePath;
    Uri photoURI;

    ImageView imageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        img = findViewById(R.id.img_id);
        img2 = findViewById(R.id.img_id2);
        progressBar = findViewById(R.id.progressBar_id);

        loadImgUrl();

    }

    private void loadImgUrl() {
        Picasso.get()
                .load(imgUrl)
                .into(img, new com.squareup.picasso.Callback() {
                    @Override
                    public void onSuccess() {
                        progressBar.setVisibility(View.INVISIBLE);
                    }

                    @Override
                    public void onError(Exception e) {
                        Log.d("ERRORE", "onError: " + e);
                        progressBar.setVisibility(View.INVISIBLE);
                    }
                });
    }

    private File createImageFile() throws IOException {
        @SuppressLint("SimpleDateFormat")
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "-";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(
                imageFileName,
                ".jpg",
                storageDir
        );

        photoFilePath = image.getAbsolutePath();
        return image;
    }

    @SuppressLint("QueryPermissionNeeded")
    private void takePicture() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if(takePictureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException e) {
                Log.d("ERRORE", "takePicture: " + e);
            }
            if (photoFile != null) {
                photoURI = FileProvider.getUriForFile(Objects.requireNonNull(getApplicationContext()),
                        BuildConfig.APPLICATION_ID + ".provider", photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);

                activityResultLauncher.launch(takePictureIntent);
            }
        }
    }

    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            result -> {
                imageView.setImageURI(photoURI);
                //uploadPhoto();
            }
    );
}