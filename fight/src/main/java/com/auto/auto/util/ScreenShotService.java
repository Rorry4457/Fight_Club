package com.auto.auto.util;

/**
 * 截屏服务
 * <p>
 * Created by Rorry on 2017/10/17.
 */

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.PixelFormat;
import android.hardware.display.DisplayManager;
import android.hardware.display.VirtualDisplay;
import android.media.Image;
import android.media.ImageReader;
import android.media.projection.MediaProjection;
import android.media.projection.MediaProjectionManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.os.AsyncTaskCompat;
import android.util.DisplayMetrics;
import android.view.WindowManager;

import com.auto.auto.Operation;
import com.newland.support.nllogger.LogUtils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

@TargetApi(Build.VERSION_CODES.LOLLIPOP)
public class ScreenShotService extends Service {

    private MediaProjection mMediaProjection;
    private VirtualDisplay mVirtualDisplay;

    private static Intent mResultData = null;

    private ImageReader mImageReader;

    private int mScreenWidth;
    private int mScreenHeight;
    private int mScreenDensity;


    @Override
    public void onCreate() {
        super.onCreate();

        prepareParameter();

        createImageReader();

        startScreenShot();
    }

    public static Intent getResultData() {
        return mResultData;
    }

    public static void setResultData(Intent mResultData) {
        ScreenShotService.mResultData = mResultData;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void prepareParameter() {
        WindowManager mWindowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);

        DisplayMetrics metrics = new DisplayMetrics();
        mWindowManager.getDefaultDisplay().getMetrics(metrics);
        mScreenDensity = metrics.densityDpi;
        mScreenWidth = metrics.widthPixels;
        mScreenHeight = metrics.heightPixels;
    }


    private void startScreenShot() {

        Handler handler1 = new Handler();
        handler1.postDelayed(new Runnable() {
            public void run() {
                //start virtual
                startVirtual();
            }
        }, 2000);

        handler1.postDelayed(new Runnable() {
            public void run() {
                //capture the screen
                startCapture();

            }
        }, 2300);
    }


    private void createImageReader() {

        mImageReader = ImageReader.newInstance(mScreenWidth, mScreenHeight, PixelFormat.RGBA_8888, 1);

    }

    public void startVirtual() {

        LogUtils.d(" $$$ 获取屏幕内容");
        if (mMediaProjection != null) {
            virtualDisplay();
        } else {
            setUpMediaProjection();
            virtualDisplay();
        }
    }

    public void setUpMediaProjection() {
        if (mResultData == null) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            startActivity(intent);
        } else {
            mMediaProjection = getMediaProjectionManager().getMediaProjection(Activity.RESULT_OK, mResultData);
        }
    }

    private MediaProjectionManager getMediaProjectionManager() {

        return (MediaProjectionManager) getSystemService(Context.MEDIA_PROJECTION_SERVICE);
    }

    private void virtualDisplay() {
        mVirtualDisplay = mMediaProjection.createVirtualDisplay("screen-mirror",
                mScreenWidth,
                mScreenHeight,
                mScreenDensity,
                DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR,
                mImageReader.getSurface(),
                null,
                null);
    }

    private void startCapture() {

        LogUtils.d("$$$ 存储截屏图片");
        Image image = mImageReader.acquireLatestImage();

        if (image == null) {
            startScreenShot();
        } else {
            SaveTask mSaveTask = new SaveTask();
            AsyncTaskCompat.executeParallel(mSaveTask, image);
        }
    }


    private class SaveTask extends AsyncTask<Image, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(Image... params) {

            if (params == null || params.length < 1 || params[0] == null) {

                return null;
            }

            Image image = params[0];

            int width = image.getWidth();
            int height = image.getHeight();
            final Image.Plane[] planes = image.getPlanes();
            final ByteBuffer buffer = planes[0].getBuffer();
            //每个像素的间距
            int pixelStride = planes[0].getPixelStride();
            //总的间距
            int rowStride = planes[0].getRowStride();
            int rowPadding = rowStride - pixelStride * width;
            Bitmap bitmap = Bitmap.createBitmap(width + rowPadding / pixelStride, height, Bitmap.Config.ARGB_8888);
            bitmap.copyPixelsFromBuffer(buffer);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height);
            image.close();
            File fileImage = null;
            if (bitmap != null) {
                try {
                    fileImage = new File(FileUtil.getScreenShotsName(getApplicationContext()));
                    if (!fileImage.exists()) {
                        fileImage.createNewFile();
                    }
                    FileOutputStream out = new FileOutputStream(fileImage);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
                    out.flush();
                    out.close();

                } catch (IOException e) {
                    e.printStackTrace();
                    fileImage = null;
                }
            }

            if (fileImage != null) {
                return bitmap;
            }
            return null;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            super.onPostExecute(bitmap);
            //发送图片
            if (bitmap != null) {

                LogUtils.d("$$$ 完成截屏");

                Operation.backToHome(ScreenShotService.this);
                Operation.sendEmailWithAttachment(ScreenShotService.this);
                stopSelf();

            }
        }
    }


    private void tearDownMediaProjection() {
        if (mMediaProjection != null) {
            mMediaProjection.stop();
            mMediaProjection = null;
        }
    }

    private void stopVirtual() {
        if (mVirtualDisplay == null) {
            return;
        }
        mVirtualDisplay.release();
        mVirtualDisplay = null;
    }

    @Override
    public void onDestroy() {

        stopVirtual();
        tearDownMediaProjection();
        super.onDestroy();

        LogUtils.d("$$$ 截屏服务已停止");
    }
}

