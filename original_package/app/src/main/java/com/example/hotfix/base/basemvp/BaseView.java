package com.example.hotfix.base.basemvp;

import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.google.gson.Gson;
import com.example.hotfix.R;
import com.example.hotfix.bean.BaseCallBackBean;

import java.lang.ref.SoftReference;
import java.lang.ref.WeakReference;

/**
 * Created by ASUS on 2018/1/10.
 */
public abstract class BaseView<T> {
    public SoftReference<T> uiSoftReference; // 页面实例
    public Gson gson = new Gson();
    // loading模糊背景图相关
    public WeakReference<Bitmap> decorViewBitmap; // 原图
    public WeakReference<Bitmap> pageCacheImg;     // 背景图
    // 需要操作的loading资源
    public ImageView iv_loading_page_bg;
    public LinearLayout ll_loading_center_view;
    // 判断第一次数据加载成功之后页面背景是否缓存了
    public boolean isReloadLoadingBgAfterSuc = false;

    public BaseView(T uiInstance) {
        uiSoftReference = new SoftReference<T>(uiInstance);
    }

    public abstract void showLoading();

    public abstract void hidLoading();

    public abstract void showLoadError();

    public abstract void ShowNoData();

    public abstract void showData(BaseCallBackBean bean);

    public abstract void loadDataSuccess(String requestUrl, BaseCallBackBean masterTalkBackBean);

    public abstract void loadDataError(Throwable throwable, int errorCode, String requestUrl);

    public abstract void getLoadingBgBitmap(int blurRadius);

    public abstract void resolveViewRes();

    /**
     * 回收加载背景相关的原图资源
     */
    public void resolveLoadingBgDecorRes() {
        if (decorViewBitmap != null && decorViewBitmap.get() != null) {
            decorViewBitmap.get().recycle();
            decorViewBitmap.clear();
        } else if (decorViewBitmap != null) {
            decorViewBitmap.clear();
        }
        System.gc();
    }

    /**
     * 回收加载背景相关的所有资源（回收资源但不回收页面专用）
     */
    public void resolveLoadingBgResource() {
        resolveLoadingBgDecorRes();
        // 模糊图片即将回收，不准用模糊效果的bitmap
        if (iv_loading_page_bg != null)
            iv_loading_page_bg.setImageResource(R.color.color_0000);
        if (pageCacheImg != null && pageCacheImg.get() != null) {
            pageCacheImg.get().recycle();
            pageCacheImg.clear();
        } else if (pageCacheImg != null) {
            pageCacheImg.clear();
        }
        System.gc();
    }

    /**
     * 回收加载背景相关的所有资源（onPause与onDestroy专用）
     */
    public void resolveLoadingBgResourcePause() {
        resolveLoadingBgDecorRes();
        // 模糊图片即将回收，不准用模糊效果的bitmap
        if (iv_loading_page_bg != null)
            iv_loading_page_bg.setImageResource(R.color.color_aaf5f3f3);
        if (pageCacheImg != null && pageCacheImg.get() != null) {
            pageCacheImg.get().recycle();
            pageCacheImg.clear();
        } else if (pageCacheImg != null) {
            pageCacheImg.clear();
        }
        System.gc();
    }

}
