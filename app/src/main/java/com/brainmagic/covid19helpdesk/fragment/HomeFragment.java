package com.brainmagic.covid19helpdesk.fragment;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.OpenableColumns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.brainmagic.covid19helpdesk.R;
import com.myhexaville.smartimagepicker.ImagePicker;
import com.myhexaville.smartimagepicker.OnImagePickedListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import static android.app.Activity.RESULT_OK;

public class HomeFragment extends Fragment {

    private TextView fragmenttitle;


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.homefragment, container, false);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);


        fragmenttitle=getActivity().findViewById(R.id.fragmenttitle);
        fragmenttitle.setText(R.string.home);

        return view;
    }
}