package com.fanny.bluetoothbloodpresuretest.bean;

import android.os.Parcelable;

/**
 * Created by Fanny on 17/4/5.
 */

public abstract class IBean implements Parcelable{
    public final static int DATA=0;/*数据*/
    public final static int MASSAGE=1;/*信息*/
    public final static int ERROR=2;/*错误*/
    public final static int PRESSURE=3;/*血压*/

    private Head head;


    public IBean() {
       head=null;
    }

    public Head getHead() {
        return head;
    }

    public void setHead(Head head) {
        this.head = head;
    }
    public abstract void analysis(int[] i);
}
