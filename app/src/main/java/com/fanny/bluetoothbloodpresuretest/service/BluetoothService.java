package com.fanny.bluetoothbloodpresuretest.service;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.fanny.bluetoothbloodpresuretest.bean.Error;
import com.fanny.bluetoothbloodpresuretest.bean.Head;
import com.fanny.bluetoothbloodpresuretest.bean.IBean;
import com.fanny.bluetoothbloodpresuretest.bean.Mesg;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;


/**
 * Created by Fanny on 17/3/27.
 */

public class BluetoothService {
    private static final UUID My_UUID=UUID.fromString("00001101-0000-1000-8000-00805f9b34fb");
    private OutputStream outputStream;
    private InputStream inputStream;
    private BluetoothSocket socket;
    private BluetoothDevice device;

    private Handler handler;
    private ConnectedThread mConnectedThread;

    private int mState;

    public BluetoothService() {
        mState = Mesg.MESSAGE_STATE_NONE;
    }

    public BluetoothDevice getDevice() {
        return device;
    }

    public Handler getHandler() {
        return handler;
    }

    public int getmState() {
        return mState;
    }

    public void setDevice(BluetoothDevice device) {
        this.device = device;
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    public void setmState(int mState) {
        this.mState = mState;
    }

    public void run(){
        connected();
    }


    /*蓝牙socket连接*/
    public void connect() {
        if(device!=null){
            try {
                socket=device.createRfcommSocketToServiceRecord(My_UUID);
                socket.connect();
                mState=Mesg.MESSAGE_STATE_CONNECTING;
                send(IBean.MASSAGE,new Mesg(mState,device.getName()));
                //连接成功后开启接受数据线程
                connected();
            } catch (IOException e) {
                e.printStackTrace();
                connectionFailed();
            }
        }
    }

    /*连接成功后启动接受数据的线程*/
    public void connected() {
        if(socket!=null){
            mConnectedThread=new ConnectedThread();
            mConnectedThread.start();
            mState=Mesg.MESSAGE_STATE_CONNECTED;
            send(IBean.MASSAGE,new Mesg(mState,device.getName()));
        }
    }

    /*接受数据子线程*/
    private class ConnectedThread extends Thread{
        public ConnectedThread() {
            try {
                inputStream=socket.getInputStream();
                outputStream=socket.getOutputStream();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            byte[] buffer=new byte[16];
            while (true){
                try {
                    if(inputStream.available()>0){  //对数据流进行解析
                        Head head=new Head();
                        inputStream.read(buffer);
                        /**
                         * 对流中的数据进行解析
                         */
                        Log.e("aaa","接受到数据流");
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                    connectionLost();
                    interrupt();
                }
            }
        }
    }

    /*发送消息方法*/
    private void send(int code,IBean bean){
        if(handler!=null){
            Message msg=handler.obtainMessage(code);
            Bundle bundle=new Bundle();
            bundle.putParcelable("bean",bean);
            msg.setData(bundle);
            msg.sendToTarget();
        }

    }

    /*发送数据*/
    public void write(byte[] buffer){
        if(outputStream!=null){
            try {
                outputStream.write(buffer);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /*停止连接*/
    public void stop(){
            try {
                if(socket!=null) {
                    socket.close();
                    mState = Mesg.MESSAGE_STATE_NONE;
                    send(IBean.MASSAGE, new Mesg(mState, device.getName()));
                }
                if(mConnectedThread!=null){
                    mConnectedThread.interrupt();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }


    }
    private void connectionLost() {
        //发送错误信息
        send(IBean.ERROR,new Error(Error.ERROR_CONNECTION_LOST));
    }
    private void connectionFailed(){
        //发送错误信息
        send(IBean.ERROR,new Error(Error.ERROR_CONNECTION_FAILED));
    }

}
