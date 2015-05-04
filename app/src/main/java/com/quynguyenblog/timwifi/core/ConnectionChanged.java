package com.quynguyenblog.timwifi.core;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.quynguyenblog.timwifi.R;

/**
 * Created by QuyNguyen on 4/19/2015.
 */
public class ConnectionChanged extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View layout = ((Activity) context).findViewById(R.id.wifi_post_nof);
        TextView lbWifiName = (TextView) layout.findViewById(R.id.wifi_post_nof_wifi_name);
        lbWifiName.setText("Bạn đang kết nối đến "+ConnectionChanged.getSSIDWifi(context));

        if(ConnectionChanged.isWifiConnected(context)){
            layout.animate().alpha(0.9f);
        }else{
            layout.animate().alpha(0.0f);
        }
    }

    public static String getSSIDWifi(Context context){
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        return wifiInfo.getSSID();
    }

    public static String getMACAddressWifi(Context context){
        WifiManager wifiMgr = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiMgr.getConnectionInfo();
        return wifiInfo.getBSSID();
    }

    public static boolean isWifiConnected(Context context){
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        return mWifi.isConnected();
    }

    public static boolean isConnected(Context context){
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connManager.getActiveNetworkInfo();
        if(networkInfo!=null && networkInfo.isConnected()){
            return true;
        }else{
            return false;
        }
    }
}
