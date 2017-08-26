package com.fanny.bluetoothbloodpresuretest.service;

import com.fanny.bluetoothbloodpresuretest.bean.Error;
import com.fanny.bluetoothbloodpresuretest.bean.IBean;
import com.fanny.bluetoothbloodpresuretest.bean.Mesg;

/**
 * Created by Fanny on 17/4/5.
 */

public interface ICallback {
    public  void onReceive(IBean bean);
    public  void onMessage(Mesg msg);
    public  void onError(Error error);
}
