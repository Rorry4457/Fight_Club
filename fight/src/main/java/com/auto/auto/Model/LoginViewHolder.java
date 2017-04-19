package com.auto.auto.Model;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.auto.auto.R;
import com.chad.library.adapter.base.BaseViewHolder;

public class LoginViewHolder extends BaseViewHolder {
    private TextView leftTextView;
    private TextView rightTextView;

    private LoginItem item;

    public LoginViewHolder(View itemView) {
        super(itemView);

        leftTextView = getView(R.id.left_text_view);
        rightTextView = getView(R.id.right_text_view);
    }

    public void setLoginItem(LoginItem item) {
        this.item = item;
    }

    public LoginItem getItem() {
        return item;
    }

    public void loadItemInfo(int index, Context context) {

        setText(index, context);
        modifyLeftTextViewPosition();
        modifyRightTextViewPosition();
        setViewHolderBackground(index,context);
    }

    private void setViewHolderBackground(int index, Context context) {

        if (index % 2 == 0) {
            itemView.setBackgroundColor(context.getResources().getColor(R.color.light_green));
        } else {
            itemView.setBackgroundColor(context.getResources().getColor(R.color.dark_green));
        }
    }

    private void setText(int index, Context context) {

        String leftText = item.getLeftText();
        leftTextView.setText(leftText);
        setTextColor(leftTextView, index, context);

        String rightText = item.getRightText();
        rightTextView.setText(rightText);
        setTextColor(rightTextView, index, context);
    }

    private void setTextColor(TextView textView, int index, Context context) {
        if (index % 2 == 0) {
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
