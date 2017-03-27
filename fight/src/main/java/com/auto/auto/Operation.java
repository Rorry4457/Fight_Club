package com.auto.auto;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.text.TextUtils;
import android.text.format.DateUtils;

import com.auto.auto.Util.HttpUtil;
import com.auto.auto.Util.Mail;
import com.newland.support.nllogger.LogUtils;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;


public class Operation {

    private static final String MAIN_MAIL_ADDRESS = "monkeyrunanddogrun@yeah.net";
    private static final String MAIN_MAIL_PASSWORD = "ELDao3xmgj";
    private static final String MAIN_BODY = "在数字时代 对这一类问题的思考就像吃完饭后要洗碗 长发者用完吹风机要清理地上的发丝 以及男性小解后要擦去马桶边沿的尿痕一样重要 妳在为什么而兴奋雀跃 又是因什么而担心受怕 该被取代的工种一定会被取代 该下岗的工人一定会下岗 十九世纪的工人对机器不满可以把它们砸烂 今天妳就算能钻进数据中心 也不可能彻底毁掉所有算法 和机器共处的能力 接受机器挑逗的能力 和机器协商的能力 将会在很大程度上决定我们的心理与生理健康 这是伦理问题 也是人类演化的问题 问题由技术而起 但技术从来不是问题";

    static void startCheckOutOperation(Context context) {

        LogUtils.d("$$$ 下班打卡");
        MediaPlayer mp = MediaPlayer.create(context, R.raw.check_out_sound);
        mp.start();

        lightUpScreen(context);
        openDingDing(context);
    }

    static void startCheckInOperation(final Context context) {

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

    public static boolean isInWorkingDuration() {

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

    public static boolean turnOnWifiWhenOff(Context context) {
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);

        return wifiManager.isWifiEnabled() || wifiManager.setWifiEnabled(true);
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

        }else {
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
                Handler uiHandler = new Handler(Looper.getMainLooper());
                uiHandler.post(uiRunnable);
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

    public static void sendSuccessEmail(Context context) {

        String mail = Account.getAccountInfo(context).getMail();

        if (!TextUtils.isEmpty(mail)) {
            Operation.sendEmailTo(new String[]{mail}, true);
        } else {
            LogUtils.d("$$$ 邮箱地址为空");
        }
    }

    public static void sendFailEmail(Context context) {

        String mail = Account.getAccountInfo(context).getMail();

        if (!TextUtils.isEmpty(mail)) {
            Operation.sendEmailTo(new String[]{mail}, false);
        } else {
            LogUtils.d("$$$ 邮箱地址为空");
        }
    }

    private static void sendEmailTo(final String[] mailAddresses, final boolean isSuccess) {

        LogUtils.d("$$$ 开始发送邮件");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Mail mail = new Mail(MAIN_MAIL_ADDRESS, MAIN_MAIL_PASSWORD);

                mail.setTo(mailAddresses);
                mail.setFrom(MAIN_MAIL_ADDRESS);
                mail.setSubject(createMailSubject(isSuccess));

                if (isSuccess) {
                    mail.setBody(createMailBody());
                } else {
                    mail.setBody("Lose");
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

    private static boolean haveRoot() {

        int i = execRootCmdSilent("echo test"); // 通过执行测试命令来检测
        if (i != -1) {
            return true;
        }
        return false;
    }

    private static int execRootCmdSilent(String paramString) {
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
