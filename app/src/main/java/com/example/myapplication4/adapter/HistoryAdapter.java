package com.example.myapplication4.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.myapplication4.R;

import java.util.ArrayList;
import java.util.Map;

public class HistoryAdapter extends BaseAdapter {
    private LayoutInflater mInflater;
    ArrayList<Map<String,String>> historyList = new ArrayList<>();
    public HistoryAdapter(LayoutInflater inflater, ArrayList<Map<String,String>> teacherArrayList){
        this.historyList = teacherArrayList;
        mInflater = inflater;
    }
    public class ItemViewHodler {
        public TextView name;
        private TextView time;

    }

    @Override
    public int getCount() {
        return historyList.size();
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
        ItemViewHodler holder = null;
        if (convertView == null) {
            holder = new ItemViewHodler();
            convertView = mInflater.inflate(R.layout.item_history,null);
            holder.name = convertView.findViewById(R.id.teacherName);
            holder.time = convertView.findViewById(R.id.times);
            convertView.setTag(holder);
        }else{
            holder = (ItemViewHodler) convertView.getTag();
        }
        Map item = historyList.get(position);
        holder.name.setText((String)item.get("teacher"));
        holder.time.setText((String)item.get("classNum"));
        return convertView;
    }


}
