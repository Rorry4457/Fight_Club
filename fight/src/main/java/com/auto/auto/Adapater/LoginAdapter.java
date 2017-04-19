package com.auto.auto.Adapater;

import android.support.v7.widget.RecyclerView;

import com.auto.auto.Model.LoginItem;
import com.auto.auto.Model.LoginViewHolder;
import com.auto.auto.R;
import com.chad.library.adapter.base.BaseItemDraggableAdapter;

import java.util.List;

public class LoginAdapter extends BaseItemDraggableAdapter<LoginItem, LoginViewHolder> {

    public LoginAdapter(List<LoginItem> data) {
        super(R.layout.login_item, data);
    }

    @Override
    public void onItemSwiped(RecyclerView.ViewHolder viewHolder) {
        super.onItemSwiped(viewHolder);
    }

    @Override
    protected void convert(LoginViewHolder loginViewHolder, LoginItem loginItem) {

        int position = loginViewHolder.getAdapterPosition();

        loginViewHolder.setLoginItem(loginItem);
        loginViewHolder.loadItemInfo(position,mContext);
    }
}
