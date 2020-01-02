package com.jzxfyun.common.widget;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.jzxfyun.common.R;
import com.jzxfyun.common.widget.ScrollPickerView.IViewProvider;
import com.jzxfyun.common.widget.ScrollPickerView.ScrollPickerAdapter;
import com.jzxfyun.common.widget.ScrollPickerView.ScrollPickerView;

import java.util.List;

public class ScrollerPopupWindow<T> extends PopupWindow implements View.OnClickListener {
    private View mView; // PopupWindow 菜单布局
    private List<T> mList;
    private Context mContext; // 上下文参数
    private IViewProvider mViewProvider;
    private ScrollPickerView mScrollPickerView;
    private View.OnClickListener mListener;
    private TextView tvRight;
    private ScrollPickerAdapter.ScrollPickerAdapterBuilder<T> builder;

    public ScrollerPopupWindow(Context context, IViewProvider viewProvider, List<T> list, View.OnClickListener listener) {
        super(context);
        this.mContext = context;
        this.mViewProvider = viewProvider;
        this.mList = list;
        this.mListener = listener;
        Init();
    }

    /**
     * 设置布局以及点击事件
     */
    private void Init() {
        LayoutInflater inflater = (LayoutInflater) mContext
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mView = inflater.inflate(R.layout.scroll_pop_item, null);
        mScrollPickerView = mView.findViewById(R.id.scrollPicker);
        mScrollPickerView.setLayoutManager(new LinearLayoutManager(mContext));

        builder = new ScrollPickerAdapter.ScrollPickerAdapterBuilder<T>(mContext)
                .setDataList(mList)
                .selectedItemOffset(1)
                .visibleItemNumber(3)
                .setDivideLineColor("#E5E5E5")
                .setItemViewProvider(mViewProvider);
        mScrollPickerView.setAdapter(builder.build());

        mView.findViewById(R.id.tv_cancel).setOnClickListener(this);
        tvRight = mView.findViewById(R.id.tv_keep);
        tvRight.setOnClickListener(mListener);

        // 导入布局
        this.setContentView(mView);
        // 设置动画效果
        this.setAnimationStyle(R.style.popwindow_anim_style);
        this.setWidth(WindowManager.LayoutParams.MATCH_PARENT);
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        // 设置可触
        this.setFocusable(true);
        ColorDrawable dw = new ColorDrawable(0x0000000);
        this.setBackgroundDrawable(dw);
        // 单击弹出窗以外处 关闭弹出窗
        mView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int height = mView.findViewById(R.id.rl_bag).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
    }

    public ScrollerPopupWindow setOnClickListener(ScrollPickerAdapter.OnClickListener listener){
        builder.setOnClickListener(listener);
        mScrollPickerView.setAdapter(builder.build());
        return this;
    }

    public void setTvRight(String str) {
        tvRight.setText(str);
    }

    public void smoothScrollToPosition(int position) {
        mScrollPickerView.setScrollToPositionWithOffset(position);
    }

    public void setTopBottonVisibility(@CommonDialog.Visibility int visibility){
        mView.findViewById(R.id.rl_btn_layout).setVisibility(visibility);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_cancel) {
            dismiss();
        }
    }
}
