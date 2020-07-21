package com.bajie.audio.utils;

import android.content.Context;
import android.graphics.Bitmap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * bajie on 2020/7/7 17:51
 */
public class FileUtils {

    public static String saveBitmapToDir(Context context,String fileName, Bitmap bitmap) throws IOException {
        String filePath = getStoragePath(context) + "/" + fileName + ".png";
        File file = new File(filePath);
        file.createNewFile();
        FileOutputStream out = new FileOutputStream(file);
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
        out.flush();
        out.close();
        return filePath;

    }

    public static String getStoragePath(Context context) {
        return context.getExternalFilesDir(null).getAbsolutePath();
    }
}
