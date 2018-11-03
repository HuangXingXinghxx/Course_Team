package com.example.myapplication4.adapter;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.myapplication4.R;

import java.util.ArrayList;
import java.util.Collections;

public class SpinnerAndAutocompleteAdapter extends ArrayAdapter {
    private ArrayList<String> teacherNameList;
    private ArrayList<String> localteacherNameList;
    public SpinnerAndAutocompleteAdapter(@NonNull Context context, int resource, ArrayList<String> teacherNameList, ArrayList<String> localteacherNameList) {
        super(context, resource);
        this.teacherNameList = teacherNameList;
        this.localteacherNameList = localteacherNameList;
    }
    public void Sort(){
        int i=0,j=0,k=0;
        String str="",str1="";
        for(i=0;i<localteacherNameList.size();i++){
            for(k=0;k<teacherNameList.size();k++)
                if(localteacherNameList.get(i).equals(teacherNameList.get(k))){
                    Collections.swap(teacherNameList,j,k);
                    j=j+1;
                }
        }
    }
    @Override
    public int getCount() {
        return teacherNameList.size();
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            Sort();
            View view;
            String str=teacherNameList.get(position);
            view = LayoutInflater.from(getContext()).inflate(R.layout.layout_item,parent,false);
            TextView tx= view.findViewById(R.id.TextView);
            tx.setText(str);
            if(position<localteacherNameList.size())
                tx.setTextColor(ContextCompat.getColor(getContext(),R.color.colorAccent));
            else
                tx.setTextColor(ContextCompat.getColor(getContext(),R.color.colorPrimary));
            return view;
    }
}
