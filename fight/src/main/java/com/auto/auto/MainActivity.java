package com.auto.auto;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.auto.auto.Fragment.FirstFragment;
import com.auto.auto.Fragment.ZeroFragment;
import com.auto.auto.Model.Account;
import com.auto.auto.stepperview.OnCancelAction;
import com.auto.auto.stepperview.OnContinueAction;
import com.auto.auto.stepperview.OnFinishAction;
import com.auto.auto.stepperview.SteppersItem;
import com.auto.auto.stepperview.SteppersView;
import com.newland.support.nllogger.LogUtils;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends AppCompatActivity implements FirstFragment.OnFirstFragmentListener{

//    private EditText phoneNumber;
//    private EditText dindinPassword;
//    private EditText authAccount;
//    private EditText authAccountPassword;
//    private EditText eMail;

    private List<SteppersItem> steppersItems = new ArrayList<>();

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SteppersView.Config config = createStepViewConfig();
        steppersItems = createSteps();

        SteppersView steppersView = (SteppersView) findViewById(R.id.stepperView);
        steppersView.setConfig(config);
        steppersView.setItems(steppersItems);
        steppersView.build();
    }

    private SteppersView.Config createStepViewConfig() {
        final SteppersView.Config config = new SteppersView.Config();
        config.setOnFinishAction(new OnFinishAction() {
            @Override
            public void onFinish() {

                System.out.println("on Finish");

                // TODO: 2017/4/11 切换到结果页
            }
        });

        config.setOnContinueAction(new OnContinueAction() {
            @Override
            public void onContinue(int position) {
                switch (position) {
                    case 1:
                        SteppersItem item = steppersItems.get(position);
                        FirstFragment fragment = (FirstFragment) item.getFragment();

                        String dingTalkAccount = fragment.getDingTalkAccount();
                        String dingTalkPassword = fragment.getDingTalkPassword();
                        String authAccount = fragment.getAuthAccount();
                        String authPassword = fragment.getAuthAccountPassword();
                        String email = fragment.geteMail();

                        saveLoginInfo(dingTalkAccount, dingTalkPassword, authAccount, authPassword, email);

                        break;
                    default:
                        break;
                }
            }
        });

        config.setOnCancelAction(new OnCancelAction() {
            @Override
            public void onCancel(int position) {

                switch (position) {
                    case 0:
                        backToLoginActivity(MainActivity.this);
                        finish();
                        break;
                    case 1:
                        SteppersItem item = steppersItems.get(position);
                        item.setPositiveButtonEnable(true);
                        // TODO: 2017/4/11 清除当前信息 回退到前一项
                        break;
                    default:
                        // TODO: 2017/4/11 回退一项
                        break;
                }
            }
        });

        config.setFragmentManager(getSupportFragmentManager());

        return config;
    }


    private List<SteppersItem> createSteps() {

        ArrayList<SteppersItem> steps = new ArrayList<>();

        SteppersItem zeroStep = new SteppersItem();
        zeroStep.setLabel(getString(R.string.step_zero));
        zeroStep.setSubLabel(getString(R.string.step_zero_instruction));
        zeroStep.setFragment(new ZeroFragment());
        zeroStep.setPositiveButtonEnable(true);

        steps.add(zeroStep);

        SteppersItem firstStep = new SteppersItem();
        firstStep.setLabel(getString(R.string.step_one));
        firstStep.setSubLabel(getString(R.string.step_one_instruction));
        firstStep.setFragment(new FirstFragment());
        firstStep.setPositiveButtonEnable(false);

        steps.add(firstStep);

        SteppersItem secondStep = new SteppersItem();
        secondStep.setLabel(getString(R.string.step_two));
        secondStep.setSubLabel(getString(R.string.step_two_instruction));
        secondStep.setFragment(null);
        secondStep.setPositiveButtonEnable(true);

        steps.add(secondStep);

        SteppersItem thirdStep = new SteppersItem();
        thirdStep.setLabel(getString(R.string.step_three));
        thirdStep.setLabel(getString(R.string.step_three_instruction));
        thirdStep.setFragment(null);
        thirdStep.setPositiveButtonEnable(true);

        steps.add(thirdStep);

        return steps;
    }

    private void saveLoginInfo(String dingTalkAccount, String dingTalkPassword, String authAccount, String authPassword, String email) {

        Account account = new Account();
        account.setPhoneNum(dingTalkAccount);
        account.setDingDingPassword(dingTalkPassword);
        account.setAuthAccount(authAccount);
        account.setAuthAccountPassword(authPassword);
        account.setMail(email);

        account.saveAccountInfo(account, this);
        LogUtils.d("$$$ 账户信息已保存");
    }

    /**
     * 跳转到系统辅助功能设置页面.<br>
     *
     * @param context
     */
    private static boolean gotoAccessibilitySettings(Context context) {
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

    private void backToLoginActivity(Context context) {

        Intent intent = new Intent(context, LoginActivity.class);
        context.startActivity(intent);
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

    @Override
    public void onInfoEnough() {

        SteppersItem item = steppersItems.get(1);
        item.setPositiveButtonEnable(true);

        InputMethodManager manager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        manager.hideSoftInputFromWindow(getWindow().getDecorView().getWindowToken(), 0);
    }
}
