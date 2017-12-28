package com.auto.auto.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.auto.auto.Model.Account;
import com.auto.auto.Constant;
import com.auto.auto.Operation;
import com.newland.support.nllogger.LogUtils;

import java.util.ArrayList;
import java.util.List;

public class AutoPushCard extends AccessibilityService {

    boolean isLoginOperate = false;
    boolean isAlreadyOpenCheckOut = false;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

        int eventType = accessibilityEvent.getEventType();
        String packageName = accessibilityEvent.getPackageName().toString();
        if (Operation.isInCheckInDuration()) {

            if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && Constant.DING_PACKAGE_NAME.equals(packageName)) {
                autoLogin();
                closeWebAlert();
            } else if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED && Constant.DING_PACKAGE_NAME.equals(packageName)) {
                //界面的切换会多次调用，在这里进行是否打卡成功的检测，比较妥当
                openWorkNotificationPage();
            }
        }

        if (Operation.isInCheckOutDuration()) {

            if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && Constant.DING_PACKAGE_NAME.equals(packageName)) {
                startCheckOut();
            }
        }
    }

    private void startCheckOut() {
        if (isAlreadyOpenCheckOut) {
            return;
        }

        List<AccessibilityNodeInfo> bottomTab = findNodeById(Constant.BOTTOM_TAB_LAYOUT);
        if (bottomTab.size() > 0) {
            List<AccessibilityNodeInfo> toWorkPageButton = bottomTab.get(0).findAccessibilityNodeInfosByViewId(Constant.TAB_FOR_WORK);
            if (toWorkPageButton.size() > 0) {
                toWorkPageButton.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                openCheckOutPage();
            }
        } else {
            LogUtils.d("$$$ 下班打卡未发现底部tabBar，点击返回按钮");
            List<AccessibilityNodeInfo> backButton = findNodeById(Constant.BACK_BUTTON);
            if (backButton.size() > 0) {
                backButton.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }

    private void openCheckOutPage() {
        List<AccessibilityNodeInfo> workLayouts = findNodeById(Constant.WORK_LAYOUT);

        if (workLayouts.size() > 0) {

            List<AccessibilityNodeInfo> items = workLayouts.get(0).findAccessibilityNodeInfosByViewId(Constant.WORK_LAYOUT_ITEM);
            if (items.size() > 0) {

                for (AccessibilityNodeInfo info : items) {

                    List<AccessibilityNodeInfo> titleItems = info.findAccessibilityNodeInfosByViewId(Constant.WORK_ITEM_TITLE);
                    if (titleItems.size() > 0 && titleItems.get(0).getText().equals(Constant.WORK_CHECK_TEXT)) {
                        isAlreadyOpenCheckOut = true;
                        info.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        return;
                    }
                }
            }
        }
    }

    private void openWorkNotificationPage() {
        if (Account.isCheckInToday(this)) {
            return;
        }

        List<AccessibilityNodeInfo> bottomTab = findNodeById(Constant.BOTTOM_TAB_LAYOUT);
        if (bottomTab.size() > 0) {
            List<AccessibilityNodeInfo> tableLayout = findNodeById(Constant.MAIN_TABLE_VIEW);
            if (tableLayout.size() > 0) {
                List<AccessibilityNodeInfo> items = tableLayout.get(0).findAccessibilityNodeInfosByText(Constant.DEPARTMENT);
                if (items.size() > 0) {
                    items.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    LogUtils.d("$$$ 打开工作通知页");

                    Operation.takeScreenShot(this);
                    Account.setIsCheckInToday(true, this);
                } else {
                    LogUtils.d("$$$ 未能打开工作通知页");
                }
            } else {
                LogUtils.d("$$$ 未发现部门打卡状况cell");
            }
        } else {
            LogUtils.d("$$$ 未发现底部tabBar");
        }
    }

    private void autoLogin() {

        if (!isLoginOperate) {
            isLoginOperate = true;
            //在登录界面 需要登录钉钉
            LogUtils.d("$$$ 需要登录  找到登录界面的控件 :");

            List<AccessibilityNodeInfo> phoneEt = findNodeById(Constant.LOGIN_PHONE_EDITTEXT);
            List<AccessibilityNodeInfo> pwdEt = findNodeById(Constant.LOGIN_PASSWROD_EDITTEXT);
            List<AccessibilityNodeInfo> loginBtn = findNodeById(Constant.LOGIN_BTN);
            Account account = Account.getAccountInfo(this);

            if (phoneEt.size() > 0) {
                AccessibilityNodeInfo phone = phoneEt.get(0);
                phone.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                setText(phone, account.getPhoneNum());
            }

            if (pwdEt.size() > 0) {
                AccessibilityNodeInfo password = pwdEt.get(0);//设置登录密码
                setText(password, account.getDingDingPassword());
            }

            LogUtils.d("$$$ 完成输入账户信息");

            if (loginBtn.size() > 0) {
                loginBtn.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                LogUtils.d("$$$ 开始登录");
            }
        }
    }

    private void closeWebAlert() {

        List<AccessibilityNodeInfo> closeBtn = findNodeById(Constant.CLOSE_BTN);
        if (closeBtn != null && closeBtn.size() > 0) {
            LogUtils.d("$$$ 发现web Alert 执行关闭操作");
            AccessibilityNodeInfo button = closeBtn.get(0);
            button.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        } else {
            LogUtils.d("$$$ 未发现web Alert");
        }
    }

    public List<AccessibilityNodeInfo> findNodeById(final String id) {
        AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();

        if (rootInActiveWindow == null) {
            LogUtils.d("$$$ 未能正常获取rootActivateWindow");
            return new ArrayList<>();
        }

        try {
            return rootInActiveWindow.findAccessibilityNodeInfosByViewId(id);
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public void setText(AccessibilityNodeInfo node, String s) {
        Bundle phoneNumber = new Bundle();
        phoneNumber.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, s);
        node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, phoneNumber);
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
        //这里可以设置动态属性
        AccessibilityServiceInfo serviceInfo = new AccessibilityServiceInfo();
        serviceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
        serviceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
        serviceInfo.notificationTimeout = 100;
        setServiceInfo(serviceInfo);
    }
}
