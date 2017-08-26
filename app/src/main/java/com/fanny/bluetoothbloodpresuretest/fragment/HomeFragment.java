package com.fanny.bluetoothbloodpresuretest.fragment;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.bluetooth.BluetoothManager;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.fanny.bluetoothbloodpresuretest.MyApplication;
import com.fanny.bluetoothbloodpresuretest.R;
import com.fanny.bluetoothbloodpresuretest.bean.Data;
import com.fanny.bluetoothbloodpresuretest.bean.Error;
import com.fanny.bluetoothbloodpresuretest.bean.Head;
import com.fanny.bluetoothbloodpresuretest.bean.IBean;
import com.fanny.bluetoothbloodpresuretest.bean.Msg;
import com.fanny.bluetoothbloodpresuretest.bean.Pressure;
import com.fanny.bluetoothbloodpresuretest.dao.DBOpenHelper;
import com.fanny.bluetoothbloodpresuretest.service.BluetoothLeService;
import com.fanny.bluetoothbloodpresuretest.util.CodeFormat;
import com.fanny.bluetoothbloodpresuretest.util.GattAttributesUUID;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static android.content.Context.BIND_AUTO_CREATE;

/**
 * Created by Fanny on 17/6/12.
 */

public class HomeFragment extends Fragment implements View.OnClickListener{


    /*搜索lbe蓝牙设备*/
    private BluetoothAdapter bluetoothAdapter;
    private BluetoothDevice mDevice;
    private List<BluetoothDevice> devices = new ArrayList<>();
    private List<Boolean> checkedlist=new ArrayList<>();

    private BluetoothGatt mBluetoothGatt;

    Handler handler=new Handler();

    /*初始化b蓝牙的广播*/
    private MyBroadCastReceiver myBroadCastReceiver=new MyBroadCastReceiver();
    /*接受处理数据的广播*/
    private MyGattUpdateReceiver myGattUpdateReceiver=new MyGattUpdateReceiver();

    private TextView tvconnect;
    private MyAdapter adapter=new MyAdapter();


    private OutputStream outputStream;
    private InputStream inputStream;
    private BluetoothSocket bluetoothSocket;

    private Button find;
    private Button open;
    private Button close;
    private Button sendbtn;
    private EditText sendet;
    private TextView receive;
    private MyApplication app;
    private View bleview;
    private ListView blelv;
    private Button btn_open;
    private View view;

    private ProgressBar progressBar;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        view = LinearLayout.inflate(getContext(), R.layout.homelayout,null);

        find = (Button) view.findViewById(R.id.find);
        open = (Button) view.findViewById(R.id.open);
        close = (Button) view.findViewById(R.id.close);
        sendbtn = (Button) view.findViewById(R.id.btn_send);
        sendet = (EditText) view.findViewById(R.id.et_send);
        receive = (TextView) view.findViewById(R.id.tv_receive);
        progressBar= (ProgressBar) view.findViewById(R.id.progressbar);
        progressBar.setMax(300);

        find.setOnClickListener(this);
        open.setOnClickListener(this);
        close.setOnClickListener(this);
        sendbtn.setOnClickListener(this);
        sendet.setOnClickListener(this);
        receive.setOnClickListener(this);

        tvconnect = (TextView) view.findViewById(R.id.tv_connevt);
        btn_open = (Button) view.findViewById(R.id.btn_open);
        btn_open.setOnClickListener(this);

        /**
         * 初始化设备
         */
        initBluetooth();
        initBLE();
        initDataReceive();

        return view;

    }


    /**
     * 初始化ble设备服务
     */
    private void initDataReceive() {
//        private final static String ACTION_GATT_CONNECTED="ACTION_GATT_CONNECTED";
//        private final static String ACTION_GATT_DISCONNECTED="ACTION_GATT_DISCONNECTED";
//        private final static String ACTION_GATT_SERVICES_DISCOVERED="ACTION_GATT_SERVICES_DISCOVERED";
//        private final static String ACTION_DATA_AVAILABLE="ACTION_DATA_AVAILABLE";
//        private final static String ACTION_GATT_WRITE_SUCCESS="ACTION_GATT_WRITE_SUCCESS";
        IntentFilter intentFilter=new IntentFilter();
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_CONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_DATA_AVAILABLE);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_DISCONNECTED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED);
        intentFilter.addAction(BluetoothLeService.ACTION_GATT_WRITE_SUCCESS);
        getActivity().registerReceiver(myGattUpdateReceiver,intentFilter);

    }

    //初始化ble蓝牙设备
    private void initBLE() {
        //判断手机是否支持ble
        if(!getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH_LE)){
            Toast.makeText(getActivity(),"您的手机不支持ble功能",Toast.LENGTH_SHORT).show();
//            finish();
        }
        //获取蓝牙终端
        final BluetoothManager bluetoothManager= (BluetoothManager) getActivity().getSystemService(Context.BLUETOOTH_SERVICE);
        bluetoothAdapter=bluetoothManager.getAdapter();
        // 判断是否支持蓝牙功能
        if(bluetoothAdapter==null){
            Toast.makeText(getActivity(), "您的手机不支持蓝牙", Toast.LENGTH_SHORT).show();
//            finish();
            return;
        }else{
            System.out.println("初始化完成，支持ble蓝牙");
        }
//        //扫描ble蓝牙
//        startscanLeDevice();

    }


    private boolean mScanning;

    /**
     * 扫描ble设备
     */
    private void startscanLeDevice() {
        mDevice=null;
        if(bluetoothAdapter==null){
            Toast.makeText(getActivity(), "请点击打开蓝牙按钮", Toast.LENGTH_SHORT).show();
//            finish();
        }
        //添加普通蓝牙设备
//        initBluetooth();
        //扫描5秒后停止扫描
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                mScanning=false;
                bluetoothAdapter.stopLeScan(mLeScanCallback);
                Log.e(TAG,"扫描结束");

            }
        },5000);
        mScanning=true;
        bluetoothAdapter.startLeScan(mLeScanCallback);
        Toast.makeText(getActivity(), "正在搜索，请稍后...", Toast.LENGTH_SHORT).show();

    }

    private boolean isSame;//避免重复添加相同设备
    private BluetoothAdapter.LeScanCallback mLeScanCallback=new BluetoothAdapter.LeScanCallback() {
        @Override
        public void onLeScan(final BluetoothDevice device, int rssi, byte[] scanRecord) {
            getActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    isSame=true;
                    for(BluetoothDevice bd:devices){
                        if(bd.getAddress().equals(device.getAddress())){
                            isSame=false;
                        }
                    }
                    if(!devices.contains(device) && isSame){
                        devices.add(device);
                        checkedlist.add(false) ;
                        adapter.notifyDataSetChanged();
                        Log.e(TAG,"添加了新的ble蓝牙设备");
                        Log.e(TAG,"设备数量＝"+devices.size()+";ck集合长度＝"+checkedlist.size());
                    }
                }
            });
        }
    };


    //初始化普通蓝牙配置
    private void initBluetooth() {
//        bluetoothAdapter=BluetoothAdapter.getDefaultAdapter();
        IntentFilter filter=new IntentFilter();
        filter.addAction(BluetoothDevice.ACTION_FOUND);//搜索发现设备
        filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);//状态发生改变
        filter.addAction(BluetoothAdapter.ACTION_SCAN_MODE_CHANGED);//行动扫描模式发生改变
        filter.addAction(BluetoothAdapter.ACTION_STATE_CHANGED);//动作状态发生改变
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);
        //通过广播,扫描设备添加至列表
        getActivity().registerReceiver(myBroadCastReceiver,filter);
    }



//    public final int REQUEST_ENABLE_BT = 110;

    private boolean opendevice=false;

    @Override
    public void onClick(View view) {
        if(bluetoothAdapter==null){
            Toast.makeText(getContext(), "不支持蓝牙", Toast.LENGTH_SHORT).show();
            return;
        }
        switch (view.getId()) {
            /**
             *  蓝牙搜索，弹出设备列表对话框
             */
            case R.id.find:
                if(bluetoothAdapter.isEnabled()){
                    if(devices.size()>0){
                        devices.clear();
                        if(checkedlist.size()>0){
                            checkedlist.clear();
                        }
                        adapter.notifyDataSetChanged();
                    }
//                    bluetoothAdapter1.startDiscovery();
                    //扫描ble蓝牙
                    startscanLeDevice();
                }
                AlertDialog.Builder bleDialog=new AlertDialog.Builder(getContext());
                bleview=View.inflate(getActivity(),R.layout.ble_item,null);
//                bleview = LayoutInflater.from(getContext()).inflate(R.layout.ble_item,null);
                bleDialog.setView(bleview);
                blelv = (ListView) bleview.findViewById(R.id.lv_ble);
                blelv.setAdapter(adapter);

//                blelv.setOnItemClickListener(this);

                bleDialog.setTitle("搜索设备列表")
                        .setPositiveButton("连接", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                if(mDevice!=null){
                                    new Thread(){
                                        @Override
                                        public void run() {
                                            startService();
                                        }

                                    }.start();
                                }

                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        })
                        .show();
                break;
            case R.id.open:
                if(!bluetoothAdapter.isEnabled()){
                    bluetoothAdapter.enable();
                    /*或者从系统界面打开蓝牙
                    Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
                    */
                }

                break;
            case R.id.close:
                if(bluetoothAdapter.isEnabled()){
                    bluetoothAdapter.disable();
                }
                break;
            case R.id.btn_send:
                //调用输出流,发送数据
//                sendCmd();
                break;
            case R.id.btn_open:

                    if(tvconnect.getText().equals(getDeviceName())){
                        //接收血压计传来的数据，进行处理数据
//                doWithData(data);
//                        if(!opendevice){
//                            toSendData(true);
////                            opendevice=true;
//                            btn_open.setText("关闭蓝牙血压计");
//                        }else {
//                            toSendData(false);
////                            opendevice=false;
//                            btn_open.setText("开启蓝牙血压计");
//                        }

                        toSendData(true);
                    }


                break;

        }

    }


    /*点击设备条目，和设备进行匹配并连接*/
//    @Override
//    public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
//
//        final BluetoothDevice bluetoothDevice=devices.get(position);
//        if(checkedlist.get(position)==true){
//            mDevice=bluetoothDevice;
//        }
//
//    }

    private String getDeviceName() {
        return "Bluetooth BP";
    }

    private boolean isBindService;
    private BluetoothLeService mBluetoothLeService;

    protected void startService(){
        if(!isBindService){
            Intent gattServiceIntent=new Intent(getContext(), BluetoothLeService.class);
            //启动service
            isBindService=getActivity().bindService(gattServiceIntent,mServiceConnection,BIND_AUTO_CREATE);
            Log.e(TAG,"是否绑定服务："+isBindService);
        }else {
            if(mBluetoothLeService!=null){
                Log.e(TAG,"蓝牙地址："+mDevice.getAddress());
                Log.e(TAG,"蓝牙名称："+mDevice.getName());
                mBluetoothLeService.connect(mDevice.getAddress());
            }
        }

    }

    private final ServiceConnection mServiceConnection=new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mBluetoothLeService=((BluetoothLeService.LocalBinder)service).getService();
            if(!mBluetoothLeService.initialize()){
//                finish();
            }
            handler.post(new Runnable() {
                @Override
                public void run() {
                    boolean isOK=mBluetoothLeService.connect(mDevice.getAddress());
                    if(!isOK){
                        reTryConnected();
                    }
                }
            });
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            mBluetoothLeService=null;
        }
    };

    private int reTryCount;
    private void reTryConnected() {
        reTryCount++;
        if(reTryCount<4){
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    boolean isOk=mBluetoothLeService.connect(getDeviceName());
                    if(!isOk){
                        reTryConnected();
                    }
                }
            },5000);
        }
    }



    /*普通蓝牙的状态广播*/
    private class MyBroadCastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            BluetoothDevice device;

            if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_STARTED)){
                // Toast.makeText(context,"扫描开始",Toast.LENGTH_SHORT).show();
            }else if(action.equals(BluetoothAdapter.ACTION_DISCOVERY_FINISHED)){
                // Toast.makeText(context,"扫描结束",Toast.LENGTH_SHORT).show();
            }
            else if(action.equals(BluetoothDevice.ACTION_FOUND)){
                // System.out.println("扫描到一个设备" + device.getName());
                device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                devices.add(device);
                adapter.notifyDataSetChanged();

            }else if(action.equals(BluetoothDevice.ACTION_BOND_STATE_CHANGED)){
                device=intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                switch (device.getBondState()){
                    case BluetoothDevice.BOND_BONDED://正在配对

                }
            }
        }

    }

    private String TAG="MainActivity";

    /*接受蓝牙连接获取通道状态类广播*/
    private class MyGattUpdateReceiver extends BroadcastReceiver{

        @Override
        public void onReceive(Context context, Intent intent) {
            String action=intent.getAction();
            if(BluetoothLeService.ACTION_GATT_CONNECTED.equals(action)){
                Log.e(TAG,"获取到gatt通道");
            }else if(BluetoothLeService.ACTION_GATT_DISCONNECTED.equals(action)){
                Log.e(TAG,"未获取到gatt通道");
            }
            if(BluetoothLeService.ACTION_GATT_SERVICES_DISCOVERED.equals(action)){
                Log.e(TAG,"发现服务");

                /**
                 * 在此处更改的ui——设置已连接的设备名称
                 */
                tvconnect.setText(mDevice.getName());

                /*进行读写操作*/
                displayGattService(mBluetoothLeService.getSupportedGattServices());

            }
            if(BluetoothLeService.ACTION_DATA_AVAILABLE.equals(action)){
                Log.e(TAG,"数据流中有数据");
                byte[] data=intent.getExtras().getByteArray("data");
                Log.e(TAG, Arrays.toString(data));

//                if(data.length>=6){
//
//                }
                //接收血压计传来的数据，进行处理数据
                doWithData(data);
//                if(data.length>=6){
//                    showData(data);
//                }


            }
            if(BluetoothLeService.ACTION_GATT_WRITE_SUCCESS.equals(action)){
                Log.e(TAG,"数据写入成功");
            }

        }
    }

    protected void doWithData(byte[] data) {
        if(data.length==1 && (byte)data[0]==-91){
            //通知手机连接
            // 手机发送｛－3，－3，－6，5，13，10｝通知血压计可以测量了
//            toSendData();
            btn_open.setEnabled(true);
        }else if(data.length==5 && data[0]==data[1] && data[0]==-3 && data[2]==6){
            //通知手机开始测量

        }else if(data.length==6 && data[0]==data[1] && data[1]==data[2] && data[0]==-3 ){
            //血压计测量结果异常

        }else if(data.length==7 && data[0]==data[1] && data[1]==-3 && data[2]==-5){
            //血压计测量过程中返回数据
            receive.setText(data.toString());
            showData(data);
        }
    }


    /**
     * 解析血压计数据，进行显示
     */

    protected void showData(byte[] buffer) {
        // TODO Auto-generated method stub
        Head head = new Head();
        int[] f = CodeFormat.bytesToHexStringTwo(buffer, 6);
        head.analysis(f);
        if (head.getType() == Head.TYPE_ERROR) {
            // APP接收到血压仪的错误信息

            Error error=new Error();
            error.analysis(f);
            error.setHead(head);
            // 前台根据错误编码显示相应的提示

//            onError(error);

        }
        if (head.getType() == Head.TYPE_RESULT) {
            // APP接收到血压仪的测量结果
            Data data = new Data();
            data.analysis(f);
            data.setHead(head);
            // 前台根据测试结果来画线性图
            onReceive(data);
//			send(IBean.DATA, data);
        }

        if (head.getType() == Head.TYPE_MESSAGE) {
            // APP接收到血压仪开始测量的通知
            Msg msg = new Msg();
            msg.analysis(f);

            msg.setHead(head);
            onReceive(msg);
            //send(IBean.MESSAGE, msg);
        }
        if (head.getType() == Head.TYPE_PRESSURE) {
            // APP接受到血压仪测量的压力数据
            Pressure pressure = new Pressure();
            pressure.analysis(f);
            pressure.setHead(head);
            // 每接收到一条数据就发送到前台，以改变进度条的显示
            onReceive(pressure);
//			send(IBean.DATA, pressure);
        }
    }

    /**
     * 存储接收到的血压数据信息
     * @param bean
     */
    public void onReceive(IBean bean) {
        switch (bean.getHead().getType()) {
            case Head.TYPE_PRESSURE:
                progressBar.setProgress(((Pressure) bean).getPressure());
//                sdp.setText(((Pressure) bean).getPressureHL() + "");
                break;
            case Head.TYPE_RESULT:
                Data data = (Data) bean;
                ContentValues value = new ContentValues();
                SimpleDateFormat formatter = new SimpleDateFormat(
                        "yyyy-MM-dd hh:mm aaa");
                Date curDate = new Date(System.currentTimeMillis());// 获取当前时间
                String time = formatter.format(curDate);
                int sys = data.getSys();
                int dia = data.getDia();
                int pul = data.getPul();
                /**
                 * 数据库中插入data测量数据
                 */
                DBOpenHelper dbOpenHelper=new DBOpenHelper(getActivity());
                SQLiteDatabase db=dbOpenHelper.getWritableDatabase();
                db.execSQL(
                        "insert into data(time,sys,dia,pul) values(?,?,?,?,?,?)",new Object[]{time,sys,dia,pul});
                db.close();

//                Intent intent = new Intent(OneoneActivity.this, OneActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putString("name", ggname);
//                bundle.putString("time", str);
//                bundle.putInt("sys", sys);
//                bundle.putInt("dia", dia);
//                bundle.putInt("pul", pul);
			/* 把bundle对象assign给Intent */
//                intent.putExtras(bundle);
//                startActivity(intent);
//
//                sendBroadcast(new Intent(SampleGattAttributes.DISCONNECTEDBLE));
//                finish();
        }
    }

//    public void onError(Error error) {
//		/*if (error.getHead() == null)
//			Toast.makeText(
//					this,
//					getResources().getStringArray(R.array.connect_message)[error
//							.getError_code()], Toast.LENGTH_SHORT).show();
//		else*/
//        sendBroadcast(new Intent(SampleGattAttributes.DISCONNECTEDBLE));
//
//        switch (error.getError()) {
//            case Error.ERROR_EEPROM:
//                new AlertDialog.Builder(OneoneActivity.this)
//                        .setMessage(
//                                "The blood pressure monitor is abnormal, please contact the dealer")
//                        .setPositiveButton("Yes",
//                                new DialogInterface.OnClickListener() {
//
//                                    @Override
//                                    public void onClick(DialogInterface dialog,
//                                                        int arg1) {
//                                        // TODO Auto-generated method stub
//                                        dialog.dismiss();
//                                        Intent one = new Intent();
//                                        one.putExtra("bname", ggname);
//                                        setResult(20, one);
//                                        OneoneActivity.this.finish();
//                                    }
//
//                                }).show();
//                break;
//            case Error.ERROR_HEART:
//                new AlertDialog.Builder(OneoneActivity.this)
//                        .setMessage(
//                                "Incorrect measurement, please follow the instruction manual, then please re-wrap the cuff, keep quiet and remeasure.")
//                        .setPositiveButton("Yes",
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog,
//                                                        int arg1) {
//                                        // TODO Auto-generated method stub
//                                        dialog.dismiss();
//                                        Intent one = new Intent();
//                                        one.putExtra("bname", ggname);
//                                        setResult(20, one);
//                                        OneoneActivity.this.finish();
//                                    }
//
//                                }).show();
//                break;
//            case Error.ERROR_DISTURB:
//                // Toast.makeText(this, "E-2 杂讯干扰!", Toast.LENGTH_SHORT).show();
//                new AlertDialog.Builder(OneoneActivity.this)
//                        .setMessage(
//                                "Incorrect measurement, please follow the instruction manual, then please re-wrap the cuff, keep quiet and remeasure.")
//                        .setPositiveButton("Yes",
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog,
//                                                        int arg1) {
//                                        // TODO Auto-generated method stub
//                                        dialog.dismiss();
//                                        Intent one = new Intent();
//                                        one.putExtra("bname", ggname);
//                                        setResult(20, one);
//                                        OneoneActivity.this.finish();
//                                    }
//
//                                }).show();
//                break;
//            case Error.ERROR_GASING:
//                // Toast.makeText(this, "E-3 充气时间过长!",
//                // Toast.LENGTH_SHORT).show();
//                new AlertDialog.Builder(OneoneActivity.this)
//                        .setMessage(
//                                "Incorrect measurement, please follow the instruction manual, then please re-wrap the cuff, keep quiet and remeasure.")
//                        .setPositiveButton("Yes",
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog,
//                                                        int arg1) {
//                                        // TODO Auto-generated method stub
//                                        dialog.dismiss();
//                                        Intent one = new Intent();
//                                        one.putExtra("bname", ggname);
//                                        setResult(20, one);
//                                        finish();
//                                    }
//
//                                }).show();
//                break;
//            case Error.ERROR_TEST:
//                new AlertDialog.Builder(OneoneActivity.this)
//                        .setMessage(
//                                "Incorrect measurement, please follow the instruction manual, then please re-wrap the cuff, keep quiet and remeasure.")
//                        .setPositiveButton("Yes",
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog,
//                                                        int arg1) {
//                                        // TODO Auto-generated method stub
//                                        dialog.dismiss();
//                                        Intent one = new Intent();
//                                        one.putExtra("bname", ggname);
//                                        setResult(20, one);
//                                        OneoneActivity.this.finish();
//                                    }
//
//                                }).show();
//                break;
//            case Error.ERROR_REVISE:
//                // Toast.makeText(this, "E-C 校正异常!", Toast.LENGTH_SHORT).show();
//                new AlertDialog.Builder(OneoneActivity.this)
//                        .setMessage(
//                                "Incorrect measurement, please follow the instruction manual, then please re-wrap the cuff, keep quiet and remeasure.")
//                        .setPositiveButton("Yes",
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog,
//                                                        int arg1) {
//                                        // TODO Auto-generated method stub
//                                        dialog.dismiss();
//                                        Intent one = new Intent();
//                                        one.putExtra("bname", ggname);
//                                        setResult(20, one);
//                                        OneoneActivity.this.finish();
//                                    }
//
//                                }).show();
//                break;
//            case Error.ERROR_POWER:
//                // Toast.makeText(this, "E-B 电源低电压!",
//                // Toast.LENGTH_SHORT).show();
//                new AlertDialog.Builder(OneoneActivity.this)
//                        .setMessage(
//                                "Low batteries, please replace the batteries.")
//                        .setPositiveButton("Yes",
//                                new DialogInterface.OnClickListener() {
//                                    @Override
//                                    public void onClick(DialogInterface dialog,
//                                                        int arg1) {
//                                        // TODO Auto-generated method stub
//                                        dialog.dismiss();
//                                        Intent one = new Intent();
//                                        one.putExtra("bname", ggname);
//                                        setResult(20, one);
//                                        OneoneActivity.this.finish();
//                                    }
//
//                                }).show();
//                break;
//        }
//    }


    /**
     * 向血压计发送数据，成功控制设备打开进行测量了。[2017/4/6]
     */

    private void toSendData( boolean isopen) {
        if(isopen){
            /**
             * 打开设备
             */
            //        byte[] sendData={(-3),(-3),(-6),5,13,10};
            byte[] sendData={(byte) 0xfd,(byte) 0xfd,(byte) 0xfa,0x05,0x0d,0x0a};
            gattCharacteristicWrite.setValue(sendData);
            mBluetoothLeService.getBluetoothGatt().writeCharacteristic(gattCharacteristicWrite);
            opendevice=true;
        }else {
            /**
             * 关闭设备
             */
            //        byte[] sendData={(-3),(-3),(-6),5,13,10};
            //[0xFD,0xFD,0xFE, 0x06, 0X0D, 0x0A]
            byte[] sendData={(byte) 0xfd,(byte) 0xfd,(byte) 0xfe,0x06,0x0d,0x0a};
            gattCharacteristicWrite.setValue(sendData);
            mBluetoothLeService.getBluetoothGatt().writeCharacteristic(gattCharacteristicWrite);
            opendevice=false;
        }

    }

    private BluetoothGattCharacteristic gattCharacteristicWrite;
    private void displayGattService(List<BluetoothGattService> gattServices) {
        if(gattServices==null){
            Log.e(TAG,"没有支持的服务通道");
            return;
        }
        for(BluetoothGattService gattService :gattServices){
            String uuid=gattService.getUuid().toString();
//            List<BluetoothGattCharacteristic> gattCharacteristics=gattService.getCharacteristics();
            if(uuid.length()>0){

                if(uuid.equalsIgnoreCase(GattAttributesUUID.SERVICE_UUID)){
                    Log.e(TAG,"发现与service的uuid符合的uuid");
                    Log.e(TAG,"uuid："+uuid);
                    List<BluetoothGattCharacteristic> gattCharacteristics=gattService.getCharacteristics();
//                    mBluetoothGatt.setCharacteristicNotification(gattService.getCharacteristic(UUID.fromString(uuid)),true);
                    for(BluetoothGattCharacteristic gattCharacteristic : gattCharacteristics){
                        String uuid1=gattCharacteristic.getUuid().toString();
                        if(uuid1.equalsIgnoreCase(GattAttributesUUID.NOTIFY_UUID)){
                            mBluetoothLeService.setCharacteristicNotification(gattCharacteristic,true);
                            Log.e(TAG,"notify_uuid:"+uuid1);
                            //接受读取数据
//                            mBluetoothLeService.readCharacteristic(gattCharacteristic);
                        }
                        if(uuid1.equalsIgnoreCase(GattAttributesUUID.WRITE_UUID)){
                            gattCharacteristicWrite=gattCharacteristic;
                            Log.e(TAG,"write_uuid:"+uuid1);

                        }
                    }
                }
//                else {
//                    Log.e(TAG,"没发现对应的uuid");
//                    Log.e(TAG,"uuid为"+uuid);
//                }
            }

        }
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        if(myBroadCastReceiver!=null){
            getContext().unregisterReceiver(myBroadCastReceiver);
        }
        if(myGattUpdateReceiver!=null){
            getContext().unregisterReceiver(myGattUpdateReceiver);
        }

        if(outputStream!=null ){
            try {
                outputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(inputStream!=null ){
            try {
                inputStream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if(bluetoothSocket!=null){
            try {
                bluetoothSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }



    private class MyAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return devices.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View view, ViewGroup viewGroup) {
            ViewHolder holder= null;
            if(view==null){
                view=View.inflate(viewGroup.getContext(),R.layout.item,null);
                holder=new ViewHolder(view);
                view.setTag(holder);
            }else {
                holder= (ViewHolder) view.getTag();
            }
            BluetoothDevice bluetoothDevice=devices.get(position);
            holder.tvName.setText(bluetoothDevice.getName());
            holder.tvAddress.setText(bluetoothDevice.getAddress());
            /**
             * 此处的选中状态用checklist来决定
             */
            holder.checkBox.setChecked(checkedlist.get(position));

            /**
             * 保证checkbox为单选。这里使用list集合来保存checkbox的选中状态
             */
            holder.checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(isChecked){
                        for(int i=0;i<checkedlist.size();i++){
                            if(i==position){
                                checkedlist.set(i,true);
                                mDevice=devices.get(i);
                            }else {
                                checkedlist.set(i,false);
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                }
            });
            return  view;
        }
    }

    class ViewHolder{
        TextView tvName;
        TextView tvAddress;
        CheckBox checkBox;
        ViewHolder(View view){
            tvName= (TextView) view.findViewById(R.id.tv_name);
            tvAddress= (TextView) view.findViewById(R.id.tv_address);
            checkBox= (CheckBox) view.findViewById(R.id.mck);
        }
    }
}
