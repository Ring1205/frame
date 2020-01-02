package com.jzxfyun.manager.fragment.personal;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.amap.api.maps.model.LatLng;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.geocoder.GeocodeResult;
import com.amap.api.services.geocoder.GeocodeSearch;
import com.amap.api.services.geocoder.RegeocodeQuery;
import com.amap.api.services.geocoder.RegeocodeResult;
import com.bigkoo.pickerview.builder.OptionsPickerBuilder;
import com.bigkoo.pickerview.listener.OnOptionsSelectListener;
import com.bigkoo.pickerview.view.OptionsPickerView;
import com.google.gson.Gson;
import com.google.zxing.common.StringUtils;
import com.jzxfyun.common.base.BaseBean;
import com.jzxfyun.common.utils.CommonUtils;
import com.jzxfyun.common.utils.SPUtils;
import com.jzxfyun.common.utils.netWork.ApiRequest;
import com.jzxfyun.common.utils.netWork.NetBean;
import com.jzxfyun.manager.R;
import com.jzxfyun.manager.base.BaseBackFragment;
import com.jzxfyun.manager.fragment.policy.MapViewFragment;
import com.jzxfyun.manager.listener.NetRequestListener;
import com.jzxfyun.manager.listener.OnMapLocationListener;
import com.jzxfyun.manager.model.RequestUserInfoAndProjectBean;
import com.jzxfyun.manager.model.ResultCityListBean;
import com.jzxfyun.manager.model.ResultUserInfoandprojectBean;
import com.jzxfyun.manager.model.bean.JsonBean;
import com.jzxfyun.manager.model.bean.JsonCityBean;
import com.jzxfyun.manager.model.bean.UserInfoandprojectBean;
import com.jzxfyun.manager.utils.GetJsonDataUtil;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class PersonalInfoFragment extends BaseBackFragment implements View.OnClickListener {
    private EditText etName, etPhone, etAdministrativeRegion, etArea, etAddress;
    private RequestUserInfoAndProjectBean userInfoAndProjectBean;
    private List<JsonBean> options1Items = new ArrayList<>();
    private List<JsonCityBean> cityBeans = new ArrayList<>();
    private ArrayList<ArrayList<String>> options2Items = new ArrayList<>();
    private ArrayList<ArrayList<ArrayList<String>>> options3Items = new ArrayList<>();
    private Thread thread, cityThread;
    private static final int MSG_LOAD_DATA = 0x0001;
    private static final int MSG_LOAD_SUCCESS = 0x0002;
    private static final int MSG_LOAD_FAILED = 0x0003;
    private static boolean isLoaded = false;
    private String opt1tx, opt2tx, opt3tx;
    private OnMapLocationListener listener;
    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_LOAD_DATA:
                    if (thread == null) {//如果已创建就不再重新创建子线程了
                        thread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                // 子线程中解析省市区数据
                                initJsonData();
                            }
                        });
                        thread.start();
                    }
                    break;
                case MSG_LOAD_SUCCESS:
                    isLoaded = true;
                    break;
                case MSG_LOAD_FAILED:
                    if (cityThread == null) {//如果已创建就不再重新创建子线程了
                        cityThread = new Thread(new Runnable() {
                            @Override
                            public void run() {
                                setRegionCode("000156", opt1tx, 0);
                            }
                        });
                        cityThread.start();
                    } else {
                        cityThread.run();
                    }
                    break;
            }
        }
    };

    private void setRegionCode(String s, String x, int i) {
        for (JsonCityBean cityBean : cityBeans) {
            if (cityBean.getCity_parent_code().equals(s)) {
                if (cityBean.getCity_name().equals(x)) {
                    switch (i) {
                        case 0:
                            userInfoAndProjectBean.setFirstLevelRegionCode(cityBean.getCity_code());
                            setRegionCode(cityBean.getCity_code(), opt2tx, 1);
                            break;
                        case 1:
                            userInfoAndProjectBean.setSecondLevelRegionCode(cityBean.getCity_code());
                            setRegionCode(cityBean.getCity_code(), opt3tx, 2);
                            break;
                        case 2:
                            userInfoAndProjectBean.setThirdLevelRegionCode(cityBean.getCity_code());
                            return;
                        default:
                            return;
                    }
                }
            }
        }
    }

    public static PersonalInfoFragment newInstance() {
        Bundle args = new Bundle();
        PersonalInfoFragment fragment = new PersonalInfoFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    protected void initView(Bundle savedInstanceState) {
        setToolbarTitle(R.string.personal_details).initToolbarNav();
        etName = findViewById(R.id.et_name);
        etPhone = findViewById(R.id.et_phone);
        etAdministrativeRegion = findViewById(R.id.et_administrative_region);
        etArea = findViewById(R.id.et_area);
        etAddress = findViewById(R.id.et_address);

        userInfoAndProjectBean = new RequestUserInfoAndProjectBean();
        userInfoAndProjectBean.setId(CommonUtils.getSPUtils(getContext()).getString(SPUtils.USER_ID));

        listener = new OnMapLocationListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                String formatAddress = regeocodeResult.getRegeocodeAddress().getFormatAddress();
                etArea.setText(formatAddress.substring(formatAddress.indexOf(regeocodeResult.getRegeocodeAddress().getDistrict())));
            }
        };

        mHandler.sendEmptyMessage(MSG_LOAD_DATA);
        initData();

        setOnClickListener(this, R.id.btn_keep_changes, R.id.et_administrative_region, R.id.et_area);
    }

    private void initData() {
        netWork().setRequestListener(
                new NetRequestListener() {
                    @Override
                    public void success(String action, BaseBean baseBean, Object tag) {
                        if (baseBean.isSuccessful()) {
                            switch (action) {
                                case NetBean.getuserinfoandproject:
                                    UserInfoandprojectBean bean = ((ResultUserInfoandprojectBean) baseBean).getData();
                                    ((TextView) findViewById(R.id.tv_user_name)).setText(bean.getUserName());
                                    ((TextView) findViewById(R.id.tv_device_number)).setText(String.format(getString(R.string.intelligent_detector_devices), bean.getDeviceNumber()));
                                    etName.setText(bean.getUserName());
                                    etPhone.setText(bean.getPhoneNumber());
                                    etAdministrativeRegion.setText(bean.getFirstLevelRegionName().concat(bean.getSecondLevelRegionName()).concat(bean.getThirdLevelRegionName()));
                                    etAddress.setText(bean.getAddress());
                                    getAddressByLatlng(new LatLng(bean.getGcj02Latitude(), bean.getGcj02Longitude()));
                                    break;
                            }
                        } else {
                            CommonUtils.toast(getContext(), baseBean.getMessage());
                        }
                    }
                }, netWork().apiRequest(NetBean.getuserinfoandproject, ResultUserInfoandprojectBean.class, ApiRequest.REQUEST_TYPE_GET));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_keep_changes:// 保存更改
                if (CommonUtils.string().isEmpty(etName.getText().toString())) {
                    CommonUtils.toast(getContext(), R.string.toast_enter_name);
                    return;
                } else if (CommonUtils.string().isEmpty(etPhone.getText().toString())) {
                    CommonUtils.toast(getContext(), R.string.hint_contact_number);
                    return;
                } else if (CommonUtils.string().isEmpty(etAdministrativeRegion.getText().toString())) {
                    CommonUtils.toast(getContext(), R.string.hint_select_area);
                    return;
                } else if (CommonUtils.string().isEmpty(etAddress.getText().toString())) {
                    CommonUtils.toast(getContext(), R.string.hint_address);
                    return;
                } else {
                    userInfoAndProjectBean.setUserName(etName.getText().toString());
                    userInfoAndProjectBean.setPhoneNumber(etPhone.getText().toString());
                    userInfoAndProjectBean.setAddress(etAddress.getText().toString());
                    netWork().setRequestListener(new NetRequestListener() {
                        @Override
                        public void success(String action, BaseBean baseBean, Object tag) {
                            if (baseBean.isSuccessful()) {
                                finish();
                            }
                            CommonUtils.toast(getContext(), baseBean.getMessage());
                        }
                    }, netWork().apiRequest(NetBean.editUserInfoAndProject, BaseBean.class, ApiRequest.REQUEST_TYPE_POST)
                            .setRequestBody(userInfoAndProjectBean));
                }
                break;
            case R.id.et_administrative_region:
                if (isLoaded) {
                    showPickerView(v);
                } else {
                    Toast.makeText(getContext(), "Please waiting until the data is parsed", Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.et_area:
                startForResult(MapViewFragment.newInstance(), 1200);
                break;
        }
    }

    @Override
    public void onFragmentResult(int requestCode, int resultCode, Bundle data) {
        super.onFragmentResult(requestCode, resultCode, data);
        if (requestCode == 1200 && resultCode == RESULT_OK && null != data) {
            etArea.setText(data.getString("area"));
            double lat = data.getDouble("lat");
            double lon = data.getDouble("lon");
            double wLat = CommonUtils.gcToWgsLat(lat, lon);
            double wLon = CommonUtils.gcToWgsLon(lat, lon);
            userInfoAndProjectBean.setGcj02Latitude(String.valueOf(lat));
            userInfoAndProjectBean.setGcj02Longitude(String.valueOf(lon));
            userInfoAndProjectBean.setWgs84Latitude(String.valueOf(wLat));
            userInfoAndProjectBean.setWgs84Longitude(String.valueOf(wLon));
        }
    }

    /**
     * 根据经纬度获取地址
     *
     * @param latLng
     */
    private void getAddressByLatlng(LatLng latLng) {
        //地理搜索类
        GeocodeSearch geocodeSearch = new GeocodeSearch(getContext());
        geocodeSearch.setOnGeocodeSearchListener(new GeocodeSearch.OnGeocodeSearchListener() {
            @Override
            public void onRegeocodeSearched(RegeocodeResult regeocodeResult, int i) {
                listener.onRegeocodeSearched(regeocodeResult, i);
            }

            @Override
            public void onGeocodeSearched(GeocodeResult geocodeResult, int i) {

            }
        });

        //逆地理编码查询条件：逆地理编码查询的地理坐标点、查询范围、坐标类型。
        LatLonPoint latLonPoint = new LatLonPoint(latLng.latitude, latLng.longitude);
        RegeocodeQuery query = new RegeocodeQuery(latLonPoint, 500f, GeocodeSearch.AMAP);
        //异步查询
        geocodeSearch.getFromLocationAsyn(query);
    }

    private void showPickerView(View view) {// 弹出选择器
        OptionsPickerView pvOptions = new OptionsPickerBuilder(getContext(), new OnOptionsSelectListener() {
            @Override
            public void onOptionsSelect(int options1, int options2, int options3, View v) {
                //返回的分别是三个级别的选中位置
                opt1tx = options1Items.size() > 0 ?
                        options1Items.get(options1).getPickerViewText() : "";

                opt2tx = options2Items.size() > 0
                        && options2Items.get(options1).size() > 0 ?
                        options2Items.get(options1).get(options2) : "";

                opt3tx = options2Items.size() > 0
                        && options3Items.get(options1).size() > 0
                        && options3Items.get(options1).get(options2).size() > 0 ?
                        options3Items.get(options1).get(options2).get(options3) : "";

                String tx = opt1tx + opt2tx + opt3tx;
                mHandler.sendEmptyMessage(MSG_LOAD_FAILED);
                etAdministrativeRegion.setText(tx);
            }
        })
                .setTitleText(getString(R.string.title_district_selection))
                .setDividerColor(Color.GRAY)
                .setTextColorCenter(Color.BLACK) //设置选中项文字颜色
                .setContentTextSize(18)
                .build();

        /*pvOptions.setPicker(options1Items);//一级选择器
        pvOptions.setPicker(options1Items, options2Items);//二级选择器*/
        pvOptions.setPicker(options1Items, options2Items, options3Items);//三级选择器
        pvOptions.show();
    }

    private void initJsonData() {//解析数据

        /**
         * 注意：assets 目录下的Json文件仅供参考，实际使用可自行替换文件
         * 关键逻辑在于循环体
         *
         * */
        String JsonData = new GetJsonDataUtil().getJson(getContext(), "province.json");//获取assets目录下的json文件数据
        String JsonCityData = new GetJsonDataUtil().getJson(getContext(), "city.json");//获取assets目录下的json文件数据

        ArrayList<JsonBean> jsonBean = parseData(JsonData);//用Gson 转成实体
        ArrayList<JsonCityBean> jsoncityBeans = parseCityData(JsonCityData);//用Gson 转成实体

        /**
         * 添加省份数据
         *
         * 注意：如果是添加的JavaBean实体，则实体类需要实现 IPickerViewData 接口，
         * PickerView会通过getPickerViewText方法获取字符串显示出来。
         */
        options1Items = jsonBean;
        cityBeans = jsoncityBeans;

        for (int i = 0; i < jsonBean.size(); i++) {//遍历省份
            ArrayList<String> cityList = new ArrayList<>();//该省的城市列表（第二级）
            ArrayList<ArrayList<String>> province_AreaList = new ArrayList<>();//该省的所有地区列表（第三极）

            for (int c = 0; c < jsonBean.get(i).getCityList().size(); c++) {//遍历该省份的所有城市
                String cityName = jsonBean.get(i).getCityList().get(c).getName();
                cityList.add(cityName);//添加城市
                ArrayList<String> city_AreaList = new ArrayList<>();//该城市的所有地区列表

                //如果无地区数据，建议添加空字符串，防止数据为null 导致三个选项长度不匹配造成崩溃
                /*if (jsonBean.get(i).getCityList().get(c).getArea() == null
                        || jsonBean.get(i).getCityList().get(c).getArea().size() == 0) {
                    city_AreaList.add("");
                } else {
                    city_AreaList.addAll(jsonBean.get(i).getCityList().get(c).getArea());
                }*/
                city_AreaList.addAll(jsonBean.get(i).getCityList().get(c).getArea());
                province_AreaList.add(city_AreaList);//添加该省所有地区数据
            }

            /**
             * 添加城市数据
             */
            options2Items.add(cityList);

            /**
             * 添加地区数据
             */
            options3Items.add(province_AreaList);
        }

        mHandler.sendEmptyMessage(MSG_LOAD_SUCCESS);

    }

    public ArrayList<JsonBean> parseData(String result) {//Gson 解析
        ArrayList<JsonBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    public ArrayList<JsonCityBean> parseCityData(String result) {//Gson 解析
        ArrayList<JsonCityBean> detail = new ArrayList<>();
        try {
            JSONArray data = new JSONArray(result);
            Gson gson = new Gson();
            for (int i = 0; i < data.length(); i++) {
                JsonCityBean entity = gson.fromJson(data.optJSONObject(i).toString(), JsonCityBean.class);
                detail.add(entity);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return detail;
    }

    @Override
    protected int getLayoutId() {
        return R.layout.fragment_personal_info;
    }
}
