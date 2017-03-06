package com.auto.auto;

import android.app.AlarmManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;

import com.auto.auto.Util.HttpUtil;
import com.newland.support.nllogger.LogUtils;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

import static com.auto.auto.Account.getAccountInfo;


public class Operation {

    public static void startCheckOutOperation(Context context) {

        LogUtils.d("$$$ 下班打卡");
        MediaPlayer mp = MediaPlayer.create(context, R.raw.check_out_sound);
        mp.start();

        lightUpScreen(context);
        openDingDing(context);
        System.out.println("Operation.startCheckOutOperation");
    }

    public static void startCheckInOperation(final Context context) {

        LogUtils.d("$$$ 上班打卡");
        lightUpScreen(context);
        authIn(context, new Runnable() {
            @Override
            public void run() {
                openDingDing(context);
            }
        });

    }

    private static void lightUpScreen(Context context) {
        //获取电源管理器对象
        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        //获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
        PowerManager.WakeLock wl = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP | PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
        wl.acquire();
        LogUtils.d("$$$ 点亮屏幕");
        //释放
        wl.release();
    }

    private static void authIn(final Context context, final Runnable uiRunnable) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                LogUtils.d("$$$ 进行网络认证");
                Account account = getAccountInfo(context);
                Map<String, String> params = new HashMap<>();
                params.put(Constant.NET_AUTH_USER, account.getAuthAccount());
                params.put(Constant.NET_AUTH_PASSWORD, account.getAuthAccountPassword());
                params.put(Constant.NET_AUTH_PWD, account.getAuthAccountPassword());
                params.put(Constant.NET_AUTH_REMBER, "1");
                params.put(Constant.NET_AUTH_SECRET, "true");

                String strResult = HttpUtil.submitPostData(Constant.AUTH_ADDREDD, params, "utf-8");

                LogUtils.d("$$$ strResult = " + strResult);
                Handler uiHandler = new Handler(Looper.getMainLooper());
                uiHandler.post(uiRunnable);
            }
        }).start();
    }

    public static void openDingDing(Context context) {

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

    protected static boolean haveRoot() {

        int i = execRootCmdSilent("echo test"); // 通过执行测试命令来检测
        if (i != -1) {
            return true;
        }
        return false;
    }

    protected static int execRootCmdSilent(String paramString) {
        try {
            Process localProcess = Runtime.getRuntime().exec("su");
            Object localObject = localProcess.getOutputStream();
            DataOutputStream localDataOutputStream = new DataOutputStream(
                    (OutputStream) localObject);
            String str = String.valueOf(paramString);
            localObject = str + "\n";
            localDataOutputStream.writeBytes((String) localObject);
            localDataOutputStream.flush();
            localDataOutputStream.writeBytes("exit\n");
            localDataOutputStream.flush();
            localProcess.waitFor();
            int result = localProcess.exitValue();
            return (Integer) result;
        } catch (Exception localException) {
            localException.printStackTrace();
            return -1;
        }
    }

    private boolean isRoot() {
        try {
            Process pro = Runtime.getRuntime().exec("su");
            pro.getOutputStream().write("exit\n".getBytes());
            pro.getOutputStream().flush();
            int i = pro.waitFor();
            if (0 == i) {
                pro = Runtime.getRuntime().exec("su");
                return true;
            }

        } catch (Exception e) {
            return false;
        }
        return false;

    }
}
