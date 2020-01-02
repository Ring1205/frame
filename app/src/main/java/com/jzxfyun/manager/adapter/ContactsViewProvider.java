package com.jzxfyun.manager.adapter;

import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;

import com.jzxfyun.common.widget.ScrollPickerView.IViewProvider;
import com.jzxfyun.manager.R;
import com.jzxfyun.manager.model.bean.ContactsBean;

public class ContactsViewProvider implements IViewProvider<ContactsBean> {
    private String selected;

    @Override
    public int resLayout() {
        return R.layout.scroll_contacts_item_layout;
    }

    @Override
    public void onBindView(@NonNull View view, @Nullable ContactsBean text, final int position) {
        TextView tv = view.findViewById(R.id.tv_phone);
        TextView name = view.findViewById(R.id.tv_name);
        view.findViewById(R.id.iv_phone).setVisibility(text != null ? View.VISIBLE : View.GONE);
        tv.setText(text != null ? text.getLinkmanPhoneNumber() : "");
        name.setText(text != null ? text.getLinkmanName() : "");
        view.setTag(text != null ? text.getLinkmanName() : "");
    }

    public String getSelected() {
        return selected;
    }

    @Override
    public void updateView(@NonNull View itemView, boolean isSelected) {
        TextView tv = itemView.findViewById(R.id.tv_phone);
        tv.setTextSize(isSelected ? 18 : 14);
        if (isSelected)
            selected = tv.getText().toString();
        tv.setTextColor(Color.parseColor(isSelected ? "#ef7133" : "#000000"));
    }
}
