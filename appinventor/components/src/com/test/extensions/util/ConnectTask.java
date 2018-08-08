package com.test.extensions.util;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import com.google.appinventor.components.runtime.Label;
import com.google.appinventor.components.runtime.TextBox;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;


/**
 * Created by Jorble on 2016/3/4.
 */
public class ConnectTask extends AsyncTask<Void, Void, Void> {

    private Context context;
    private Data data;
    private Float sun;

    private byte[] read_buff;


    private Socket mSocket;
    private SocketAddress mSocketAddress;
    private InputStream inputStream;
    private OutputStream outputStream;

    private boolean STATU = false;
    private boolean CIRCLE = false;

    private String ip = "";
    private String port = "";

    private String SUN_CHK = "01 03 00 2a 00 01 a5 c2";
    public int SUN_LEN = 7;
    public int SUN_NUM = 1;
    private String tag = "sunCase";
    private String sunData;
    private String TAG = "ConnectTask";
    private Label sunDataLabel;
    private Label connectInfoLabel;

    /**
     * @param context
     * @param data             DataBean类
     * @param connectInfoLabel 连接信息的文本
     * @param sunDataLabel     光照数据的文本
     */
    public ConnectTask(Context context, Data data, String ip, String port, Label connectInfoLabel, Label sunDataLabel) {
        this.context = context;
        this.data = data;
        this.sunDataLabel = sunDataLabel;
        this.connectInfoLabel = connectInfoLabel;
        this.ip = ip;
        this.port = port;


    }

    /**
     * 更新界面
     */
    @Override
    protected void onProgressUpdate(Void... values) {



        if (STATU) {
            connectInfoLabel.Text("连接正常");
        } else connectInfoLabel.Text("连接失败");


        contant.sunData = String.valueOf(data.getSun());

        Log.d(TAG, "onProgressUpdate: " + contant.sunData);

        if (String.valueOf(data.getSun()) != null) {
            sunDataLabel.Text(String.valueOf(data.getSun()));
        }

    }


    /**
     * 准备
     */
    @Override
    protected void onPreExecute() {
        connectInfoLabel.Text("连接中");
    }

    /**
     * 子线程任务
     *
     * @param params
     * @return
     */
    @Override
    protected Void doInBackground(Void... params) {
        mSocket = new Socket();
        mSocketAddress = new InetSocketAddress(ip, Integer.parseInt(port));
        try {
            // socket连接
            mSocket.connect(mSocketAddress, 3000);// 设置连接超时时间为3秒
            if (mSocket.isConnected()) {
                setstatu(true);
                inputStream = mSocket.getInputStream();// 得到输入流
                outputStream = mSocket.getOutputStream();// 得到输出流
            } else {
                setstatu(false);
            }

            // 循环读取数据
            while (CIRCLE) {
                // 查询光照度
                StreamUtil.writeCommand(outputStream, SUN_CHK);
                Thread.sleep(500);
                read_buff = StreamUtil.readData(inputStream);
                sun = FROSun.getData(SUN_LEN, SUN_NUM, read_buff);
                Log.d(tag, "doInBackground: " + sun);
                if (sun != null) {
                    data.setSun((int) (float) sun);
                }

                // 更新界面
                publishProgress();
                Thread.sleep(1000);
            }

        } catch (IOException e) {
            setstatu(false);
            publishProgress();
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 判断socket是否还在连接
     *
     * @return
     */
    public Boolean isSuccess() {
        return mSocket.isConnected();
    }

    /**
     * 获取socket
     *
     * @return
     */
    public Socket getmSocket() {
        return mSocket;
    }

    /**
     * 获取输入流
     *
     * @return
     */
    public InputStream getInputStream() {
        return inputStream;
    }

    /**
     * 获取输出流
     *
     * @return
     */
    public OutputStream getOutputStream() {
        return outputStream;
    }

    public Boolean getSTATU() {
        return STATU;
    }

    public void setstatu(boolean sTATU) {
        STATU = sTATU;
    }

    public boolean getCIRCLE() {
        return CIRCLE;
    }

    public void setCIRCLE(boolean cIRCLE) {
        CIRCLE = cIRCLE;
    }


}
