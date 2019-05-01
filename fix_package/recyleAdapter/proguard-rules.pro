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

-optimizationpasses 5                   # 指定代码的压缩级别 0 ~ 7
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

#（可选）避免Log打印输出
-assumenosideeffects class android.util.Log {
   public static *** v(...);
   public static *** d(...);
   public static *** i(...);
   public static *** w(...);
 }

# 数据bean类不可以加密
-keep class net.lucode.hackware.magicindicator.bean.**{*;}