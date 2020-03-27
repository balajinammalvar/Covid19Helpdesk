package com.brainmagic.covid19helpdesk.locationapi;


import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;

import static com.brainmagic.covid19helpdesk.Constants.APIKEY;

public class GetGeoCodeAPIAsyncTask extends AsyncTask<Double, Void, String[]> {

    private static final String TAG = "GetGeoCodeAPIAsynchTask";
    
    
    String address="",City="";
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String[] doInBackground(Double... latlang) {
        String response;
        try {
            String URL = "https://maps.googleapis.com/maps/api/geocode/json?latlng=" + latlang[0] + "," + latlang[1] + "&key=" + APIKEY;
            Log.v("URL", URL);
            response = getLatLongByURL(URL);
            return new String[]{response};
        } catch (Exception e) {
            return new String[]{"error"};
        }
    }

    @SuppressLint("LongLogTag")
    @Override
    protected void onPostExecute(String... result) {
        try {
            JSONObject jsonObject = new JSONObject(result[0]);

            address = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                    .getJSONArray("address_components").getJSONObject(0).getString("long_name");

//                editor.putString("FromAddress", address);
//                editor.putString("ToAddress", address);
//                editor.commit();
//                editor.apply();

            City = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                    .getJSONArray("address_components").getJSONObject(2).getString("long_name");

            String state = ((JSONArray) jsonObject.get("results")).getJSONObject(0)
                    .getJSONArray("address_components").getJSONObject(4).getString("long_name");

            Log.d(TAG, "onPostExecute: " + City);

//                String title = city + "-" + state;

//                Alertbox alertbox=new Alertbox(MainActivity.this);
//                alertbox.showAlertbox("Your Current location is "+city);

//                if (fetchType == FROMADDRESS) {
//
//                    mPrimaryAddress.setText(address);
//                    mSecondaryAddress.setText(title);
//                    mDropText.requestFocus();
//                    fromaddress = address;
//                    mPickupText.setText(address);
//
//                    editor.putString("FromAddress", fromaddress);
//                    editor.apply();
//                    editor.commit();
//
//                    Log.i("FromAddress1", address);
//                    Log.i("FromAddress2", title);
//                } else {
//                    mPrimaryAddress.setText(address);
//                    mSecondaryAddress.setText(title);
//                    mDropText.setText(address);
//                    toaddress = address;
//                    editor.putString("ToAddress", toaddress).commit();
//
//                    Log.i("ToAddress1", address);
//                    Log.i("ToAddress2", title);
//
//                }


            Log.d("Address", "" + address);
        } catch (JSONException e) {
            e.printStackTrace();
        }
//            progressBar.setVisibility(View.GONE);

    }


    private String getLatLongByURL(String requestURL) {
        URL url;
        String response = "";
        try {
            url = new URL(requestURL);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setReadTimeout(15000);
            conn.setConnectTimeout(15000);
            conn.setRequestMethod("GET");
            conn.setDoInput(true);
            conn.setRequestProperty("Content-Type",
                    "application/x-www-form-urlencoded");
            conn.setDoOutput(true);
            int responseCode = conn.getResponseCode();

            if (responseCode == HttpsURLConnection.HTTP_OK) {
                String line;
                BufferedReader br = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                while ((line = br.readLine()) != null) {
                    response += line;
                }
            } else {
                response = "";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return response;
    }
}
