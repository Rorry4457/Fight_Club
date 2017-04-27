package com.auto.auto.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.auto.auto.Model.Account;
import com.auto.auto.CheckListener;
import com.auto.auto.Operation;
import com.auto.auto.Util.AlarmClock;
import com.newland.support.nllogger.LogUtils;

import java.util.Date;

public class BootReceiver extends BroadcastReceiver {
    private static int MAX_DELAY = 6 * 60;
    private static int MIN_DELAY = 3 * 60;

//    private static int MIN_DELAY = 5;
//    private static int MAX_DELAY = 10;

    @Override
    public void onReceive(Context context, Intent intent) {
        CheckListener checkListener = new CheckListener();

        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {

            LogUtils.d("$$$ 收到开机广播");
            Account.setIsCheckInToday(false, context);
            AlarmClock.getInstance().delayOpenWifi(context, MIN_DELAY, MAX_DELAY, checkListener);
            AlarmClock.getInstance().wakeUpCheckOut(context, checkListener);

        } else if (intent.getAction().equals(Intent.ACTION_SHUTDOWN)) {

            LogUtils.d("$$$ 收到关机广播");
            Operation.turnOffWifi(context);
        }
    }
}
