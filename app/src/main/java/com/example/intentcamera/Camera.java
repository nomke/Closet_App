package com.example.intentcamera;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class Camera extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private ImageView imageView;
    private File imageFile;

    private LinearLayout imageLayout;
    private Button displayButton;

    private String Folder_name;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.camera);

        imageView = findViewById(R.id.imageView);
        Button captureButton = findViewById(R.id.T_shirt_Button);

        captureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Folder_name = "T_shirt";
                if (ContextCompat.checkSelfPermission(Camera.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Camera.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                } else {
                    dispatchTakePictureIntent();
                }
            }
        });

        Button pantsButton = findViewById(R.id.pants_button);

        pantsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Folder_name = "pants";
                if (ContextCompat.checkSelfPermission(Camera.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Camera.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                } else {
                    dispatchTakePictureIntent();
                }
            }
        });

        Button outerButton = findViewById(R.id.outer_button);

        outerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Folder_name = "outer";
                if (ContextCompat.checkSelfPermission(Camera.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Camera.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                } else {
                    dispatchTakePictureIntent();
                }
            }
        });

        Button shoesButton = findViewById(R.id.shoes_button);

        shoesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Folder_name = "shoes";
                if (ContextCompat.checkSelfPermission(Camera.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(Camera.this, new String[]{Manifest.permission.CAMERA}, REQUEST_CAMERA_PERMISSION);
                } else {
                    dispatchTakePictureIntent();
                }
            }
        });
    }

    private void displayImages() {
        List<String> imagePaths = getAllImagePaths();
        imageLayout.removeAllViews();

        for (String imagePath : imagePaths) {
            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 0, 0, 16);
            imageView.setLayoutParams(layoutParams);

            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            imageView.setImageBitmap(bitmap);

            imageLayout.addView(imageView);
        }
    }

    private List<String> getAllImagePaths() {
        List<String> imagePaths = new ArrayList<>();
        File directory = new File(getFilesDir(), Folder_name);
        if (!directory.exists()) {
            directory.mkdirs();
        }
        getAllImagePathsRecursive(directory, imagePaths);

        return imagePaths;
    }

    private void getAllImagePathsRecursive(File directory, List<String> imagePaths) {
        File[] files = directory.listFiles();
        if (files != null) {
            Arrays.sort(files);
            for (File file : files) {
                if (file.isDirectory()) {
                    getAllImagePathsRecursive(file, imagePaths);
                } else {
                    if (isImageFile(file.getPath())) {
                        imagePaths.add(file.getPath());
                    }
                }
            }
        }
    }

    private boolean isImageFile(String filePath) {
        String extension = filePath.substring(filePath.lastIndexOf(".") + 1);
        String[] imageExtensions = {"jpg"};
        for (String ext : imageExtensions) {
            if (ext.equalsIgnoreCase(extension)) {
                return true;
            }
        }
        return false;
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageView.setImageBitmap(imageBitmap);

            saveImageToInternalStorage(imageBitmap);
        }
    }

    private void saveImageToInternalStorage(Bitmap imageBitmap) {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        String imageFileName = "IMG_" + timeStamp + ".jpg";

        try {
            // アプリ内のディレクトリを作成
            File directory = new File(getFilesDir(), Folder_name);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // ファイルのパスを作成
            File imageFile = new File(directory, imageFileName);

            FileOutputStream outputStream = new FileOutputStream(imageFile);
            imageBitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
            outputStream.close();

            Toast.makeText(this, "Image saved successfully", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CAMERA_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                dispatchTakePictureIntent();
            } else {
                Toast.makeText(this, "Camera permission required", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
