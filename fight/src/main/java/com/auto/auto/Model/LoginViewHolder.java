package com.auto.auto.Model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.auto.auto.R;

public class LoginViewHolder extends RecyclerView.ViewHolder {
    private TextView leftTextView;
    private TextView rightTextView;
    private LoginItem item;
    private Context context;

    public LoginViewHolder(View itemView, Context context) {
        super(itemView);

        leftTextView = (TextView) itemView.findViewById(R.id.left_text_view);
        rightTextView = (TextView) itemView.findViewById(R.id.right_text_view);
        this.context = context;
    }

    public void setLoginItem(LoginItem item) {
        this.item = item;
    }

    public void loadItemInfo() {

        setText();
        modifyLeftTextViewPosition();
        modifyRightTextViewPosition();
        setParentViewBackground();

    }

    private void setParentViewBackground() {

        if (item.isOdd()) {
            itemView.setBackgroundColor(context.getResources().getColor(R.color.light_green));
        } else {
            itemView.setBackgroundColor(context.getResources().getColor(R.color.dark_green));
        }
    }

    private void setText() {

        String leftText = item.getLeftText();
        leftTextView.setText(leftText);
        setTextColor(leftTextView);

        String rightText = item.getRightText();
        rightTextView.setText(rightText);
        setTextColor(rightTextView);
    }

    private void setTextColor(TextView textView) {
        if (item.isOdd()) {
            textView.setTextColor(context.getResources().getColor(R.color.light_red));
        } else {
            textView.setTextColor(context.getResources().getColor(R.color.dark_red));
        }
    }

    private void modifyLeftTextViewPosition() {
        modifyPosition(true, leftTextView);
    }

    private void modifyRightTextViewPosition() {
        modifyPosition(false, rightTextView);
    }

    private void modifyPosition(boolean isLeft, TextView textView) {

        TextPaint paint = textView.getPaint();
        int dif = (int) (paint.measureText((String) textView.getText()) / 2);

        RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) textView.getLayoutParams();
        if (layoutParams != null) {

            if (isLeft) {
                layoutParams.setMargins(-dif, layoutParams.topMargin, layoutParams.rightMargin, layoutParams.bottomMargin);
            } else {
                layoutParams.setMargins(layoutParams.leftMargin, layoutParams.topMargin, -dif, layoutParams.bottomMargin);
            }
        }
    }
}
