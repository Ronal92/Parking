package com.jinwoo.android.parking;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by JINWOO on 2017-03-06.
 */

public class Remote {

    public void getData(final Callback obj){
        String urlString = obj.getUrl();
        if(!urlString.startsWith("http")){
            urlString = "http://" + urlString;
        }

        new AsyncTask<String, Void, String>(){

            @Override
            protected void onPreExecute() {
                super.onPreExecute();

                obj.getProgress().setProgressStyle(ProgressDialog.STYLE_SPINNER);
                obj.getProgress().setMessage("불러오는 중........");
                obj.getProgress().show();
            }

            @Override
            protected String doInBackground(String... params) {
                String urlString = params[0];

                try {
                    URL url = new URL(urlString);
                    HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                    connection.setRequestMethod("GET");

                    int responseCode = connection.getResponseCode();
                    if (responseCode == HttpURLConnection.HTTP_OK) {
                        BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                        StringBuilder result = new StringBuilder();
                        String lineOfData = "";

                        while ( (lineOfData = br.readLine()) != null) {

                            result.append(lineOfData);
                        }

                        return result.toString();

                    } else {
                        Log.e("HTTPConnection", "Error Code =" + responseCode);
                    }

                } catch( Exception e){
                    e.printStackTrace();
                }


                return null;
            }

            @Override
            protected void onPostExecute(String result) {
                super.onPostExecute(result);
                // 결과값 출력
                Log.i("Remote",result);
                //obj.getProgress().dismiss();
                // remote 객체를 생성한 측의 callback 함수 호출
                obj.call(result);
            }
        }.execute(urlString);
    }


    interface Callback{
        public Context getContext();
        public String getUrl();
        public void call(String jsonString);
        public ProgressDialog getProgress();
    }
}
