package com.example.hotfix.ui.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import com.example.hotfix.R;
import com.example.hotfix.base.baseui.BaseActivity;
import com.example.hotfix.bean.BaseCallBackBean;
import com.example.hotfix.mvp.Presenters.SplashActivityPresenterImpl;
import com.example.hotfix.mvp.Views.SplashActivityVieImpl;
import com.example.hotfix.utils.BackgroundUtils;
import com.example.hotfix.utils.isEmulatorUtil;

public class SplashActivityPlug extends BaseActivity<SplashActivityPresenterImpl, SplashActivityVieImpl> {
    // 判断是否h5打开
    public static String isFromH5 = "";
    // 页面实例
    public static SplashActivityPlug splashActivityPlugInstance;
    // 用于判断运行设备是不是模拟器
    public static boolean emulator = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // 记录页面实例
        splashActivityPlugInstance = this;

        super.onCreate(savedInstanceState);

        // 重置请求字典初始化状态
        BackgroundUtils.isInitUrlIntentFilterFinish = false;
        // 初始化App必须数据
        presenterImpl.initAppData();

        /** 获取第三方跳转链接所传参数（如果有，格式为：renxingzhuanapp://cainiao/openwith?isfromh5=true）*/
        Intent i_getvalue = getIntent();
        String action = i_getvalue.getAction();
        if (Intent.ACTION_VIEW.equals(action)) {
            Uri uri = i_getvalue.getData();
            if (uri != null) {
                // 获取链接所传参数
                isFromH5 = uri.getQueryParameter("isfromh5");
            }
        }
    }

    @Override
    public int setAcContentView() {
        return R.layout.activity_splash;
    }

    @Override
    protected void onDestroy() {
        splashActivityPlugInstance = null;
        // Splash关闭，无需再用到线程池，后台线程池回收
        BackgroundUtils.shoutDownTimeRefresh();
        super.onDestroy();
    }

    @Override
    public SplashActivityPresenterImpl getPresenter() {
        return new SplashActivityPresenterImpl(this);
    }

    @Override
    public SplashActivityVieImpl getViewImpl() {
        return new SplashActivityVieImpl(this);
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initListener() {

    }

    @Override
    public void initData() {
        // 判断是不是模拟器
        emulator = isEmulatorUtil.isEmulator(this);
        presenterImpl.startNextActivity(3000);
    }

    @Override
    public void bindData2View(BaseCallBackBean baseCallBackBean) {

    }

    // 告诉前端时间列表和请求字典已经在子线程中完成了，可以开始请求网络了
    public static void backgroundInitFinish() {
        try {
            if (splashActivityPlugInstance != null)
                splashActivityPlugInstance.runOnUiThread(() -> splashActivityPlugInstance.initData());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
