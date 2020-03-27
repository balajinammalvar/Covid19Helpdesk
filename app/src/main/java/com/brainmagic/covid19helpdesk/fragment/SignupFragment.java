package com.brainmagic.covid19helpdesk.fragment;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.brainmagic.covid19helpdesk.Constants;
import com.brainmagic.covid19helpdesk.activities.MainActivity;
import com.brainmagic.covid19helpdesk.alert.AlertBox;
import com.brainmagic.covid19helpdesk.locationapi.GeocodeAsyncTask;
import com.brainmagic.covid19helpdesk.locationapi.GetGeoCodeAPIAsyncTask;
import com.brainmagic.covid19helpdesk.R;
import com.brainmagic.covid19helpdesk.network.NetworkConnection;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;

import static android.content.Context.MODE_PRIVATE;
import static com.brainmagic.covid19helpdesk.Constants.editor;

public class SignupFragment extends Fragment {

    private static final long UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

    // fastest updates interval - 5 sec
    // location updates will be received if another app is requesting the locations
    // than your app can handle
    private static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS = 5000;

    private static final int REQUEST_CHECK_SETTINGS = 100;
    private boolean permissionGranted=true;
    // bunch of location related apis
    private FusedLocationProviderClient mFusedLocationClient;
    private SettingsClient mSettingsClient;
    private LocationRequest mLocationRequest;
    private LocationSettingsRequest mLocationSettingsRequest;
    private LocationCallback mLocationCallback;
    private Location mCurrentLocation;
    private EditText currentAddressEt, doorNoEt, buildingEt, streetEt, cityEt, stateEt, pinEt, landmarkEt;
    private String currentAddressSt = "";
    private boolean isSameAsAbove = false;
    private TextView fragmenttitle;
    private static final String TAG = "SignupFragment";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.sign_up_fragment,container, false);

        Constants.covidshare=getActivity().getSharedPreferences("covid19",MODE_PRIVATE);
        editor=Constants.covidshare.edit();
        final Spinner userTypeSp =view.findViewById(R.id.user_type_sp);
        final TextView sameAboveTv =view.findViewById(R.id.same_above);
        final EditText firstNameEt =view.findViewById(R.id.first_name_et);
        EditText lastNameEt =view.findViewById(R.id.last_name_et);
        EditText emailEt =view.findViewById(R.id.email_et);
        final EditText mobileEt =view.findViewById(R.id.mobile_et);
        Button signUpBt =view.findViewById(R.id.sign_up_bt);
        currentAddressEt =view.findViewById(R.id.current_address_et);
        doorNoEt =view.findViewById(R.id.door_no_et);
        buildingEt =view.findViewById(R.id.building_et);
        streetEt =view.findViewById(R.id.street_et);
        cityEt =view.findViewById(R.id.city_et);
        stateEt =view.findViewById(R.id.state_et);
        pinEt =view.findViewById(R.id.pin_et);
        landmarkEt =view.findViewById(R.id.landmark_et);
        fragmenttitle=getActivity().findViewById(R.id.fragmenttitle);
        fragmenttitle.setText(R.string.registration);
        String[] userTypeArray = getResources().getStringArray(R.array.user_types);
        ArrayAdapter arrayAdapter = new ArrayAdapter(getActivity(), R.layout.support_simple_spinner_dropdown_item,userTypeArray);
        userTypeSp.setAdapter(arrayAdapter);


//        getCurrentAddressCb.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if(isChecked) getCurrentLocation(sameAboveTv);
//                else {
//                    sameAboveTv.setVisibility(View.INVISIBLE);
//                    stopLocationUpdates();
//                }
//            }
//        });

        sameAboveTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isSameAsAbove = !isSameAsAbove;
            }
        });

        signUpBt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(TextUtils.isEmpty(firstNameEt.getText().toString().trim()))
                {
                    firstNameEt.setError(getString(R.string.enter_first_name));
                    Toast.makeText(getActivity(), getString(R.string.enter_first_name), Toast.LENGTH_LONG).show();
                }
                else if(TextUtils.isEmpty(mobileEt.getText().toString().trim()))
                {
                    mobileEt.setError(getString(R.string.enter_first_name));
                    Toast.makeText(getActivity(), getString(R.string.enter_first_name), Toast.LENGTH_LONG).show();
                }
                else if(mobileEt.getText().toString().trim().length() !=10)
                {
                    mobileEt.setError(getString(R.string.enter_valid_mobile_no));
                    Toast.makeText(getActivity(), getString(R.string.enter_valid_mobile_no), Toast.LENGTH_LONG).show();
                }
                else  if(userTypeSp.getSelectedItem().toString().equalsIgnoreCase(getString(R.string.user_type)))
                {
                    Toast.makeText(getActivity(), getString(R.string.choose_user_type), Toast.LENGTH_LONG).show();
                }
                else {

                    callApiToRegister();
                }
            }
        });

        getCurrentLocation();
        return view;
    }

    private void callApiToRegister()
    {
        AlertBox alertBox = new AlertBox(getActivity());
        alertBox.listenerWithPinView(getString(R.string.register_alert_msg));
        alertBox.setOnPositiveClickListener(new AlertBox.OnPositiveClickListener() {
            @Override
            public void onPositiveClick() {
                editor.putBoolean("alreadyregistred",true);
                editor.commit();
                editor.apply();
                MainActivity.hideKeyboard(getActivity());
                FragmentManager manager = getActivity().getSupportFragmentManager();
                FragmentTransaction transaction = manager.beginTransaction();
                PersonalDetailFragment detailFragment = new PersonalDetailFragment();
                transaction.replace(R.id.fragment_container,detailFragment, "personalDetailFragment");
                transaction.commit();

            }
        });

        alertBox.setOnCustomClickListener(new AlertBox.OnCustomClickListener() {
            @Override
            public void onClick() {
               // Resend Otp
            }
        });


    }

    private void getAddressFromLocation(){
        if(mCurrentLocation !=null) {
            GeocodeAsyncTask geocodeAsyncTask = new GeocodeAsyncTask(getActivity());
            geocodeAsyncTask.execute(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        }
    }

    public void updateLocationUI(Address address)
    {
        currentAddressEt.setText(address.getAddressLine(0));
        if(isSameAsAbove) {
            doorNoEt.setText(address.getPremises());
            cityEt.setText(address.getLocality());
            stateEt.setText(address.getAdminArea());
            streetEt.setText(address.getThoroughfare());
//         = address.getSubLocality();
            pinEt.setText(address.getPostalCode());
            isSameAsAbove = !isSameAsAbove;
        }
    }

    private void getCurrentLocation()
    {
        NetworkConnection connection = new NetworkConnection(getActivity());
        if(connection.checkInternet()) {
            initPermission();
        }
        else {
            AlertBox alertBox = new AlertBox(getActivity());
            alertBox.showAlertBox(getString(R.string.no_internet_connection),View.GONE);

        }
    }

    private void initPermission() {
        if(PackageManager.PERMISSION_GRANTED != ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION))
        {
            ActivityCompat.requestPermissions(getActivity(),new String[]{Manifest.permission.ACCESS_FINE_LOCATION},100);
        }
        else {
            permissionGranted=true;
            init();
            startLocationUpdate();
        }
    }

    // first time permissions for location
    @Override
    public void onRequestPermissionsResult(final int requestCode, final String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length>0) {
            int len=  permissions.length;
            for (int i = 0;i <len; i++) {
                if (ActivityCompat.checkSelfPermission(getActivity(), permissions[i]) != PackageManager.PERMISSION_GRANTED) {
                    permissionGranted=false;
                }
            }


            if(permissionGranted)
            {
                init();
                {
                    startLocationUpdate();
                }
            }
            else {

                AlertDialog.Builder dialog=new AlertDialog.Builder(getActivity());
                dialog.setTitle("Location");
                dialog.setMessage("You did not give permission to access your Location. Do want to exit");
                dialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        getActivity().finish();
                    }
                });

                dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestManualPermission(permissions,requestCode);
                        permissionGranted=true;
                    }
                });
                dialog.show();
            }
        }

    }

    private void requestManualPermission(String[] permissions, int requestCode)
    {
        ActivityCompat.requestPermissions(getActivity(),permissions,requestCode);
    }

    @Override
    public void onPause() {
        super.onPause();

        if (permissionGranted) {
            // pausing location updates
            Log.e(TAG, "onPause:");
            stopLocationUpdates();
        }

    }


    private void init() {
        if(permissionGranted) {
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(getActivity());
            mSettingsClient = LocationServices.getSettingsClient(getActivity());

            mLocationCallback = new LocationCallback() {
                @Override
                public void onLocationResult(LocationResult locationResult) {
                    super.onLocationResult(locationResult);
                    // location is received
                    mCurrentLocation = locationResult.getLastLocation();
//                    mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
                    getAddressFromLocation();
                }
            };

            mLocationRequest = new LocationRequest();
            mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);
            mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);
            mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
            LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
            builder.addLocationRequest(mLocationRequest);
            mLocationSettingsRequest = builder.build();
//        startLocationButtonClick();
        }
    }

    public void stopLocationButtonClick(View view) {
        if(permissionGranted) {
            stopLocationUpdates();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            // Check for the integer request code originally supplied to startResolutionForResult().
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.e(TAG, "User agreed to make required location settings changes.");
                        // Nothing to do. startLocationupdates() gets called in onResume again.
                        if(permissionGranted)
                        {
                            startLocationUpdate();
                        }
                        else {
                            initPermission();
                        }
                        break;
                    case Activity.RESULT_CANCELED:
                        Log.e(TAG, "User choose not to make required location settings changes.");
                        break;
                }
                break;
        }
    }

    //to check location is enabled or not
    private void startLocationUpdate() {
        mSettingsClient
                .checkLocationSettings(mLocationSettingsRequest)
                .addOnSuccessListener(getActivity(), new OnSuccessListener<LocationSettingsResponse>() {
                    @SuppressLint("MissingPermission")
                    @Override
                    public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                        Log.i(TAG, "All location settings are satisfied.");

//                        Toast.makeText(getActivity(), "Started location updates!", Toast.LENGTH_SHORT).show();

                        //noinspection MissingPermission
                        mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                                mLocationCallback, Looper.myLooper());
//                        LocationManager manager= (LocationManager) getSystemService(LOCATION_SERVICE);

                        getAddressFromLocation();
                    }
                })
                .addOnFailureListener(getActivity(), new OnFailureListener() {
                    @Override
                    public void onFailure(Exception e) {
                        int statusCode = ((ApiException) e).getStatusCode();
                        switch (statusCode) {
                            case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                Log.i(TAG, "Location settings are not satisfied. Attempting to upgrade " +
                                        "location settings ");
                                try {
                                    // Show the dialog by calling startResolutionForResult(), and check the
                                    // result in onActivityResult().
                                    ResolvableApiException rae = (ResolvableApiException) e;
                                    rae.startResolutionForResult(getActivity(), REQUEST_CHECK_SETTINGS);
                                } catch (IntentSender.SendIntentException sie) {
                                    Log.i(TAG, "PendingIntent unable to execute request.");
                                }
                                break;
                            case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                                String errorMessage = "Location settings are inadequate, and cannot be " +
                                        "fixed here. Fix in Settings.";
                                Log.e(TAG, errorMessage);

                                Toast.makeText(getActivity(), errorMessage, Toast.LENGTH_LONG).show();
                                break;
                            case LocationSettingsStatusCodes.SUCCESS:
                                permissionGranted=true;
                                init();
                        }
                        getAddressFromLocation();
                    }
                });
    }

    public void stopLocationUpdates() {
        // Removing location updates
        if(mLocationCallback!=null)
            mFusedLocationClient
                    .removeLocationUpdates(mLocationCallback)
                    .addOnCompleteListener(getActivity(), new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(Task<Void> task) {
                            Log.e(TAG, "onComplete: location stopped" );
//                            Toast.makeText(getActivity(), "Location updates stopped!", Toast.LENGTH_SHORT).show();
                            //                        toggleButtons();
                        }
                    });
    }



}
