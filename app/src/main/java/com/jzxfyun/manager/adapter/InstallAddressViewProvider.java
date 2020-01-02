package com.jzxfyun.manager.adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.jzxfyun.common.R;
import com.jzxfyun.common.widget.ScrollPickerView.IViewProvider;
import com.jzxfyun.manager.model.bean.InstallAddressBean;

public class InstallAddressViewProvider implements IViewProvider<InstallAddressBean> {
    private String selected;

    @Override
    public int resLayout() {
        return R.layout.scroll_picker_default_item_layout;
    }

    @Override
    public void onBindView(@NonNull View view, @Nullable InstallAddressBean text, final int position) {
        TextView tv = view.findViewById(R.id.tv_content);
        tv.setText(text != null ? text.getInstallAddress() : "");
    }

    public String getSelected() {
        return selected;
    }

    @Override
    public void updateView(@NonNull View itemView, boolean isSelected) {
        TextView tv = itemView.findViewById(R.id.tv_content);
        tv.setTextSize(isSelected ? 18 : 14);
        if (isSelected)
            selected = tv.getText().toString();
        tv.setTextColor(Color.parseColor(isSelected ? "#ef7133" : "#000000"));
    }
}
