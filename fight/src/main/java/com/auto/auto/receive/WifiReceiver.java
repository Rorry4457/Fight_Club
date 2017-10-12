package com.auto.auto.receive;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.os.Parcelable;

import com.auto.auto.Operation;
import com.newland.support.nllogger.LogUtils;

public class WifiReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {

        //防止非打卡时间通断wifi导致钉钉启动
        if (!Operation.isInCheckInDuration()) {
            return;
        }

        if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(intent.getAction())) {
            Parcelable parcelableExtra = intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            if (null != parcelableExtra) {
                NetworkInfo networkInfo = (NetworkInfo) parcelableExtra;

                switch (networkInfo.getState()) {
                    case CONNECTED:
                        if (!networkInfo.getExtraInfo().equals("<unknown ssid>")) {

                            LogUtils.d("$$$ 连上Wi-Fi " + networkInfo.toString());
                            Operation.startCheckInOperation(context);
                        }
                        break;
                    case CONNECTING:
                        break;
                    case DISCONNECTED:
                        break;
                    case DISCONNECTING:
                        break;
                    case SUSPENDED:
                        break;
                    case UNKNOWN:
                        break;
                    default:
                        break;
                }
            }
        }
    }
}
