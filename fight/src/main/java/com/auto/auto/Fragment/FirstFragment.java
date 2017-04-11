package com.auto.auto.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;

import com.auto.auto.R;


/**
 * A simple {@link Fragment} subclass.
 */
public class FirstFragment extends Fragment {

    private EditText dingTalkAccountEditText;
    private EditText dingTalkPasswordEditText;
    private EditText authAccountEditText;
    private EditText authAccountPasswordEditText;
    private EditText eMailEditText;
    private OnFirstFragmentListener listener;

    public FirstFragment() {
        // Required empty public constructor
    }

    public String getDingTalkAccount() {
        return dingTalkAccountEditText.getText().toString();
    }

    public String getDingTalkPassword() {
        return dingTalkPasswordEditText.getText().toString();
    }

    public String getAuthAccount() {
        return authAccountEditText.getText().toString();
    }

    public String getAuthAccountPassword() {
        return authAccountPasswordEditText.getText().toString();
    }

    public String geteMail() {
        return eMailEditText.getText().toString();
    }

    public boolean isInfoEnough() {

        boolean dingTalkAccountIsEmpty = TextUtils.isEmpty(dingTalkAccountEditText.getText());
        boolean dingTalkPasswordIsEmpty = TextUtils.isEmpty(dingTalkPasswordEditText.getText());
        boolean authAccountIsEmpty = TextUtils.isEmpty(authAccountEditText.getText());
        boolean authAccountPasswordIsEmpty = TextUtils.isEmpty(authAccountPasswordEditText.getText());
        boolean emailIsEmpty = TextUtils.isEmpty(eMailEditText.getText());


        return !dingTalkAccountIsEmpty && !dingTalkPasswordIsEmpty && !authAccountIsEmpty && !authAccountPasswordIsEmpty && !emailIsEmpty;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View rootView = inflater.inflate(R.layout.fragment_first, container, false);
        dingTalkAccountEditText = (EditText) rootView.findViewById(R.id.dingTalkAccount);
        dingTalkPasswordEditText = (EditText) rootView.findViewById(R.id.dingTalkPassword);
        authAccountEditText = (EditText) rootView.findViewById(R.id.authAccount);
        authAccountPasswordEditText = (EditText) rootView.findViewById(R.id.authAccountPassword);
        eMailEditText = (EditText) rootView.findViewById(R.id.eMail);

        eMailEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    if (isInfoEnough()) {
                        listener.onInfoEnough();
                    }
                    return true;
                }
                return false;
            }
        });
        return rootView;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFirstFragmentListener) {
            listener = (OnFirstFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        listener = null;
    }

    public interface OnFirstFragmentListener{
        void onInfoEnough();
    }
}
