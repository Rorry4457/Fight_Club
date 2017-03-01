package com.auto.auto.receive;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.PowerManager;

import com.auto.auto.Account;
import com.auto.auto.Constant;
import com.auto.auto.Util.HttpUtil;
import com.newland.support.nllogger.LogUtils;


import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static com.auto.auto.Account.getAccountInfo;

/**
 * Created by x on 2016/11/2.
 */
public class BootReceiver extends BroadcastReceiver {
    public static String TAG = BootReceiver.class.getSimpleName();
    private static int MAX_DELAY = 10 * 60 * 1000;
    private static int MIN_DELAY = 3 * 60 * 1000;

//    private static int MAX_DELAY = 10 * 1000;
//    private static int MIN_DELAY = 5 * 1000;

    @Override
    public void onReceive(Context context, Intent intent) {
        // 打卡时间已到  唤醒屏幕

        LogUtils.d("$$$ 收到开机广播");
        Account.setIsCheckInToday(false, context);
        wakeUpAndUnlock(context);
    }

    private static void wakeUpAndUnlock(final Context context) {
        KeyguardManager km = (KeyguardManager) context.getSystemService(Context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock kl = km.newKeyguardLock("unLock");
        //解锁
        kl.disableKeyguard();
        //获取电源管理器对象
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
        //点亮屏幕
        wl.acquire();
        //释放
        wl.release();

        KeyguardManager keyguardManager = (KeyguardManager) context.getSystemService(context.KEYGUARD_SERVICE);
        KeyguardManager.KeyguardLock keyguardLock = keyguardManager.newKeyguardLock("");
        keyguardLock.disableKeyguard();


        Random random = new Random();
        final int delay = random.nextInt(MAX_DELAY) % (MAX_DELAY - MIN_DELAY + 1) + MIN_DELAY;
        LogUtils.d("$$$ delay = " + delay / 60000 + "mins" + delay % 60000 / 1000 + "sec");

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Thread.sleep(delay);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                authIn(context);
                openDingDing(context);

            }
        }).start();
    }

    private static void authIn(Context context) {

        LogUtils.d("$$$ 进行网络认证");
        Account account = getAccountInfo(context);
        Map<String, String> params = new HashMap<>();
        params.put("username", account.getAuthAccount());
        params.put("password", account.getAuthAccountPassword());
        params.put("pwd", account.getAuthAccountPassword());
        params.put("rememberPwd", "1");
        params.put("secret", "true");

        String strResult = HttpUtil.submitPostData(Constant.AUTH_ADDREDD, params, "utf-8");

        LogUtils.d("$$$ strResult = " + strResult);
    }

    private static void openDingDing(Context context) {

        LogUtils.d("$$$ 打开钉钉");
        if (isAppInstalled(context, Constant.DING_PACKAGE_NAME)) {
            context.startActivity(context.getPackageManager().getLaunchIntentForPackage(Constant.DING_PACKAGE_NAME));
        } else {
            LogUtils.d("$$$ 未安装钉钉");
        }
    }

    private static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

}
