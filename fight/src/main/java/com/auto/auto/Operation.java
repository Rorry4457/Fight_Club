package com.auto.auto;

import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.os.PowerManager;
import android.text.TextUtils;

import com.auto.auto.Util.HttpUtil;
import com.auto.auto.Util.Mail;
import com.newland.support.nllogger.LogUtils;

import java.io.DataOutputStream;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import static com.sun.mail.imap.protocol.INTERNALDATE.format;


public class Operation {

    private static final String MAIN_MAIL_ADDRESS = "monkeyrunanddogrun@yeah.net";
    private static final String MAIN_MAIL_PASSWORD = "ELDao3xmgj";

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

    public static void sendEmail(Context context) {

        String mail = Account.getAccountInfo(context).getMail();

        if (!TextUtils.isEmpty(mail)) {
            Operation.sendEmailTo(new String[]{mail}, true);
        } else {
            LogUtils.d("$$$ 邮箱地址为空");
        }
    }

    public static void sendEmailTo(final String[] mailAddresses, final boolean isSuccess) {

        LogUtils.d("$$$ 开始发送邮件");
        new Thread(new Runnable() {
            @Override
            public void run() {
                Mail mail = new Mail(MAIN_MAIL_ADDRESS, MAIN_MAIL_PASSWORD);

                mail.setTo(mailAddresses);
                mail.setFrom(MAIN_MAIL_ADDRESS);
                mail.setSubject(createMailSubject());

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

    private static String createMailSubject() {

        DateFormat dateFormat = DateFormat.getDateTimeInstance();
        String dateTime = dateFormat.format(new Date());

        return "系统测试报告请注意查收" + dateTime;
    }

    private static String createMailBody() {
        return "今天系统运行正常，可以适当的休息，路上不必匆忙。本邮件为搏击俱乐部后台自动发送，请勿回复，有任何问题请自行联系运维人员";
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
