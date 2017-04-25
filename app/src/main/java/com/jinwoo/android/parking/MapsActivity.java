package com.jinwoo.android.parking;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.jinwoo.android.parking.domain.Park;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, Remote.Callback {

    private GoogleMap mMap;
    private String hangulParameter;
    private String url = "http://openapi.seoul.go.kr:8088/417058676568616e3137534e56494f/json/SearchParkingInfoRealtime/1/500";
    ProgressDialog dialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);


        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        dialog = new ProgressDialog(this);
        // Add a marker in Sydney and move the camera
        // 1. 공영주차장 마커 전체를 화면에 출력
//        try {
//            hangulParameter = URLEncoder.encode("중구", "UTF-8");
//            url = url + hangulParameter;
//        }catch(Exception e){
//            e.printStackTrace();
//        }
        Remote remote = new Remote();
        remote.getData(this);


        // 2. 중심점을 서울로 이동
        // 서울 위도(Latitude) : 북위 37도
        // 서울 경도(Longitude) : 동경 126도
        // 서울시청 좌표 :.....


        LatLng seoul = new LatLng(37.566696, 126.977942);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(seoul, 10));
    }

    @Override
    public Context getContext() {
        return this;
    }

    @Override
    public String getUrl() {
        return url;
    }

    @Override
    public void call(String jsonString) {
        try {

            // MapsActivity의 화면에 뭔가를 세팅해주면, Remote 에서 이 함수를 호출해 준다.
            // 1. json String 전체를 JSONObject로 변환
            JSONObject jsonObject = new JSONObject(jsonString);
            // 2. JSONObject 중에 최상위의 object를 꺼낸다.
            JSONObject rootObject = jsonObject.getJSONObject("SearchParkingInfoRealtime"); // root 태그를 객체로
            // 3. 사용하려는 주차장 정보들을 JSONArray로 꺼낸다.
            //    이 데이터를 rootObject 바로 아래에 실제 정보가 있지만 계층구조상 더 아래에 존재할 수도 있다.
            JSONArray rows = rootObject.getJSONArray("row");

            List<String> parkCode = new ArrayList<>();
            String code = "";
            int arrayLength = rows.length();
            for (int i = 0; i < arrayLength; i++) {

                JSONObject park = rows.getJSONObject(i);
                code = park.getString("PARKING_CODE");
                if(parkCode.contains(code)){
                    continue;
                }


                parkCode.add(code);

                double lat = getDouble(park, "LAT");
                double lng = getDouble(park, "LNG");
                LatLng parking = new LatLng(lat, lng);

                int capacity = getInt(park, "CAPACITY");
                int current = getInt(park,"CUR_PARKING");
                int space = capacity - current;
                mMap.addMarker(new MarkerOptions().position(parking).title(space + "/" + capacity)).showInfoWindow();

            }
        }catch(Exception e){
            e.printStackTrace();
        }
        dialog.dismiss();
    }

    @Override
    public ProgressDialog getProgress() {
        return dialog;
    }

    // JSON 객체에서 받은 값이 아무것도 없을 경우를 위해 exception 처리??
    private double getDouble(JSONObject obj, String key){
        double result = 0;
        try {
            result = obj.getDouble(key);
        }catch(Exception e){

        }

        return result;
    }

    private int getInt(JSONObject obj, String key){
        int result = 0;
        try {
            result = obj.getInt(key);
        }catch(Exception e){

        }

        return result;
    }
}
