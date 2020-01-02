package com.jzxfyun.manager.fragment.policy;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.jzxfyun.common.utils.CommonUtils;
import com.jzxfyun.manager.R;
import com.jzxfyun.manager.base.BaseMapViewFragment;
import com.jzxfyun.manager.listener.OnMapLocationListener;

public class MapViewFragment extends BaseMapViewFragment {
    private LatLonPoint mLatLng;
    public static MapViewFragment newInstance() {
        Bundle args = new Bundle();
        MapViewFragment fragment = new MapViewFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setToolbarTitle(R.string.title_select_area).initToolbarNav();
        initMapView(savedInstanceState).setPointToCenter(
                CommonUtils.measureScreen().getScreenWidth(_mActivity) / 2,
                ((CommonUtils.measureScreen().getScreenHeight(_mActivity)) * 3 / 5));

        setOnMarkerClickListener(new OnMapLocationListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                mLatLng = regeocodeResult.getRegeocodeQuery().getPoint();
                ((EditText) findViewById(R.id.et_area)).setText(regeocodeResult.getRegeocodeAddress().getFormatAddress());
            }
        });

        setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.tv_location:
                        mlocationClient.startLocation();
                        break;
                    case R.id.btn_save:
                        Bundle bundle = new Bundle();
                        bundle.putString("area", ((EditText) findViewById(R.id.et_area)).getText().toString());
                        bundle.putDouble("lat", mLatLng.getLatitude());
                        bundle.putDouble("lon", mLatLng.getLongitude());
                        setFragmentResult(RESULT_OK, bundle);
                        finish();
                        break;
                }
            }
        }, R.id.tv_location, R.id.btn_save);
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_select_map_address;
    }
}
