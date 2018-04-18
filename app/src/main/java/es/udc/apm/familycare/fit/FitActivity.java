package es.udc.apm.familycare.fit;

import android.app.Activity;
import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.BleDevice;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.request.BleScanCallback;

import java.util.Arrays;

public class FitActivity extends ListActivity {

    private static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 111;

    private BleDeviceListAdapter mBleDeviceListAdapter;

    BleScanCallback bleScanCallbacks = new BleScanCallback() {
        @Override
        public void onDeviceFound(BleDevice device) {
            runOnUiThread(() -> {
                mBleDeviceListAdapter.addDevice(device);
                mBleDeviceListAdapter.notifyDataSetChanged();
            });
        }

        @Override
        public void onScanStopped() {
            // The scan timed out or was interrupted
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FitnessOptions fitnessOptions = FitnessOptions.builder()
                .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
                .addDataType(DataType.TYPE_STEP_COUNT_CUMULATIVE, FitnessOptions.ACCESS_READ)
                // More values can be added
                .build();

        mBleDeviceListAdapter = new BleDeviceListAdapter(this);
        setListAdapter(mBleDeviceListAdapter);

        if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(this), fitnessOptions)) {
            GoogleSignIn.requestPermissions(
                    this, // your activity
                    GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                    GoogleSignIn.getLastSignedInAccount(this),
                    fitnessOptions);
        } else {
            scanDevices();
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
                scanDevices();
            }
        }
    }

    private void scanDevices() {
        Fitness.getBleClient(this, GoogleSignIn.getLastSignedInAccount(this))
                .startBleScan(Arrays.asList(
                        DataType.TYPE_HEART_RATE_BPM,
                        DataType.TYPE_STEP_COUNT_DELTA,
                        DataType.TYPE_STEP_COUNT_CUMULATIVE),1000, bleScanCallbacks);
    }

}
