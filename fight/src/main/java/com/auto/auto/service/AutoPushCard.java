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
        if (Operation.isInWorkingDuration()) {

            if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && Constant.DING_PACKAGE_NAME.equals(packageName)) {
                autoLogin();
                closeWebAlert();
            } else if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED && Constant.DING_PACKAGE_NAME.equals(packageName)) {
                openWorkNotificationPage();
                //界面的切换会多次调用，在这里进行是否打卡成功的检测，比较妥当
                isAlreadyCheckIn();
            }
        }

        if (Operation.isInCheckOutDuration()) {

            if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && Constant.DING_PACKAGE_NAME.equals(packageName)) {
                openPageForCheckOut();
            }
        }
    }

    private void openPageForCheckOut() {
        List<AccessibilityNodeInfo> bottomTab = findNodeById(Constant.BOTTOM_TAB_LAYOUT);
        if (bottomTab.size() > 0) {
            List<AccessibilityNodeInfo> tableLayout = findNodeById(Constant.MAIN_TABLE_VIEW);
            if (tableLayout.size() > 0) {
                List<AccessibilityNodeInfo> items = tableLayout.get(0).findAccessibilityNodeInfosByText(Constant.DEPARTMENT);
                if (items.size() > 0) {
                    items.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                }
            }
        }

        List<AccessibilityNodeInfo> items = findNodeById(Constant.LIST_ITEM);
        if (items.size() > 0 && !isAlreadyOpenCheckOut) {
            isAlreadyOpenCheckOut = true;
            items.get(items.size() - 1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
    }

    private void openWorkNotificationPage() {
        if (Account.isCheckInToday(this)) {
            return;
        }

        List<AccessibilityNodeInfo> bottmeTab = findNodeById(Constant.BOTTOM_TAB_LAYOUT);
        if (bottmeTab.size() > 0) {
            List<AccessibilityNodeInfo> tableLayout = findNodeById(Constant.MAIN_TABLE_VIEW);
            if (tableLayout.size() > 0) {
                List<AccessibilityNodeInfo> items = tableLayout.get(0).findAccessibilityNodeInfosByText(Constant.DEPARTMENT);
                if (items.size() > 0) {
                    items.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    LogUtils.d("$$$ 打开工作通知页");
                }
            }
        }
    }

    private void isAlreadyCheckIn() {

        if (Account.isCheckInToday(this)) {
            return;
        }

        List<AccessibilityNodeInfo> titleView = findNodeById(Constant.ALL_VIEW_TITLE);
        if (titleView.size() > 0) {
            String title = titleView.get(0).getText().toString();

            if (title.equals(Constant.DEPARTMENT)) {
                LogUtils.d("$$$ 打开工作通知页，开始检测是否打卡成功");
                List<AccessibilityNodeInfo> listView = findNodeById(Constant.BODY_TITLE);
                int size = listView.size();
                if (size > 0) {
                    AccessibilityNodeInfo info = listView.get(size - 1);
                    String checkInfo = info.getText().toString();
                    LogUtils.d("$$$ 获取到的打卡情况信息为： " + checkInfo);

                    if (checkInfo.contains(Constant.SUCCESS)) {
                        String dateString = checkInfo.substring(0, checkInfo.indexOf(" "));
                        if (Operation.isToday(dateString)) {
                            LogUtils.d("$$$ 今天极速打卡成功");
                            Account.setIsCheckInToday(true, this);
                            Operation.sendSuccessEmail(this);
                        } else {
                            LogUtils.d("$$$ 获取到的打卡信息 不是今天的信息 将尝试重新获取");
                            return;
                        }
                    } else if (checkInfo.contains(Constant.FAIL)) {
                        LogUtils.d("$$$ 极速打卡未成功");
                        Operation.sendFailEmail(this);
                    } else {
                        LogUtils.d("$$$ 检测到未知的事件类型" + checkInfo);
                    }
                    Operation.backToHome(this);
                }
            }
        }
    }

    private void autoLogin() {

        List<AccessibilityNodeInfo> loginLayout = findNodeById(Constant.LOGIN_LAYOUT);
        if (loginLayout != null && loginLayout.size() > 0 && !isLoginOperate) {
            isLoginOperate = true;
            //在登录界面 需要登录钉钉
            LogUtils.d("$$$ 需要登录  找到登录界面的控件 :");

            List<AccessibilityNodeInfo> phoneEt = findNodeById(Constant.LOGIN_PHONE_EDITTEXT);
            List<AccessibilityNodeInfo> pwdEt = findNodeById(Constant.LOGIN_PASSWROD_EDITTEXT);
            List<AccessibilityNodeInfo> loginBtn = findNodeById(Constant.LOGIN_BTN);
            Account account = Account.getAccountInfo(this);

            AccessibilityNodeInfo phone = phoneEt.get(0);
            phone.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            setText(phone, account.getPhoneNum());


            AccessibilityNodeInfo password = pwdEt.get(0);//设置登录密码
            setText(password, account.getDingDingPassword());

            LogUtils.d("$$$ 完成输入账户信息");
            loginBtn.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            LogUtils.d("$$$ 开始登录");
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
