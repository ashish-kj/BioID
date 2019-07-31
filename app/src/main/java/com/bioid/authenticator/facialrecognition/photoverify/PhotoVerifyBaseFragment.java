package com.bioid.authenticator.facialrecognition.photoverify;

import android.Manifest;
import android.app.Fragment;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bioid.authenticator.R;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;

public class PhotoVerifyBaseFragment extends Fragment {
    private static final int CAMERA_PIC_REQUEST = 968;
    private static final int PICK_IMAGE = 228;
    private static final int PERMISSIONS_REQUEST_CAMERA = 123;

    View fragmentView;

    public PhotoVerifyBaseFragment() {
        // Required empty public constructor
    }

    @Override
    public void onResume() {
        super.onResume();

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LOCKED);
    }

    @Override
    public void onPause() {
        super.onPause();

        getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_photo_verify_base, container, false);

        fragmentView.findViewById(R.id.photo_verify_camera_btn).setOnClickListener(v -> openCamera());
        fragmentView.findViewById(R.id.photo_verify_gallery_btn).setOnClickListener(v -> openGallery());

        return fragmentView;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_CAMERA: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    this.openCamera();
                }
                break;
            }
        }
    }

    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode != RESULT_OK) {
            return;
        }

        switch(requestCode){
            case CAMERA_PIC_REQUEST:
                if (data.getExtras() == null) {
                    return;
                }

                Bitmap imageFromCamera = (Bitmap) data.getExtras().get("data");
                endFragment(imageFromCamera);
                break;
            case PICK_IMAGE:
                Uri uri = data.getData();
                try {
                    Bitmap imageFromGallery = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), uri);
                    endFragment(imageFromGallery);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
            default:
                throw new IllegalStateException("Unknown activity result code");
        }
    }

    private void openCamera() {
        if(ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.CAMERA};
            ActivityCompat.requestPermissions(getActivity(), permissions, PERMISSIONS_REQUEST_CAMERA);
        } else {
            Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
            startActivityForResult(cameraIntent, CAMERA_PIC_REQUEST);
        }
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE);
    }

    private void endFragment(Bitmap imageFromCamera) {
        ((PhotoVerifyActivity) getActivity()).endIdPhotoSession(imageFromCamera);
    }
}
