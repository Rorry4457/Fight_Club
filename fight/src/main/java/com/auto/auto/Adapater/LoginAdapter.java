package com.auto.auto.Adapater;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.auto.auto.Model.LoginItem;
import com.auto.auto.Model.LoginViewHolder;
import com.auto.auto.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rorry on 2017/4/5.
 */

public class LoginAdapter extends RecyclerView.Adapter<LoginViewHolder> {

    private List<LoginItem> itemList = new ArrayList<>();

    public LoginAdapter(List<LoginItem> itemList) {
        this.itemList = itemList;
    }

    @Override
    public LoginViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.login_item, parent, false);
        return new LoginViewHolder(view, parent.getContext());
    }

    @Override
    public void onBindViewHolder(LoginViewHolder holder, int position) {

        LoginItem item = itemList.get(position);

        holder.setLoginItem(item);
        holder.loadItemInfo();
    }

    @Override
    public int getItemCount() {
        return itemList.size();
    }

}
