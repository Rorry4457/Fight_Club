package com.auto.auto;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import com.auto.auto.receive.BootReceiver;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private Calendar calendar;
    private Button btn;
    private EditText phoneNumber;
    private EditText password;
    private final int repeatInterval = 1 * 60 * 1000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        calendar = Calendar.getInstance();
        calendar.setTimeInMillis(System.currentTimeMillis());

        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // TODO: 2017/2/22 开启定时开关机页
                openSettings(MainActivity.this);
            }
        });


        Button button = (Button) findViewById(R.id.btn_1);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoAccessibilitySettings(MainActivity.this);
            }
        });

        phoneNumber = (EditText) findViewById(R.id.phone_number);
        password = (EditText) findViewById(R.id.password);
    }

    private void saveLoginInfo() {

        String number = phoneNumber.getText().toString();
        String pass = password.getText().toString();

        if (!TextUtils.isEmpty(number) && !TextUtils.isEmpty(pass)) {

            SharedPreferences sharedPreferences = getSharedPreferences(Constant.SHARE_PREFERENCE, Context.MODE_PRIVATE);
            sharedPreferences.edit().putString(Constant.PHONE, number).putString(Constant.PASSWORD, pass).apply();
        } else {
            Toast.makeText(this, "no enough login info", Toast.LENGTH_SHORT).show();
        }
    }

    private void showTimePicker() {
        new TimePickerDialog(this, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {
                calendar.setTimeInMillis(System.currentTimeMillis());
                calendar.set(Calendar.HOUR_OF_DAY, i);
                calendar.set(Calendar.MINUTE, i1);
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);
                calendar.set(Calendar.DATE, 22);
                btn.setText(calendar.getTime().toString());
                Intent intent = new Intent(MainActivity.this, BootReceiver.class);
                PendingIntent broadcast = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);

                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), repeatInterval, broadcast);
                System.out.println("MainActivity.onTimeSet");
            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true).show();
    }


    /**
     * 跳转到系统辅助功能设置页面.<br>
     *
     * @param context
     */
    public static boolean gotoAccessibilitySettings(Context context) {
        Intent settingsIntent = new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS);
        if (!(context instanceof Activity)) {
            settingsIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        boolean isOk = true;
        try {
            context.startActivity(settingsIntent);
        } catch (ActivityNotFoundException e) {
            isOk = false;
        }
        return isOk;
    }

    private static boolean openSettings(Context context) {

        Intent intent = new Intent(Settings.ACTION_SETTINGS);
        if (!(context instanceof Activity)) {
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        }
        boolean isOk = true;
        try {
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            isOk = false;
        }
        return isOk;
    }
}
