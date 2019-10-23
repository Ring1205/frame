package com.jzxfyun.manager.listener;

import android.support.v7.widget.RecyclerView;
import android.view.View;

public interface OnLongClickListener {
    void onItemLongClick(int position, View view, RecyclerView.ViewHolder vh);
}
