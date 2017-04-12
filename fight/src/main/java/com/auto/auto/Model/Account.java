package com.auto.auto.Model;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Base64;

import com.auto.auto.Constant;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Account implements Serializable {

    @Override
    public String toString() {

        String account = "";
        account += "account :" + phoneNum + "\n";
        account += "dingDingPassword" + dingDingPassword + "\n";
        account += "authAccount" + authAccount + "\n";
        account += "authAccountPassword" + authAccountPassword + "\n";
        account += "email" + mail + "\n";
        return account;
    }

    public String getPhoneNum() {
        return phoneNum;
    }

    public void setPhoneNum(String phoneNum) {
        this.phoneNum = phoneNum;
    }

    public String getDingDingPassword() {
        return dingDingPassword;
    }

    public void setDingDingPassword(String dingDingPassword) {
        this.dingDingPassword = dingDingPassword;
    }

    public String getAuthAccount() {
        return authAccount;
    }

    public void setAuthAccount(String authAccount) {
        this.authAccount = authAccount;
    }

    public String getAuthAccountPassword() {
        return authAccountPassword;
    }

    public void setAuthAccountPassword(String authAccountPassword) {
        this.authAccountPassword = authAccountPassword;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public static boolean isCheckInToday(Context context) {
        Account account = getAccountInfo(context);
        return account.isCheckInToday();
    }

    public static void setIsCheckInToday(boolean isCheckInToday, Context context) {
        Account account = getAccountInfo(context);
        account.setCheckInToday(isCheckInToday);
        account.saveAccountInfo(account, context);
    }

    public static void clearAccount(Context context) {
        Account account = getAccountInfo(context);
        account.saveAccountInfo(new Account(), context);
    }

    private void setCheckInToday(boolean checkInToday) {
        isCheckInToday = checkInToday;
    }

    private boolean isCheckInToday() {
        return isCheckInToday;
    }

    public boolean hasAlreadySavedLoginInfo() {
        return (!phoneNum.isEmpty() && !dingDingPassword.isEmpty() && !authAccount.isEmpty() && !authAccountPassword.isEmpty() && !mail.isEmpty());
    }

    public void saveAccountInfo(Account account, Context context) {

        SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.SHARE_PREFERENCE, Context.MODE_PRIVATE);
        saveData(account, sharedPreferences);
    }

    public static Account getAccountInfo(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences(Constant.SHARE_PREFERENCE, Context.MODE_PRIVATE);
        return getData(sharedPreferences);
    }

    private void saveData(Account account, SharedPreferences sharedPreferences) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try {   //Device为自定义类
            // 创建对象输出流，并封装字节流
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            // 将对象写入字节流
            oos.writeObject(account);
            // 将字节流编码成base64的字符串
            String oAuth_Base64 = new String(Base64.encode(baos.toByteArray(), Base64.DEFAULT));

            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putString(Constant.ACCOUNT, oAuth_Base64);
            editor.apply();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static Account getData(SharedPreferences sharedPreferences) {

        Account account = new Account();
        String productBase64 = sharedPreferences.getString(Constant.ACCOUNT, "");

        // 读取字节
        byte[] base64 = Base64.decode(productBase64.getBytes(), Base64.DEFAULT);

        // 封装到字节流
        ByteArrayInputStream bais = new ByteArrayInputStream(base64);
        try {
            // 再次封装
            ObjectInputStream bis = new ObjectInputStream(bais);

            // 读取对象
            account = (Account) bis.readObject();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return account;
    }

    private String phoneNum = "";
    private String dingDingPassword = "";
    private String authAccount = "";
    private String mail = "";
    private String authAccountPassword = "";
    private boolean isCheckInToday = false;
}
