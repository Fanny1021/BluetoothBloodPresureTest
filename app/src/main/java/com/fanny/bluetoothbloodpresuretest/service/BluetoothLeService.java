package com.fanny.bluetoothbloodpresuretest.service;

import android.app.Service;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCallback;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattDescriptor;
import android.bluetooth.BluetoothGattServer;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothProfile;
import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.fanny.bluetoothbloodpresuretest.util.GattAttributesUUID;

import java.util.List;
import java.util.UUID;

/**
 * Created by Fanny on 17/3/27.
 */

public class BluetoothLeService extends Service {
    private final static String TAG=BluetoothService.class.getSimpleName();
    private BluetoothManager mBluetoothManager;
    private BluetoothAdapter mBluetoothAdapter;

    private String MyBluetoothDeviceAddress;

    //GAT profile的封装，通过此对象可以进行gatt client端的数据操作
    private BluetoothGatt mBluetoothGatt;

    //连接状态
    private static final int STATE_DISCONNECTED=0;
    private static final int STATE_CONNECTING=1;
    private static final int STATE_CONNECTED=2;
    private int mConnectionState=STATE_DISCONNECTED;

    // GATT的相关状态
    public final static String ACTION_GATT_CONNECTED="ACTION_GATT_CONNECTED";
    public final static String ACTION_GATT_DISCONNECTED="ACTION_GATT_DISCONNECTED";
    public final static String ACTION_GATT_SERVICES_DISCOVERED="ACTION_GATT_SERVICES_DISCOVERED";
    public final static String ACTION_DATA_AVAILABLE="ACTION_DATA_AVAILABLE";
    public final static String ACTION_GATT_WRITE_SUCCESS="ACTION_GATT_WRITE_SUCCESS";

    private String mUUID = null;
    private String mUUID1=null;
    private BluetoothGattService mservice;
    private BluetoothGattCharacteristic mcharacter;


    //用于传递一些连接状态和结果
    private final BluetoothGattCallback mGattCallback=new BluetoothGattCallback() {

        @Override
        public void onConnectionStateChange(BluetoothGatt gatt, int status, int newState) {
            if (newState== BluetoothProfile.STATE_CONNECTED){
                mConnectionState=STATE_CONNECTED;
                broadcastUpdate(ACTION_GATT_CONNECTED);
                gatt.discoverServices();
//                Log.e(TAG,"启动服务，发现："+gatt.discoverServices());
//                System.out.println("已连接");
            }else if(newState==BluetoothProfile.STATE_DISCONNECTED){
                mConnectionState=STATE_DISCONNECTED;
                broadcastUpdate(ACTION_GATT_DISCONNECTED);
//                Log.e("aaaaaaa","未启动服务");
//                System.out.println("未连接");
            }
            super.onConnectionStateChange(gatt, status, newState);

        }

        @Override
        public void onServicesDiscovered(BluetoothGatt gatt, int status) {
            super.onServicesDiscovered(gatt, status);

            broadcastUpdate(ACTION_GATT_SERVICES_DISCOVERED);
                /*将服务的data打包发送广播出去*/

//                if(status==BluetoothGatt.GATT_SUCCESS){
//                    List<BluetoothGattService> supportedGattServices=gatt.getServices();
//                    for(int i=0;i<supportedGattServices.size();i++){
////                        Log.e("bbbbbbb","服务UUID＝："+supportedGattServices.get(i).getUuid());
//                        UUID mUUID=supportedGattServices.get(i).getUuid();
//                        if(mUUID.equals(GattAttributesUUID.SERVICE_UUID)){
//                            Intent intent=new Intent(ACTION_GATT_SERVICES_DISCOVERED);
//                            sendBroadcast(intent);
//                        }
////                        List<BluetoothGattCharacteristic> listGattCharacteristic =
////                                supportedGattServices.get(i).getCharacteristics();
////                        for(int j=0;j<listGattCharacteristic.size();j++){
////                            Log.e("bbbbbbb","特征UUID=:"+listGattCharacteristic.get(i).getUuid());
////                            int charaProp=listGattCharacteristic.get(i).getProperties();
////                            if((charaProp | BluetoothGattCharacteristic.PROPERTY_READ)>0){
////                                Log.e("bbbbbbb","可读");
////                            }
////                            if((charaProp | BluetoothGattCharacteristic.PROPERTY_WRITE)>0){
////                                Log.e("bbbbbbb","可写");
////                            }
////                            if((charaProp | BluetoothGattCharacteristic.PROPERTY_NOTIFY)>0){
////                                Log.e("bbbbbbb","具备通知属性");
////                            }
////                        }
//                    }
//
//                } else {
//                Log.e(TAG,"发现服务结果"+status);
//            }
        }

        @Override
        public void onCharacteristicRead(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicRead(gatt, characteristic, status);
            if(status==BluetoothGatt.GATT_SUCCESS){
                broadcastUpdate(ACTION_DATA_AVAILABLE, characteristic);
                Log.e(TAG,"读取成功"+characteristic.getValue());
            }
        }

        @Override
        public void onCharacteristicWrite(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic, int status) {
            super.onCharacteristicWrite(gatt, characteristic, status);
            if(status==BluetoothGatt.GATT_SUCCESS){

                Log.e(TAG,"写入成功"+characteristic.getValue());
            }
        }

        @Override
        public void onCharacteristicChanged(BluetoothGatt gatt, BluetoothGattCharacteristic characteristic) {
//            super.onCharacteristicChanged(gatt, characteristic);

//            Log.e(TAG,"数据返回:"+characteristic.getValue());
            broadcastUpdate(ACTION_DATA_AVAILABLE,characteristic);
        }

        @Override
        public void onDescriptorRead(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorRead(gatt, descriptor, status);

        }

        //发送广播
        @Override
        public void onDescriptorWrite(BluetoothGatt gatt, BluetoothGattDescriptor descriptor, int status) {
            super.onDescriptorWrite(gatt, descriptor, status);
            if(status==BluetoothGatt.GATT_SUCCESS){
                Log.e(TAG,"descriptorwrite");
                Intent intent=new Intent(ACTION_GATT_WRITE_SUCCESS);
                sendBroadcast(intent);
            }
        }
    };

    private void broadcastUpdate(final String action) {
        final Intent intent = new Intent(action);
        sendBroadcast(intent);
    }

    public final static String EXTRA_DATA = "com.fanny.bluetoothbloodpresuretest.EXTRA_DATA";

    private void broadcastUpdate(final String action,
                                 final BluetoothGattCharacteristic characteristic) {
        final Intent intent = new Intent(action);

        // This is special handling for the Heart Rate Measurement profile.  Data parsing is
        // carried out as per profile specifications:
        // http://developer.bluetooth.org/gatt/characteristics/Pages/CharacteristicViewer.aspx?u=org.bluetooth.characteristic.heart_rate_measurement.xml
//        if (GattAttributesUUID.SERVICE_UUID.equals(characteristic.getUuid())) {
//            int flag = characteristic.getProperties();
//            int format = -1;
//            if ((flag & 0x01) != 0) {
//                format = BluetoothGattCharacteristic.FORMAT_UINT16;
//                Log.d(TAG, "DATA rate format UINT16.");
//            } else {
//                format = BluetoothGattCharacteristic.FORMAT_UINT8;
//                Log.d(TAG, "DATA rate format UINT8.");
//            }
//            final int data = characteristic.getIntValue(format, 1);
//            Log.d(TAG, String.format("Received heart rate: %d", data));
//            intent.putExtra(EXTRA_DATA, String.valueOf(data));
//        } else {
//            // For all other profiles, writes the data formatted in HEX.
//            final byte[] data = characteristic.getValue();
//            if (data != null && data.length > 0) {
//                final StringBuilder stringBuilder = new StringBuilder(data.length);
//                for(byte byteChar : data)
//                    stringBuilder.append(String.format("%02X ", byteChar));
//                intent.putExtra(EXTRA_DATA, new String(data) + "\n" + stringBuilder.toString());
//            }
//        }
//        sendBroadcast(intent);
        if(GattAttributesUUID.NOTIFY_UUID.equalsIgnoreCase(characteristic.getUuid().toString())){
            final byte[] data=characteristic.getValue();
            for(int i=0;i<data.length;i++){
                int b = data[i];
                Log.e(TAG, String.valueOf(b));
            }
            Bundle bundle=new Bundle();
            bundle.putByteArray("data",data);
            intent.putExtras(bundle);
            sendBroadcast(intent);

        }
    }


    public void readCharacteristic(BluetoothGattCharacteristic characteristic) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.w(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.readCharacteristic(characteristic);
    }


    /**
     * Enables or disables notification on a give characteristic.
     *
     * @param characteristic Characteristic to act on.
     * @param enabled If true, enable notification.  False otherwise.
     */
    public void setCharacteristicNotification(BluetoothGattCharacteristic characteristic,
                                              boolean enabled) {
        if (mBluetoothAdapter == null || mBluetoothGatt == null) {
            Log.e(TAG, "BluetoothAdapter not initialized");
            return;
        }
        mBluetoothGatt.setCharacteristicNotification(characteristic, enabled);

        // This is specific to Heart Rate Measurement.
        if (GattAttributesUUID.NOTIFY_UUID.equalsIgnoreCase(characteristic.getUuid().toString())) {
            BluetoothGattDescriptor descriptor = characteristic.getDescriptor(
                    UUID.fromString(GattAttributesUUID.CLIENT_CHARACTERISTIC_CONFIG));
            descriptor.setValue(BluetoothGattDescriptor.ENABLE_NOTIFICATION_VALUE);
            mBluetoothGatt.writeDescriptor(descriptor);
        }
    }



    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return myBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {

        return super.onUnbind(intent);
    }

    private final IBinder myBinder=new LocalBinder();

    public class LocalBinder extends Binder {
        public BluetoothLeService getService(){
            return BluetoothLeService.this;
        }
    }

    public boolean initialize(){
        if(mBluetoothManager==null){
            mBluetoothManager=(BluetoothManager)getSystemService(Context.BLUETOOTH_SERVICE);
            if(mBluetoothManager==null){
                return false;
            }
        }
        mBluetoothAdapter=mBluetoothManager.getAdapter();
        if(mBluetoothAdapter==null){
            return false;
        }
        return true;
    }

    /*连接蓝牙设备的方法*/
    public boolean connect(String address){
        if(mBluetoothAdapter==null || address==null){
            return false;
        }
        final BluetoothDevice device=mBluetoothAdapter.getRemoteDevice(address);
        if(device==null){
            return false;
        }
        mBluetoothGatt=device.connectGatt(this,true,mGattCallback);

        if(mBluetoothGatt.connect()){

            mConnectionState=STATE_CONNECTING;
            MyBluetoothDeviceAddress=address;
            Log.e(TAG,"state0:"+mConnectionState);

//            int connectionState = mBluetoothGatt.getConnectionState(device);
//            Log.e("1111","state1"+connectionState);

            //读写操作：
//            BluetoothGattService service=mBluetoothGatt.getService(UUID.fromString(GattAttributesUUID.SERVICE_UUID));
//            Log.e("aaa","服务id"+service.getUuid());
//            BluetoothGattCharacteristic characteristic=service.getCharacteristic(UUID.fromString(GattAttributesUUID.WRITE_UUID));
//            //读操作：
//            mBluetoothGatt.readCharacteristic(characteristic);

            return true;
        }else {
            return false;
        }
    }

    public int getConnectionState(){
        return mConnectionState;
    }

    public List<BluetoothGattService> getSupportedGattServices(){
        if(mBluetoothGatt==null){
            return null;
        }
        return mBluetoothGatt.getServices();
    }

    public BluetoothGatt getBluetoothGatt(){
        if(mBluetoothGatt==null){
            return null;
        }
        return mBluetoothGatt;
    }
}
