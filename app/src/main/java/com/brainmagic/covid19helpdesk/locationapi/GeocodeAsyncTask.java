package com.brainmagic.covid19helpdesk.locationapi;

import android.annotation.SuppressLint;
import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class GeocodeAsyncTask extends AsyncTask<Double, Void, Address> {

    String errorMessage = "";
    String addressString;
    String doorNo, city, state, street, area, pin;
    private Context context;
    private AddressListener addressListener;

    public GeocodeAsyncTask(Context context) {
        this.context = context;
        addressListener = (AddressListener) context;
    }

    private static final String TAG = "GeocodeAsyncTask";


    @SuppressLint("LongLogTag")
    @Override
    protected Address doInBackground(Double... latlang) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        List<Address> addresses = null;
        if (geocoder.isPresent()) {
            try {
                addresses = geocoder.getFromLocation(latlang[0], latlang[1], 1);
                Log.d(TAG, "doInBackground: ************");
            } catch (IOException ioException) {
                errorMessage = "Service Not Available";
                Log.e(TAG, errorMessage, ioException);
            } catch (IllegalArgumentException illegalArgumentException) {
                errorMessage = "Invalid Latitude or Longitude Used";
                Log.e(TAG, errorMessage + ". " +
                        "Latitude = " + latlang[0] + ", Longitude = " +
                        latlang[1], illegalArgumentException);
            }

            if (addresses != null && addresses.size() > 0)
                return addresses.get(0);
        }
//            else {
//                new GetGeoCodeAPIAsynchTask().execute(latlang[0], latlang[1]);
//            }

        return null;
    }

    @SuppressLint("LongLogTag")
    protected void onPostExecute(Address address) {

        if (address == null) {
//            new GetGeoCodeAPIAsynchTask().execute(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
            Log.d(TAG, "onPostExecute: *****");
        } else {
//                progressBar.setVisibility(View.GONE);
            addressString = address.getAddressLine(0);
            addressListener.addressListener(address);
//                City = addresss.getLocality();
//                Log.d(TAG, "onPostExecute: **************************" + City);
//                doorNo = address.getPremises();
//                city = address.getLocality();
//                state = address.getAdminArea();
//                street = address.getFeatureName();
//                area = address.getSubLocality();
//                pin = address.getPostalCode();
//                //create your custom title
//                String title = city + "-" + state;
//            tv.setText(address);
//                Geocoder geocoder = new Geocoder(context);
//                try {
//                    ArrayList<Address> addresses = (ArrayList<Address>) geocoder.getFromLocationName("karur", 50);
//                    for (Address address3 : addresses) {
//                        double lat = address3.getLatitude();
//                        double lon = address3.getLongitude();
////                        address2.setText(lat +
////                                "\n"
////                                + lon);
//
//                    }
//                } catch (IOException e) {
//                    e.printStackTrace();
//
//                }
            //create your custom title
//                String title = city + "-" + state;
//                Alertbox alertbox=new Alertbox(MainActivity.this);
//                alertbox.showAlertboxwithback("Your Current location is "+city);
        }
    }

    public interface AddressListener {
        void addressListener (Address address);
    }

    public String getAddress() {
        return addressString;
    }

    public String getDoorNo() {
        return doorNo;
    }

    public String getCity() {
        return city;
    }

    public String getState() {
        return state;
    }

    public String getStreet() {
        return street;
    }

    public String getArea() {
        return area;
    }

    public String getPin() {
        return pin;
    }
}
