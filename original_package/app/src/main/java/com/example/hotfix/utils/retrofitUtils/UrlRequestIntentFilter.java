package com.example.hotfix.utils.retrofitUtils;

import com.google.gson.Gson;
import com.example.hotfix.bean.BaseCallBackBean;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.http.GET;
import retrofit2.http.POST;
import rx.Observable;

public class UrlRequestIntentFilter {
    private static HashMap<String, Method> requestUtilsMap;
    private static final Gson gson = new Gson();

    // 初始化方法和请求名称字典对象
    public static void initRequestUrlsMap() {
        if (requestUtilsMap != null)
            return;
        Method[] methods = Services.class.getMethods();
        requestUtilsMap = new HashMap<>(); // 初始化键值对
        // 遍历获取所有的请求方法和请求网络地址对应关系填充空字典
        try {
            for (Method method : methods) {
                Annotation[] annotations = method.getAnnotations();
                for (Annotation annotation : annotations) {
                    // 获取注解的具体类型
                    Class<? extends Annotation> annotationType = annotation.annotationType();
                    if (POST.class == annotationType || GET.class == annotationType) {
                        String postValueRaw = annotation.toString();
                        postValueRaw = postValueRaw.substring(postValueRaw.indexOf("(") + 1);
                        postValueRaw = postValueRaw.substring(0, postValueRaw.indexOf(")"));
                        postValueRaw = postValueRaw.substring(postValueRaw.indexOf("=") + 1, postValueRaw.length());
                        // key 请求链接 value 请求 Method 对象
                        requestUtilsMap.put(postValueRaw, method);
                        // 打印出java.lang.annotation.Annotation，注解类其实都实现了Annotation这个接口
                        // Class<?>[] interfaces = POST.class.getInterfaces();
                        // Log.i("test", interfaces[0].getName());
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static Observable<BaseCallBackBean> requestUrl(String json, Services serviceApi, String requestUrl) {
        try {
            if (requestUtilsMap == null)
                return null;
            Method method = requestUtilsMap.get(requestUrl);
            Method requestMethod = serviceApi.getClass().getMethod(method.getName(), RequestBody.class);
            RequestBody body = RequestBody.create(okhttp3.MediaType.parse("application/json;charset=utf-8"), json);
            return (Observable<BaseCallBackBean>) requestMethod.invoke(serviceApi, body);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static Observable<BaseCallBackBean> requestQuery(HashMap<String, String> requestParams, Services serviceApi, String requestUrl) {
        ArrayList<String> paramsList = new ArrayList<>();
        for (Map.Entry<String, String> entry : requestParams.entrySet()) {
            paramsList.add(entry.getValue());
        }
        try {
            if (requestUtilsMap == null)
                return null;
            Method method = requestUtilsMap.get(requestUrl);
            Method requestMethod = null;
            if (paramsList.size() == 0) {
                return requestUrl(gson.toJson(requestParams).toString(), serviceApi, requestUrl);
            }else if (paramsList.size() == 1) {
                requestMethod = serviceApi.getClass().getMethod(method.getName(), String.class, String.class);
                return (Observable<BaseCallBackBean>) requestMethod.invoke(serviceApi, paramsList.get(0), "");
            } else if (paramsList.size() == 2) {
                requestMethod = serviceApi.getClass().getMethod(method.getName(), String.class, String.class, String.class);
                return (Observable<BaseCallBackBean>) requestMethod.invoke(serviceApi, paramsList.get(0), paramsList.get(1), "");
            } else if (paramsList.size() == 3) {
                requestMethod = serviceApi.getClass().getMethod(method.getName(), String.class, String.class, String.class, String.class);
                return (Observable<BaseCallBackBean>) requestMethod.invoke(serviceApi, paramsList.get(0), paramsList.get(1), paramsList.get(2), "");
            }
            return null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}