package com.example.myapplication4.adapter;

import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.myapplication4.utill.Teacher;

import java.util.ArrayList;

public class SpinnerAdapter extends BaseAdapter {
    ArrayList<Teacher> teacherArrayList = new ArrayList<>();
    public void SpinnerAdapter(ArrayList<Teacher> teacherArrayList){
        this.teacherArrayList = teacherArrayList;
    }
    public class ItemViewHodler {
        public TextView name;
        private LinearLayout background;
    }

    @Override
    public int getCount() {
        return teacherArrayList.size();
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
//        ItemViewHodler holder = null;
//        if (convertView == null) {
//            holder = new ItemViewHodler();
//            convertView = mInflater.inflate(R.layout.music_item, null);
//            holder.img = convertView.findViewById(R.id.item_tv_img);
//            holder.name = convertView.findViewById(R.id.item_tv_name);
//            holder.background = convertView.findViewById(R.id.item_ll_background);
//            convertView.setTag(holder);
//        }else{
//            holder = (ItemViewHodler) convertView.getTag();
//        }
//        holder.img.setText(musicList.get(position).getUri());
//        holder.name.setText(musicList.get(position).getName());
//        if(musicList.get(position).isPlay()){
//            holder.background.setBackgroundColor(Color.BLUE);
//        }else{
//            holder.background.setBackgroundColor(Color.WHITE);
//        }
        return convertView;
    }
}
