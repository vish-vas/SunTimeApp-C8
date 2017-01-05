package com.example.vov.suntimeapp;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collection;

public class CustomListAdapter extends BaseAdapter {

    ArrayList<String> data = new ArrayList<>();
    private static LayoutInflater inflater = null;

    public CustomListAdapter(Activity activity, ArrayList<String> list)
    {
        data = list;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }


    @Override
    public int getCount()
    {
        return data.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View rowView, ViewGroup parent)
    {
        Holder holder;
        if(rowView==null)
        {
            holder = new Holder();
            rowView = inflater.inflate(R.layout.activity_custom_list_adapter, parent, false);
            holder.textView = (TextView) rowView.findViewById(R.id.textView3);
            rowView.setTag(holder);
        }
        else
        {
            holder = (Holder) rowView.getTag();
        }

        holder.textView.setText(data.get(position));
        return rowView;
    }

    public static class Holder
    {
        TextView textView;
    }
}
