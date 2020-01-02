package com.jzxfyun.manager.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.jzxfyun.common.utils.CommonUtils;
import com.jzxfyun.common.utils.StringFormatUtils;
import com.jzxfyun.manager.R;

/**
 * @author leiming
 * @date 2019/3/26.
 */
public class PrivacyPolicyDialog extends DialogFragment {
    private FragmentActivity activity;

    private StringFormatUtils formatUtils;

    private boolean isShowing;
    private Context mContext;
    private OnClickListener listener;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        formatUtils = CommonUtils.string();
        setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth);
    }

    @Override
    public void onStart() {
        super.onStart();
        getDialog().getWindow().setBackgroundDrawableResource(R.drawable.common_bg_dialog);
        getDialog().setCanceledOnTouchOutside(false);
        getDialog().setOnKeyListener(new DialogInterface.OnKeyListener() {
            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    return true;
                }
                return false;
            }
        });
    }

    public PrivacyPolicyDialog show(AppCompatActivity activity) {
        this.activity = activity;
        FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
        ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE);
        show(ft, "df");
        isShowing = true;
        return this;
    }

    public boolean isShowing() {
        return isShowing;
    }

    public void setOnClickListener(OnClickListener listener) {
        this.listener = listener;
    }

    @Override
    public void dismiss() {
        try {
            super.dismiss();
            isShowing = false;
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.dialog_privacy_policy_layout, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        TextView privacyPolicyTitle = view.findViewById(R.id.privacy_policy_title);

        privacyPolicyTitle.setText(R.string.privacy_policy_title);

        view.findViewById(R.id.privacy_policy_btn).setOnClickListener(onClickListener);
        view.findViewById(R.id.privacy_policy_quit).setOnClickListener(onClickListener);
        view.findViewById(R.id.privacy_policy_agree).setOnClickListener(onClickListener);


    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (null != listener)
                listener.onClick(v);
        }
    };

    public interface OnClickListener {
        void onClick(View view);
    }
}
