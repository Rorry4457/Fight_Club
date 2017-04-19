package com.auto.auto;

import android.content.Intent;
import android.graphics.Canvas;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.auto.auto.Adapater.LoginAdapter;
import com.auto.auto.Model.Account;
import com.auto.auto.Model.LoginItem;
import com.auto.auto.Model.LoginViewHolder;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.callback.ItemDragAndSwipeCallback;
import com.chad.library.adapter.base.listener.OnItemDragListener;
import com.chad.library.adapter.base.listener.OnItemSwipeListener;

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

        final LoginAdapter adapter = new LoginAdapter(makeLoginInfo());
        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        adapter.isFirstOnly(false);
        ItemDragAndSwipeCallback callback = new ItemDragAndSwipeCallback(adapter);
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        touchHelper.attachToRecyclerView(loginControl);

        OnItemSwipeListener swipeListener = new OnItemSwipeListener() {
            @Override
            public void onItemSwipeStart(RecyclerView.ViewHolder viewHolder, int i) {
            }

            @Override
            public void clearView(RecyclerView.ViewHolder viewHolder, int i) {
            }

            @Override
            public void onItemSwiped(RecyclerView.ViewHolder viewHolder, int i) {

                LoginViewHolder holder = (LoginViewHolder) viewHolder;
                LoginItem item = holder.getItem();

                String left = item.getLeftText();
                String right = item.getRightText();

                if (left.equals("G") && right.equals("H") && adapter.getData().size() == 9) {
                    startNextActivity();
                }
            }

            @Override
            public void onItemSwipeMoving(Canvas canvas, RecyclerView.ViewHolder viewHolder, float v, float v1, boolean b) {
            }
        };

        callback.setSwipeMoveFlags(ItemTouchHelper.UP | ItemTouchHelper.DOWN);
        adapter.enableSwipeItem();
        adapter.setOnItemSwipeListener(swipeListener);
        adapter.enableDragItem(touchHelper);

        loginControl.setAdapter(adapter);

        View loginBtn = findViewById(R.id.login);
        loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (adapter.getData().size() != 10) {
                    adapter.setNewData(makeLoginInfo());
                }
            }
        });
    }

    private void startNextActivity() {
        Account account = Account.getAccountInfo(LoginActivity.this);
        if (account != null && account.hasAlreadySavedLoginInfo()) {
            startResultActivity();
        } else {
            startMainActivity();
        }
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

            LoginItem item = new LoginItem(leftString, rightString);
            itemList.add(item);
        }

        return itemList;
    }
}
