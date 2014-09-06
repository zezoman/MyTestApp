package com.example.tsvetan.mytestapp;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.Fragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.query.Filters;
import com.google.android.gms.drive.query.Query;
import com.google.android.gms.drive.query.SearchableField;


public class TestGoogleAPIsActivity extends Activity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static GoogleApiClient mGoogleApiClient;
    // Request code to use when launching the resolution activity
    private static final int REQUEST_UPDATE_SERVICES_ERROR = 1002;
    private static final int REQUEST_RESOLVE_ERROR = 1001;
    // Unique tag for the error dialog fragment
    private static final String DIALOG_ERROR = "dialog_error";
    // Bool to track whether the app is already resolving an error
    private static boolean mResolvingError = false;
    // track resolving attempts across activity restarts (e.g. when phone is rotated while the error dialog is displayed)
    private static final String STATE_RESOLVING_ERROR = "resolving_error";
    private static String fileName = "ZezoDatabase.kdbx";
    private static final String TAG = "TestGoogleAPIsActivity";
    private static boolean googlePlayServicesDeviceOK = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_google_apis);
        if (savedInstanceState == null) {
            getFragmentManager().beginTransaction().add(R.id.container, new PlaceholderFragment()).commit();
        }

        int supportPlayServices = GooglePlayServicesUtil.isGooglePlayServicesAvailable(this);
        if(supportPlayServices != ConnectionResult.SUCCESS){
            // Possible problems: SERVICE_MISSING, SERVICE_VERSION_UPDATE_REQUIRED, SERVICE_DISABLED, SERVICE_INVALID, DATE_INVALID
            Log.e(TAG,"Problem with google play services detected on this device! ConnectionResult error code is: "+supportPlayServices);
            googlePlayServicesDeviceOK=false;
            Dialog d = GooglePlayServicesUtil.getErrorDialog(supportPlayServices, this, REQUEST_UPDATE_SERVICES_ERROR);
            d.show();
            return;
        }

        mResolvingError = savedInstanceState != null && savedInstanceState.getBoolean(STATE_RESOLVING_ERROR, false);

        // You can add multiple APIs and multiple scopes to the same GoogleApiClient by appending
        // additional calls to addApi() and addScope().
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Drive.API)
                .addScope(Drive.SCOPE_FILE)
                .build();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putBoolean(STATE_RESOLVING_ERROR, mResolvingError);
    }

    @Override
    protected void onStart() {
        // To gracefully manage the lifecycle of the connection, you should call connect() during the activity's
        // onStart() (unless you want to connect later), then call disconnect() during the onStop() method.
        super.onStart();
        if (!mResolvingError && googlePlayServicesDeviceOK) {
            mGoogleApiClient.connect();
        }
    }

    @Override
    protected void onStop() {
        if(googlePlayServicesDeviceOK){
            mGoogleApiClient.disconnect();
        }

        super.onStop();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.test_google_apis, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onConnected(Bundle bundle) {
        // Connected to Google Play services! The good stuff goes here.
    }

    @Override
    public void onConnectionSuspended(int i) {
        // The connection has been interrupted.
        // Disable any UI components that depend on Google APIs until onConnected() is called.
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // This callback is important for handling errors that may occur while attempting to connect with Google.
        if (mResolvingError) {
            // Already attempting to resolve an error.
            return;
        } else if (connectionResult.hasResolution()) {
            try {
                mResolvingError = true;
                connectionResult.startResolutionForResult(this, REQUEST_RESOLVE_ERROR);
            } catch (IntentSender.SendIntentException e) {
                // There was an error with the resolution intent. Try again.
                mGoogleApiClient.connect();
            }
        } else {
            // Show dialog using GooglePlayServicesUtil.getErrorDialog()
            showErrorDialog(connectionResult.getErrorCode());
            mResolvingError = true;
        }
    }

    // The following section is all about building the error dialog
    //=====================================================================================

    /* Creates a dialog for an error message */
    private void showErrorDialog(int errorCode) {
        // Create a fragment for the error dialog
        ErrorDialogFragment dialogFragment = new ErrorDialogFragment();
        // Pass the error that should be displayed
        Bundle args = new Bundle();
        args.putInt(DIALOG_ERROR, errorCode);
        dialogFragment.setArguments(args);
        dialogFragment.show(getFragmentManager(), "errordialog");
    }

    /* Called from ErrorDialogFragment when the dialog is dismissed. */
    public static void onDialogDismissed() {
        mResolvingError = false;
    }

    /* A fragment to display an error dialog */
    // Fragment inner classes must be static:
    // http://stackoverflow.com/questions/15571010/fragment-inner-class-should-be-static
    public static class ErrorDialogFragment extends DialogFragment {
        public ErrorDialogFragment() {
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Get the error code and retrieve the appropriate dialog
            int errorCode = this.getArguments().getInt(DIALOG_ERROR);
            return GooglePlayServicesUtil.getErrorDialog(errorCode, this.getActivity(), REQUEST_RESOLVE_ERROR);
        }

        @Override
        public void onDismiss(DialogInterface dialog) {
            TestGoogleAPIsActivity.onDialogDismissed();
        }
    }

    //=====================================================================================

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_RESOLVE_ERROR) {
            mResolvingError = false;
            if (resultCode == RESULT_OK) {
                // Make sure the app is not already connected or attempting to connect
                if (!mGoogleApiClient.isConnecting() && !mGoogleApiClient.isConnected()) {
                    mGoogleApiClient.connect();
                }
            }
        }
    }



    /* A placeholder fragment containing a simple view.*/
    public static class PlaceholderFragment extends Fragment {
        TextView tv_drive_result = null;

        public PlaceholderFragment() { }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_test_google_apis, container, false);
            tv_drive_result = (TextView)rootView.findViewById(R.id.tv_fragment_google_apis);
            if(googlePlayServicesDeviceOK){
                loadFileAsynch(fileName);
            } else {
                tv_drive_result.setText("Problem with google play services detected on this device!");
            }

            return rootView;
        }

        // To make the request asynchronous, call setResultCallback() on the PendingResult
        private void loadFileAsynch(String filename) {
            // Create a query for a specific filename in Drive.
            Query query = new Query.Builder().addFilter(Filters.eq(SearchableField.TITLE, filename)).build();
            // Invoke the query asynchronously with a callback method
            Drive.DriveApi.query(mGoogleApiClient, query).setResultCallback(new ResultCallback<DriveApi.MetadataBufferResult>() {
                @Override
                public void onResult(DriveApi.MetadataBufferResult result) {
                    // Success! Handle the query result.
                    String status = result.getStatus().getStatusMessage();
                    tv_drive_result.setText("Status: "+status);
                }
            });
        }

        // If you want your code to execute in a strictly defined order, perhaps because the result of one call is needed
        // as an argument to another, you can make your request synchronous by calling await() on the PendingResult. This
        // blocks the thread and returns the Result object when the request completes
        private void loadFileSynch(String filename) {
            new GetFileTask().execute(filename);
        }

        // Because calling await() blocks the thread until the result arrives, it's important that you never perform this
        // call on the UI thread. So, if you want to perform synchronous requests to a Google Play service, you should
        // create a new thread, such as with AsyncTask in which to perform the request
        private class GetFileTask extends AsyncTask<String, Void, Void> {
            @Override
            protected Void doInBackground(String... filename) {
                Query query = new Query.Builder()
                        .addFilter(Filters.eq(SearchableField.TITLE, filename[0]))
                        .build();
                // Invoke the query synchronously
                DriveApi.MetadataBufferResult result = Drive.DriveApi.query(mGoogleApiClient, query).await();

                // Continue doing other stuff synchronously

                return null;
            }

        }
    }
}
