/*
 * Copyright (C) 2015 Krystian Drożdżyński
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.auto.auto.stepperview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import java.util.List;

public class SteppersView extends LinearLayout {

    private RecyclerView recyclerView;
    private com.auto.auto.stepperview.SteppersAdapter steppersAdapter;

    private Config config;
    private List<com.auto.auto.stepperview.SteppersItem> items;

    public SteppersView(Context context) {
        super(context);
    }

    public SteppersView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public SteppersView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public SteppersView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public SteppersView setConfig(Config config) {
        this.config = config;
        return this;
    }

    public SteppersView setItems(List<com.auto.auto.stepperview.SteppersItem> items) {
        this.items = items;
        return this;
    }

    /*public void setPositiveButtonEnable(int position, boolean enable) {
        this.items.get(position).setPositiveButtonEnable(enable);

    }*/

    public void build() {
        if (config != null) {
            setOrientation(LinearLayout.HORIZONTAL);

            //LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            //inflater.inflate(R.layout.steppers_recycle, this, true);
            //recyclerView = (RecyclerView) getChildAt(0);

            recyclerView = new RecyclerView(getContext());
            RecyclerView.LayoutParams layoutParams = new RecyclerView.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            recyclerView.setLayoutParams(layoutParams);

            addView(recyclerView);

            recyclerView.setHasFixedSize(true);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            steppersAdapter = new SteppersAdapter(this, config, items);
            //steppersAdapter.setPossitiveButtonEnable(possitiveButtonEnable);

            recyclerView.setAdapter(steppersAdapter);

        } else {
            throw new RuntimeException("SteppersView need config, read documentation to get more info");
        }
    }

    private boolean possitiveButtonEnable = true;

    public static class Config {

        private OnFinishAction onFinishAction;
        private OnCancelAction onCancelAction;
        private OnChangeStepAction onChangeStepAction;

        private OnContinueAction onContinueAction;

        private FragmentManager fragmentManager;
        public Config() {

        }

        public OnContinueAction getOnContinueAction() {
            return onContinueAction;
        }

        public void setOnContinueAction(OnContinueAction onContinueAction) {
            this.onContinueAction = onContinueAction;
        }

        public Config setOnFinishAction(com.auto.auto.stepperview.OnFinishAction onFinishAction) {
            this.onFinishAction = onFinishAction;
            return this;
        }

        public OnFinishAction getOnFinishAction() {
            return onFinishAction;
        }

        public Config setOnCancelAction(com.auto.auto.stepperview.OnCancelAction onCancelAction) {
            this.onCancelAction = onCancelAction;
            return this;
        }

        public OnCancelAction getOnCancelAction() {
            return onCancelAction;
        }

        public void setOnChangeStepAction(com.auto.auto.stepperview.OnChangeStepAction onChangeStepAction) {
            this.onChangeStepAction = onChangeStepAction;
        }

        public OnChangeStepAction getOnChangeStepAction() {
            return onChangeStepAction;
        }

        public void setFragmentManager(FragmentManager fragmentManager) {
            this.fragmentManager = fragmentManager;
        }

        public FragmentManager getFragmentManager() {
            return fragmentManager;
        }
    }

    static int fID = 190980;

    protected static int findUnusedId(View view) {
        while (view.getRootView().findViewById(++fID) != null) ;
        return fID;
    }

}
