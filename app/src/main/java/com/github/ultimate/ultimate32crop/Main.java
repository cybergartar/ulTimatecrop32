package com.github.ultimate.ultimate32crop;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.github.ultimate.ultimate32crop.R;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.channels.FileChannel;
import java.util.Calendar;

public class Main extends AppCompatActivity{
    Button pickImageBtn;
    static final int PICK_IMAGE = 1;
    Context mContext = this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_interface);

        pickImageBtn = (Button) findViewById(R.id.pickImageBtn);

        pickImageBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage();
            }
        });

        if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE}, 101);
        }

        if (ContextCompat.checkSelfPermission(mContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, 101);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == PICK_IMAGE && data != null && data.getData() != null) {
                Uri uri = data.getData();
                startCropActivity(uri);
            }

            else if (requestCode == UCrop.REQUEST_CROP ) {
                try {
                    saveToDownloads(UCrop.getOutput(data));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        else {
            handleCropError(data);
        }

    }

    private void pickImage() {
        Intent pickImageIntent = new Intent(Intent.ACTION_GET_CONTENT);
        pickImageIntent.setType("image/*");

        startActivityForResult(pickImageIntent, PICK_IMAGE);
    }

    private void startCropActivity (@NonNull Uri uri) {
        String uriString = uri.toString();
        String imgExtension = uriString.substring(uriString.lastIndexOf("%"));
        String destFile = imgExtension + ".jpg";

        UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(getCacheDir(), destFile))).withAspectRatio(3, 2);
        uCrop.start(Main.this);

    }

    private void shareImage (Uri imageUri) {
        String type = "image/*";
        Intent share = new Intent(Intent.ACTION_SEND);

        share.setType(type);
        share.putExtra(Intent.EXTRA_STREAM, imageUri);

        startActivity(Intent.createChooser(share, "Share to"));
    }

    private void saveToDownloads(Uri croppedFileUri) throws Exception {
        String downloadsDirectoryPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS).getAbsolutePath();
        String filename = String.format("%d_%s", Calendar.getInstance().getTimeInMillis(), croppedFileUri.getLastPathSegment());
        Log.i("DBG", downloadsDirectoryPath);

        File saveFile = new File(downloadsDirectoryPath, filename);

        FileInputStream inStream = new FileInputStream(new File(croppedFileUri.getPath()));
        FileOutputStream outStream = new FileOutputStream(saveFile);
        FileChannel inChannel = inStream.getChannel();
        FileChannel outChannel = outStream.getChannel();
        inChannel.transferTo(0, inChannel.size(), outChannel);
        inStream.close();
        outStream.close();

        Uri saveFileUri = FileProvider.getUriForFile(Main.this, BuildConfig.APPLICATION_ID + ".provider", saveFile);
        shareImage(saveFileUri);
    }

    @SuppressWarnings("ThrowableResultOfMethodCallIgnored")
    private void handleCropError(@NonNull Intent result) {
        final Throwable cropError = UCrop.getError(result);
        if (cropError != null) {
            Log.e("ERRRR", "handleCropError: ", cropError);
            Toast.makeText(Main.this, cropError.getMessage(), Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(Main.this, "NO EXPECT", Toast.LENGTH_SHORT).show();
        }
    }
}
