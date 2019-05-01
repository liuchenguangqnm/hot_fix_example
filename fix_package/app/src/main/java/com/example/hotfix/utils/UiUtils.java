package com.example.hotfix.utils;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.Build;
import android.support.v8.renderscript.Allocation;
import android.support.v8.renderscript.Element;
import android.support.v8.renderscript.RenderScript;
import android.support.v8.renderscript.ScriptIntrinsicBlur;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;

import com.example.hotfix.MyApplicationPlug;

import java.io.ByteArrayOutputStream;
import java.lang.reflect.Method;

/**
 * Created by Sunny on 2016/10/2.
 */
public class UiUtils {
    private static Context context = MyApplicationPlug.instance.getApplicationContext();

    /**
     * 将像素值转化为对应的dp值
     */
    public static int dp2px(double dp) {
        float density = context.getResources().getDisplayMetrics().density;
        return (int) (dp * density + .5);
    }

    // 最新消息在屏幕头部弹框的显示动画效果
    public static void showNewMessageNotify(final RelativeLayout rl_new_message) {
        rl_new_message.setAlpha(0);
        rl_new_message.setTop(-UiUtils.dp2px(10));
        rl_new_message.setVisibility(View.VISIBLE);

        ValueAnimator animFloat = ValueAnimator.ofFloat(0, 1);
        animFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alapha = (float) animation.getAnimatedValue();
                rl_new_message.setAlpha(alapha);
                rl_new_message.invalidate();
            }
        });
        animFloat.setDuration(500);

        ValueAnimator animInteger = ValueAnimator.ofInt(-UiUtils.dp2px(10), 0);
        animInteger.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int top = (int) animation.getAnimatedValue();
                rl_new_message.setTop(top);
                rl_new_message.invalidate();
            }
        });
        animInteger.setDuration(500);

        animFloat.start();
        animInteger.start();
    }

    // 最新消息在屏幕头部弹框的隐藏动画效果
    public static void hidNewMessageNotify(final RelativeLayout rl_new_message) {
        rl_new_message.setAlpha(1);
        rl_new_message.setTop(0);
        ValueAnimator animFloat = ValueAnimator.ofFloat(1, 0);
        animFloat.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float alapha = (float) animation.getAnimatedValue();
                rl_new_message.setAlpha(alapha);
                rl_new_message.invalidate();
                if (alapha == 0) {
                    rl_new_message.setVisibility(View.GONE);
                }
            }
        });
        animFloat.setDuration(1000);

        ValueAnimator animInteger = ValueAnimator.ofInt(0, -UiUtils.dp2px(10));
        animInteger.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int top = (int) animation.getAnimatedValue();
                rl_new_message.setTop(top);
                rl_new_message.invalidate();
            }
        });
        animInteger.setDuration(1000);

        animFloat.start();
        animInteger.start();
    }

    /**
     * 获取屏幕的高度
     */
    public static int getScreenHeight() {
        // WindowManager windowManager = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
        // return windowManager.getDefaultDisplay().getHeight();
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.heightPixels;
    }

    /**
     * 获取屏幕的宽度
     */
    public static int getScreenWidth() {
        // WindowManager windowManager = (WindowManager) context.getSystemService(context.WINDOW_SERVICE);
        // return windowManager.getDefaultDisplay().getWidth();
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        return dm.widthPixels;
    }

    /**
     * 将View转为Bitmap的方法
     */
    public static Bitmap convertViewToBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        return bitmap;
    }

    /**
     * 获取状态栏的高度
     */
    public static int getStatusBarHeight(Context context) {
        int statusBarHeight = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            statusBarHeight = context.getResources().getDimensionPixelSize(resourceId);
        }
        return statusBarHeight;
    }

    /**
     * 设置添加屏幕的背景透明度
     */
    public static void backgroundAlpha(final Activity context, float bgAlphaStart, float bgAlphaEnd, long duration) {
        final WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = bgAlphaStart;

        ValueAnimator valueAnimator = ValueAnimator.ofFloat(bgAlphaStart, bgAlphaEnd);
        valueAnimator.setDuration(duration);
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float newAlpha = (float) animation.getAnimatedValue();
                lp.alpha = newAlpha;
                context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
                context.getWindow().setAttributes(lp);
            }
        });
        valueAnimator.start();
    }

    /**
     * 将bitmap图片转化为字节码存储或进行网络传输
     */
    public static byte[] Bitmap2Bytes(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.PNG, 70, baos);
        return baos.toByteArray();
    }

    public static Bitmap makeSmallBitmap(Bitmap bitmap) {
        Matrix matrix = new Matrix();
        matrix.postScale(0.7f, 0.7f); //长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }

    /**
     * 获取是否存在NavigationBar
     */
    public static boolean checkDeviceHasNavigationBar(Context context) {
        boolean hasNavigationBar = false;
        Resources rs = context.getResources();
        int id = rs.getIdentifier("config_showNavigationBar", "bool", "android");
        if (id > 0) {
            hasNavigationBar = rs.getBoolean(id);
        }
        try {
            Class systemPropertiesClass = Class.forName("android.os.SystemProperties");
            Method m = systemPropertiesClass.getMethod("get", String.class);
            String navBarOverride = (String) m.invoke(systemPropertiesClass, "qemu.hw.mainkeys");
            if ("1".equals(navBarOverride)) {
                hasNavigationBar = false;
            } else if ("0".equals(navBarOverride)) {
                hasNavigationBar = true;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return hasNavigationBar;
    }

    /**
     * 获取控件对应Bitmap
     */
    public static Bitmap getViewBitmap(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getMeasuredWidth(), view.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        if (Build.VERSION.SDK_INT >= 11) {
            view.measure(View.MeasureSpec.makeMeasureSpec(view.getMeasuredWidth(), View.MeasureSpec.EXACTLY), View.MeasureSpec.makeMeasureSpec(view.getMeasuredHeight(), View.MeasureSpec.EXACTLY));
            view.layout((int) view.getX(), (int) view.getY(), (int) view.getX() + view.getMeasuredWidth(), (int) view.getY() + view.getMeasuredHeight());
        } else {
            view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
            view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        }
        view.draw(canvas);
        return bitmap;
    }

    public static Bitmap getViewCacheBitmapHeighQuality(View view) {
        // 保存缓存图片，首先要把准许这个控件保存缓存图片的开关打开
        view.setDrawingCacheEnabled(true);
        // 其次是要设置缓存图片的质量
        view.setDrawingCacheQuality(View.DRAWING_CACHE_QUALITY_HIGH);
        // 最后是得到ImageView的缓存图片
        Bitmap drawingCache = view.getDrawingCache();
        return drawingCache;
    }

    /**
     * 获取页面模糊背景原图的一部分
     *
     * @param decorView 原页面整体的View
     * @param topView   新图片中处于最上方的View
     * @param offsetDb  偏移量微调距离
     * @return
     */
    public static Bitmap getPartSourceBitmap(View decorView, View topView, int offsetDb) {
        Bitmap sourceBitmap = getViewBitmap(decorView);
        Bitmap newBitmap = Bitmap.createBitmap(sourceBitmap, 0, topView.getTop() - offsetDb, sourceBitmap.getWidth(),
                UiUtils.getScreenHeight() - topView.getTop() + offsetDb);
        sourceBitmap.recycle();
        sourceBitmap = null;
        return newBitmap;
    }

    /**
     * RenderScript 高斯模糊
     * TODO 如果使用需要打开Proguard混淆的禁止加密，增加包大小0.04MB，不如第三方依赖实用
     *
     * @param source
     * @param radius
     * @param scale
     * @return
     */
    public static Bitmap rsBlur(Bitmap source, int radius, float scale) {
        int width = Math.round(source.getWidth() * scale);
        int height = Math.round(source.getHeight() * scale);
        Bitmap inputBmp = Bitmap.createScaledBitmap(source, width, height, false);
        source.recycle();
        source = null;
        RenderScript renderScript = RenderScript.create(context);
        // Allocate memory for Renderscript to work with final
        Allocation input = Allocation.createFromBitmap(renderScript, inputBmp);
        final Allocation output = Allocation.createTyped(renderScript, input.getType());
        // Load up an instance of the specific script that we want to use.
        ScriptIntrinsicBlur scriptIntrinsicBlur = ScriptIntrinsicBlur.create(renderScript, Element.U8_4(renderScript));
        scriptIntrinsicBlur.setInput(input);
        // Set the blur radius
        scriptIntrinsicBlur.setRadius(radius);
        // Start the ScriptIntrinisicBlur
        scriptIntrinsicBlur.forEach(output);
        // Copy the output to the blurred bitmap
        output.copyTo(inputBmp);
        renderScript.destroy();
        return inputBmp;
    }

}
