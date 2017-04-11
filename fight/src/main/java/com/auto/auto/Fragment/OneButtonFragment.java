package com.auto.auto.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.auto.auto.R;

public class OneButtonFragment extends Fragment {

    Button oneButton;

    private String buttonText = "";
    private static final String BTN_TEXT = "oneButtonText";
    private View.OnClickListener oneButtonListener;

    public OneButtonFragment() {
        // Required empty public constructor
    }

    public static OneButtonFragment newInstance(String buttonText) {
        OneButtonFragment fragment = new OneButtonFragment();
        Bundle bundle = new Bundle();
        bundle.putString(BTN_TEXT, buttonText);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            buttonText = getArguments().getString(BTN_TEXT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_one_button, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {

        super.onViewCreated(view, savedInstanceState);
        oneButton = (Button) view.findViewById(R.id.oneButton);
        oneButton.setText(buttonText);

        if (oneButtonListener != null) {
            oneButton.setOnClickListener(oneButtonListener);
        }
    }

    public void setOneButtonListener(View.OnClickListener listener) {
        this.oneButtonListener = listener;
    }
}
