package com.google.firebase.codelab.friendlychat;

import android.content.Context;
import android.database.DataSetObserver;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

import kankan.wheel.widget.adapters.ArrayWheelAdapter;
import kankan.wheel.widget.adapters.WheelViewAdapter;

//import com.wx.wheelview.adapter.ArrayWheelAdapter;

/**
 * Created by zeevi on 2/3/2017.
 */

public class MyArrayWheelAdapter implements WheelViewAdapter {
    private LayoutInflater mInflater;
    private Context mContext;
    private ArrayList<User>  mUserArrayList;

    public MyArrayWheelAdapter(Context mContext, ArrayList<User>  userArrayList) {
        this.mContext = mContext;
        mInflater = LayoutInflater.from(mContext);
        mUserArrayList = userArrayList;
    }

    @Override
    public int getItemsCount() {
        return mUserArrayList.size();
    }

    @Override
    public View getItem(int i, View view, ViewGroup viewGroup) {
        View view1 = mInflater.inflate(R.layout.vertical_wheel_view,viewGroup,false);
        return view1;
    }

    @Override
    public View getEmptyItem(View view, ViewGroup viewGroup) {
        return null;
    }

    @Override
    public void registerDataSetObserver(DataSetObserver dataSetObserver) {

    }

    @Override
    public void unregisterDataSetObserver(DataSetObserver dataSetObserver) {

    }
}