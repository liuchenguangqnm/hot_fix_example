package com.example.hotfix.base.baseui;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import com.google.gson.Gson;
import com.example.hotfix.AppConfig;
import com.example.hotfix.base.basemvp.BasePresenter;
import com.example.hotfix.base.basemvp.BaseView;
import com.example.hotfix.bean.BaseCallBackBean;

import static com.example.hotfix.utils.sunUi.SunUiUtil.fixLayout;

/**
 * Created by ASUS on 2018/1/3.
 */
public abstract class BaseActivityFullScreen<T extends BasePresenter, H extends BaseView> extends Activity {
    public T presenterImpl;
    public H viewImpl;
    // 数据解析gson
    public Gson gson = new Gson();
    // view加载状态
    private boolean isViewLoadFinish = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setWindow(true);
        // UI适配
        fixLayout(this);
        super.onCreate(savedInstanceState);
        setContentView(setAcContentView());
        presenterImpl = getPresenter();
        viewImpl = getViewImpl();
        initView();
        fixTitlePadding();
        initListener();
        initViewStatusListener();
    }

    @Override
    protected void onResume() {
        // 重新生成加载背景的图片
        if (viewImpl instanceof BaseView && isViewLoadFinish)
            ((BaseView) viewImpl).getLoadingBgBitmap(AppConfig.blurRadius);
        super.onResume();
    }

    @Override
    protected void onPause() {
        // 防止内存不足，回收所有的背景loading图片
        if (viewImpl instanceof BaseView)
            ((BaseView) viewImpl).resolveLoadingBgResourcePause();
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        if (presenterImpl instanceof BasePresenter)
            ((BasePresenter) presenterImpl).stopNormalRequestWhenDestroy();
        if (viewImpl instanceof BaseView)
            ((BaseView) viewImpl).resolveViewRes();
        super.onDestroy();
    }

    public abstract int setAcContentView();

    protected abstract T getPresenter();

    protected abstract H getViewImpl();

    protected abstract void initView();

    protected abstract void initListener();

    protected abstract void initData();

    public abstract void bindData2View(BaseCallBackBean baseCallBackBean);

    protected void setWindow(boolean isTransalact) {
        if (isTransalact && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            requestWindowFeature(Window.FEATURE_NO_TITLE);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
        }
    }

    /**
     * 根据状态栏高度修正页面title距离顶部的高度
     */
    private void fixTitlePadding() {

    }

    /**
     * view加载状态监听
     */
    public void initViewStatusListener() {
        getWindow().getDecorView().getViewTreeObserver().addOnGlobalLayoutListener(() -> {
            if (getWindow().getDecorView().getWidth() != 0 && !isViewLoadFinish) {
                isViewLoadFinish = true;
                if (viewImpl instanceof BaseView) // 生成加载背景的图片
                    ((BaseView) viewImpl).getLoadingBgBitmap(AppConfig.blurRadius);
                initData();
            }
        });
    }

}
