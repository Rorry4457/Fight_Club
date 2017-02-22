package com.auto.auto;

import android.app.Activity;
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
import android.widget.Toast;

import java.util.Calendar;

public class MainActivity extends AppCompatActivity {

    private EditText phoneNumber;
    private EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phoneNumber = (EditText) findViewById(R.id.phone_number);
        password = (EditText) findViewById(R.id.password);

        Button save = (Button) findViewById(R.id.save_login_info);
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveLoginInfo();
            }
        });

        Button openSetting = (Button) findViewById(R.id.btn_schedule);
        openSetting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openSettings(MainActivity.this);
            }
        });


        Button openAccessibility = (Button) findViewById(R.id.btn_accessibility);
        openAccessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gotoAccessibilitySettings(MainActivity.this);
            }
        });
    }

    private void saveLoginInfo() {

        String number = phoneNumber.getText().toString();
        String pass = password.getText().toString();

        if (!TextUtils.isEmpty(number) && !TextUtils.isEmpty(pass)) {

            SharedPreferences sharedPreferences = getSharedPreferences(Constant.SHARE_PREFERENCE, Context.MODE_PRIVATE);
            sharedPreferences.edit().putString(Constant.PHONE, number).putString(Constant.PASSWORD, pass).apply();
            Toast.makeText(this, "login info saved", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "no enough login info", Toast.LENGTH_SHORT).show();
        }
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
