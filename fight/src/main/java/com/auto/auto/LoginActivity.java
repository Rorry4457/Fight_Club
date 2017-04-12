package com.auto.auto;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.auto.auto.Adapater.LoginAdapter;
import com.auto.auto.Model.Account;
import com.auto.auto.Model.LoginItem;

import java.util.ArrayList;
import java.util.List;


public class LoginActivity extends AppCompatActivity {

    RecyclerView loginControl;
    private static final String name = "FIGHTCLUB";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginControl = (RecyclerView) findViewById(R.id.login_control);
        loginControl.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        loginControl.setAdapter(new LoginAdapter(makeLoginInfo()));
        loginControl.setItemAnimator(new DefaultItemAnimator());

        final EditText passwordEditText = (EditText) findViewById(R.id.password);

        View loginBtn = findViewById(R.id.login);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String password = passwordEditText.getText().toString();

                if (password.equals("998877")) {

                    Account account = Account.getAccountInfo(LoginActivity.this);

                    if (account !=null && account.hasAlreadySavedLoginInfo()) {
                        startResultActivity();
                    } else {
                        startMainActivity();
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "Wrong", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void startMainActivity() {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    private void startResultActivity() {
        Intent intent = new Intent(this, ResultActivity.class);
        startActivity(intent);
        finish();
    }

    private List<LoginItem> makeLoginInfo() {

        List<LoginItem> itemList = new ArrayList<>();

        for (int i = 0; i <= name.length(); i++) {

            int leftIndex = i - 1;
            int rightIndex = i + 1;
            String leftString = "";
            String rightString = "";

            if (leftIndex >= 0) {
                leftString = name.substring(leftIndex, i);
            }

            if (rightIndex <= name.length()) {
                rightString = name.substring(i, rightIndex);
            }

            LoginItem item = new LoginItem(leftString, rightString, i);
            itemList.add(item);
        }

        return itemList;
    }
}
