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
    private static int MAX_DELAY = 6;
    private static int MIN_DELAY = 3;

//    private static int MIN_DELAY = 5;
//    private static int MAX_DELAY = 10;

    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction().equals(Intent.ACTION_BOOT_COMPLETED)) {
            LogUtils.d("$$$ 收到开机广播");
            Account.setIsCheckInToday(false, context);

            CheckListener checkListener = new CheckListener();

            //仅在打卡时间内开机才会进行「延时上班打卡」
            if (Operation.isInCheckInDuration()) {
                AlarmClock.getInstance().delayOpenWifi(context, MIN_DELAY, MAX_DELAY, checkListener);
            }

            AlarmClock.getInstance().wakeUpCheckOut(context, checkListener);
        } else if (intent.getAction().equals(Intent.ACTION_SHUTDOWN)) {
            LogUtils.d("$$$ 收到关机广播");

            //仅在下班后关系会关闭wifi，避免工作是重启设备导致wifi。
            if (Operation.isAfterWorkingTime()) {
                Operation.turnOffWifi(context);
            }
        }
    }
}