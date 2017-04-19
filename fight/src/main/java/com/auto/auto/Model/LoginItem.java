package com.auto.auto.Model;

public class LoginItem {

    public String getLeftText() {
        return leftText;
    }

    public String getRightText() {
        return rightText;
    }

    private String leftText = "";
    private String rightText = "";

    public LoginItem(String leftText, String rightText) {
        this.leftText = leftText;
        this.rightText = rightText;
    }
}
