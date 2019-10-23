package com.jzxfyun.manager.base;

import android.view.LayoutInflater;

import com.jzxfyun.common.widget.ScanPopupWindow;
import com.jzxfyun.manager.MainActivity;
import com.jzxfyun.manager.R;
import com.jzxfyun.manager.fragment.service.patrol.task.TaskCaptureFragment;
import com.jzxfyun.manager.listener.OnNewIntentListener;

public abstract class BaseNFCFragment extends BaseBackFragment {
    private ScanPopupWindow scanPopupWindow;

    public void jumpCaptureFragment(String taskId) {
        start(TaskCaptureFragment.newInstance(taskId));
    }

    public void showScanPopupWindow(OnNewIntentListener listener) {
            scanPopupWindow = new ScanPopupWindow(getContext());
            scanPopupWindow.show(LayoutInflater.from(getContext()).inflate(R.layout.activity_base, null));
            ((MainActivity) getContext()).setmOnScanListener(listener);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (scanPopupWindow != null)
            scanPopupWindow.dismiss();
    }
}
