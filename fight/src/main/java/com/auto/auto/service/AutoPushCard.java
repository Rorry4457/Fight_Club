package com.auto.auto.service;

import android.accessibilityservice.AccessibilityService;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.accessibility.AccessibilityEvent;
import android.view.accessibility.AccessibilityNodeInfo;

import com.auto.auto.Account;
import com.auto.auto.Constant;
import com.auto.auto.Operation;
import com.newland.support.nllogger.LogUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by x on 2016/11/1.
 */
public class AutoPushCard extends AccessibilityService {

    boolean isLoginOperate = false;
    boolean isSetSchedul = false;

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onAccessibilityEvent(AccessibilityEvent accessibilityEvent) {

        if (!Operation.isInWorkingDuration()) {
            return;
        }

        int eventType = accessibilityEvent.getEventType();
        String packageName = accessibilityEvent.getPackageName().toString();

        if (eventType == AccessibilityEvent.TYPE_WINDOW_STATE_CHANGED && Constant.DING_PACKAGE_NAME.equals(packageName)) {
            autoLogin();
        } else if (eventType == AccessibilityEvent.TYPE_WINDOW_CONTENT_CHANGED && Constant.DING_PACKAGE_NAME.equals(packageName)) {
            openWorkNotificationPage();
            //界面的切换回多次调用，在这里进行是都打卡成功的检测，比较妥当
            isAlreadyCheckIn();
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

    //此方法经验证可行，但不是最优方案，切换到另一方案，暂不删除
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
                    LogUtils.d("$$$ 找到了 考勤打卡的 item  点击进入打卡页面");
                    info.getParent().performAction(AccessibilityNodeInfo.ACTION_CLICK);
                    Account.setIsCheckInToday(true, this);

                    waitAndCheck(new Runnable() {
                        @Override
                        public void run() {
                            if (isCheckFinished()) {
                                Operation.sendSuccessEmail(AutoPushCard.this);
                            }
                        }
                    });
                    return;
                }
            }
        }
    }

    private void waitAndCheck(final Runnable runnable) {

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    LogUtils.d("$$$ 开始等待页面加载");
                    Thread.sleep(10000);
                    Handler uiHandler = new Handler(Looper.getMainLooper());
                    uiHandler.post(runnable);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }

    private boolean isCheckFinished() {
        LogUtils.d("$$$ 开始检测是否已经打卡");
        try {
            String description = findNodeById(Constant.WEB_VIEW).get(0).getChild(0).getChild(0).getChild(0).getChild(4).getChild(1).getChild(3).getChild(0).getContentDescription().toString();
            if (description.equals("正常")) {
                LogUtils.d("$$$ 检测到已经打卡");
                return true;
            }
        } catch (Exception e) {
            LogUtils.e(e);
        }

        return false;
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
