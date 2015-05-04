package com.quynguyenblog.timwifi.adapter;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.quynguyenblog.timwifi.R;
import com.quynguyenblog.timwifi.model.WifiModel;

import java.util.ArrayList;

/**
 * Created by QuyNguyen on 4/21/2015.
 */
public class WifiListAdapter extends BaseAdapter {
    private Activity mActivity;
    private LayoutInflater mLayoutInflater;
    private ArrayList<WifiModel> mWifiModelArray = new ArrayList<WifiModel>();

    public WifiListAdapter(Activity activity, ArrayList<WifiModel> model){
        mActivity = activity;
        mWifiModelArray = model;
    }

    @Override
    public int getCount() {
        Log.d("quylogcat","Array size: "+mWifiModelArray.size());
        return mWifiModelArray.size();
    }

    @Override
    public Object getItem(int i) {
        return mWifiModelArray.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        if(mLayoutInflater==null)
            mLayoutInflater = (LayoutInflater) mActivity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if(view == null)
            view = mLayoutInflater.inflate(R.layout.wifi_list_row, null);

        TextView wifiName = (TextView) view.findViewById(R.id.list_row_wifi_name);
        TextView wifiPass = (TextView) view.findViewById(R.id.list_row_wifi_pass);

        WifiModel model = mWifiModelArray.get(i);

        wifiName.setText(model.getName());
        wifiPass.setText(model.getPassword());

        return view;
    }
}
