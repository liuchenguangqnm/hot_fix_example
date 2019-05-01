package com.example.hotfix.base.baseui;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.view.Window;
import android.view.WindowManager;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.google.gson.Gson;
import com.example.hotfix.AppConfig;
import com.example.hotfix.base.basemvp.BasePresenter;
import com.example.hotfix.base.basemvp.BaseView;
import com.example.hotfix.bean.BaseCallBackBean;
import com.example.hotfix.ui.activity.SplashActivityPlug;
import com.example.hotfix.utils.lightStatusBarUtils.IsStatusBarCanChangeTextColor;

import static com.example.hotfix.utils.sunUi.SunUiUtil.fixLayout;

/**
 * Created by ASUS on 2018/1/3.
 */
public abstract class BaseActivity<T extends BasePresenter, H extends BaseView> extends AppCompatActivity {
    public T presenterImpl;
    public H viewImpl;
    // 用以判断是否正在刷新数据，避免重复请求
    public boolean isLoading = false;
    // 当前页面的刷新布局，用以记录内部的adapter对象
    private SwipeRefreshLayout swipeRefreshView;
    // 下拉刷新上拉加载相关
    public int currentPage = 1;
    public boolean isLoadMore;
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
        IsStatusBarCanChangeTextColor.setStatusBarTextIsDark(this, true); // 状态栏文字颜色是否显示黑色

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
     * 没有下拉炫酷动画的下拉刷新
     */
    protected void swipeRefresh(SwipeRefreshLayout swipeRefreshView) {
        // 用于记录当前Fragment的刷新布局
        this.swipeRefreshView = swipeRefreshView;
        // 设置颜色属性的时候一定要注意是引用了资源文件还是直接设置16进制的颜色，因为都是int值容易搞混
        // 设置下拉进度的背景颜色，默认就是白色的
        swipeRefreshView.setProgressBackgroundColorSchemeResource(android.R.color.white);
        // 设置下拉进度的主题颜色
        swipeRefreshView.setColorSchemeResources(android.R.color.holo_red_light, android.R.color.holo_orange_light, android.R.color.holo_green_light, android.R.color.holo_blue_light);
        // 下拉时触发SwipeRefreshLayout的下拉动画，动画完毕之后就会回调这个方法
        swipeRefreshView.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (!isLoadMore) {
                    currentPage = 1;
                    isLoadMore = false;
                    initData();
                }
            }
        });
    }

    /**
     * 上拉加载相关
     */
    public class MyRequestLoadMoreListener implements BaseQuickAdapter.RequestLoadMoreListener {

        @Override
        public void onLoadMoreRequested() {
            // loadMore();
            if (!isLoading) {
                currentPage = ++currentPage;
                isLoadMore = true;
                initData();
            } else {
                Object tag = swipeRefreshView.getTag();
                if (tag != null) {
                    BaseQuickAdapter adapter = (BaseQuickAdapter) tag;
                    adapter.loadMoreComplete();
                }
            }
        }
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
                // SplashActivity必须数据初始化完成之后，内部就自己请求了数据，无需在这里调用
                if (!(BaseActivity.this instanceof SplashActivityPlug))
                    initData();
            }
        });
    }
}
