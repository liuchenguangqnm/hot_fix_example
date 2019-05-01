package com.example.hotfix.ui.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.hotfix.R;
import com.example.hotfix.base.baseui.BaseActivity;
import com.example.hotfix.bean.BaseCallBackBean;
import com.example.hotfix.mvp.Presenters.MainActivityPresenterImpl;
import com.example.hotfix.mvp.Views.MainActivityVieImpl;

public class MainActivity extends BaseActivity<MainActivityPresenterImpl, MainActivityVieImpl> {
    public static MainActivity mainActivityInstance;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        // 页面实例
        mainActivityInstance = this;
        super.onCreate(savedInstanceState);
    }

    @Override
    public int setAcContentView() {
        return R.layout.activity_main;
    }

    @Override
    protected void onRestart() {
        // 更新用户和任务信息
        initData();
        super.onRestart();
    }

    @Override
    protected void onDestroy() {
        // 清空页面实例
        mainActivityInstance = null;
        // App进程自杀，下次打开即可展现修复成功的状态
        System.exit(0);
        super.onDestroy();
    }

    @Override
    protected MainActivityPresenterImpl getPresenter() {
        return new MainActivityPresenterImpl(this);
    }

    @Override
    protected MainActivityVieImpl getViewImpl() {
        return new MainActivityVieImpl(this);
    }

    @Override
    protected void initView() {
    }

    @Override
    protected void initListener() {
    }

    @Override
    public void initData() {
    }

    @Override
    public void bindData2View(BaseCallBackBean baseCallBackBean) {
    }

    public void click(View view) {
        Toast.makeText(getApplicationContext(), "未修复", Toast.LENGTH_LONG).show();
    }
}
