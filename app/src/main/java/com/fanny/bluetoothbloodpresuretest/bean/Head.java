package com.fanny.bluetoothbloodpresuretest.bean;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by Fanny on 17/4/5.
 */

public class Head implements Parcelable{

    /*测量结果*/
    public final static int TYPE_RESULT=0xFC;

    /*错误信息*/
    public final static int TYPE_ERROR=0xFD;

    /*测量开始*/
    public final static int TYPE_MESSAGE=0x06;

    /*压力数据*/
    public final static int TYPE_PRESSURE=0xFB;

    private int head1;
    private int head2;
    private int type;

    public Head(){}

    public int getHead1() {
        return head1;
    }

    public int getHead2() {
        return head2;
    }

    public int getType() {
        return type;
    }

    public void setHead1(int head1) {
        this.head1 = head1;
    }

    public void setHead2(int head2) {
        this.head2 = head2;
    }

    public void setType(int type) {
        this.type = type;
    }

    /*数据分析*/
    public void analysis(int[] i){
        head1=i[0];
        head2=i[1];
        type=i[2];
    }

    private Head(Parcel in) {
        head1=in.readInt();
        head2=in.readInt();
        type=in.readInt();
    }

    public static final Creator<Head> CREATOR = new Creator<Head>() {
        @Override
        public Head createFromParcel(Parcel in) {
            return new Head(in);
        }

        @Override
        public Head[] newArray(int size) {
            return new Head[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeFloat(head1);
        dest.writeFloat(head2);
        dest.writeFloat(type);
    }
}
