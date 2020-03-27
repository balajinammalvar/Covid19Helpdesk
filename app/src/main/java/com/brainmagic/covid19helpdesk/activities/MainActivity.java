package com.brainmagic.covid19helpdesk.activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.brainmagic.covid19helpdesk.Constants;
import com.brainmagic.covid19helpdesk.R;
import com.brainmagic.covid19helpdesk.fragment.HomeFragment;
import com.brainmagic.covid19helpdesk.fragment.SignupFragment;
import com.brainmagic.covid19helpdesk.locationapi.GeocodeAsyncTask;

import static com.brainmagic.covid19helpdesk.Constants.covidshare;

public class MainActivity extends AppCompatActivity implements GeocodeAsyncTask.AddressListener {

    private SignupFragment signupFragment;
    private HomeFragment homeFragment;
    private Boolean alreadyregistred=false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        covidshare=getSharedPreferences("covid19",MODE_PRIVATE);
        Constants.editor= covidshare.edit();
        alreadyregistred=covidshare.getBoolean("alreadyregistred",false);
        if (alreadyregistred){
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            homeFragment = new HomeFragment();
            transaction.add(R.id.fragment_container,homeFragment,"home");
            transaction.commit();
        }else {
            FragmentManager manager = getSupportFragmentManager();
            FragmentTransaction transaction = manager.beginTransaction();
            signupFragment = new SignupFragment();
            transaction.add(R.id.fragment_container,signupFragment,"signUp");
            transaction.commit();
        }


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onActivityResult(requestCode,resultCode,data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        for (Fragment fragment : getSupportFragmentManager().getFragments()) {
            fragment.onRequestPermissionsResult(requestCode,permissions,grantResults);
        }
    }

    @Override
    public void addressListener(Address address) {
        signupFragment.updateLocationUI(address);
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }
}
