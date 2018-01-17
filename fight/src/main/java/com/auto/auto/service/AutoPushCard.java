package com.auto.auto.service;

import android.accessibilityservice.AccessibilityService;
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


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

        int eventType = accessibilityEvent.getEventType();
        String packageName = accessibilityEvent.getPackageName().toString();
        if (Operation.isInCheckInDuration()) {

            if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && Constant.DING_PACKAGE_NAME.equals(packageName)) {
                autoLogin();
                closeWebAlert();
                closeUpdateDilog();
                openCheckPage(true);
            }
        }

        if (Operation.isInCheckOutDuration()) {

            if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && Constant.DING_PACKAGE_NAME.equals(packageName)) {
                autoLogin();
                closeUpdateDilog();
                openCheckPage(false);
            }
        }
    }

    private boolean isNetworkDown(final AccessibilityNodeInfo nodeInfo) {

        try {
            CharSequence result = nodeInfo.getChild(0).getChild(0).getChild(0).getContentDescription();
            if (result.equals("当前网络环境不稳定，可以尝试使用WiFi网络或点此进入极简模式")) {
                backToRefresh();
                return true;
            }
        } catch (Exception e) {
            return false;
        }

        return false;
    }

    private void backToRefresh() {
        List<AccessibilityNodeInfo> backButton = findNodeById(Constant.BACK_LAYOUT);
        if (backButton.size() > 0) {
            backButton.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
            LogUtils.d("$$$ 刷新页面");
        }
    }

    private boolean isAutoCheckInSuccess(AccessibilityNodeInfo nodeInfo) {

        LogUtils.d(" $$$ 正在检索上班打卡信息");

        try {
            CharSequence result = nodeInfo.getChild(0).getChild(0).getChild(4).getChild(0).getChild(3).getContentDescription();

            LogUtils.d(String.format("$$$ 打卡 %s", result));
            if (result.equals("正常")) {
                Account.setIsCheckInToday(true, AutoPushCard.this);
                Operation.sendEmailWithAttachment(AutoPushCard.this);
            }
            Operation.backToHome(AutoPushCard.this);
            return true;

        } catch (Exception e) {
            LogUtils.d("$$$ 未打卡");
            return false;
        }
    }

    private void findAndClickCheckInButton(final AccessibilityNodeInfo nodeInfo) {

        LogUtils.d(" $$$ 正在搜寻「上班」打卡按钮");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //打卡按钮的点击触发事件不稳定
                    AccessibilityNodeInfo firstNode = nodeInfo.getChild(0).getChild(0).getChild(3).getChild(0).getChild(1);
                    boolean isClick = firstNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);

                    LogUtils.d("$$$ 点击了上班打卡按钮" + isClick);
                } catch (Exception e) {
                    LogUtils.d("$$$ 未找到上班打卡按钮");
                }
            }
        }).start();
    }

    private void findAndClickCheckoutBtn(final AccessibilityNodeInfo nodeInfo) {

        LogUtils.d(" $$$ 正在搜寻「下班」打卡按钮");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AccessibilityNodeInfo checkOutNode = nodeInfo.getChild(0).getChild(0).getChild(3).getChild(1).getChild(1).getParent();
                    boolean isClick = checkOutNode.performAction(AccessibilityNodeInfo.ACTION_CLICK);

                    LogUtils.d("$$$ 点击了下班打卡按钮" + isClick);
                } catch (Exception e) {
                    LogUtils.d("$$$ 未找到下班打卡按钮");
                }
            }
        }).start();
    }

    private boolean isCheckOutSuccess(AccessibilityNodeInfo nodeInfo) {

        try {
            AccessibilityNodeInfo parentNode = nodeInfo.getChild(0).getChild(0);
            if (parentNode.getChildCount() == 6) {
                if (parentNode.getChild(5).getClassName().equals("android.app.Dialog")) {
                    Account.setIsCheckOutToday(true, AutoPushCard.this);
                    LogUtils.d(" $$$ 自动下班打卡成功");
                    return true;
                }
            }
            return false;

        } catch (Exception exception) {
            return false;
        }
    }

    /**
     * 打开打卡页
     */
    private void openCheckPage(boolean isCheckIn) {

        if (isCheckIn) {
            if (Account.isCheckInToday(this)) {
                return;
            }
        } else {
            if (Account.isCheckOutToady(this)) {
                return;
            }
        }

        List<AccessibilityNodeInfo> bottomTab = findNodeById(Constant.BOTTOM_TAB_LAYOUT);
        if (bottomTab.size() > 0) {
            List<AccessibilityNodeInfo> toWorkPageButton = bottomTab.get(0).findAccessibilityNodeInfosByViewId(Constant.TAB_FOR_WORK);
            if (toWorkPageButton.size() > 0) {
                toWorkPageButton.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
                clickCheckItem(isCheckIn);

                recycleNodes(toWorkPageButton);
            }

            recycleNodes(bottomTab);
        } else {

            //判断是否已经在打卡页面内
            List<AccessibilityNodeInfo> titles = findNodeById(Constant.TITLE);
            if (titles != null && titles.size() > 0) {

                CharSequence title = titles.get(0).getText();
                if (title != null && title.equals("新大陆支付-云端支付-系统研发部")) {
                    LogUtils.d("$$$ 已经进入打卡页面");
                    startToCheck(isCheckIn);
                }

                recycleNodes(titles);
            }
        }
    }

    private void startCheckInProcessNew() {

        for (int i = 0; i < 15; i++) {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            AccessibilityNodeInfo nodeInfo = null;

            List<AccessibilityNodeInfo> webList = findNodeById(Constant.WEB_VIEW);
            if (webList != null && webList.size() > 0) {
                nodeInfo = webList.get(0);

                //回收nodeInfo。第一个元素流程结束时回收，这里仅回收第二个及以后的元素。
                if (webList.size() > 1) {
                    for (int j = 1; j < webList.size(); j++) {
                        webList.get(j).recycle();
                    }
                }
            }

            //判断网络挂掉
            if (isNetworkDown(nodeInfo)) {
                return;
            }

            //判断是否已经极速打卡
            if (isAutoCheckInSuccess(nodeInfo)) {

                if (nodeInfo != null) {
                    nodeInfo.recycle();
                }

                return;
            } else {
                findAndClickCheckInButton(nodeInfo);
            }

            //遍历15次仍未打上卡，则回退刷新页面
            if (i == 14) {

                if (nodeInfo != null) {
                    nodeInfo.recycle();
                }
                backToRefresh();
            }
        }
    }

    private void startCheckOutProcessNew() {
        for (int i = 0; i < 15; i++) {

            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            AccessibilityNodeInfo nodeInfo = null;

            List<AccessibilityNodeInfo> webList = findNodeById(Constant.WEB_VIEW);
            if (webList != null && webList.size() > 0) {
                nodeInfo = webList.get(0);

                //回收nodeInfo。第一个元素流程结束时回收，这里仅回收第二个及以后的元素。
                if (webList.size() > 1) {
                    for (int j = 1; j < webList.size(); j++) {
                        webList.get(j).recycle();
                    }
                }
            }

            //判断是否已经打上下班卡
            if (isCheckOutSuccess(nodeInfo)) {

                if (nodeInfo != null) {
                    nodeInfo.recycle();
                }
                return;
            } else {
                findAndClickCheckoutBtn(nodeInfo);
            }

            //遍历15次仍未打上卡，则回退刷新页面
            if (i == 14) {

                if (nodeInfo != null) {
                    nodeInfo.recycle();
                }
                backToRefresh();
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

    private void clickCheckItem(boolean isCheckIn) {
        List<AccessibilityNodeInfo> workLayouts = findNodeById(Constant.WORK_LAYOUT);

        if (workLayouts.size() > 0) {

            List<AccessibilityNodeInfo> items = workLayouts.get(0).findAccessibilityNodeInfosByViewId(Constant.WORK_LAYOUT_ITEM);
            if (items.size() > 0) {

                for (AccessibilityNodeInfo info : items) {

                    List<AccessibilityNodeInfo> titleItems = info.findAccessibilityNodeInfosByViewId(Constant.WORK_ITEM_TITLE);
                    if (titleItems.size() > 0 && titleItems.get(0).getText().equals(Constant.WORK_CHECK_TEXT)) {
                        info.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                        startToCheck(isCheckIn);

                        recycleNodes(titleItems);
                        recycleNodes(items);
                        return;
                    }
                }
            }

            recycleNodes(workLayouts);
        }
    }

    private void startToCheck(boolean isCheckIn) {
        if (isCheckIn) {
            startCheckInProcessNew();
        } else {
            startCheckOutProcessNew();
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

        //在登录界面 需要登录钉钉
        List<AccessibilityNodeInfo> phoneEt = findNodeById(Constant.LOGIN_PHONE_EDITTEXT);
        List<AccessibilityNodeInfo> pwdEt = findNodeById(Constant.LOGIN_PASSWROD_EDITTEXT);
        List<AccessibilityNodeInfo> loginBtn = findNodeById(Constant.LOGIN_BTN);
        Account account = Account.getAccountInfo(this);

        if (phoneEt.size() > 0) {
            AccessibilityNodeInfo phone = phoneEt.get(0);
            phone.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            setText(phone, account.getPhoneNum());
            LogUtils.d("$$$ 完成输入账户信息");
        }

        if (pwdEt.size() > 0) {
            AccessibilityNodeInfo password = pwdEt.get(0);//设置登录密码
            setText(password, account.getDingDingPassword());
            LogUtils.d("$$$ 完成输入密码");
        }


        if (loginBtn.size() > 0) {
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
            recycleNodes(closeBtn);
        }
    }

    private void closeUpdateDilog() {
        List<AccessibilityNodeInfo> closeBtn = findNodeById(Constant.CLOSE_DIALOG);
        if (closeBtn != null && closeBtn.size() > 0) {
            LogUtils.d("$$$ 发现更新提醒，执行关闭操作");
            AccessibilityNodeInfo button = closeBtn.get(0);
            button.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            recycleNodes(closeBtn);
        }
    }

    private void recycleNodes(List<AccessibilityNodeInfo> nodeInfos) {

        if (nodeInfos != null && nodeInfos.size() > 0) {
            for (AccessibilityNodeInfo node : nodeInfos) {
                node.recycle();
            }
        }
    }

    public List<AccessibilityNodeInfo> findNodeById(final String id) {
        AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();

        if (rootInActiveWindow == null) {
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
        Bundle text = new Bundle();
        text.putCharSequence(AccessibilityNodeInfo.ACTION_ARGUMENT_SET_TEXT_CHARSEQUENCE, s);
        node.performAction(AccessibilityNodeInfo.ACTION_SET_TEXT, text);
    }

    @Override
    public void onInterrupt() {
    }

    @Override
    protected void onServiceConnected() {
        super.onServiceConnected();
//        //这里可以设置动态属性
//        AccessibilityServiceInfo serviceInfo = new AccessibilityServiceInfo();
//        serviceInfo.eventTypes = AccessibilityEvent.TYPES_ALL_MASK;
//        serviceInfo.feedbackType = AccessibilityServiceInfo.FEEDBACK_GENERIC;
//        serviceInfo.notificationTimeout = 100;
//        serviceInfo.flags = AccessibilityServiceInfo.FLAG_REQUEST_ENHANCED_WEB_ACCESSIBILITY;
//        setServiceInfo(serviceInfo);
    }
}
