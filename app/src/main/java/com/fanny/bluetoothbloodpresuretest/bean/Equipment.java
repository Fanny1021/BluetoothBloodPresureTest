package com.fanny.bluetoothbloodpresuretest.bean;

import android.bluetooth.BluetoothDevice;

/**
 * Created by Fanny on 17/6/2.
 */

public class Equipment {
    private BluetoothDevice device;
    private Boolean ischecked;

    public Equipment(BluetoothDevice device,Boolean ischecked){
        this.device=device;
        this.ischecked=ischecked;
    }
}
