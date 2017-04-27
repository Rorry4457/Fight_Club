package com.auto.auto;

import android.content.Context;

import com.auto.auto.Util.AlarmClock;

public class CheckListener implements AlarmClock.TimeoutListener {
    @Override
    public void onTimeout(Context context, int requestCode) {

        switch (requestCode) {
            case Constant.CHECK_IN:
                Operation.startCheckInOperation(context);
                break;
            case Constant.CHECK_OUT:
                Operation.startCheckOutOperation(context);
                break;
            case Constant.CHECK_WIFI:
                Operation.turnOnWifi(context);
            default:
                break;
        }
    }
}
