package com.jzxfyun.common.widget.ScrollPickerView;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;

public interface IViewProvider<T> {
    @LayoutRes
    int resLayout();

    void onBindView(@NonNull View view, @Nullable T itemData, @Nullable int position);

    void updateView(@NonNull View itemView, boolean isSelected);
}
