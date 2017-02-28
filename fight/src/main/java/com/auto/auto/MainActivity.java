package com.auto.auto;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.newland.support.nllogger.LogUtils;


public class MainActivity extends AppCompatActivity {

    private EditText phoneNumber;
    private EditText dindinPassword;
    private EditText authAccount;
    private EditText authAccountPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        phoneNumber = (EditText) findViewById(R.id.phone_number);
        dindinPassword = (EditText) findViewById(R.id.password);
        authAccount = (EditText) findViewById(R.id.authAccount);
        authAccountPassword = (EditText) findViewById(R.id.account_password);

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

        Account savedAccount = Account.getAccountInfo(this);
        if (savedAccount != null && savedAccount.hasAlreadySavedLoginInfo()) {
            phoneNumber.setText(savedAccount.getPhoneNum());
            dindinPassword.setText(savedAccount.getDingDingPassword());
            authAccount.setText(savedAccount.getAuthAccount());
            authAccountPassword.setText(savedAccount.getAuthAccountPassword());
        }
    }

    private void saveLoginInfo() {

        String number = phoneNumber.getText().toString();
        String pass = dindinPassword.getText().toString();
        String ac = authAccount.getText().toString();
        String acPass = authAccountPassword.getText().toString();

        if (!TextUtils.isEmpty(number) && !TextUtils.isEmpty(pass)) {

            Account account = new Account();
            account.setPhoneNum(number);
            account.setDingDingPassword(pass);
            account.setAuthAccount(ac);
            account.setAuthAccountPassword(acPass);

            account.saveAccountInfo(account, this);
            Toast.makeText(this, "login info saved", Toast.LENGTH_SHORT).show();
            LogUtils.d("$$$ 账户信息已保存");
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
