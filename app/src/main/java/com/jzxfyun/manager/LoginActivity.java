package com.jzxfyun.manager;

import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Shader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.jzxfyun.common.utils.SystemUtil;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 设置状态栏白底黑字
        SystemUtil.StatusBarLightMode(this);

//        Button button = findViewById(R.id.btn_wechat_login);// 微信授权
        Button button = findViewById(R.id.btn_account_login);// 账号登录
        button.getPaint().setShader(new LinearGradient(0, 0, 0, 90, Color.parseColor("#ff6633"), Color.parseColor("#ffba3f"), Shader.TileMode.CLAMP));
        button.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btn_account_login:

                break;
        }
    }
}
