package com.bioid.authenticator.facialrecognition.photoverify;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.databinding.DataBindingUtil;
import android.graphics.Bitmap;
import android.support.annotation.VisibleForTesting;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.bioid.authenticator.R;
import com.bioid.authenticator.base.network.bioid.webservice.BioIdWebserviceClient;
import com.bioid.authenticator.base.threading.AsynchronousBackgroundHandler;
import com.bioid.authenticator.base.threading.BackgroundHandler;
import com.bioid.authenticator.facialrecognition.FacialRecognitionFragment;

public class PhotoVerifyActivity extends AppCompatActivity {
    private Bitmap[] selfies = new Bitmap[2];
    private Bitmap idphoto;
    private BackgroundHandler backgroundHandler;
    private BioIdWebserviceClient bioIdWebserviceClient;

    @VisibleForTesting
    public static final String TAG_FACIAL_RECOGNITION_FRAGMENT = "FacialRecognitionFragment";
    public static final String TAG_IN_PROGRESS_FRAGMENT = "PhotoVerifyProgressFragment";
    public static final String TAG_BASE_FRAGMENT = "PhotoVerifyBaseFragment";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        DataBindingUtil.setContentView(this, R.layout.activity_facial_recognition);
        setupFragment();

        this.backgroundHandler = new AsynchronousBackgroundHandler();
        this.bioIdWebserviceClient = new BioIdWebserviceClient();
    }

    /**
     * User took 2 selfies (via facial recognition mechanism), so we have all 3 required photos to send request to API
     * We resize photo of ID to increase performance
     */
    public void endSelfiesSession(Bitmap[] selfies) {
        this.selfies = selfies;
        replaceFragment(new PhotoVerifyProgressFragment(), TAG_IN_PROGRESS_FRAGMENT);
        performPhotoVerify(resizeBitmap(this.idphoto, 600));
    }

    /**
     * User selected photo of his ID, so we should ask him for selfies
     * We resize photo of ID to increase performance
     */
    public void endIdPhotoSession(Bitmap idphoto) {
        this.idphoto = idphoto;
        Fragment facialFragment = getFragmentManager().findFragmentByTag(TAG_FACIAL_RECOGNITION_FRAGMENT);

        if(facialFragment == null) {
            replaceFragment(FacialRecognitionFragment.newInstanceForPhotoVerify(), TAG_FACIAL_RECOGNITION_FRAGMENT);
        } else {
            replaceFragment(facialFragment, TAG_FACIAL_RECOGNITION_FRAGMENT);
        }
    }

    /**
     * Setup first fragment for activity
     */
    private void setupFragment() {
        Fragment baseFragment = getFragmentManager().findFragmentByTag(TAG_BASE_FRAGMENT);
        if (baseFragment == null) {
            // fragment is not retained
            PhotoVerifyBaseFragment fragment = new PhotoVerifyBaseFragment();
            FragmentTransaction transaction = getFragmentManager().beginTransaction();
            transaction.add(R.id.frame_layout, fragment, TAG_BASE_FRAGMENT);
            transaction.commit();
        }
    }

    /**
     * Change current fragment for the new one
     */
    private void replaceFragment(Fragment fragment, String fragmentTag) {
        getFragmentManager()
                .beginTransaction()
                .replace(R.id.frame_layout, fragment, fragmentTag)
                .commit();

        getFragmentManager().executePendingTransactions();
    }

    /**
     * Perform call to API in background thread
     */
    private void performPhotoVerify(Bitmap idphoto) {
        this.backgroundHandler.runOnBackgroundThread(
                () -> this.bioIdWebserviceClient.performPhotoVerify(this.selfies, idphoto),
                () -> setProgressMessage("Success"),
                (error) -> setProgressMessage(error.getMessage()),
                null
        );
    }

    private Bitmap resizeBitmap(Bitmap bitmap, int maxSize) {
        int outWidth, outHeight;
        int inWidth = bitmap.getWidth();
        int inHeight = bitmap.getHeight();

        if (inWidth > inHeight){
            outWidth = maxSize;
            outHeight = (inHeight * maxSize) / inWidth;
        } else {
            outHeight = maxSize;
            outWidth = (inWidth * maxSize) / inHeight;
        }

        return Bitmap.createScaledBitmap(bitmap, outWidth, outHeight, false);
    }

    /**
     * Set messages on {@link PhotoVerifyProgressFragment} to inform user what is going on
     */
    private void setProgressMessage(String message) {
        PhotoVerifyProgressFragment progressFragment = (PhotoVerifyProgressFragment) getFragmentManager().findFragmentByTag(TAG_IN_PROGRESS_FRAGMENT);
        if (progressFragment != null) {
            progressFragment.setStatusMessage(message);
        }
    }
}
