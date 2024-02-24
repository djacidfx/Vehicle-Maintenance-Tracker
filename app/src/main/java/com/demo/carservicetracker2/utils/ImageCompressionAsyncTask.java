package com.demo.carservicetracker2.utils;



import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.ImageDecoder;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.util.Log;

import net.lingala.zip4j.util.InternalZipConstants;

import java.io.FileOutputStream;
import java.io.IOException;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.schedulers.Schedulers;

public class ImageCompressionAsyncTask  {
       String TAG = "ImageCompressionAsyncTask";

    private Context mContext = null;
    private Uri mImagePath = null;
    private ImageListener mImageListener = null;
    private final CompositeDisposable mDisposables = new CompositeDisposable();

    public ImageCompressionAsyncTask(Context context, Uri imagePath, ImageListener imageListener) {
        mContext = context;
        mImagePath = imagePath;
        mImageListener = imageListener;
        doInBackground();
    }

    private void doInBackground() {
        try {
            Bitmap bitmap = null;
            ContentResolver contentResolver = mContext.getContentResolver();
            try {
                if(Build.VERSION.SDK_INT < 28) {
                    bitmap = MediaStore.Images.Media.getBitmap(contentResolver, mImagePath);
                } else {
                    ImageDecoder.Source source = ImageDecoder.createSource(contentResolver, mImagePath);
                    bitmap = ImageDecoder.decodeBitmap(source);
                }
            } catch (Exception e) {
                e.printStackTrace();
                e.printStackTrace();
                Log.e("MYTAG", "ErrorNo: doInBackground:" +e);
            }



            String outputFile  = getFilename();


            FileOutputStream outputStream = new FileOutputStream(AppConstants.getMediaDir(mContext) + InternalZipConstants.ZIP_FILE_SEPARATOR + outputFile);
            Observable.just(bitmap)
                    .subscribeOn(Schedulers.computation())
                    .map(bmp -> {
                        int width = bmp.getWidth();
                        int height = bmp.getHeight();
                        if (width > 2048 || height > 2048) {
                            float aspectRatio = (float) width / (float) height;
                            if (aspectRatio > 1) {
                                width = 2048;
                                height = (int) (width / aspectRatio);
                            } else {
                                height = 2048;
                                width = (int) (height * aspectRatio);
                            }
                            bmp = Bitmap.createScaledBitmap(bmp, width, height, true);
                        }

                        bmp.compress(Bitmap.CompressFormat.JPEG, 80, outputStream);
                        return outputFile;
                    })
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe(file -> {
                        Log.e(TAG, "Image compressed successfully: " + file);
                        mImageListener.onImageReady(file);
                    }, error -> {
                        Log.e(TAG, "Error compressing image", error);
                        mImageListener.onImageError(error);
                    });
            mDisposables.dispose();

        } catch (IOException e) {
            Log.e(TAG, "Error creating temp file", e);
            mDisposables.dispose();
        }
    }

    public String getFilename() {
        return "IMG_" + System.currentTimeMillis() + ".jpg";
    }

}






























































































































































































































































































































































































































































































