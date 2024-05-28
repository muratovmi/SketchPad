package com.itschoolsamsung.sketchpad.util;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.net.Uri;
import android.provider.MediaStore;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;

public class SavePhotoUtil {
    private static String sLastImageSavedName;
    private static Uri sLastImageUri;

    public SavePhotoUtil(Context mContext) {
    }

    public String saveToGallery(ContentResolver resolver, Bitmap bitmap, String title, String description) {
        Uri uri = null;
        String stringUri = null;
        if (bitmap != null) {
            if (sLastImageSavedName == null || !sLastImageSavedName.equalsIgnoreCase(title)) {
                ContentValues values = new ContentValues();
                values.put(MediaStore.Images.Media.TITLE, title);
                values.put(MediaStore.Images.Media.DISPLAY_NAME, title);
                values.put(MediaStore.Images.Media.DESCRIPTION, description);
                values.put(MediaStore.Images.Media.MIME_TYPE, "images/jpeg");
                values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
                values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis());
                values.put(MediaStore.Images.Media.ORIENTATION, 0);
                values.put(MediaStore.Images.Media.HEIGHT, bitmap.getHeight());
                values.put(MediaStore.Images.Media.WIDTH, bitmap.getWidth());
                uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
                sLastImageUri = uri;
                sLastImageSavedName = title;
            } else {
                uri = sLastImageUri;
            }
            OutputStream out = null;
            try {
                assert uri != null;
                out = resolver.openOutputStream(uri);
                assert out != null;
                bitmap.compress(Bitmap.CompressFormat.PNG, 50, out);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } finally {
                if (out != null)
                    try {
                        out.flush();
                        out.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
            }
            long id = ContentUris.parseId(uri);
            Bitmap miniThumb = MediaStore.Images.Thumbnails.getThumbnail(resolver, id, MediaStore.Images.Thumbnails.MINI_KIND, null);
            storeThumbnail(resolver, miniThumb, id);
        }
        if (uri != null) {
            stringUri = uri.toString();
        }
        return stringUri;
    }

    private static void storeThumbnail(
            ContentResolver cr,
            Bitmap source,
            long id) {
        Matrix matrix = new Matrix();
        float scaleX = (float) 50.0 / source.getWidth();
        float scaleY = (float) 50.0 / source.getHeight();
        matrix.setScale(scaleX, scaleY);
        // createScaledBitmap.
        Bitmap thumb = Bitmap.createBitmap(source, 0, 0,
                source.getWidth(),
                source.getHeight(), matrix,
                true
        );
        ContentValues values = new ContentValues(4);
        values.put(MediaStore.Images.Thumbnails.KIND, MediaStore.Images.Thumbnails.MICRO_KIND);
        values.put(MediaStore.Images.Thumbnails.IMAGE_ID, (int) id);
        values.put(MediaStore.Images.Thumbnails.HEIGHT, thumb.getHeight());
        values.put(MediaStore.Images.Thumbnails.WIDTH, thumb.getWidth());
        Uri url = cr.insert(MediaStore.Images.Thumbnails.EXTERNAL_CONTENT_URI, values);
        try {
            OutputStream thumbOut = cr.openOutputStream(url);
            thumb.compress(Bitmap.CompressFormat.PNG, 100, thumbOut);
            thumbOut.close();
        } catch (IOException ignored) {
        }
    }
}