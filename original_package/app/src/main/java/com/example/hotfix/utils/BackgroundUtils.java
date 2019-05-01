package com.example.hotfix.utils;

import com.example.hotfix.MyApplication;
import com.example.hotfix.bean.PushDateSortBean;
import com.example.hotfix.ui.activity.SplashActivity;
import com.example.hotfix.utils.StringParseCutUtils.ParseUtil;
import com.example.hotfix.utils.retrofitUtils.UrlRequestIntentFilter;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 日期 ---------- 维护人 ------------ 变更内容 --------
 * 2018/4/24        Sunshine   时间和请求链接键值对生成工具类
 */
public class BackgroundUtils {
    private static ArrayList<Integer> day31Month = new ArrayList<>();
    private static PushDateSortBean pushDateSortBean;
    // 处理时间列表任务的线程池
    private static ThreadPoolExecutor threadPoolExecutor;
    // 初始化网络请求链接字典标记
    public static boolean isInitUrlIntentFilterFinish = false;
    // 初始化日期列表标记(已经不需要初始化日期列表了，直接初始化true跳过)
    public static boolean isInitDateFinish = true;

    static {
        // 静态代码块初始化31天的月份
        day31Month.add(1);
        day31Month.add(3);
        day31Month.add(5);
        day31Month.add(7);
        day31Month.add(8);
        day31Month.add(10);
        day31Month.add(12);
    }

    public static void initUrlRequestIntentFilter() {
        System.gc(); // 手动清内存，照顾老手机
        //设置线程池缓存队列的排队策略为FIFO，并且指定缓存队列大小为10
        if (threadPoolExecutor == null) {
            BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(10, false);
            threadPoolExecutor = new ThreadPoolExecutor(4, 4, 2, TimeUnit.SECONDS, workQueue);
            threadPoolExecutor.allowCoreThreadTimeOut(true);
        }

        //创建线程类对象
        RequestUrlMapTask requestUrlMapTask = new RequestUrlMapTask();
        //开启线程
        threadPoolExecutor.execute(requestUrlMapTask);

    }

    /**
     * 获取2月份天数
     *
     * @param year
     * @return
     */
    private static int get2Days(int year) { // 计算某一年2月份有多少天
        Calendar c = Calendar.getInstance();
        c.set(year, 2, 1);//0-11->1-12  将日期设置为某一年的3月1号
        c.add(Calendar.DAY_OF_MONTH, -1);//将日期减去一天，即日期变成2月的最后一天
        return c.get(Calendar.DAY_OF_MONTH);//返回二月最后一天的具体值
    }

    /**
     * 通过年份和月份获取天数
     *
     * @param month
     */
    private static int getMonthDay(int year, int month) {
        if (month == 2) {
            return get2Days(year);
        } else {
            if (day31Month.contains(month)) {
                return 31;
            } else {
                return 30;
            }
        }
    }

    /**
     * 获取今年今天到明年的所有日期
     *
     * @return
     */
    private static ArrayList<String> getDateList(Date date) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
        String formatTime = df.format(date);
        ArrayList<String> dates = new ArrayList<>();
        int year = ParseUtil.parseInt(formatTime.substring(0, 4));
        int month = ParseUtil.parseInt(formatTime.substring(5, 7));
        int day = ParseUtil.parseInt(formatTime.substring(8, 10));
        for (int i = year; i <= year + 1; i++) {
            int fixMonth = 1;
            if (i == year)
                fixMonth = month;
            for (int j = fixMonth; j <= 12; j++) {
                int monthDay = getMonthDay(year, j);
                int fixDay = 1;
                if (i == year && j == month)
                    fixDay = day;
                String fixJ = "";
                if (j < 10) {
                    fixJ = "0" + j;
                } else {
                    fixJ = j + "";
                }
                for (int k = fixDay; k <= monthDay; k++) {
                    if (k < 10) {
                        dates.add(i + "年 " + fixJ + "月0" + k + "日");
                    } else {
                        dates.add(i + "年 " + fixJ + "月" + k + "日");
                    }
                }
            }
        }
        return dates;
    }

    /**
     * 获取今年今天到明年的所有日期
     *
     * @param isShowDatas 这个变量用于判断是否前端要调用它展示数据，如果是，就要尽量用现有的数据，避免重新费时生成新数据
     * @return
     */
    public static PushDateSortBean getTimedatas(boolean isShowDatas) {
        // 预清理手机内存
        System.gc();

        if (isShowDatas && BackgroundUtils.pushDateSortBean != null) { // 前端显示时间列表，尽量避免花费时间
            return refreshOrChangeDataSortBean(true, BackgroundUtils.pushDateSortBean);
        } else {  // 不是用于前端显示，则刷新整个列表的数据
            System.gc(); // 手动清内存，照顾老手机
            //设置线程池缓存队列的排队策略为FIFO，并且指定缓存队列大小为10
            if (threadPoolExecutor == null) {
                BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(10, false);
                threadPoolExecutor = new ThreadPoolExecutor(4, 4, 2, TimeUnit.SECONDS, workQueue);
            }

            // 获取时间数据
            Calendar calendar = Calendar.getInstance();
            final Date date = calendar.getTime();
            SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            final String formatTime = df.format(date);
            String clock = formatTime.split(" ")[1];
            String[] timeSplit = clock.split(":");
            final int hour = ParseUtil.parseInt(timeSplit[0]);
            final int min = ParseUtil.parseInt(timeSplit[1]);

            //创建线程类对象
            TimeTask timeTask = new TimeTask(date, hour, min);
            //开启线程
            threadPoolExecutor.execute(timeTask);

            return BackgroundUtils.pushDateSortBean;
        }
    }

    public static void shoutDownTimeRefresh() {
        //待线程池以及缓存队列中所有的线程任务完成后关闭线程池。
        if (threadPoolExecutor != null && isInitUrlIntentFilterFinish && isInitDateFinish) {
            threadPoolExecutor.shutdown();
            threadPoolExecutor = null;
        }
    }

    private static PushDateSortBean refreshOrChangeDataSortBean(Boolean isMainThread, PushDateSortBean pushDateSortBean) {
        synchronized (MyApplication.instance) { // 子线程数据覆盖期间，UI线程不得立即返回这个bean的数据给前端展示
            if (!isMainThread) // 记录当前的时间列表，避免每次展示都要耗时重新形成所有的数据
                BackgroundUtils.pushDateSortBean = pushDateSortBean;
            return BackgroundUtils.pushDateSortBean;
        }
    }

    /**
     * 根据时间格式返回今天或者昨天前天和之前的时间
     *
     * @return
     */
    public static String formatStringUtil(String sourceTimeStr, boolean isTheDayBeforeYesterday) {
        String normalReturnTime = "";
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd kk:mm:ss");
        try {
            // 参数年月日
            long sourceTimeL = new SimpleDateFormat("yyyy-MM-ddkk:mm:ss").parse(sourceTimeStr.replace(" ", "")).getTime();
            String paramsFormatTime = df.format(new Date(sourceTimeL));
            int paramsYear = ParseUtil.parseInt(paramsFormatTime.substring(0, 4));
            int paramsMonth = ParseUtil.parseInt(paramsFormatTime.substring(5, 7));
            int paramsDay = ParseUtil.parseInt(paramsFormatTime.substring(8, 10));

            // 当前年月日
            Calendar calendar = Calendar.getInstance();
            final Date date = calendar.getTime();
            String formatTime = df.format(date);
            int year = ParseUtil.parseInt(formatTime.substring(0, 4));
            int month = ParseUtil.parseInt(formatTime.substring(5, 7));
            int day = ParseUtil.parseInt(formatTime.substring(8, 10));

            String timeString = paramsFormatTime.substring(10, 16);
            if (!timeString.startsWith("00")) {
                if (timeString.startsWith("0"))
                    timeString = timeString.substring(1, timeString.length());
            }
            if (paramsYear == year && paramsMonth == month && paramsDay == day) {
                return "今天" + timeString;
            } else if (paramsYear == year && paramsMonth == month) {
                if (paramsDay == day - 1) {
                    return "昨天" + timeString;
                } else if (paramsDay == day - 2 && isTheDayBeforeYesterday) {
                    return "前天" + timeString;
                }
            }

            normalReturnTime = paramsFormatTime.split(" ")[0];
            normalReturnTime = normalReturnTime.replaceFirst("-", "年");
            normalReturnTime = normalReturnTime.replaceFirst("-", "月");
            normalReturnTime += "日";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return normalReturnTime;
    }

    /**
     * 线程类用于刷新时间列表
     */
    static class TimeTask implements Runnable {
        private final Date date;
        private final int hour;
        private final int min;

        public TimeTask(Date date, int hour, int min) {
            this.date = date;
            this.hour = hour;
            this.min = min;
        }

        @Override
        public void run() {
            List<String> yearList = getDateList(date);
            List<List<String>> hourList = new ArrayList<>();
            List<List<List<String>>> minuteList = new ArrayList<>();
            for (int i = 0; i < yearList.size(); i++) {
                int fixHour = 0;
                if (i == 0) {
                    fixHour = hour;
                }
                List<String> perDateHourList = new ArrayList<>();
                List<List<String>> perDateMinList = new ArrayList<>();
                for (int j = fixHour; j < 24; j++) {
                    if (j < 10) {
                        perDateHourList.add("0" + j + "");
                    } else {
                        perDateHourList.add(j + "");
                    }
                    int fixMinute = 0;
                    List<String> perHourMinList = new ArrayList<>();
                    if (i == 0 && j == hour) {
                        fixMinute = min;
                    }
                    for (int k = fixMinute; k < 60; k++) {
                        if (k < 10) {
                            perHourMinList.add("0" + k + "");
                        } else {
                            perHourMinList.add(k + "");
                        }
                    }
                    perDateMinList.add(perHourMinList);
                }
                hourList.add(perDateHourList);
                minuteList.add(perDateMinList);
            }

            PushDateSortBean pushDateSortBean = new PushDateSortBean();
            pushDateSortBean.firstYear = yearList.get(0);
            yearList.remove(0);
            yearList.add(0, "今天");
            pushDateSortBean.yearList = yearList;
            pushDateSortBean.hourList = hourList;
            pushDateSortBean.minuteList = minuteList;

            refreshOrChangeDataSortBean(false, pushDateSortBean);
            isInitDateFinish = true;
            // 刷新完成，通知SplashActivity可以进行关闭操作
            if (isInitDateFinish && isInitUrlIntentFilterFinish)
                SplashActivity.backgroundInitFinish();
            return;
        }
    }


    /**
     * 线程类用于反射生成网络请求字典列表
     */
    static class RequestUrlMapTask implements Runnable {
        @Override
        public void run() {
            UrlRequestIntentFilter.initRequestUrlsMap();
            isInitUrlIntentFilterFinish = true;
            // 刷新完成，通知SplashActivity可以进行关闭操作
            if (isInitDateFinish && isInitUrlIntentFilterFinish)
                SplashActivity.backgroundInitFinish();
            return;
        }
    }

}
