package com.auto.auto.Util;

import android.content.Context;

import com.newland.support.nllogger.LogUtils;

import java.text.DateFormat;
import java.util.Date;

public class Delay {

    public static DelayDelegate delegate;

    public static void delayIn(int minSeconds, int maxSeconds, final Context context) {

        final int randomSeconds = getRandomSecond(minSeconds, maxSeconds);
        LogUtils.d("$$$ 延迟 " + randomSeconds / 60 + "mins" + randomSeconds % 60 + "sec 打卡");
        LogUtils.d("$$$ 预计打卡时间为" + predictCheckInTime(randomSeconds));
        new Thread(new Runnable() {
            @Override
            public void run() {

                try {
                    LogUtils.d("$$$ 开始延时");
                    Thread.sleep(randomSeconds * 1000);
                    LogUtils.d("$$$ 延时结束");
                    if (delegate != null) {
                        delegate.afterDelay(context);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private static int getRandomSecond(int minSeconds, int maxSeconds) {
        return (int) (Math.random() * (maxSeconds - minSeconds + 1)) + minSeconds;
    }

    private static String predictCheckInTime(int delaySeconds) {

        long currentTime = System.currentTimeMillis();
        currentTime += delaySeconds * 1000;

        Date predict = new Date(currentTime);

        DateFormat format = DateFormat.getDateTimeInstance();
        return format.format(predict);
    }
}
