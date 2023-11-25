package com.example.onlinelaundry.Utils;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.Base64;
import android.widget.ImageView;

import java.io.ByteArrayOutputStream;


public class ImageConverter {

    public static String ImageToString(ImageView imageView) {
        Drawable background = imageView.getBackground();

        if (background instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) background).getBitmap();
            String format;
            if (background instanceof BitmapDrawable) {
                format = "png";
            } else {
                format = "jpeg";
            }

            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            if (format.equals("png")) {
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
            } else if (format.equals("jpeg")) {
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            }

            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } else {
            return null;
        }
    }

    public static Bitmap StringToImage(String base64String) {
        byte[] decodedBytes = Base64.decode(base64String, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    public static String encodeImageToBase64JPEG(ImageView imageView) {
        Drawable background = imageView.getBackground();

        if (background instanceof BitmapDrawable) {
            Bitmap bitmap = ((BitmapDrawable) background).getBitmap();
            ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, byteArrayOutputStream);
            byte[] byteArray = byteArrayOutputStream.toByteArray();
            return Base64.encodeToString(byteArray, Base64.DEFAULT);
        } else {
            return null;
        }
    }
}

