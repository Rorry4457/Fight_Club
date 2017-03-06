package com.auto.auto.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.auto.auto.Account;
import com.auto.auto.Constant;
import com.newland.support.nllogger.LogUtils;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by x on 2016/11/1.
 */
public class AutoPushCard extends AccessibilityService {

    public static String TAG = AutoPushCard.class.getSimpleName();
    boolean isLoginOperate = false;
    boolean isSetSchedul = false;
    private static int IN_Y_COORDINATES = 600;
    private static int OUT_Y_COORDINATES = 890;
    private static int CENTER_X_COORDINATES = 360;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

        int eventType = accessibilityEvent.getEventType();
        String packageName = accessibilityEvent.getPackageName().toString();

        if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && Constant.DING_PACKAGE_NAME.equals(packageName)) {
            autoLogin();
        } else if (eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED && Constant.DING_PACKAGE_NAME.equals(packageName)) {
            openCheckInPage();
        } else if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && Constant.SETTING.equals(packageName)) {
//            System.out.println("packageName = " + packageName);
//            openScheduleSetting();
        }
    }

    // TODO: 2017/2/22 逻辑要修正 待下一期需求
    private void openScheduleSetting() {
        if (!isSetSchedul) {
            AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
            if (rootInActiveWindow.getChild(1).getChildCount() > 0) {
                AccessibilityNodeInfo scheduled = rootInActiveWindow.getChild(1).getChild(13);
                scheduled.performAction(AccessibilityNodeInfo.ACTION_CLICK);
                isSetSchedul = true;
            } else {
                LogUtils.d("$$$ 未找到设置定时开关机");
            }
        }
    }

    private void openCheckInPage() {
        if (Account.isCheckInToday(this)) {
            return;
        }

        if (findNodeById(Constant.BOTTOM_TAB_LAYOUT).size() > 0) {
            LogUtils.d("$$$ 滑动tabBar到 工作 ");
            AccessibilityNodeInfo layoutWork = findNodeById(Constant.TAB_BUTTON_WORK).get(0);
            layoutWork.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }

        if (findNodeById(Constant.WORK_LAYOUT).size() > 0) {
            List<AccessibilityNodeInfo> itemList = findNodeById(Constant.WORK_LAYOUT_ITEM);

            // 找出考勤打卡的模块
            for (AccessibilityNodeInfo info : itemList) {
                AccessibilityNodeInfo child = info.getChild(info.getChildCount() - 1);
                if (child.getClassName().equals("android.widget.TextView") && child.getText().equals("考勤打卡")) {
                    LogUtils.d(" 找到了 考勤打卡的 item  点击进入打卡页面");
                    info.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    Account.setIsCheckInToday(true, this);
                    return;
                }
            }
        }
    }

    private void checkIn() {
        System.out.println("AutoPushCard.checkIn");
        String[] checkInOrder = {"input", "tap", "" + CENTER_X_COORDINATES, "" + IN_Y_COORDINATES};

        try {
            new ProcessBuilder(checkInOrder).start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkOut() throws IOException {
        LogUtils.d("AutoPushCard.checkOut");

//        AccessibilityNodeInfo btn = findNodeById(Constant.WEB_VIEW).get(1).getChild(0).getChild(0).getChild(4).getChild(2).getChild(3);
//        System.out.println("btn = " + btn.getContentDescription());
//        boolean success = btn.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//        System.out.println("success = " + success);
//        for (AccessibilityNodeInfo button :
//                buttons) {
//            button.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//            System.out.println("button.getContentDescription() = " + button.getContentDescription());
//        }

//
//        final String checkOutOrder = "input tap " + CENTER_X_COORDINATES + " " + OUT_Y_COORDINATES;
//        System.out.println("checkOutOrder = " + checkOutOrder);
//
//        ShellUtils.CommandResult result = ShellUtils.execCommand(checkOutOrder, false);
//        System.out.println("result = " + result.errorMsg);
//        Runtime.getRuntime().exec(checkOutOrder);


//        new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    Thread.sleep(5000);
//                } catch (InterruptedException e) {
//                    e.printStackTrace();
//                }
//
//            }
//        }).start();


//        Process p = Runtime.getRuntime().exec(checkOutOrder);
//        String data = null;
//        BufferedReader ie = new BufferedReader(new InputStreamReader(p.getErrorStream()));
//        BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
//        String error = null;
//        while ((error = ie.readLine()) != null
//                && !error.equals("null")) {
//            data += error + "\n";
//        }
//        String line = null;
//        while ((line = in.readLine()) != null
//                && !line.equals("null")) {
//            data += line + "\n";
//        }
//
//        Log.v("ls", data);
//        ShellUtils shellUtils = new ShellUtils();
//        try {
//            Runtime.getRuntime().exec(checkOutOrder);
//            shellUtils.start();
//            shellUtils.execCommand(checkOutOrder);
//            shellUtils.stop();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


    private void waitUntilCheckOut() {
        try {
            String description = findNodeById(Constant.WEB_VIEW).get(1).getChild(0).getChild(0).getChild(4).getChild(2).getChild(3).getContentDescription().toString();
            if (description.equals("下班打卡")) {
                checkOut();
            }
        } catch (Exception e) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    System.out.println("wait 1 second");
                    try {
                        Thread.sleep(1000);
                        waitUntilCheckOut();
                    } catch (InterruptedException e1) {
                        e1.printStackTrace();
                    }

                }
            }).start();
        }
    }

    private void test() {

        System.out.println("AutoPushCard.test");
        try {
            // 到达listView
            AccessibilityNodeInfo checkOut = findNodeById("com.alibaba.android.rimet:id/common_webview").get(1).getChild(0).getChild(0).getChild(4).getChild(2);
            checkOut.getChild(3).getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
//            int count = checkOut.getChildCount();
//            for(int i = 0;i<count;i++){
//                AccessibilityNodeInfo child = checkOut.getChild(i);
//                CharSequence desc = child.getContentDescription();
//                System.out.println("desc = " + desc);
//                boolean result = child.performAction(AccessibilityNodeInfo.ACTION_CLICK);
//                System.out.println("result = " + result);
//                System.out.println("i = " + i);
//            }
        } catch (Exception e) {
            e.printStackTrace();
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


    public List<AccessibilityNodeInfo> findNodeById(String id) {
        AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
        try {
            return rootInActiveWindow.findAccessibilityNodeInfosByViewId(id);
        } catch (Exception e) {
            e.printStackTrace();
            LogUtils.e(e);
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
