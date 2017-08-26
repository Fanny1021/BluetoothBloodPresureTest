package com.fanny.bluetoothbloodpresuretest.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.fanny.bluetoothbloodpresuretest.R;
import com.fanny.bluetoothbloodpresuretest.bean.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Fanny on 17/6/13.
 */

public class HistoryFragment extends Fragment {

    private List<Data> myDataList=new ArrayList<>();


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LinearLayout.inflate(getContext(), R.layout.historylayout, null);

        /**
         * 测量数据
         */

        myDataList.add(new Data("2017-01",9,80,70,60));
        myDataList.add(new Data("2017-02",9,80,70,60));
        myDataList.add(new Data("2017-03",9,80,70,60));
        myDataList.add(new Data("2017-04",9,80,70,60));
        myDataList.add(new Data("2017-05",9,80,70,60));
        myDataList.add(new Data("2017-06",9,80,70,60));
        myDataList.add(new Data("2017-07",9,80,70,60));

        /**
         * listview
         */
        ListView list = (ListView) view.findViewById(R.id.list_history);
        list.setAdapter(new MyAdapeter());
        return view;
    }


    public class MyAdapeter extends BaseAdapter {
        @Override
        public int getCount() {
            return myDataList.size();
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
            ViewHolder myHolder;
            if(convertView==null){
                convertView=View.inflate(getContext(),R.layout.history_item,null);
                myHolder=new ViewHolder(convertView);
                convertView.setTag(myHolder);
            }else {
                myHolder= (ViewHolder) convertView.getTag();
            }
            Data data=myDataList.get(position);
            myHolder.tv_time.setText( String.valueOf(data.getTime()));
            myHolder.tv_sys.setText( String.valueOf(data.getSys()));
            myHolder.tv_dia.setText( String.valueOf(data.getDia()));
            myHolder.tv_pulse.setText( String.valueOf(data.getPul()));
            return convertView;
        }
    }

    class ViewHolder {
        TextView tv_time;
        TextView tv_sys;
        TextView tv_dia;
        TextView tv_pulse;

        ViewHolder(View view) {
            tv_time = (TextView) view.findViewById(R.id.his_time);
            tv_sys = (TextView) view.findViewById(R.id.his_sys);
            tv_dia = (TextView) view.findViewById(R.id.his_dia);
            tv_pulse = (TextView) view.findViewById(R.id.his_pulse);
        }
    }
}
