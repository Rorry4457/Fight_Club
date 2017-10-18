package com.auto.auto;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.auto.auto.Model.Account;


public class ResultActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        Account account = Account.getAccountInfo(this);

        TextView dingTalkAccount = (TextView) findViewById(R.id.dingTalkAccount);
        dingTalkAccount.setText(account.getPhoneNum());

        TextView authAccount = (TextView) findViewById(R.id.authAccount);
        authAccount.setText(account.getAuthAccount());

        TextView email = (TextView) findViewById(R.id.eMail);
        email.setText(account.getMail());

        TextView versionNum = (TextView) findViewById(R.id.version);
        versionNum.setText(getVersion());

        View openAccessibility = findViewById(R.id.openAccessibility);
        openAccessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Operation.openAccessibilitySetting(ResultActivity.this);
            }
        });

        View resetInfo = findViewById(R.id.reset);
        resetInfo.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Account.clearAccount(ResultActivity.this);
                startMainActivity();
                return true;
            }
        });

        resetInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ResultActivity.this, "Long click to reset", Toast.LENGTH_SHORT).show();
            }
        });

        findViewById(R.id.permission_request).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent = new Intent(ResultActivity.this, PrepareActivity.class);
                intent.putExtra("FROM", "ResultActivity");
                startActivity(intent);
            }
        });
    }

    private String decoratePhoneNum(String num) {
        if (!TextUtils.isEmpty(num)) {

            int start = 3;
            int end = num.length() - 3;

            return decorateString(start, end, num);
        } else {
            return num;
        }
    }

    private String decorateAccout(String account) {

        if (!TextUtils.isEmpty(account)) {

            int start = 1;
            int end = account.length() - 2;

            return decorateString(start, end, account);
        } else {
            return account;
        }
    }

    private String decorateEmail(String email) {

        if (!TextUtils.isEmpty(email)) {

            int start = 4;
            int end = email.indexOf("@");

            return decorateString(start, end, email);
        } else {
            return email;
        }
    }

    private String decorateString(int startIndex, int endIndex, String target) {

        String mask = "";
        for (int i = 0; i <= (endIndex - startIndex); i++) {
            mask += "*";
        }

        String origin = target.substring(startIndex, endIndex);
        return target.replace(origin, mask);
    }

    private void startMainActivity() {
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    private String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            return "Version: " + info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
