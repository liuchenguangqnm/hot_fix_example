package com.example.hotfix.bean;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * 日期 ---------- 维护人 ------------ 变更内容 --------
 * 2018/4/25        Sunshine          请填写变更内容
 */

public class PushDateSortBean implements Serializable {
    public String firstYear = "";
    public List<String> yearList = new ArrayList<>();
    public List<List<String>> hourList = new ArrayList<>();
    public List<List<List<String>>> minuteList = new ArrayList<>();

    @Override
    public String toString() {
        return "PushDateSortBean{" +
                "firstYear='" + firstYear + '\'' +
                ", yearList=" + yearList +
                ", hourList=" + hourList +
                ", minuteList=" + minuteList +
                '}';
    }
}
