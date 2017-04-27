package com.auto.auto.Util;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.auto.auto.Constant;
import com.newland.support.nllogger.LogUtils;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

/**
 * 闹钟定时器
 */
public class AlarmClock {

    private static AlarmClock instance;
    private AlarmManager alarmManager;
    //    private PendingIntent sendIntent;
//    private TimeoutListener timeoutListener;
    private TimeoutListener timeoutAtTimeListener;

    public static AlarmClock getInstance() {
        if (instance == null) {
            synchronized (AlarmClock.class) {
                if (instance == null) {
                    instance = new AlarmClock();
                }
            }
        }

        return instance;
    }

    private AlarmClock() {
    }

//    /**
//     * 执行轮询闹钟定时器
//     *
//     * @param delayTime  首次执行的间隔时间(s)，0：立即执行
//     * @param periodTime 间隔时间(s)
//     */
//    public void start(Context context, int delayTime, int periodTime, TimeoutListener timeoutListener) {
//        this.timeoutListener = timeoutListener;
//
//        if (alarmManager == null) {
//            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
//        }
//        if (sendIntent != null) {
//            alarmManager.cancel(sendIntent);
//        }
//        long intervalMillis = periodTime * 1000;
//        Intent intent = new Intent(context, SendReceiver.class);
//        intent.setAction(SendReceiver.ACTION_CHECK_OUT);
//
//        long triggerAtMillis = SystemClock.elapsedRealtime() + (delayTime * 1000);
//
//        sendIntent = PendingIntent.getBroadcast(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
//        LogUtils.d("定时器设置完成，间隔时间(秒)：" + periodTime);
//        alarmManager.setRepeating(AlarmManager.ELAPSED_REALTIME_WAKEUP, triggerAtMillis, intervalMillis, sendIntent);
//    }

    /**
     * 执行指定时间点闹钟定时器
     *
     * @param context
     * @param timestamp       指定时间的时间戳
     * @param timeoutListener
     * @param action
     * @param requestCode
     */
    private void startAtTime(Context context, long timestamp, TimeoutListener timeoutListener, String action, int requestCode) {
        this.timeoutAtTimeListener = timeoutListener;
        if (alarmManager == null) {
            alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        }

        Intent intent = new Intent(context, SendReceiver.class);
        intent.setAction(action);
        PendingIntent pending = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_ONE_SHOT);
        alarmManager.setWindow(AlarmManager.RTC_WAKEUP, timestamp, 0, pending);
    }

    /**
     * 执行指定时间范围内的闹钟定时器
     *
     * @param context         context
     * @param minSec          最小延迟时间
     * @param maxSec          最大延迟时间
     * @param timeoutListener listener
     */
    public void delayCheckIn(Context context, int minSec, int maxSec, TimeoutListener timeoutListener) {

        int randSec = getRandomSecond(minSec, maxSec);
        LogUtils.d("$$$ 延时 ： " + randSec + "secs");

        long timeStamp = System.currentTimeMillis();
        timeStamp += randSec * 1000;

        LogUtils.d("$$$ 预计在 " + predictCheckInTime(timeStamp) + " 打卡");

        startAtTime(context, timeStamp, timeoutListener, SendReceiver.ACTION_CHECK_IN, Constant.CHECK_IN);
    }

    public void delayOpenWifi(Context context, int minSec, int maxSec, TimeoutListener timeoutListener) {

        int randSec = getRandomSecond(minSec, maxSec);
        LogUtils.d("$$$ 延时 ： " + randSec + "secs");

        long timeStamp = System.currentTimeMillis();
        timeStamp += randSec * 1000;

        LogUtils.d("$$$ 预计在 " + predictCheckInTime(timeStamp) + " 打开Wi-Fi");

        startAtTime(context, timeStamp, timeoutListener, SendReceiver.ACTION_CHECK_WIFI, Constant.CHECK_WIFI);
    }

    public void wakeUpCheckOut(Context context, TimeoutListener timeoutListener) {

        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, Constant.HOUR_OF_CHECK_OUT);
        calendar.set(Calendar.MINUTE, Constant.MINUTE_OF_CHECK_OUT);
        calendar.set(Calendar.SECOND, Constant.SECOND_OF_CHECK_OUT);
        long timeStamp = calendar.getTimeInMillis();

        LogUtils.d("$$$ 将在 " + predictCheckInTime(timeStamp) + " 提醒下班");

        startAtTime(context, timeStamp, timeoutListener, SendReceiver.ACTION_CHECK_OUT, Constant.CHECK_OUT);
    }

    private static String predictCheckInTime(long timeStamp) {

        Date predict = new Date(timeStamp);

        DateFormat format = DateFormat.getDateTimeInstance();
        return format.format(predict);
    }

    private static int getRandomSecond(int minSeconds, int maxSeconds) {
        return (int) (Math.random() * (maxSeconds - minSeconds + 1)) + minSeconds;
    }

    private void timeoutAtTime(Context context, Intent intent, int requestCode) {
        try {
            PendingIntent pendingIntent = PendingIntent.getBroadcast(context, requestCode, intent, PendingIntent.FLAG_UPDATE_CURRENT);
            alarmManager.cancel(pendingIntent);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (timeoutAtTimeListener != null) {
            timeoutAtTimeListener.onTimeout(context, requestCode);
        }
    }

    public interface TimeoutListener {
        void onTimeout(Context context, int requestCode);
    }

    public static class SendReceiver extends BroadcastReceiver {
        public final static String ACTION_CHECK_OUT = "com.fight.club.check.out";
        public final static String ACTION_CHECK_IN = "com.fight.club.check.in";
        public final static String ACTION_CHECK_WIFI = "com.fight.club.check.wifi";

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            LogUtils.d("$$$ 收到定时广播");
            if (instance != null) {
                if (ACTION_CHECK_OUT.equals(action)) {
                    instance.timeoutAtTime(context, intent, Constant.CHECK_OUT);
                } else if (ACTION_CHECK_IN.equals(action)) {
                    instance.timeoutAtTime(context, intent, Constant.CHECK_IN);
                } else if (ACTION_CHECK_WIFI.equals(action)) {
                    instance.timeoutAtTime(context, intent, Constant.CHECK_WIFI);
                }
            }
        }
    }
}

