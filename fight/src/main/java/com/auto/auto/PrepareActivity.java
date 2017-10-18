package com.auto.auto;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.projection.MediaProjectionManager;
import android.os.Bundle;

import com.auto.auto.util.ScreenShotService;
import com.newland.support.nllogger.LogUtils;

public class PrepareActivity extends Activity {

    public static final int REQUEST_MEDIA_PROJECTION = 18;
    private boolean takeScreenShot = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestCapturePermission();

        String from = getIntent().getStringExtra("FROM");

        if (from != null) {
            takeScreenShot = !from.equals("ResultActivity");
        }

        LogUtils.d("$$$ 打开截屏授权页");
    }

    public void requestCapturePermission() {

        MediaProjectionManager mediaProjectionManager = (MediaProjectionManager)
                getSystemService(Context.MEDIA_PROJECTION_SERVICE);
        startActivityForResult(
                mediaProjectionManager.createScreenCaptureIntent(),
                REQUEST_MEDIA_PROJECTION);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case REQUEST_MEDIA_PROJECTION:

                if (resultCode == RESULT_OK && data != null && takeScreenShot) {

                    LogUtils.d("$$$ 启动截屏服务");

                    ScreenShotService.setResultData(data);
                    startService(new Intent(getApplicationContext(), ScreenShotService.class));
                    finish();
                }
                break;
        }
    }
}
