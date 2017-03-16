package com.auto.auto;

/**
 * Created by x on 2016/11/3.
 */
public class Constant {

    public static final String DING_PACKAGE_NAME = "com.alibaba.android.rimet"; //钉钉的包名
    // 钉钉登录页面
    public static final String LOGIN_LAYOUT = "com.alibaba.android.rimet:id/sign_up_with_pwd_top_rl"; //登录界面root id
    public static final String LOGIN_PHONE_EDITTEXT = "com.alibaba.android.rimet:id/et_phone_input"; // 登录界面 手机号码输入框
    public static final String LOGIN_PASSWROD_EDITTEXT = "com.alibaba.android.rimet:id/et_pwd_login"; // 登录界面 手机号码输入框
    public static final String LOGIN_BTN = "com.alibaba.android.rimet:id/btn_next"; // 登录界面 登录按钮
    public static final String WEB_VIEW = "com.alibaba.android.rimet:id/common_webview";

    // 钉钉主页

    //钉钉主页tab
    public static final String BOTTOM_TAB_LAYOUT = "com.alibaba.android.rimet:id/bottom_tab"; // 底部导航按钮
    public static final String TAB_BUTTON_WORK = "com.alibaba.android.rimet:id/home_bottom_tab_button_work"; // 底部考勤打卡按钮
    public static final String MAIN_TABLE_VIEW = "com.alibaba.android.rimet:id/session_list";//首页列表
    public static final String ALL_VIEW_TITLE = "com.alibaba.android.rimet:id/tv_title";
    public static final String BODY_TITLE = "com.alibaba.android.rimet:id/tv_body_title";
    public static final String DEPARTMENT = "工作通知:新大陆支付-云端支付-系统研发部";//首页列表Item标题

    // 考勤打卡模块
    public static final String WORK_LAYOUT = "com.alibaba.android.rimet:id/oa_fragment_gridview"; // 考勤打卡模块fragment layout id
    public static final String WORK_LAYOUT_ITEM = "com.alibaba.android.rimet:id/oa_entry_inner_layout"; // 考勤打卡模块每一个小块的名称

    //key
    public static final String ACCOUNT = "com.fight.club.account";
    public static final String SETTING = "com.android.settings";
    public static final String NET_AUTH_USER = "username";
    public static final String NET_AUTH_PASSWORD = "password";
    public static final String NET_AUTH_PWD = "pwd";
    public static final String NET_AUTH_REMBER = "rememberPwd";
    public static final String NET_AUTH_SECRET = "secret";

    //sharePreference
    public static final String SHARE_PREFERENCE = "com.login.share.preference";

    //authWeb Address
    public static final String AUTH_ADDREDD = "http://192.168.30.111/webAuth/";

    //Time
    public static final int HOUR_OF_CHECK_OUT = 17;
    public static final int MINUTE_OF_CHECK_OUT = 45;

    //RequestCode
    public static final int CHECK_IN = 1001;
    public static final int CHECK_OUT = 1002;

    //
    public static final String SUCCESS = "上班极速打卡成功";
}
