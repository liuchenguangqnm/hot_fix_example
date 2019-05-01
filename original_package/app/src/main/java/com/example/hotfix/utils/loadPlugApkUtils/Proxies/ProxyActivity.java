package com.example.hotfix.utils.loadPlugApkUtils.Proxies;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;

import com.example.hotfix.utils.loadPlugApkUtils.PlugAppInstallUtil;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class ProxyActivity extends Activity {
    private Resources mBundleResources;
    // 被加载出来的activity的类
    private Class<?> localClass;
    // 被加载出来的activity对象
    private Object instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 获取需要启动的类
        localClass = (Class<?>) getIntent().getSerializableExtra("activityClass");
        try {
            // 获取这个Activity类的构造方法
            Constructor<?> localConstructor = localClass.getConstructor(new Class[]{});
            // 创建一个Activity实例
            instance = localConstructor.newInstance(new Object[]{});

            // 最重要的方法;需要在调用onCreate方法之前调用;设置代理Activity
            Method setProxy = localClass.getMethod("setProxy", new Class[]{Activity.class});
            setProxy.setAccessible(true); // 设置方法访问权限
            setProxy.invoke(instance, new Object[]{this}); // 调用方法

            // 通过反射,得到onCreate方法
            Method onCreate = localClass.getDeclaredMethod("onCreate", new Class[]{Bundle.class});
            onCreate.setAccessible(true); // 设置方法访问权限
            onCreate.invoke(instance, new Object[]{new Bundle()}); // 调用onCreate方法

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        // 通过反射,onResume，调用加载页面的onResume
        try {
            Method onResume = localClass.getDeclaredMethod("onResume");
            onResume.setAccessible(true); // 设置方法访问权限
            onResume.invoke(instance); // onResume
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // 通过反射,onDestroy，调用加载页面的onDestroy
        try {
            Method onDestroy = localClass.getDeclaredMethod("onDestroy");
            onDestroy.setAccessible(true); // 设置方法访问权限
            onDestroy.invoke(instance); // onDestroy
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // 通过反射,onPause，调用加载页面的onPause
        try {
            Method onPause = localClass.getDeclaredMethod("onPause");
            onPause.setAccessible(true); // 设置方法访问权限
            onPause.invoke(instance); // onPause
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        // 通过反射,onRestart，调用加载页面的onRestart
        try {
            Method onRestart = localClass.getDeclaredMethod("onRestart");
            onRestart.setAccessible(true); // 设置方法访问权限
            onRestart.invoke(instance); // onRestart
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        // 通过反射,onStart，调用加载页面的onStart
        try {
            Method onStart = localClass.getDeclaredMethod("onStart");
            onStart.setAccessible(true); // 设置方法访问权限
            onStart.invoke(instance); // onStart
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 通过反射,onStart，调用加载页面的onStart
        try {
            Method onStop = localClass.getDeclaredMethod("onStop");
            onStop.setAccessible(true); // 设置方法访问权限
            onStop.invoke(instance); // onStop
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 在 Activity 的父类 ContextWrapper 类中，有一个 mBase 属性
     * 创建Application、Service、Activity，会调用该方法给 mBase 属性赋值
     * 这个属性是一个 ContextIml 实例，一般在创建Application、Service、Activity时赋值
     * 而 ContextIml 的实现类就是 Context
     */
    @Override
    protected void attachBaseContext(Context newBase) {
        // 加载插件 apk 包的 Resources 资源对象
        if (PlugAppInstallUtil.apkResources != null) {
            // 替换当前代理 Activity 的 Resource 为插件 apk 的 Resource
            replaceContextResources(newBase, PlugAppInstallUtil.apkResources);
        }
        super.attachBaseContext(newBase);
    }

    /**
     * 使用反射的方式，使用插件 Activity 所属的 apk包的 Resource对象，替换当前 Context 的 mResources 对象
     *
     * @param context
     * @param resources
     */
    public void replaceContextResources(Context context, Resources resources) {
        try {
            Field field = context.getClass().getDeclaredField("mResources");
            field.setAccessible(true);
            if (null == mBundleResources) {
                mBundleResources = resources;
            }
            field.set(context, mBundleResources);
            System.out.println("debug:repalceResources succ");
        } catch (Exception e) {
            System.out.println("debug:repalceResources error");
            e.printStackTrace();
        }
    }

    /**
     * 复写父类的startActivity预处理
     *
     * @param intent
     */
    @Override
    public void startActivity(Intent intent) {
        // // 反射Intent的mComponent对象 (此段代码无用，权当复习反射知识了)
        // Class<? extends Intent> intentClazz = intent.getClass();
        // Field mComponentField = intentClazz.getDeclaredField("mComponent");
        // mComponentField.setAccessible(true);
        // ComponentName componentNameInstance = (ComponentName) mComponentField.get(intent);

        ComponentName componentNameInstance = intent.getComponent();
        String mClass = componentNameInstance.getClassName();

        if (mClass == null)
            return;

        try {
            Class clazz = Class.forName(mClass);
            Intent intentFinish = new Intent(this, ProxyActivity.class);
            Bundle bundle = new Bundle();
            bundle.putSerializable("activityClass", clazz);
            intentFinish.putExtras(bundle);
            super.startActivity(intentFinish);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}