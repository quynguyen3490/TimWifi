package com.quynguyenblog.timwifi;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.IntentFilter;
import android.location.Location;
import android.net.ConnectivityManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.maps.*;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.quynguyenblog.timwifi.adapter.MyAdapter;
import com.quynguyenblog.timwifi.adapter.WifiListAdapter;
import com.quynguyenblog.timwifi.app.AppController;
import com.quynguyenblog.timwifi.core.ConnectionChanged;
import com.quynguyenblog.timwifi.core.GPSTracking;
import com.quynguyenblog.timwifi.model.WifiModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends ActionBarActivity {
    private Activity mActivity = this;
    public static final String TAG = "quylogcat";
    private RequestQueue mRequestQueue;

    private Toolbar toolbar;

    private String[] TITLES = {"Home","Event","Mail","Shop","Travel"};
    private int[] ICONS = {R.mipmap.ic_launcher,
            R.mipmap.ic_launcher,
            R.mipmap.ic_launcher,
            R.mipmap.ic_launcher,
            R.mipmap.ic_launcher};
    private String NAME = "Quy Nguyen";
    private String EMAIL = "quynguyen3490@gmail.com";
    private int PROFILE = R.mipmap.ic_launcher;

    RecyclerView recyclerView;
    RecyclerView.Adapter adapter;

    LinearLayout wifiPopupLayout;
    TextView lbWifiName;

    GoogleMap mGoogleMap;
    Location mLocation;
    private int DEFAULT_ZOOM = 15;
    private double DEFAULT_DISTANCE = 0.3; //kilometer
    GPSTracking mGPSTracking;

    ListView mWifiListView;

    final static String SERVER_URL = "http://quy.ddns.net/timwifi/wifi";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* create toolbar */
        toolbar = (Toolbar) findViewById(R.id.tool_bar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setIcon(R.mipmap.ic_wifi);

        recyclerView = (RecyclerView) findViewById(R.id.RecyclerView);
        recyclerView.setHasFixedSize(true);

        adapter = new MyAdapter(TITLES,ICONS,NAME,EMAIL,PROFILE);

        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        /* create toolbar */

        /* register all broadcast */
        this.registerReceiverBroadcast();
        /* register all broadcast */

        /* declare view element*/
        wifiPopupLayout = (LinearLayout) findViewById(R.id.wifi_post_nof);
        lbWifiName = (TextView) findViewById(R.id.wifi_post_nof_wifi_name);
        mWifiListView = (ListView) findViewById(R.id.wifi_list);
        /* declare view element*/

        if(ConnectionChanged.isWifiConnected(getApplicationContext())){
            wifiPopupLayout.animate().alpha(0.9f);
        }

        mGPSTracking = new GPSTracking(getApplicationContext());
        mLocation = mGPSTracking.getLocation();
        createGoogleMapView();

//        getWifiFromServer(mGPSTracking.getLat(), mGPSTracking.getLng());

        getWifiFromServer(mLocation.getLatitude(),mLocation.getLongitude());
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement

        return super.onOptionsItemSelected(item);
    }

    public void registerReceiverBroadcast(){
        this.registerReceiver(new ConnectionChanged(), new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
    }

    public void createGoogleMapView(){
        try{
            if(mGoogleMap==null){
                mGoogleMap = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapView)).getMap();
                mGoogleMap.setMyLocationEnabled(true);
                if(mGoogleMap==null){
                    Log.d("quylogcat","Khong tao duoc google map.");
                }else{
                    mLocation = mGPSTracking.getLocation();
                    CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(mGPSTracking.getLat(),mGPSTracking.getLng()),DEFAULT_ZOOM);
                    mGoogleMap.animateCamera(cameraUpdate);

                    //get center camera
                    mGoogleMap.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                        @Override
                        public void onCameraChange(CameraPosition cameraPosition) {
//                            AppController.getInstance().cancelPendingRequest(AppController.TAG);
//                            LatLng currentCamera = cameraPosition.target;
//                            getWifiFromServer(currentCamera.latitude,currentCamera.longitude);
                        }
                    });
                }
            }
        }catch (Exception e){
            Log.d(TAG, e.getMessage());
        }
    }

    public void showPostWifiDialog(View view) {
        final EditText wifiPass = new EditText(this);
        wifiPass.setHint(R.string.wifi_input_hint);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Chia sẻ "+ConnectionChanged.getSSIDWifi(getApplicationContext()))
            .setView(wifiPass)
            .setCancelable(true)
            .setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    postWifiToServer(wifiPass.getText().toString());
                }
            })
            .setPositiveButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    //Do nothing
                }
            })
            .show();
    }

    public void postWifiToServer(final String wifiPass){
        StringRequest stringRequest = new StringRequest(Request.Method.POST, SERVER_URL,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.d("quylogcat",response);
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.getBoolean("status")) {
                                Toast.makeText(getApplicationContext(), R.string.post_success, Toast.LENGTH_SHORT).show();
                                wifiPopupLayout.animate().alpha(0.0f);
                            }
                        } catch (JSONException e) {
                            Log.d("quylogcat", e.getMessage());
                        }
                        getWifiFromServer(mLocation.getLatitude(),mLocation.getLongitude());
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("quylogcat",error.getMessage());
                    }
                }){
                    @Override
                    protected Map<String, String> getParams() throws AuthFailureError {
                        Map<String,String> params = new HashMap<String,String>();
                        params.put("mac",ConnectionChanged.getMACAddressWifi(getApplicationContext()));
                        params.put("name",ConnectionChanged.getSSIDWifi(getApplicationContext()).replace("\"",""));
                        params.put("password",wifiPass);
                        params.put("address","unknown");
                        params.put("lat",String.valueOf(mGPSTracking.getLat()));
                        params.put("lng",String.valueOf(mGPSTracking.getLng()));
                        return params;
                    }
                };
        AppController.getInstance().addToRequestQueue(stringRequest);
    }

    public ArrayList<WifiModel> getWifiFromServer(double lat, double lng){
        final ArrayList<WifiModel> modelArrayList = new ArrayList<WifiModel>();
        String url = SERVER_URL + "/" + lat + "/" + lng + "/" + DEFAULT_DISTANCE;
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(url,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        Log.d("quylogcat", "Request response: " + response.toString());
                        for (int i = 0; i < response.length(); i++) {
                            try {
                                JSONObject obj = response.getJSONObject(i);
                                WifiModel model = new WifiModel(obj.getString("mac"),
                                        obj.getString("name"),
                                        obj.getString("password"),
                                        obj.getDouble("lat"),
                                        obj.getDouble("lng"));
                                modelArrayList.add(model);
                            } catch (JSONException e) {
                                Log.d("quylogcat", e.getMessage());
                            }
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("quylogcat", error.getMessage());
                    }
                });
        AppController.getInstance().addToRequestQueue(jsonArrayRequest);
        mWifiListView.setAdapter(new WifiListAdapter(this,modelArrayList));
        return modelArrayList;
    }
}
