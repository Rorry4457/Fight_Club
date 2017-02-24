package com.auto.auto.receive;

import android.app.KeyguardManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.PowerManager;
import android.util.Log;

import com.auto.auto.Constant;
import com.auto.auto.ShellUtil.HttpUtil;


import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by x on 2016/11/2.
 */
public class BootReceiver extends BroadcastReceiver {
    public static String TAG = BootReceiver.class.getSimpleName();
//    private static int MAX_DELAY = 10 * 60 * 1000;
//    private static int MIN_DELAY = 3 * 60 * 1000;

    private static int MAX_DELAY = 10 * 1000;
    private static int MIN_DELAY = 5 * 1000;

    @Override
    public void onReceive(Context context, Intent intent) {
        // 打卡时间已到  唤醒屏幕

        Log.d(TAG, " 接收到定时的信息 ");
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
        System.out.println("delay = " + delay / 60000 + "mins" + delay % 60000 / 1000 + "sec");

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

        SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.SHARE_PREFERENCE, MODE_PRIVATE);
        String account = sharedPreferences.getString(Constant.ACCOUNT, "000000");
        System.out.println("account = " + account);
        String accountPassword = sharedPreferences.getString(Constant.ACCOUNT_PASSWORD, "000000");
        System.out.println("accountPassword = " + accountPassword);
        System.out.println("BootReceiver.authIn");
        Map<String, String> params = new HashMap<>();
        params.put("username", account);
        params.put("password", accountPassword);
        params.put("pwd", accountPassword);
        params.put("rememberPwd", "1");
        params.put("secret", "true");

        String strResult = HttpUtil.submitPostData(Constant.AUTH_ADDREDD, params, "utf-8");

        Log.d("result ", strResult);
    }

    private static void openDingDing(Context context) {

        System.out.println("BootReceiver.openDingDing");
        if (isAppInstalled(context, Constant.DING_PACKAGE_NAME)) {
            context.startActivity(context.getPackageManager().getLaunchIntentForPackage(Constant.DING_PACKAGE_NAME));
        } else {
            System.out.println("BootReceiver.openDingDing + no isnstalled DingDing");
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
