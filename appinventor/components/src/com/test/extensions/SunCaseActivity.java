package com.test.extensions;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import com.google.appinventor.components.annotations.*;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.runtime.*;
import com.test.extensions.util.*;


import java.io.IOException;


@DesignerComponent(
        version = 1,
        description = "能获取 WiFi 光照传感器数据的扩展插件",
        category = ComponentCategory.EXTENSION,
        nonVisible = true)
@SimpleObject(external = true)
@UsesPermissions(permissionNames = "android.permission.ACCESS_FINE_LOCATION,ACCESS_COARSE_LOCATION,android.permission.INTERNET,android.permission.ACCESS_WIFI_STATE,android.permission.CHANGE_WIFI_STATE")
public class SunCaseActivity extends AndroidNonvisibleComponent implements Component {
    private ComponentContainer container;

    /**
     * Creates a new AndroidNonvisibleComponent.
     *
     * @param form the container that this component will be placed in
     */
    private final Context context;
    private ConnectTask connectTask;
    private final Data dataBean;


    /**
     * Creates a new AndroidNonvisibleComponent.
     */
    public SunCaseActivity(ComponentContainer container) {
        super(container.$form());
        this.container = container;
        context = (Context) container.$context();
        dataBean = new Data();
    }


    @SimpleFunction(description = "开启子线程，读取光照数据,需要传入 IP 地址，端口号，连接信息 tv，光照信息 tv")
    public void WiFiSunConnectOn(String IP,String port, Label connectInfoLabel, Label sunDataLabel) {
        connectTask = new ConnectTask(context, dataBean,IP,port,connectInfoLabel, sunDataLabel);
        connectTask.setCIRCLE(true);
        connectTask.execute();
    }


    @SimpleFunction(description = "关闭")
    public void WiFiSunConnectOff() {
        // 取消任务
        if (connectTask != null && connectTask.getStatus() == AsyncTask.Status.RUNNING) {
            connectTask.setCIRCLE(false);
            connectTask.setstatu(false);
            // 如果Task还在运行，则先取消它
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE) {
                connectTask.cancel(true);
            }
            try {
                connectTask.getmSocket().close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }
}
