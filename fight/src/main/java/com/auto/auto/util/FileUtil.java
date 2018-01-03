package com.auto.auto.util;

import android.content.Context;
import android.os.Environment;

import com.newland.support.nllogger.LogUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * 文件工具类
 * <p>
 * Created by Rorry on 2017/10/17.
 */

public class FileUtil {

    //系统保存截图的路径
    private static final String SCREENCAPTURE_PATH = "ScreenCapture" + File.separator + "Screenshots" + File.separator;
//  public static final String SCREENCAPTURE_PATH = "ZAKER" + File.separator + "Screenshots" + File.separator;

    private static final String SCREENSHOT_NAME = "Screenshot";

    private static String getAppPath(Context context) {

        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {


            return Environment.getExternalStorageDirectory().toString();

        } else {

            return context.getFilesDir().toString();
        }

    }

    public static void cleanScreenCapture(Context context) {

        File file = new File(getScreenShotsPath(context));
        long fileSize = getFileSize(file);

        if (fileSize > 0) {

            if (file.isDirectory()) {
                File[] files = file.listFiles();
                for (File f : files) {
                    LogUtils.d("$$$ 图片名称 " + f.getName() + " deleted status " + f.delete());
                }
            }
        } else {
            LogUtils.d("$$$ 未超出容量限制");
        }
    }

    private static long getFileSize(File file) {

        if (!file.exists()) {
            return 0;
        }

        long size = 0;
        File[] files = file.listFiles();

        if (files == null) {
            size += file.length();
        } else {
            for (File f : files) {
                size += getFileSize(f);
            }
        }

        return size;
    }

    private static String getScreenShotsPath(Context context) {

        StringBuilder stringBuffer = new StringBuilder(getAppPath(context));
        stringBuffer.append(File.separator);

        stringBuffer.append(SCREENCAPTURE_PATH);

        File file = new File(stringBuffer.toString());

        if (!file.exists()) {
            file.mkdirs();
        }

        return stringBuffer.toString();

    }

    public static String getScreenShotsName(Context context) {

        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());

        String date = simpleDateFormat.format(new Date());

        return getScreenShotsPath(context) + SCREENSHOT_NAME + "_" + date + ".png";
    }

    public static String getLogPath() {
        return new File(Environment.getExternalStorageDirectory(), "loggers").getPath();
    }
}