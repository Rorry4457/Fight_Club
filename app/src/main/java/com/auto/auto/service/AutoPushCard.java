package com.auto.auto.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.auto.auto.Constant;

import java.util.List;

/**
 * Created by x on 2016/11/1.
 */
public class AutoPushCard extends AccessibilityService {

    public static String TAG = AutoPushCard.class.getSimpleName();
    boolean isLoginOperate = false;
    boolean isSetSchedul = false;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

        int eventType = accessibilityEvent.getEventType();
        String packageName = accessibilityEvent.getPackageName().toString();

        if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && Constant.DING_PACKAGE_NAME.equals(packageName)) {
            autoLoginOrScroll();
        } else if (eventType == AccessibilityEvent.TYPE_VIEW_SCROLLED) {
            try {
                findAndCheckIn();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && Constant.SETTING.equals(packageName)) {
            openScheduleSetting();
        }
    }

    // TODO: 2017/2/22 逻辑要修正 待下一期需求
    private void openScheduleSetting() {
        if (!isSetSchedul) {
            AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
            AccessibilityNodeInfo scheduled = rootInActiveWindow.getChild(1).getChild(13);
            scheduled.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            isSetSchedul = true;
        }
    }

    private void findAndCheckIn() {
        //监听页面滑动
        if (findNodeById(Constant.WORK_LAYOUT).size() > 0) {
            Log.d(TAG, " 进入 考勤打卡模块");
            List<AccessibilityNodeInfo> itemList = findNodeById(Constant.WORK_LAYOUT_ITEM);

            // 找出考勤打卡的模块
            for (AccessibilityNodeInfo info : itemList) {
                AccessibilityNodeInfo child = info.getChild(info.getChildCount() - 1);
                if (child.getClassName().equals("android.widget.TextView") && child.getText().equals("考勤打卡")) {
                    Log.d(TAG, " 找到了 考勤打卡的 item  点击进入打卡页面");
                    info.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    break;
                }
            }
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

    private void autoLoginOrScroll() {

        List<AccessibilityNodeInfo> loginLayout = findNodeById(Constant.LOGIN_LAYOUT);
        if (loginLayout != null && loginLayout.size() > 0 && !isLoginOperate) {
            isLoginOperate = true;
            //在登录界面 需要登录钉钉
            Log.d(TAG, "需要登录  找到登录界面的控件 :" + loginLayout.size());

            List<AccessibilityNodeInfo> phoneEt = findNodeById(Constant.LOGIN_PHONE_EDITTEXT);
            List<AccessibilityNodeInfo> pwdEt = findNodeById(Constant.LOGIN_PASSWROD_EDITTEXT);
            List<AccessibilityNodeInfo> loginBtn = findNodeById(Constant.LOGIN_BTN);
            SharedPreferences sharedPreferences = getSharedPreferences(Constant.SHARE_PREFERENCE, MODE_PRIVATE);

            System.out.println("Input login phone number");

            AccessibilityNodeInfo phone = phoneEt.get(0);
            phone.performAction(AccessibilityNodeInfo.ACTION_CLICK);
            setText(phone, sharedPreferences.getString(Constant.PHONE, "110011001100"));

            System.out.println("Input login password");

            AccessibilityNodeInfo password = pwdEt.get(0);//设置登录密码
            setText(password, sharedPreferences.getString(Constant.PASSWORD, "110011001100"));

            System.out.println("Start Login");

            loginBtn.get(0).performAction(AccessibilityNodeInfo.ACTION_CLICK);
        } else if (findNodeById(Constant.BOTTOM_TAB_LAYOUT).size() > 0) {

            System.out.println("Scorll to Portal/工作");

            AccessibilityNodeInfo layoutWork = findNodeById(Constant.TAB_BUTTON_WORK).get(0);
            layoutWork.performAction(AccessibilityNodeInfo.ACTION_CLICK);
        }
    }


    public List<AccessibilityNodeInfo> findNodeById(String id) {
        AccessibilityNodeInfo rootInActiveWindow = getRootInActiveWindow();
        List<AccessibilityNodeInfo> findList = rootInActiveWindow.findAccessibilityNodeInfosByViewId(id);
        return findList;
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
        Log.d(TAG, " onserviceConnected 辅助服务连接成功");
    }
}
