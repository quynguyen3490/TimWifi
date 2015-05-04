package com.quynguyenblog.timwifi.model;

/**
 * Created by QuyNguyen on 4/21/2015.
 */
public class WifiModel {
    private String mac;
    private String name;
    private String password;
    private double lat;
    private double lng;

    public WifiModel(String mac, String name, String password, double lat, double lng) {
        this.mac = mac;
        this.name = name;
        this.password = password;
        this.lat = lat;
        this.lng = lng;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public String getMac() {
        return mac;
    }

    public void setMac(String mac) {
        this.mac = mac;
    }
}
