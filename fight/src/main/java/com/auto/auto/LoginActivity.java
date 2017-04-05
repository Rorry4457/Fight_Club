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

import com.auto.auto.Adapater.LoginAdapter;
import com.auto.auto.Model.LoginItem;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    RecyclerView loginControl;
    private static final String name = "FightClub";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        loginControl = (RecyclerView) findViewById(R.id.login_control);
        loginControl.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        loginControl.setAdapter(new LoginAdapter(makeLoginInfo()));
        loginControl.setItemAnimator(new DefaultItemAnimator());

        View login = findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMainActivity();
            }
        });
    }

    private void startMainActivity() {

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
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

    private String getVersion() {
        try {
            PackageManager manager = this.getPackageManager();
            PackageInfo info = manager.getPackageInfo(this.getPackageName(), 0);
            System.out.println("info.versionName = " + info.versionName);
            return info.versionName;
        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }
}
