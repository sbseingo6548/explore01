package com.explore01.kakao;

import android.app.Application;

import com.kakao.sdk.common.KakaoSdk;

public class KaKaoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        KakaoSdk.init(this, "04925ea9c45d96f56dc673ba8c319f49");
    }
}
