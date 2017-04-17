package com.auto.auto.Model;

public class LoginItem {

    String getLeftText() {
        return leftText;
    }

    String getRightText() {
        return rightText;
    }

    private String leftText = "";
    private String rightText = "";

    public LoginItem(String leftText, String rightText) {
        this.leftText = leftText;
        this.rightText = rightText;
    }
}
