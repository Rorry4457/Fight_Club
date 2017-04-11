package com.auto.auto;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

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

        View openAccessibility = findViewById(R.id.openAccessibility);
        openAccessibility.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Operation.openAccessibilitySetting(ResultActivity.this);
            }
        });
    }


}
