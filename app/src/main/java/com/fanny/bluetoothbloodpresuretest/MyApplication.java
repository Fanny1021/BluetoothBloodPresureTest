package com.fanny.bluetoothbloodpresuretest;

import android.app.Application;
import android.os.Handler;
import android.os.Message;

import com.fanny.bluetoothbloodpresuretest.bean.IBean;
import com.fanny.bluetoothbloodpresuretest.bean.Mesg;
import com.fanny.bluetoothbloodpresuretest.service.BluetoothService;
import com.fanny.bluetoothbloodpresuretest.service.ICallback;


/**
 * Created by Fanny on 17/4/5.
 */

public class MyApplication extends Application{
    private BluetoothService service;

    public static final int REQUEST_CONNECT_DEVICE_SECURE=1;
    public static final int REQUEST_CONNECT_DEVICE_INSECURE=2;
    public static final int REQUEST_CONNECT_DEVICE=1;
    public static final int REQUEST_ENABLE_BT=2;

    private ICallback call;
    private Handler handler;

    public ICallback getCall() {
        return call;
    }

    public BluetoothService getService() {
        return service;
    }

    public void setService(BluetoothService service) {
        this.service = service;
    }

    public void setCall(ICallback call) {
        this.call = call;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        createHandler();
        setupChat();
        service.setHandler(handler);
    }

    private void setupChat() {
        if(service==null){
            service=new BluetoothService();
        }
    }

    private void createHandler() {
        handler=new Handler(){
          public void handlerMessage(Message msg){
              IBean bean=msg.getData().getParcelable("bean");
              if(call!=null){
                  switch (msg.what){
                      case IBean.ERROR:
                          getCall().onError((com.fanny.bluetoothbloodpresuretest.bean.Error) bean);
                          break;
                      case IBean.MASSAGE:
                          getCall().onMessage((Mesg) bean);
                          break;
                      case IBean.DATA:
                          getCall().onReceive(bean);
                          break;
                  }
              }
          }
        };
    }
}
