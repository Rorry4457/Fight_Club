package com.auto.auto.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.auto.auto.Model.Account;
import com.auto.auto.CheckListener;
import com.auto.auto.Operation;
import com.auto.auto.Util.AlarmClock;
import com.newland.support.nllogger.LogUtils;

public class BootReceiver extends BroadcastReceiver {
    private static int MAX_DELAY = 6 * 60;
    private static int MIN_DELAY = 3 * 60;

//    private static int MIN_DELAY = 5;
//    private static int MAX_DELAY = 10;

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.d("$$$ 收到开机广播");
        Account.setIsCheckInToday(false, context);

        Operation.turnOnWifi(context);

        CheckListener checkListener = new CheckListener();

        //仅在打卡时间内开机才会进行「延时上班打卡」防止出现迟到后开机自动打卡钉钉 打迟到卡的情况
        if (Operation.isInWorkingDuration()) {
            AlarmClock.getInstance().delayCheckIn(context, MIN_DELAY, MAX_DELAY, checkListener);
        }

        AlarmClock.getInstance().wakeUpCheckOut(context, checkListener);
    }
}
