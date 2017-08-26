package com.fanny.bluetoothbloodpresuretest.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.fanny.bluetoothbloodpresuretest.MyApplication;
import com.fanny.bluetoothbloodpresuretest.R;
import com.fanny.bluetoothbloodpresuretest.bean.User;
import com.fanny.bluetoothbloodpresuretest.dao.DBOpenHelper;
import com.fanny.bluetoothbloodpresuretest.fragment.HistoryFragment;
import com.fanny.bluetoothbloodpresuretest.fragment.HomeFragment;
import com.fanny.bluetoothbloodpresuretest.fragment.TrendFragment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static com.fanny.bluetoothbloodpresuretest.R.id.et_name;

public class MainActivity extends FragmentActivity implements View.OnClickListener {


    private FrameLayout topLayout;
    private LinearLayout bottomLayout;
    private MyApplication app;
    private HomeFragment homeFragment;
    private TrendFragment trendFragment;
    private HistoryFragment historyFragment;
    private ArrayList<Fragment> fragments;
    private ImageButton btn_home;
    private ImageButton btn_trend;
    private ImageButton btn_hostory;
    private FragmentManager fm;
    private FragmentTransaction ft;
    private RelativeLayout titleLayout;
    private ImageButton user;
    private ImageButton edit;
    private ImageView bluetooth;
    private View view1;
    private EditText ed_name;
    private EditText ed_height;
    private EditText ed_weight;
    private DBOpenHelper dbOpenHelper;
    private RadioGroup rg;
    private RadioButton rb_male;
    private RadioButton rb_female;
    private EditText ed_age;
    private ImageView photo;
    private Bitmap newPhoto;
    private Drawable drawable;

    private View view2;
    private List<User> users;
    private ListView list;
    private MyAdapter myAdapter;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        app = (MyApplication) getApplication();

        users = new ArrayList<User>();
        users.add(new User("ff","femal",25,165,50));

        myAdapter = new MyAdapter();

        dbOpenHelper=new DBOpenHelper(this);

        initview();

        initFragment();

        initclick();
        View firstview=bottomLayout.getChildAt(0);
        onClick(firstview);

        /**
         * 存储用户数据
         */




    }

    private class MyAdapter extends BaseAdapter {
        @Override
        public int getCount() {
            return users.size();
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
        public View getView(int position, View convertView, ViewGroup parent) {
            View v;
            if(convertView==null){
                v=View.inflate(getApplicationContext(),R.layout.user_item,null);
            }else {
                v=convertView;
            }
            TextView item_name = (TextView) v.findViewById(R.id.item_name);
            TextView item_gentle = (TextView) v.findViewById(R.id.item_gentle);
            TextView item_age = (TextView) v.findViewById(R.id.item_age);
            TextView item_height = (TextView) v.findViewById(R.id.item_height);
            TextView item_weight = (TextView) v.findViewById(R.id.item_weight);

            User user0=users.get(position);
            item_name.setText(user0.getName());
            item_gentle.setText(user0.getSex());

            item_age.setText(String.valueOf(user0.getAge()));
            item_height.setText(String.valueOf(user0.getHeight()));
            item_weight.setText(String.valueOf(user0.getWeight()));

            return v;
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
        }
    }

    private void initclick() {
        int childCount=bottomLayout.getChildCount();
        for(int i=0;i<childCount;i++){
            FrameLayout framelayout= (FrameLayout) bottomLayout.getChildAt(i);
            framelayout.setOnClickListener(this);

        }
    }


    private void initview() {
        titleLayout = (RelativeLayout) findViewById(R.id.title_layout);
        topLayout = (FrameLayout) findViewById(R.id.top_layout);
        bottomLayout = (LinearLayout) findViewById(R.id.bottom_layout);


        user = (ImageButton) titleLayout.findViewById(R.id.user);
        edit = (ImageButton) titleLayout.findViewById(R.id.edit);
        bluetooth = (ImageView) titleLayout.findViewById(R.id.bluetooth);




        user.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                /**
                 * 数据库查询user表，封装数据至javabean中，显示在ls中
                 */
                final SQLiteDatabase db=dbOpenHelper.getWritableDatabase();
                if(users!=null){
                    users.clear();
                }
                Cursor cursor=db.query("user",null,null,null,null,null,null);
                if(cursor!=null && cursor.getCount()>0){
                    while (cursor.moveToNext()){

                        User user=new User();
                        user.setName(cursor.getString(cursor.getColumnIndex("name")));
                        user.setSex(cursor.getString(cursor.getColumnIndex("sex")));
                        user.setAge(cursor.getInt(cursor.getColumnIndex("age")));
                        user.setHeight(cursor.getInt(cursor.getColumnIndex("height")));
                        user.setWeight(cursor.getInt(cursor.getColumnIndex("weight")));

                        users.add(user);
                    }
//                    list.setAdapter(new MyAdapter());
                }

//                db.close();

                /**
                 * 利用javabean保存
                 */
                final AlertDialog.Builder user_dia=new AlertDialog.Builder(MainActivity.this);
                view2 = View.inflate(getApplicationContext(), R.layout.user_dialog,null);
                list = (ListView) view2.findViewById(R.id.lv_user);
                myAdapter.notifyDataSetChanged();
                list.setAdapter(myAdapter);

                user_dia.setView(view2);

                user_dia.setTitle("用户信息")
                        .show();

                list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                        return false;
                    }
                });
                list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        AlertDialog.Builder warningdia=new AlertDialog.Builder(MainActivity.this);
                        warningdia.setTitle("是否删除该用户数据？")
                                .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        /**
                                         * 数据库删除操作
                                         */
                                        String desName=users.get(position).getName();
                                        /**
                                         * 删除javabean中数据
                                         */
                                        users.remove(position);
                                        /**
                                         * 删除数据库中数据
                                         */
                                        db.delete("user","name=?",new String[]{desName});
                                        myAdapter.notifyDataSetChanged();

                                    }
                                })
                                .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                }).show();
                    }
                });




            }
        });

        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder edit_dia=new AlertDialog.Builder(MainActivity.this);
                view1 = View.inflate(getApplicationContext(), R.layout.edit_dialog,null);

                photo = (ImageView) view1.findViewById(R.id.user_photo);

                Drawable d=photo.getBackground();
                BitmapDrawable bd= (BitmapDrawable) d;
                newPhoto=bd.getBitmap();

                ed_name = (EditText) view1.findViewById(et_name);
                ed_height = (EditText) view1.findViewById(R.id.et_height);
                ed_weight = (EditText) view1.findViewById(R.id.et_weight);
                ed_age = (EditText) view1.findViewById(R.id.et_age);


                rg = (RadioGroup) view1.findViewById(R.id.rg);
                rb_male = (RadioButton) view1.findViewById(R.id.rb_male);
                rb_female = (RadioButton) view1.findViewById(R.id.rb_female);

                photo.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        ShowPickDialog();
                    }
                });

                edit_dia.setView(view1);
                edit_dia.setPositiveButton("保存", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                saveUser();
                            }
                        })
                        .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();

            }
        });
//        bluetooth.setOnClickListener(this);

    }

    private void ShowPickDialog() {
        new AlertDialog.Builder(this).setTitle("头像设置...")
                .setNegativeButton("打开相册选择", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(Intent.ACTION_PICK,
                                null);
                        intent.setDataAndType(
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                "image/*");
                        startActivityForResult(intent, 1);
                    }
                })
                .setPositiveButton("拍摄一张照片", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        Intent intent = new Intent(
                                MediaStore.ACTION_IMAGE_CAPTURE);
                        // 下面这句指定调用相机拍照后的照片存储的路径
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(Environment
                                        .getExternalStorageDirectory(),
                                        "xiaoma.jpg")));
                        startActivityForResult(intent, 2);
                    }
                }).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            // 如果是直接从相册获取
            case 1:
                startPhotoZoom(data.getData());
                break;
            // 如果是调用相机拍照时
            case 2:
                File temp = new File(Environment.getExternalStorageDirectory()
                        + "/xiaoma.jpg");
                startPhotoZoom(Uri.fromFile(temp));
                break;
            // 取得裁剪后的图片
            case 3:

                if (data != null) {
                    setPicToView(data);
                }
                break;
//            case 100:
//                String bname = data.getExtras().getString("bname");
//                username.setText(bname);
//
//                break;
            default:
                break;



        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * 裁剪图片方法实现
     *
     * @param uri
     */
    public void startPhotoZoom(Uri uri) {

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*");
        // 下面这个crop=true是设置在开启的Intent中设置显示的VIEW可裁剪
        intent.putExtra("crop", "true");
        // aspectX aspectY 是宽高的比例
        intent.putExtra("aspectX", 1);
        intent.putExtra("aspectY", 1);
        // outputX outputY 是裁剪图片宽高
        intent.putExtra("outputX", 150);
        intent.putExtra("outputY", 150);
        intent.putExtra("return-data", true);
        startActivityForResult(intent, 3);
    }

    /**
     * 保存裁剪之后的图片数据
     *
     * @param picdata
     */
    private void setPicToView(Intent picdata) {
        Bundle extras = picdata.getExtras();
        if (extras != null) {
            newPhoto = extras.getParcelable("data");
            drawable = new BitmapDrawable(newPhoto);
            photo.setBackgroundDrawable(drawable);
        }
    }

    private boolean zc=true;
    private void saveUser() {
        SQLiteDatabase db=dbOpenHelper.getWritableDatabase();

        ByteArrayOutputStream os=new ByteArrayOutputStream();


        String sex="";
        int checkedid=rg.getCheckedRadioButtonId();
        if(checkedid==R.id.rb_male){
            sex="男";
        }else {
            sex="女";
        }

        if(ed_name==null || ed_name.getText().toString().length()==0){
            Toast.makeText(this,"姓名输入语法错误",Toast.LENGTH_SHORT).show();
            ed_name.setText("");
            zc=false;
        }else {
            String zcname=ed_name.getText().toString();
            Cursor cursor=db.query("user",new String[]{"name"},null,null,null,null,null);
            while (cursor.moveToNext()){
                String ssname=cursor.getString(cursor.getColumnIndex("name"));
                if(ssname.equals(zcname)){
                    Toast.makeText(this,"用户已注册",Toast.LENGTH_SHORT).show();
                    ed_name.setText("");
                    zc=false;
                }else {
                    zc=true;
                }
            }
            if(ed_weight==null || ed_weight.getText().toString().length()==0){
                Toast.makeText(this,"体重输入语法错误",Toast.LENGTH_SHORT).show();
                ed_weight.setText("");
                zc=false;
            }
            if(ed_height==null || ed_height.getText().toString().length()==0){
                Toast.makeText(this,"身高输入语法错误",Toast.LENGTH_SHORT).show();
                ed_height.setText("");
                zc=false;
            }
            if(ed_age==null || ed_age.getText().toString().length()==0){
                Toast.makeText(this,"年龄输入语法错误",Toast.LENGTH_SHORT).show();
                ed_age.setText("");
                zc=false;
            }
        }

        if(zc==true){
            String name=ed_name.getText().toString();

            String ages=ed_age.getText().toString();
            int age=Integer.parseInt(ages);

            String heights=ed_height.getText().toString();
            int height=Integer.parseInt(heights);

            String weights=ed_weight.getText().toString();
            int weight=Integer.parseInt(weights);

            db.execSQL(
                    "insert into user(name,sex,age,height,weight,touxiang) values(?,?,?,?,?,?)",new Object[]{name,sex,age,height,weight,os.toByteArray()});
            db.close();

            User user=new User(name,sex,age,height,weight);
            users.add(user);


            /**
             * 讲编辑数据保存后，更改用户的信息
             */
//            Intent back=new Intent();
//            back.putExtra("bname",name);
//            startActivityForResult(back, 100);
//            setResult(20,back);
//            finish();
        }



    }

    private void initFragment() {
        homeFragment = new HomeFragment();
        trendFragment = new TrendFragment();
        historyFragment = new HistoryFragment();
        fragments = new ArrayList<>();
        fragments.add(homeFragment);
        fragments.add(trendFragment);
        fragments.add(historyFragment);

    }


    @Override
    public void onClick(View v) {
        int indexfoChild=bottomLayout.indexOfChild(v);
        changeUI(indexfoChild);
        changeFragment(indexfoChild);



    }

    private void changeFragment(int indexofChild) {
        getSupportFragmentManager().beginTransaction()
                .replace(R.id.top_layout,fragments.get(indexofChild)).commit();
    }

    private void changeUI(int indexofChild) {
        int childCount=bottomLayout.getChildCount();
        for(int i=0;i<childCount;i++){
            View childView=bottomLayout.getChildAt(i);
            if(i==indexofChild){
                setEnable(childView,false);
            }else{
                setEnable(childView,true);
            }
        }
    }

    private void setEnable(View childView, boolean b) {
        if(childView instanceof ViewGroup){
            int childCount=((ViewGroup) childView).getChildCount();
            for(int i=0;i<childCount;i++){
                View view=((ViewGroup) childView).getChildAt(i);
                view.setEnabled(b);
            }
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if(event.getKeyCode()==KeyEvent.KEYCODE_BACK){
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    public void delete() {
        SQLiteDatabase db = dbOpenHelper.getWritableDatabase();
        db.delete("user", "name = ?", new String[] { });
        db.delete("sdp", "name = ?", new String[] {  });
        db.close();
    }

}
