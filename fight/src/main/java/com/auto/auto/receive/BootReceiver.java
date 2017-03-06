package com.auto.auto.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.auto.auto.Account;
import com.auto.auto.CheckListener;
import com.auto.auto.Util.AlarmClock;
import com.newland.support.nllogger.LogUtils;

/**
 * Created by x on 2016/11/2.
 */
public class BootReceiver extends BroadcastReceiver {
    private static int MAX_DELAY = 6 * 60;
    private static int MIN_DELAY = 3 * 60;

//    private static int MIN_DELAY = 5;
//    private static int MAX_DELAY = 10;

    @Override
    public void onReceive(Context context, Intent intent) {
        LogUtils.d("$$$ 收到开机广播");
        Account.setIsCheckInToday(false, context);

        CheckListener checkListener = new CheckListener();
        AlarmClock.getInstance().delayCheckIn(context, MIN_DELAY, MAX_DELAY, checkListener);
        AlarmClock.getInstance().wakeUpCheckOut(context, checkListener);
    }
}
