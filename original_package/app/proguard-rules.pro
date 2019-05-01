# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/hechao/Documents/Android/Android_SDK/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
#-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

#-optimizationpasses 5                   # 指定代码的压缩级别 0 ~ 7
-dontusemixedcaseclassnames
-dontskipnonpubliclibraryclasses       # 是否混淆第三方jar
-dontpreverify        # 混淆时是否做预校验
-keepattributes Signature
-ignorewarning   # 忽略警告，避免打包时某些警告出现
-verbose  # 混淆时是否记录日志
-optimizations !code/simplification/arithmetic,!field/*,!class/merging/*    # 混淆时所采用的算法
# 防止第三方内部类被混淆
-dontoptimize
-keepattributes EnclosingMethod
-keepattributes InnerClasses

-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider
-keep public class * extends android.app.backup.BackupAgentHelper
-keep public class * extends android.preference.Preference
-keep public class * extends android.support.v7.widget.RecyclerView
-keep public class * extends android.support.v7.widget.RecyclerView.Adapter
-keep public class com.android.vending.licensing.ILicensingService

# 保持 native 的方法不去混淆
-keepclasseswithmembernames class * {
    native <methods>;
}
# 自定义控件构造方法不被混淆
#-keepclasseswithmembernames class * {
#    public <init>(android.content.Context, android.util.AttributeSet);
#}
#-keepclasseswithmembernames class * {
#    public <init>(android.content.Context, android.util.AttributeSet, int);
#}
# 枚举
#-keepclassmembers enum * {
#    public static **[] values();
#    public static ** valueOf(java.lang.String);
#}

#-keep class * implements android.os.Parcelable {
#  public static final android.os.Parcelable$Creator *;
#}

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
  **[] $VALUES;
  public *;
}

#universalimageloader图片加载框架不混淆
-keep class com.nostra13.universalimageloader.**{*;}
-dontwarn com.nostra13.universalimageloader.**

# 友盟基础包
-keep class com.umeng.commonsdk.**{*;}

# 友盟社会化
-dontwarn com.umeng.**
-dontwarn com.umeng.socialize.**

# 友盟统计
-keep class com.umeng.**{*;}
-keepclassmembers class * {
  public <init> (org.json.JSONObject);
}
-keep public class [com.example.hotfix].R$*{ # 中括号内目标是应用包名 TODO 每次重新使用都要改
  public static final int *;
}
-keepclassmembers enum * {
  public static **[] values();
  public static ** valueOf(java.lang.String);
}

# 友盟推送相关
-dontwarn com.taobao.**
-dontwarn anet.channel.**
-dontwarn anetwork.channel.**
-dontwarn org.android.**
-dontwarn org.apache.thrift.**
-dontwarn com.xiaomi.**
-dontwarn com.huawei.**
-dontwarn com.meizu.**
-keepattributes *Annotation*
-keep class com.taobao.**{*;}
-keep class org.android.**{*;}
-keep class anet.channel.**{*;}
-keep class com.umeng.**{*;}
-keep class com.xiaomi.**{*;}
-keep class com.huawei.**{*;}
-keep class com.meizu.**{*;}
-keep class org.apache.thrift.**{*;}
-keep class com.alibaba.sdk.android.**{*;}
-keep class com.ut.**{*;}
-keep class com.ta.**{*;}
-keep public class **.R$*{
   public static final int *;
}

#（可选）避免Log打印输出
-assumenosideeffects class android.util.Log {
   public static *** v(...);
   public static *** d(...);
   public static *** i(...);
   public static *** w(...);
 }

# RxJava
-dontwarn rx.**
-keep class rx.**{*;}
-keep interface rx.**{*;}
-dontwarn sun.misc.**
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
   long producerIndex;
   long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

# Gson
-keep class sun.misc.Unsafe{*;}
-keep class com.google.gson.stream.**{*;}
# Application classes that will be serialized/deserialized over Gson 下面替换成自己的实体类
# 数据bean类不可以加密
-keep class com.example.hotfix.bean.**{*;}

#Okhttp3
-dontwarn com.squareup.okhttp3.**
-keep class com.squareup.okhttp3.**{*;}
-keep class rx.Completable.**{*;}
-dontwarn okio.**

#-libraryjars libs/alibaba-cloudapi-sdk-core-1.0.2.jar 防止jar包混淆模板

#高德地图混淆相关
#3D 地图 V5.0.0之前：
-keep   class com.amap.api.maps.**{*;}
-keep   class com.autonavi.amap.mapcore.*{*;}
-keep   class com.amap.api.trace.**{*;}

#3D 地图 V5.0.0之后：
-keep   class com.amap.api.maps.**{*;}
-keep   class com.autonavi.**{*;}
-keep   class com.amap.api.trace.**{*;}

#定位
-keep class com.amap.api.location.**{*;}
-keep class com.amap.api.fence.**{*;}
-keep class com.autonavi.aps.amapapi.model.**{*;}

#搜索
-keep   class com.amap.api.services.**{*;}

#2D地图
-keep class com.amap.api.maps2d.**{*;}
-keep class com.amap.api.mapcore2d.**{*;}

#导航
-keep class com.amap.api.navi.**{*;}
-keep class com.autonavi.**{*;}

# Retrofit
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on RoboVM on iOS. Will not be used at runtime.
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions
# 网络请求相关封装框架的代码禁止加密
-keep class com.example.hotfix.utils.retrofitUtils.**{*;} # 自定义Retrofit的反射等工具类
-keep class com.example.hotfix.utils.retrofitUtils.downloadpg.SoUtils{*;}

# pingpp支付的禁止加密
-dontwarn com.pingplusplus.**
-keep class com.pingplusplus.**{*;}
-dontwarn com.baidu.**
-keep class com.baidu.**{*;}
-dontwarn com.tencent.**
-keep class com.tencent.**{*;}
-dontwarn android.net.**
-keep class android.net.**{*;}
-dontwarn com.unionpay.**
-keep class com.unionpay.**{*;}
-dontwarn com.switfpass.**
-keep class com.switfpass.**{*;}
-dontwarn com.litesuits.orm.**
-keep class com.litesuits.orm.** {*;}
-dontwarn cmb.pb.**
-keep class cmb.pb.** {*;}

# 有盾第三方身份证识别禁止加密
# Gson
-keep class com.lianlian.face.**{*;}
-keepclassmembers class * implements java.io.Serializable {
    static final long serialVersionUID;
    private static final java.io.ObjectStreamField[] serialPersistentFields;
    private void writeObject(java.io.ObjectOutputStream);
    private void readObject(java.io.ObjectInputStream);
    java.lang.Object writeReplace();
    java.lang.Object readResolve();
}
-keep public class * implements java.io.Serializable{*;}
-keep class java.awt.**{*;}
-dontwarn com.sun.jna.**
-keep class com.sun.jna.**{*;}
# -- udcredit  NEED --
-keep class com.face.**{*;}
-keep class cn.com.bsfit.**{*;}
-keep class com.android.snetjob.**{*;}
-keep class com.udcredit.**{*;}
-keep class com.authreal.**{*;}
-keep class com.hotvision.**{*;}
-keep class com.google.gson.stream.**{*;}

# 禁止加密CPU兼容查验的第三方jar包（这是最基本的避免混淆模式）
-keep class com.kejiee.**{*;}

# WebView上传文件的方法禁止混淆
-keepclassmembers class * extends android.webkit.WebChromeClient{
    public void openFileChooser(...);
}

# 微信相关禁止混淆
-keep class com.tencent.mm.opensdk.**{*;}
-keep class com.tencent.wxop.**{*;}
-keep class com.tencent.mm.sdk.**{*;}
-keep class com.example.hotfix.wxapi.**{*;}

# QQ相关禁止混淆
-keepattributes InnerClasses
-keep class com.tencent.bugly.**{*;}
-keep class com.tencent.stat.**{*;}
-keep class com.tencent.smtt.**{*;}
-keep class com.tencent.beacon.**{*;}
-keep class com.tencent.mm.**{*;}
-keep class com.tencent.apkupdate.**{*;}
-keep class com.tencent.tmassistantsdk.**{*;}
-keep class org.apache.http.**{*;}
-keep class com.qq.jce.**{*;}
-keep class com.qq.taf.**{*;}
-keep class com.tencent.connect.**{*;}
-keep class com.tencent.map.**{*;}
-keep class com.tencent.open.**{*;}
-keep class com.tencent.qqconnect.**{*;}
-keep class com.tencent.tauth.**{*;}
-keep class com.tencent.feedback.**{*;}
-keep class common.**{*;}
-keep class exceptionupload.**{*;}
-keep class mqq.**{*;}
-keep class qimei.**{*;}
-keep class strategy.**{*;}
-keep class userinfo.**{*;}
-keep class com.pay.**{*;}
-keep class com.demon.plugin.**{*;}
-keep class com.tencent.midas.**{*;}
-keep class oicq.wlogin_sdk.**{*;}
-keep class com.tencent.ysdk.**{*;}
-keep class com.tencent.ysdk.**{*;}
-dontwarn java.nio.file.Files
-dontwarn java.nio.file.Path
-dontwarn java.nio.file.OpenOption
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# ScriptIntrinsicBlur高斯模糊相关类禁止混淆 （增加包大小0.04MB）
-keep class android.support.v8.renderscript.** { *; }

# fastjson禁止混淆
-keep class com.alibaba.fastjson.** { *; }
-dontwarn com.alibaba.fastjson.**

# 下拉刷新框架禁止混淆
-keep class com.yalantis.phoenix.** { *; }
-dontwarn com.yalantis.phoenix.**

# 防止WebView和Js的交互被混淆
# 保留annotation， 例如 @JavascriptInterface 等 annotation等尽量保持
-keepattributes *Annotation*
# 保留跟 javascript相关的属性
-keepattributes JavascriptInterface
# 保留JavascriptInterface中的方法
-keepclassmembers class * {
    @android.webkit.JavascriptInterface <methods>;
}
# 这个根据自己的project来设置，这个类用来与js交互，所以这个类中的 字段 ，方法，
-keepclassmembers public class com.example.hotfix.ui.activity.WebViewActivity$InJavaScriptLocalObj{
   <fields>;
   <methods>;
   public *;
   private *;
}
# 这个根据自己的project来设置，这个类用来与js交互，所以这个类中的 字段 ，方法， 等尽量保持
-keepclassmembers public class com.example.hotfix.ui.activity.ActiveDetailActivity$InJavaScriptLocalObj{
   <fields>;
   <methods>;
   public *;
   private *;
}
# 数盟第三方禁止混淆
-keep class cn.shuzilm.core.** {*;}