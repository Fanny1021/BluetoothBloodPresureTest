package com.fanny.bluetoothbloodpresuretest.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Fanny on 17/4/5.
 */

public class Error extends IBean {
    /*连接失败*/
    public static final int ERROR_CONNECTION_FAILED=0;
    /*连接丢失*/
    public static final int ERROR_CONNECTION_LOST=1;

    //血压仪错误信息常量
    /**
     * E-E EEPROM异常
     */
    private static final int ERROR_EEPROM=0x0E;
    /**
     * E-1 人体心跳信号太小或压力突降
     */
    private static final int ERROR_HEART=0x01;
    /**
     * E-2 杂讯干扰
     */
    private static final int ERROR_DISTURB=0x02;
    /**
     * E-3 充气时间过长
     */
    private static final int ERROR_GASING=0x03;
    /**
     * E-5 测量的结果过长
     */
    private static final int ERROR_TEST=0x05;
    /**
     * E-C 矫正异常
     */
    private static final int ERROR_REVISE=0x0C;
    /**
     * E-B 电源低电压
     */
    private static final int ERROR_POWET=0x0B;

    private int error_code;
    private int error;

    public Error() {
        super();
    }
    public Error(int error_code) {
        this.error_code = error_code;
    }



    public int getError_code() {
        return error_code;
    }

    public int getError() {
        return error;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public void setError(int error) {
        this.error = error;
    }

    @Override
    public void analysis(int[] i) {
        error=i[3];
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(error_code);
        dest.writeInt(error);
    }
    public static final Parcelable.Creator<Error> CREATOR=new Parcelable.Creator<Error>(){

        @Override
        public Error createFromParcel(Parcel source) {
            return new Error(source);
        }

        @Override
        public Error[] newArray(int size) {
            return new Error[size];
        }
    };
    public Error(Parcel source) {
        error_code=source.readInt();
        error=source.readInt();
    }
}
