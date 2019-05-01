package com.example.hotfix.bean;

/**
 * Created by ZhangPan on 2017/12/25
 */
public class BaseCallBackBean {
    public int code;
    public Object data;
    public String devMsg = "";
    public String msg = "";
    public int status;

    @Override
    public String toString() {
        return "BaseCallBackBean{" +
                "code=" + code +
                ", data=" + data +
                ", devMsg='" + devMsg + '\'' +
                ", msg='" + msg + '\'' +
                ", status=" + status +
                '}';
    }

}
