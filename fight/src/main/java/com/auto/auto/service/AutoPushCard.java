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
    boolean isAlreadyOpenCheckPage = false;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

        int eventType = accessibilityEvent.getEventType();
        String packageName = accessibilityEvent.getPackageName().toString();
        if (Operation.isInCheckInDuration()) {

            if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && Constant.DING_PACKAGE_NAME.equals(packageName)) {
                autoLogin();
                closeWebAlert();
                openCheckPage();
            } else if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED && Constant.DING_PACKAGE_NAME.equals(packageName)) {
                startCheckInProcess();
            }
        }

        if (Operation.isInCheckOutDuration()) {

            if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && Constant.DING_PACKAGE_NAME.equals(packageName)) {
                autoLogin();
                openCheckPage();
            }

            if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED && Constant.DING_PACKAGE_NAME.equals(packageName)) {
                findAndClickCheckoutBtn();
            }
        }
    }

    private void startCheckInProcess() {
        if (Account.isCheckInToday(this)) {
            return;
        }

        isAutoCheckInSuccess();
    }

    private void isAutoCheckInSuccess() {
        List<AccessibilityNodeInfo> webList = findNodeById(Constant.WEB_VIEW);
        if (webList != null && webList.size() > 0) {
            final AccessibilityNodeInfo nodeInfo = webList.get(0);
            LogUtils.d(" $$$ 打开打卡页面，正在检索上班打卡信息");

            try {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            CharSequence result = nodeInfo.getChild(0).getChild(0).getChild(2).getChild(0).getChild(3).getContentDescription();
                            LogUtils.d(String.format("$$$ 打卡 %s", result));
                            if (result.equals("正常")) {

                                Account.setIsCheckInToday(true, AutoPushCard.this);
                                Operation.sendEmailWithAttachment(AutoPushCard.this);
                                Operation.backToHome(AutoPushCard.this);
                            }


                        } catch (Exception e) {
                            e.printStackTrace();
                            LogUtils.d("$$$ 未打卡");

                            findAndClickCheckInButton();
                        }
                    }
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void findAndClickCheckInButton() {

        List<AccessibilityNodeInfo> webList = findNodeById(Constant.WEB_VIEW);
        if (webList != null && webList.size() > 0) {
            final AccessibilityNodeInfo nodeInfo = webList.get(0);
            LogUtils.d(" $$$ 打开打卡页面，正在尝试搜寻「上班」打卡按钮");

            try {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            LogUtils.d(" $$$ 点击打卡按钮");
                            //打卡按钮的点击触发事件不稳定
                            nodeInfo.getChild(0).getChild(0).getChild(2).getChild(0).getChild(1).performAction(AccessibilityNodeInfo.ACTION_CLICK);

                            // TODO: 2018/1/2 关闭打卡alert
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void findAndClickCheckoutBtn() {
        List<AccessibilityNodeInfo> webList = findNodeById(Constant.WEB_VIEW);
        if (webList != null && webList.size() > 0) {
            final AccessibilityNodeInfo webNode = webList.get(0);
            LogUtils.d(" $$$ 打开打卡页面，正在尝试搜寻「下班」打卡按钮");
            try {
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            webNode.getChild(0).getChild(0).getChild(2).getChild(1).getChild(1).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }).start();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 打开打卡页
     */
    private void openCheckPage() {

        if (isAlreadyOpenCheckPage) {
            return;
        }

        List<AccessibilityNodeInfo> bottomTab = findNodeById(Constant.BOTTOM_TAB_LAYOUT);
        if (bottomTab.size() > 0) {
            List<AccessibilityNodeInfo> toWorkPageButton = bottomTab.get(0).findAccessibilityNodeInfosByViewId(Constant.TAB_FOR_WORK);
            if (toWorkPageButton.size() > 0) {
                toWorkPageButton.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                clickCheckItem();
            }
        } else {
            List<AccessibilityNodeInfo> backButton = findNodeById(Constant.BACK_BUTTON);
            if (backButton.size() > 0) {
                LogUtils.d("$$$ 下班打卡未发现底部tabBar，点击返回按钮");
                backButton.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            }
        }
    }

//    private void startCheckOutProcess() {
//        if (isAlreadyOpenCheckPage) {
//            return;
//        }

//        List<AccessibilityNodeInfo> bottomTab = findNodeById(Constant.BOTTOM_TAB_LAYOUT);
//        if (bottomTab.size() > 0) {
//            List<AccessibilityNodeInfo> toWorkPageButton = bottomTab.get(0).findAccessibilityNodeInfosByViewId(Constant.TAB_FOR_WORK);
//            if (toWorkPageButton.size() > 0) {
//                toWorkPageButton.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                openCheckPage();
//            }
//        } else {
//            LogUtils.d("$$$ 下班打卡未发现底部tabBar，点击返回按钮");
//            List<AccessibilityNodeInfo> backButton = findNodeById(Constant.BACK_BUTTON);
//            if (backButton.size() > 0) {
//                backButton.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
//            }
//        }
//    }

    private void clickCheckItem() {
        List<AccessibilityNodeInfo> workLayouts = findNodeById(Constant.WORK_LAYOUT);

        if (workLayouts.size() > 0) {

            List<AccessibilityNodeInfo> items = workLayouts.get(0).findAccessibilityNodeInfosByViewId(Constant.WORK_LAYOUT_ITEM);
            if (items.size() > 0) {

                for (AccessibilityNodeInfo info : items) {

                    List<AccessibilityNodeInfo> titleItems = info.findAccessibilityNodeInfosByViewId(Constant.WORK_ITEM_TITLE);
                    if (titleItems.size() > 0 && titleItems.get(0).getText().equals(Constant.WORK_CHECK_TEXT)) {
                        isAlreadyOpenCheckPage = true;
                        info.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        return;
                    }
                }
            }
        }
    }

//    private void openWorkNotificationPage() {
//        if (Account.isCheckInToday(this)) {
//            return;
//        }
//
//        List<AccessibilityNodeInfo> bottomTab = findNodeById(Constant.BOTTOM_TAB_LAYOUT);
//        if (bottomTab.size() > 0) {
//            List<AccessibilityNodeInfo> tableLayout = findNodeById(Constant.MAIN_TABLE_VIEW);
//            if (tableLayout.size() > 0) {
//                List<AccessibilityNodeInfo> items = tableLayout.get(0).findAccessibilityNodeInfosByText(Constant.DEPARTMENT);
//                if (items.size() > 0) {
//                    items.get(0).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                    LogUtils.d("$$$ 打开工作通知页");
//
//                    Operation.takeScreenShot(this);
//                    Account.setIsCheckInToday(true, this);
//                } else {
//                    LogUtils.d("$$$ 未能打开工作通知页");
//                }
//            } else {
//                LogUtils.d("$$$ 未发现部门打卡状况cell");
//            }
//        } else {
//            LogUtils.d("$$$ 未发现底部tabBar");
//        }
//    }

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
