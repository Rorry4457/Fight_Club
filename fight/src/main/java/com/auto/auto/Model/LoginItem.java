package com.auto.auto.Model;

public class LoginItem {

    public String getLeftText() {
        return leftText;
    }

    public void setLeftText(String leftText) {
        this.leftText = leftText;
    }

    public String getRightText() {
        return rightText;
    }

    public void setRightText(String rightText) {
        this.rightText = rightText;
    }

    public boolean isOdd() {
        return this.index % 2 != 0;
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    private String leftText = "";
    private String rightText = "";
    private int index;

    public LoginItem(String leftText, String rightText, int index) {
        this.leftText = leftText;
        this.rightText = rightText;
        this.index = index;
    }
}
