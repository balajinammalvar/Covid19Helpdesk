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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.brainmagic.covid19helpdesk.R;
import com.brainmagic.covid19helpdesk.activities.MainActivity;
import com.myhexaville.smartimagepicker.ImagePicker;
import com.myhexaville.smartimagepicker.OnImagePickedListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;

import static android.app.Activity.RESULT_OK;

public class PersonalDetailFragment extends Fragment {

    private ImagePicker imagePicker;
    private ImageView selectimage;
    private File mImageFile;
    private String mImageName = "";


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);

        View view = LayoutInflater.from(getActivity()).inflate(R.layout.personal_detail_fragment, container, false);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        Button uploadPhoto = view.findViewById(R.id.personal_photo_bt);
        Button uploadAddressProof = view.findViewById(R.id.address_proof_bt);

        uploadPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePicker=new ImagePicker(getActivity(), null, new OnImagePickedListener() {
                    @Override
                    public void onImagePicked(Uri imageUri) {
//                        imageLayout.setVisibility(View.VISIBLE);
                        selectimage.setImageURI(imageUri);
                        mImageName = getFileName(imageUri);
                        mImageFile = getFileFromImage();
//                        i=imageUri;
                    }
                });
                imagePicker.choosePicture(true);
            }
        });

        uploadAddressProof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imagePicker=new ImagePicker(getActivity(), null, new OnImagePickedListener() {
                    @Override
                    public void onImagePicked(Uri imageUri) {
//                        imageLayout.setVisibility(View.VISIBLE);
                        selectimage.setImageURI(imageUri);
                        mImageName = getFileName(imageUri);
                        mImageFile = getFileFromImage();
//                        i=imageUri;
                    }
                });
                imagePicker.choosePicture(true);
            }
        });

        return view;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            imagePicker.handleActivityResult(resultCode, requestCode, data);
        }
    }

    public String getFileName(Uri uri) {
        String result = null;
        if (uri.getScheme().equals("content")) {
            Cursor cursor = getActivity().getContentResolver().query(uri, null, null, null, null);
            try {
                if (cursor != null && cursor.moveToFirst()) {
                    result = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
                }
            } finally {
                cursor.close();
            }
        }
        if (result == null) {
            result = uri.getPath();
            int cut = result.lastIndexOf('/');
            if (cut != -1) {
                result = result.substring(cut + 1);
            }
        }
        return result;
    }

    private File getFileFromImage() {
        try {
            BitmapDrawable drawable = (BitmapDrawable) selectimage.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            File directory = new File(getActivity().getFilesDir(), "profile");
            if (!directory.exists())
                directory.mkdirs();
            File myappFile = new File(directory
                    + File.separator + mImageName);
            FileOutputStream fos = new FileOutputStream(myappFile);
            fos.write(byteArray);
//                        mImageName = File_URL + myappFile.getName();
            return myappFile;
        } catch (Exception e) {
            e.printStackTrace();
            return new File("");
        }
    }
}
