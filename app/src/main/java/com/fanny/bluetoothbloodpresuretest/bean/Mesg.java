package com.fanny.bluetoothbloodpresuretest.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Fanny on 17/4/5.
 */

public class Mesg extends IBean {

    public static final int MESSAGE_STATE_CONNECTING=0;
    public static final int MESSAGE_STATE_NONE=1;
    public static final int MESSAGE_STATE_CONNECTED=2;

    public static final int START=0x06;

    private int msg_code;
    private String device_name;

    public Mesg(int msg_code, String device_name) {
        this.msg_code = msg_code;
        this.device_name = device_name;
    }

    public int getMsg_code() {
        return msg_code;
    }

    public String getDevice_name() {
        return device_name;
    }

    public void setMsg_code(int msg_code) {
        this.msg_code = msg_code;
    }

    public void setDevice_name(String device_name) {
        this.device_name = device_name;
    }

    @Override
    public void analysis(int[] i) {
        msg_code=i[2];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(msg_code);
        dest.writeString(device_name);
    }

    public static final Parcelable.Creator<Mesg> CREATOR=new Parcelable.Creator<Mesg>(){

        @Override
        public Mesg createFromParcel(Parcel in) {
            return new Mesg(in);
        }

        @Override
        public Mesg[] newArray(int size) {
            return new Mesg[size];
        }
    };

    private Mesg(Parcel in) {
        msg_code=in.readInt();
        device_name=in.readString();
    }
}
