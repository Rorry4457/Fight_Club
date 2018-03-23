package com.auto.auto;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.provider.Settings;
import android.text.TextUtils;
import android.text.format.DateUtils;

import com.auto.auto.Model.Account;
import com.auto.auto.util.FileUtil;
import com.auto.auto.util.HttpUtil;
import com.auto.auto.util.Mail;
import com.newland.support.nllogger.LogUtils;

import java.sql.Time;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Operation {

    private static final String MAIN_MAIL_ADDRESS = "monkeyrunanddogrun@yeah.net";
    private static final String MAIN_MAIL_PASSWORD = "ELDao3xmgj";
    private static final String MAIN_BODY = "总有人有这样的想法：没关系 封了这个 以后会有别的办法的 这种人的思维就是上有政策下有对策 而且这「别的办法」只有她们才有 其她人只能偷偷摸摸地在地下市场寻求质量存疑的「对策」 长此以往 有办法的人和没办法的人就会彻底成为陌生人 微信能够成为lifestyle 就是因为在中国太多人不在乎也不知道何谓style Style一定是不方便的 微信是非常方便的";

    static void startCheckOutOperation(Context context) {

        FileUtil.cleanScreenCapture(context);

        LogUtils.d("$$$ 下班打卡");
        MediaPlayer mp = MediaPlayer.create(context, R.raw.check_out_sound);
        mp.start();

        lightUpScreen(context);
        openDingDing(context);
    }

    public static void startCheckInOperation(final Context context) {

        LogUtils.d("$$$ 上班打卡");
        lightUpScreen(context);
        authIn(context, new Runnable() {
            @Override
            public void run() {
                HttpUtil.ping();
                openDingDing(context);
            }
        });
    }

    public static boolean isInCheckInDuration() {

        Long currentTime = System.currentTimeMillis();

        Time now = new Time(currentTime);

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 8);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Time minLimit = new Time(calendar.getTimeInMillis());

        calendar.set(Calendar.HOUR_OF_DAY, 9);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        Time maxLimit = new Time(calendar.getTimeInMillis());

        return now.after(minLimit) && now.before(maxLimit);
    }

    public static boolean isInCheckOutDuration() {

        Long currentTime = System.currentTimeMillis();

        Time now = new Time(currentTime);

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 17);
        calendar.set(Calendar.MINUTE, 45);
        calendar.set(Calendar.SECOND, 0);
        Time minLimit = new Time(calendar.getTimeInMillis());

        calendar.set(Calendar.HOUR_OF_DAY, 17);
        calendar.set(Calendar.MINUTE, 48);
        calendar.set(Calendar.SECOND, 0);
        Time maxLimit = new Time(calendar.getTimeInMillis());

        return now.after(minLimit) && now.before(maxLimit);
    }

    public static boolean isAfterWorkingTime() {

        Long currentTime = System.currentTimeMillis();

        Time now = new Time(currentTime);

        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, 17);
        calendar.set(Calendar.MINUTE, 45);
        calendar.set(Calendar.SECOND, 0);
        Time minLimit = new Time(calendar.getTimeInMillis());

        return now.after(minLimit);
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

    public static boolean turnOnWifi(Context context) {

        //需要点亮屏幕之后打开wifi，才会进行wifi连接。因为系统会出于节能的考虑，不亮屏时不会真正发起wifi连接操作
        lightUpScreen(context);

        LogUtils.d("$$$ 打开Wi-Fi");
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        return wifiManager.isWifiEnabled() || wifiManager.setWifiEnabled(true);
    }

    public static boolean turnOffWifi(Context context) {

        LogUtils.d("$$$ 关闭Wi-Fi");
        WifiManager wifiManager = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);

        return wifiManager.setWifiEnabled(false);
    }

    public static boolean openAccessibilitySetting(Context context) {
        Intent settingsIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        if (!(context instanceof Activity)) {
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        boolean isOk = true;
        try {
            context.startActivity(settingsIntent);
        } catch (ActivityNotFoundException e) {
            isOk = false;
        }
        return isOk;
    }

    public static boolean isToday(String dateString) {

        if (TextUtils.isEmpty(dateString)) {
            return false;
        }

        int yearIndex = dateString.indexOf("年");
        int monthIndex = dateString.indexOf("月");
        int dayIndex = dateString.indexOf("日");

        if (yearIndex == -1 && monthIndex == -1 && dayIndex == -1) {
            return false;
        }

        Calendar calendar = Calendar.getInstance();

        if (yearIndex != -1) {
            String year = dateString.substring(0, yearIndex);
            calendar.set(Calendar.YEAR, Integer.valueOf(year));

        } else {
            calendar.set(Calendar.YEAR, calendar.get(Calendar.YEAR));
        }

        if (monthIndex != -1) {
            String month = dateString.substring(yearIndex + 1, monthIndex);
            calendar.set(Calendar.MONTH, Integer.valueOf(month) - 1);
        } else {
            return false;
        }

        if (dayIndex != -1) {
            String day = dateString.substring(monthIndex + 1, dayIndex);
            calendar.set(Calendar.DAY_OF_MONTH, Integer.valueOf(day));
        } else {
            return false;
        }

        return DateUtils.isToday(calendar.getTimeInMillis());
    }

    private static void authIn(final Context context, final Runnable uiRunnable) {
        new Thread(new Runnable() {
            @Override
            public void run() {

                LogUtils.d("$$$ 进行网络认证");
                Account account = Account.getAccountInfo(context);
                Map<String, String> params = new HashMap<>();
                params.put(Constant.NET_AUTH_USER, account.getAuthAccount());
                params.put(Constant.NET_AUTH_PASSWORD, account.getAuthAccountPassword());
                params.put(Constant.NET_AUTH_PWD, account.getAuthAccountPassword());
                params.put(Constant.NET_AUTH_REMBER, "1");
                params.put(Constant.NET_AUTH_SECRET, "true");

                String strResult = HttpUtil.submitPostData(Constant.AUTH_ADDREDD, params, "utf-8");

                LogUtils.d("$$$ strResult = " + strResult);

                if (uiRunnable != null) {
                    Handler uiHandler = new Handler(Looper.getMainLooper());
                    LogUtils.d("$$$ 延时5s");
                    uiHandler.postDelayed(uiRunnable, 5000);
                }
            }
        }).start();
    }

    private static void openDingDing(Context context) {

        LogUtils.d("$$$ 打开钉钉");
        if (isAppInstalled(context, Constant.DING_PACKAGE_NAME)) {
            context.startActivity(context.getPackageManager().getLaunchIntentForPackage(Constant.DING_PACKAGE_NAME));
        } else {
            LogUtils.d("$$$ 未安装钉钉");
        }
    }

    public static void backToHome(Context context) {
        LogUtils.d("$$$ 回到桌面");
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.addCategory(Intent.CATEGORY_HOME);
        context.startActivity(intent);
    }

    private static boolean isAppInstalled(Context context, String packageName) {
        try {
            context.getPackageManager().getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public static void sendEmailWithAttachment(Context context) {

        String mail = Account.getAccountInfo(context).getMail();

        if (!TextUtils.isEmpty(mail)) {
            sendEmailTo(new String[]{mail}, null);
        } else {
            LogUtils.d("$$$ 邮箱地址为空");
        }
    }

    private static void sendEmailTo(final String[] mailAddresses, final String attachMentFile) {

        LogUtils.d("$$$ 开始发送邮件");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Mail mail = new Mail(MAIN_MAIL_ADDRESS, MAIN_MAIL_PASSWORD);

                mail.setTo(mailAddresses);
                mail.setFrom(MAIN_MAIL_ADDRESS);
                mail.setSubject(createMailSubject(true));
                mail.setBody(createMailBody());

                //添加附件
                if (attachMentFile != null) {
                    try {
                        mail.addAttachment(attachMentFile);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

                try {
                    boolean isMailSended = mail.send();
                    if (isMailSended) {
                        LogUtils.d("$$$ 邮件发送成功");
                    } else {
                        LogUtils.d("$$$ 邮件发送失败");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    LogUtils.e(e);
                }
            }
        }).start();
    }

    private static String createMailSubject(boolean isSuccess) {

        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        String dateTime = dateFormat.format(new Date());

        if (isSuccess) {
            return "系统测试报告：今日系统运行良好" + dateTime;
        } else {
            return "系统测试报告：今日系统故障" + dateTime;
        }
    }

    private static String createMailBody() {

        String body = "";

        String[] splits = MAIN_BODY.split(" ");
        int length = splits.length;

        ArrayList<Integer> indexArray = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            indexArray.add(i);
        }

        Collections.shuffle(indexArray);

        for (int i = 0; i < indexArray.size(); i++) {
            body += splits[indexArray.get(i)];
            body += " ";
        }

        String subBody = "\n\n\n\n\n\n\n                    本邮件为搏击俱乐部后台自动发送，请勿回复，有任何问题请联系运维人员";

        return body + subBody;
    }

    public static void takeScreenShot(Context context) {

        Intent intent = new Intent(context, PrepareActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static void pushTransparentActivity(Context context) {

        Intent intent = new Intent(context, TransparentActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }
}
