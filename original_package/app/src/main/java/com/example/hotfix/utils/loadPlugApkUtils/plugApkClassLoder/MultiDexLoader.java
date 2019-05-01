package com.example.hotfix.utils.loadPlugApkUtils.plugApkClassLoder;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.util.Log;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;
import java.util.zip.ZipFile;

import dalvik.system.DexFile;

/**
 * 用于加载任意 .class 文件的类
 */
public class MultiDexLoader {

    private static final String TAG = "AssetsApkLoader";
    private static boolean installed = false;
    private static final int MAX_SUPPORTED_SDK_VERSION = 22;
    private static final int MIN_SDK_VERSION = 4;
    private static final Set<String> installedApk = new HashSet<String>();

    private MultiDexLoader() {

    }

    /**
     * 安装Assets中的apk文件
     * 其原理如下，找到
     *
     * @param context
     */
    public static void install(Context context, String downUrl, String fileName) {
        /**  “安装”插件化应用的准备工作  */
        Log.i(TAG, "install...");
        if (installed) {
            return;
        }
        try {
            // 删除掉插件apk在程序包中所处的位置，准备另一次复制和apk包dex文件的加载
            clearOldDexDir(context);
        } catch (Exception e) {
            Log.w(TAG, "Something went wrong when trying to clear old MultiDex extraction, continuing without cleaning.", e);
        }
        // 将 apk存放 目录下的 apk 文件拷贝到手机指定的项目文件夹下
        DownSourceManager.getInstance(downUrl, fileName).copyDownLoadedApk(context);

        /**  正式开始“安装”插件apk  */
        Log.i(TAG, "install");

        // 安卓 SDK 兼容最小版本判断
        if (Build.VERSION.SDK_INT < MIN_SDK_VERSION) {
            throw new RuntimeException("Multi dex installation failed. SDK "
                    + Build.VERSION.SDK_INT
                    + " is unsupported. Min SDK version is "
                    + MIN_SDK_VERSION + ".");
        }

        try {
            // 拿到 当前App的ApplicationInfo对象，并借助它拿到 sourceDir 实例
            ApplicationInfo applicationInfo = getApplicationInfo(context);
            if (applicationInfo == null) {
                // Looks like running on a test Context, so just return without patching.
                return;
            }
            synchronized (installedApk) {
                String apkPath = applicationInfo.sourceDir;
                if (installedApk.contains(apkPath)) {
                    return;
                }
                installedApk.add(apkPath);
                /*
                 * The patched class loader is expected to be a descendant of
                 * dalvik.system.BaseDexClassLoader. We modify its
                 * dalvik.system.DexPathList pathList field to append additional
                 * DEX file entries.
                 */
                ClassLoader loader;  // 拿到ClassLoader，准备反射获取最关键的变量：dalvik.system.PathClassLoader.DexPathList
                try {
                    loader = context.getClassLoader();
                } catch (RuntimeException e) {
                    /*
                     * Ignore those exceptions so that we don't break tests
                     * relying on Context like a android.test.mock.MockContext
                     * or a android.content.ContextWrapper with a null base
                     * Context.
                     */
                    Log.w(TAG, "Failure while trying to obtain Context class loader. Must be running in test mode. Skip patching.", e);
                    return;
                }
                if (loader == null) {
                    // Note, the context class loader is null when running
                    // Robolectric tests.
                    Log.e(TAG, "Context class loader is null. Must be running in test mode. Skip patching.");
                    return;
                }
                // 存放加载插件的目录列表
                File apkLoadingDirFile = context.getDir(DownSourceManager.APK_DIR, Context.MODE_PRIVATE);
                // 过滤掉不是 apk 的文件，只把 apk 文件的路径存储在 szFiles 对象中
                File[] szFiles = apkLoadingDirFile.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File dir, String filename) { // TODO 方法体中是过滤条件
                        return filename.equals(DownSourceManager.getInstance(downUrl, fileName).fileNameFilter);
                    }
                });
                if (szFiles == null || szFiles.length == 0) { // 没有合规的文件就返回
                    return;
                }
                // 新建一个存储File对象的List，并将 szFiles 列表整个复制过来
                List<File> files = new ArrayList<File>();
                for (File f : szFiles) {
                    Log.i(TAG, "load file:" + f.getName());
                    files.add(f);
                }
                Log.i(TAG, "loader before:" + context.getClassLoader());
                /**
                 * 一百分注意 ！！！！！！！！！！！！！！！！！！
                 * 一百分注意 ！！！！！！！！！！！！！！！！！！
                 * 一百分注意 ！！！！！！！！！！！！！！！！！！
                 * “安装”插件 apk 的方法入口在下面 ！！！！！！！！！！！！！！
                 */
                // “安装”插件 apk 的方法入口
                installSecondaryDexes(loader, apkLoadingDirFile, files);
                Log.i(TAG, "loader end:" + context.getClassLoader());
            }
        } catch (Exception e) {
            Log.e(TAG, "Multidex installation failure", e);
            throw new RuntimeException("Multi dex installation failed (" + e.getMessage() + ").");
        }
        installed = true;
        Log.i(TAG, "install done");
    }

    private static ApplicationInfo getApplicationInfo(Context context)
            throws NameNotFoundException {
        PackageManager pm;
        String packageName;
        try {
            pm = context.getPackageManager();
            packageName = context.getPackageName();
        } catch (RuntimeException e) {
            /*
             * Ignore those exceptions so that we don't break tests relying on
             * Context like a android.test.mock.MockContext or a
             * android.content.ContextWrapper with a null base Context.
             */
            Log.w(TAG,
                    "Failure while trying to obtain ApplicationInfo from Context. "
                            + "Must be running in test mode. Skip patching.", e);
            return null;
        }
        if (pm == null || packageName == null) {
            // This is most likely a mock context, so just return without
            // patching.
            return null;
        }
        ApplicationInfo applicationInfo = pm.getApplicationInfo(packageName, PackageManager.GET_META_DATA);
        return applicationInfo;
    }

    /**
     * 一百分注意 ！！！！！！！！！！！！！！！！！！
     * 一百分注意 ！！！！！！！！！！！！！！！！！！
     * 一百分注意 ！！！！！！！！！！！！！！！！！！
     * “安装”插件 apk 的方法入口在方法里面 ！！！！！！！！！！！！！！
     */
    private static void installSecondaryDexes(ClassLoader loader, File apkLoadingDirFile, List<File> files) throws
            IllegalArgumentException, IllegalAccessException, NoSuchFieldException,
            InvocationTargetException, NoSuchMethodException, IOException {
        if (!files.isEmpty()) {
            // 根据不同的安卓 SDK 版本做不同的处理
            if (Build.VERSION.SDK_INT >= 19) {
                V19.install(loader, files, apkLoadingDirFile);
            } else if (Build.VERSION.SDK_INT >= 14) {
                V14.install(loader, files, apkLoadingDirFile);
            } else {
                V4.install(loader, files);
            }
        }
    }

    /**
     * Locates a given field anywhere in the class inheritance hierarchy.
     * <p>
     * 通过反射找到某各类的成员变量
     *
     * @param instance an object to search the field into.
     * @param name     field name
     * @return a field object（成员变量反射所得的 File 对象）
     * @throws NoSuchFieldException if the field cannot be located
     */
    private static Field findField(Object instance, String name) throws NoSuchFieldException {
        for (Class<?> clazz = instance.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            try {
                Field field = clazz.getDeclaredField(name);
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
                return field;
            } catch (NoSuchFieldException e) {
                // ignore and search next
            }
        }

        throw new NoSuchFieldException("Field " + name + " not found in " + instance.getClass());
    }

    /**
     * Locates a given method anywhere in the class inheritance hierarchy.
     * <p>
     * 通过反射找到某各类的成员变量
     *
     * @param instance       an object to search the method into.
     * @param name           method name
     * @param parameterTypes method parameter types
     * @return a method object
     * @throws NoSuchMethodException if the method cannot be located
     */
    private static Method findMethod(Object instance, String name, Class<?>... parameterTypes)
            throws NoSuchMethodException {
        for (Class<?> clazz = instance.getClass(); clazz != null; clazz = clazz.getSuperclass()) {
            try {
                Method method = clazz.getDeclaredMethod(name, parameterTypes);

                if (!method.isAccessible()) {
                    method.setAccessible(true);
                }

                return method;
            } catch (NoSuchMethodException e) {
                // ignore and search next
            }
        }

        throw new NoSuchMethodException("Method " + name + " with parameters "
                + Arrays.asList(parameterTypes) + " not found in "
                + instance.getClass());
    }

    /**
     * Replace the value of a field containing a non null array, by a new array
     * containing the elements of the original array plus the elements of
     * extraElements.
     *
     * @param instance      宿主 apk 的 ClassLoader实例的 成员变量 pathList（DexPathList类的实例）
     * @param fieldName     需要被反射和替换的 DexPathList类对象的成员变量名 "dexElements", 它就是用于存储 .dex  加载对象的 List
     * @param extraElements 被加载的 插件apk 的.dex实例 列表
     */
    private static void expandFieldArray(Object instance, String fieldName, Object[] extraElements) throws NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        // TODO 接下来是修改宿主App的 dexPathList 的关键代码
        // 以 api14 之后的源码分析为例：
        // 通过反射获得 ClassLoader实例的 成员变量 pathList（DexPathList类的实例）的成员变量 dexElements
        Field jlrField = findField(instance, fieldName);
        jlrField.setAccessible(true);
        // 获取当前 dexElements 这个成员变量在  ClassLoader实例的 成员变量 pathList（DexPathList类的实例）中的取值
        Object[] original = (Object[]) jlrField.get(instance);
        // 新建一个数组，这个数组用于容纳  宿主apk  .dex文件加载出的Element[] 和  插件apk  .dex文件加载出的Element[]
        Object[] combined = (Object[]) Array.newInstance(original.getClass().getComponentType(), original.length + extraElements.length);
        // 先把从插件 apk 中获取的 element[] 以及dexFileArr  复制到新数组里面，方便我们改进做热修复
        System.arraycopy(extraElements, 0, combined, 0, extraElements.length);
        // 再把apk原有的 dexElements 成员变量的取值复制到新数组里面
        System.arraycopy(original, 0, combined, extraElements.length, original.length);
        // 覆盖 dexElements 成员变量的取值
        jlrField.set(instance, combined);
    }

    private static void clearOldDexDir(Context context) throws Exception {
        File dexDir = context.getDir(DownSourceManager.APK_DIR, Context.MODE_PRIVATE);
        if (dexDir.isDirectory()) {
            Log.i(TAG, "Clearing old secondary dex dir (" + dexDir.getPath() + ").");
            File[] files = dexDir.listFiles();
            if (files == null) {
                Log.w(TAG, "Failed to list secondary dex dir content (" + dexDir.getPath() + ").");
                return;
            }
            for (File oldFile : files) {
                Log.i(TAG, "Trying to delete old file " + oldFile.getPath()
                        + " of size " + oldFile.length());
                if (!oldFile.delete()) {
                    Log.w(TAG, "Failed to delete old file " + oldFile.getPath());
                } else {
                    Log.i(TAG, "Deleted old file " + oldFile.getPath());
                }
            }
            if (!dexDir.delete()) {
                Log.w(TAG,
                        "Failed to delete secondary dex dir " + dexDir.getPath());
            } else {
                Log.i(TAG, "Deleted old secondary dex dir " + dexDir.getPath());
            }
        }
    }

    /**
     * Installer for platform versions 19.
     * 根据 19 及以上的 api 来“安装” 插件apk
     * （实质上是加载插件 apk 的 dex 文件，并将这个 dex 文件的实例加入当前宿主 apk 的 dex 文件列表 List 对象中）
     */
    private static final class V19 {
        /**
         * @param loader                     当前宿主 apk 的 classLoader 通过方法 getCLassLoader 获得
         * @param additionalClassPathEntries 所有插件 apk 加载前的存放路径
         * @param optimizedDirectory         插件 apk 加载前存放目录的 File 对象
         * @throws IllegalArgumentException
         * @throws IllegalAccessException
         * @throws NoSuchFieldException
         * @throws InvocationTargetException
         * @throws NoSuchMethodException
         */
        private static void install(ClassLoader loader, List<File> additionalClassPathEntries, File optimizedDirectory)
                throws IllegalArgumentException, IllegalAccessException,
                NoSuchFieldException, InvocationTargetException, NoSuchMethodException {
            /*
             * The patched class loader is expected to be a descendant of
             * dalvik.system.BaseDexClassLoader. We modify its
             * dalvik.system.DexPathList pathList field to append additional DEX
             * file entries.
             */
            /**  反射宿主 apk 的 classLoader 的 pathList 成员变量  */
            Field pathListField = findField(loader, "pathList");
            pathListField.setAccessible(true);
            /**  获取这个成员变量在 宿主apk 的 classLoader 对象中的 取值  */
            Object dexPathList = pathListField.get(loader);
            ArrayList<IOException> suppressedExceptions = new ArrayList<IOException>();
            /**
             * 替换 宿主 apk 的 ClassLoader 的 pathList 成员变量 (pathList实际上是DexPathList类的实例) 的内部成员变量的 List 实例
             * 这个 List 中存储的是 被加载的.dex文件 的实例
             */
            expandFieldArray(dexPathList, "dexElements",
                    /**
                     * 反射 宿主 apk 的 ClassLoader 的 pathList 成员变量（DexPathList类的）的 静态方法：
                     * makeDexElements 获得 Element[] 数组
                     * 这个数组的每一个元素都代表着被加载进来的 .dex 文件对象
                     */
                    makeDexElements(dexPathList, new ArrayList<File>(additionalClassPathEntries), optimizedDirectory, suppressedExceptions));
            if (suppressedExceptions.size() > 0) {
                for (IOException e : suppressedExceptions) {
                    Log.w(TAG, "Exception in makeDexElement", e);
                }
                // 如果有异常，我们还要去反射得到 宿主apk的dexElementsSuppressedExceptions变量，去设置它
                Field suppressedExceptionsField = findField(loader, "dexElementsSuppressedExceptions");
                IOException[] dexElementsSuppressedExceptions = (IOException[]) suppressedExceptionsField.get(loader);

                if (dexElementsSuppressedExceptions == null) {
                    /**  当toarray方法参数为集合时，这一部分可以直接转化为数组内容  */
                    dexElementsSuppressedExceptions = suppressedExceptions.toArray(new IOException[suppressedExceptions.size()]);
                } else {
                    /**  当toarray方法参数为集合时，这一部分可以直接转化为数组内容  */
                    IOException[] combined = new IOException[suppressedExceptions.size() + dexElementsSuppressedExceptions.length];
                    suppressedExceptions.toArray(combined);
                    // 复制非集合的那一部分进入目标数组
                    System.arraycopy(dexElementsSuppressedExceptions, 0, combined, suppressedExceptions.size(), dexElementsSuppressedExceptions.length);
                    dexElementsSuppressedExceptions = combined;
                }
                // 通过反射对这个成员变量赋值
                suppressedExceptionsField.set(loader, dexElementsSuppressedExceptions);
            }
        }

        /**
         * A wrapper around
         * {@code private static final dalvik.system.DexPathList#makeDexElements}.
         * 反射 宿主 apk 的 ClassLoader 的 pathList 成员变量（DexPathList类的）的 静态方法：
         * makeDexElements 获得 Element[] 数组
         * 这个数组的每一个元素都代表着被加载进来的 .dex 文件对象
         *
         * @param dexPathList          宿主 apk 的 ClassLoader实例的 成员变量 pathList（DexPathList类的实例）
         * @param files                存储 File 对象的 List ，内部的 File 指的是 插件 的存储全路径
         * @param optimizedDirectory   插件apk的存储全路径
         * @param suppressedExceptions 一个用于存放 IOException 的空集合
         */
        private static Object[] makeDexElements(Object dexPathList,
                                                ArrayList<File> files, File optimizedDirectory,
                                                ArrayList<IOException> suppressedExceptions)
                throws IllegalAccessException, InvocationTargetException,
                NoSuchMethodException {
            // 反射 DexPathList类的 makeDexElements 方法
            // Google在5.1后修改了makeDexElement的方法名,所以需做如下判断:
            Method makeDexElements = Build.VERSION.SDK_INT >= 23 ?
                    findMethod(dexPathList, "makePathElements", List.class, File.class, List.class) :
                    findMethod(dexPathList, "makeDexElements", ArrayList.class, File.class, ArrayList.class);
            // 输入调用对象、插件apk所在的目录、插件apk的全路径、和用于存储 IO 异常的 List，获得 Element[] 返回
            return (Object[]) makeDexElements.invoke(dexPathList, files,
                    optimizedDirectory, suppressedExceptions);
        }
    }

    /**
     * Installer for platform versions 14, 15, 16, 17 and 18.
     * 根据 14, 15, 16, 17 和18 等级的 api 来“安装” 插件apk
     * （实质上是加载插件 apk 的 dex 文件，并将这个 dex 文件的实例加入当前宿主 apk 的 dex 文件列表 List 对象中）
     */
    private static final class V14 {
        /**
         * @param loader                     当前宿主 apk 的 classLoader 通过方法 getCLassLoader 获得
         * @param additionalClassPathEntries 资产目录 apk 的 File 对象列表
         * @param optimizedDirectory         资产目录 apk 的 File 对象
         * @throws IllegalArgumentException
         * @throws IllegalAccessException
         * @throws NoSuchFieldException
         * @throws InvocationTargetException
         * @throws NoSuchMethodException
         */
        private static void install(ClassLoader loader, List<File> additionalClassPathEntries, File optimizedDirectory)
                throws IllegalArgumentException, IllegalAccessException,
                NoSuchFieldException, InvocationTargetException,
                NoSuchMethodException {
            /*
             * The patched class loader is expected to be a descendant of
             * dalvik.system.BaseDexClassLoader. We modify its
             * dalvik.system.DexPathList pathList field to append additional DEX
             * file entries.
             */
            Field pathListField = findField(loader, "pathList");
            Object dexPathList = pathListField.get(loader);
            expandFieldArray(dexPathList, "dexElements", makeDexElements(dexPathList, new ArrayList<File>(additionalClassPathEntries), optimizedDirectory));
        }

        /**
         * A wrapper around
         * {@code private static final dalvik.system.DexPathList#makeDexElements}.
         */
        private static Object[] makeDexElements(Object dexPathList,
                                                ArrayList<File> files, File optimizedDirectory)
                throws IllegalAccessException, InvocationTargetException,
                NoSuchMethodException {
            Method makeDexElements = findMethod(dexPathList, "makeDexElements",
                    ArrayList.class, File.class);

            return (Object[]) makeDexElements.invoke(dexPathList, files,
                    optimizedDirectory);
        }
    }

    /**
     * Installer for platform versions 4 to 13.
     * 根据 4 ~ 13 等级的 api 来“安装” 插件apk
     * （实质上是加载插件 apk 的 dex 文件，并将这个 dex 文件的实例加入当前宿主 apk 的 dex 文件列表 List 对象中）
     */
    private static final class V4 {
        /**
         * @param loader                     当前宿主 apk 的 classLoader 通过方法 getCLassLoader 获得
         * @param additionalClassPathEntries 资产目录 apk 的 File 对象列表
         * @throws IllegalArgumentException
         * @throws IllegalAccessException
         * @throws NoSuchFieldException
         * @throws InvocationTargetException
         * @throws NoSuchMethodException
         */
        private static void install(ClassLoader loader, List<File> additionalClassPathEntries)
                throws IllegalArgumentException, IllegalAccessException,
                NoSuchFieldException, IOException {
            /*
             * The patched class loader is expected to be a descendant of
             * dalvik.system.DexClassLoader. We modify its fields mPaths,
             * mFiles, mZips and mDexs to append additional DEX file entries.
             */
            int extraSize = additionalClassPathEntries.size();

            Field pathField = findField(loader, "path");

            StringBuilder path = new StringBuilder((String) pathField.get(loader));
            String[] extraPaths = new String[extraSize];
            File[] extraFiles = new File[extraSize];
            ZipFile[] extraZips = new ZipFile[extraSize];
            DexFile[] extraDexs = new DexFile[extraSize];
            for (ListIterator<File> iterator = additionalClassPathEntries
                    .listIterator(); iterator.hasNext(); ) {
                File additionalEntry = iterator.next();
                String entryPath = additionalEntry.getAbsolutePath();
                path.append(':').append(entryPath);
                int index = iterator.previousIndex();
                extraPaths[index] = entryPath;
                extraFiles[index] = additionalEntry;
                extraZips[index] = new ZipFile(additionalEntry);
                extraDexs[index] = DexFile.loadDex(entryPath, entryPath + ".dex", 0);
            }

            pathField.set(loader, path.toString());
            expandFieldArray(loader, "mPaths", extraPaths);
            expandFieldArray(loader, "mFiles", extraFiles);
            expandFieldArray(loader, "mZips", extraZips);
            expandFieldArray(loader, "mDexs", extraDexs);
        }
    }

    /**
     * 获取插件的dexPathList
     *
     * @param list
     * @param dexFileList
     */
    private static void getDexFile(File parentFile, String[] list, List<File> dexFileList) {
        for (String fileName : list) {
            File currentFile = new File(parentFile, fileName);
            if (currentFile.isDirectory()) { // 文件夹
                getDexFile(currentFile, currentFile.list(), dexFileList);
            } else if (fileName.endsWith(".dex") || fileName.endsWith(".odex") || fileName.endsWith(".vdex")) {
                if (currentFile.exists())
                    dexFileList.add(currentFile);
            }
        }
    }
}
