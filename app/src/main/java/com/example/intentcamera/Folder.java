package com.example.intentcamera;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
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

public class Folder extends AppCompatActivity {

    private static final int REQUEST_CAMERA_PERMISSION = 200;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    private ImageView imageView;
    private File imageFile;

    private LinearLayout imageLayout;
    private Button displayButton;

    private String  Folder_name;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.folder);

        imageLayout = findViewById(R.id.photo_list);
        Button T_shirt_Button = findViewById(R.id.T_shirt_Button);
        T_shirt_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Folder_name = "T_shirt";
                displayImages();
            }
        });

        imageLayout = findViewById(R.id.photo_list);
        Button pants_Button = findViewById(R.id.pants_button);
        pants_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Folder_name = "pants";
                displayImages();
            }
        });

        imageLayout = findViewById(R.id.photo_list);
        Button outer_Button = findViewById(R.id.outer_button);
        outer_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Folder_name = "outer";
                displayImages();
            }
        });

        imageLayout = findViewById(R.id.photo_list);
        Button shoes_Button = findViewById(R.id.shoes_button);
        shoes_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Folder_name = "shoes";
                displayImages();
            }
        });
    }
    private void displayImages() {
        List<String> imagePaths = getAllImagePaths();
        LinearLayout imageLayout = findViewById(R.id.photo_list);
        imageLayout.removeAllViews();

        for (String imagePath : imagePaths) {
            ImageView imageView = new ImageView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
            );
            layoutParams.setMargins(0, 0, 0, 16);
            imageView.setLayoutParams(layoutParams);

            // 画像を読み込む際に適切なサイズで読み込むように設定
            BitmapFactory.Options options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(imagePath, options);
            int imageWidth = options.outWidth;
            int imageHeight = options.outHeight;

            // 画像の拡大縮小比率を計算
            int targetWidth = getResources().getDisplayMetrics().widthPixels;
            int targetHeight = (int) (targetWidth * ((float) imageHeight / imageWidth));

            // 画像を読み込む際にサイズを指定してデコード
            options.inJustDecodeBounds = false;
            options.inSampleSize = calculateInSampleSize(options, targetWidth, targetHeight);
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath, options);

            // アスペクト比を維持しながら拡大縮小した画像を設定
            imageView.setImageBitmap(scaleBitmap(bitmap, targetWidth, targetHeight));
            imageView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);

            imageLayout.addView(imageView);
        }
    }

    private Bitmap scaleBitmap(Bitmap bitmap, int targetWidth, int targetHeight) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        // 拡大縮小後の画像サイズを計算
        float scaleWidth = ((float) targetWidth) / width;
        float scaleHeight = ((float) targetHeight) / height;
        float scaleFactor = Math.min(scaleWidth, scaleHeight);

        // Matrix を使用して画像を拡大縮小
        Matrix matrix = new Matrix();
        matrix.postScale(scaleFactor, scaleFactor);

        // 拡大縮小後の画像を生成
        return Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);
    }



    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        final int imageWidth = options.outWidth;
        final int imageHeight = options.outHeight;
        int inSampleSize = 1;

        if (imageHeight > reqHeight || imageWidth > reqWidth) {
            final int halfHeight = imageHeight / 2;
            final int halfWidth = imageWidth / 2;

            while ((halfHeight / inSampleSize) >= reqHeight && (halfWidth / inSampleSize) >= reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
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
            File directory = new File(getFilesDir(), "T-shirt");
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
