package com.bioid.authenticator.facialrecognition.photoverify;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bioid.authenticator.R;

public class PhotoVerifyProgressFragment extends Fragment {
    View fragmentView;
    TextView statusTextView;

    public PhotoVerifyProgressFragment() {
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_photo_verify_progress, container, false);

        statusTextView = fragmentView.findViewById(R.id.photo_verify_status);

        return fragmentView;
    }

    public void setStatusMessage(String message) {
        statusTextView.setText(message);
    }
}
