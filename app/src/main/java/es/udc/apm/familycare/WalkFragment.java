package es.udc.apm.familycare;


import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.fitness.Fitness;
import com.google.android.gms.fitness.FitnessOptions;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.fitness.data.DataType;
import com.google.android.gms.fitness.data.Field;
import com.google.android.gms.fitness.request.DataReadRequest;
import com.google.android.gms.fitness.result.DataReadResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import es.udc.apm.familycare.interfaces.RouterActivity;


/**
 * A simple {@link Fragment} subclass.
 */
public class WalkFragment extends Fragment {
    private static final String TAG = WalkFragment.class.getSimpleName();

    private static final int GOOGLE_FIT_PERMISSIONS_REQUEST_CODE = 111;
    private List<DataType> options = Arrays.asList(DataType.TYPE_HEART_RATE_BPM,
            DataType.AGGREGATE_HEART_RATE_SUMMARY, DataType.TYPE_STEP_COUNT_DELTA,
            DataType.TYPE_STEP_COUNT_CUMULATIVE, DataType.AGGREGATE_STEP_COUNT_DELTA);

    private Unbinder mUnbinder;
    private RouterActivity routerActivity = null;
    private boolean isEnabled = true;

    @BindView(R.id.bpm_text) public TextView bpmText;
    @BindView(R.id.bar_chart) public HorizontalBarChart barChart;
    @BindView(R.id.walkToolbar) Toolbar toolbar;

    public WalkFragment() {
        // Required empty public constructor
    }

    private boolean isPackageInstalled(String packageName, PackageManager packageManager) {
        try {
            packageManager.getPackageInfo(packageName, 0);
            return true;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        if (!isPackageInstalled("com.google.android.apps.fitness", getContext().getPackageManager())) {
            Toast.makeText(getContext(), R.string.error_fit, Toast.LENGTH_SHORT).show();
            isEnabled = false;
        }
        View view = inflater.inflate(R.layout.fragment_walk, container, false);
        mUnbinder = ButterKnife.bind(this, view);

        this.routerActivity.setActionBar(this.toolbar);

        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (isEnabled) {
            FitnessOptions.Builder builder = FitnessOptions.builder();
            for (DataType t : options) {
                builder.addDataType(t, FitnessOptions.ACCESS_READ);
            }
            FitnessOptions fitnessOptions = builder.build();

            if (!GoogleSignIn.hasPermissions(GoogleSignIn.getLastSignedInAccount(getContext()), fitnessOptions)) {
                GoogleSignIn.requestPermissions(
                        this, // your activity
                        GOOGLE_FIT_PERMISSIONS_REQUEST_CODE,
                        GoogleSignIn.getLastSignedInAccount(getContext()),
                        fitnessOptions);
            } else {
                accessGoogleFit();
            }
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            this.routerActivity = (RouterActivity) context;
        } catch (ClassCastException ex) {
            throw new ClassCastException(context.toString() + " must implement RouterActivity");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        this.routerActivity = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        if (mUnbinder != null) {
            mUnbinder.unbind();
            mUnbinder = null;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == GOOGLE_FIT_PERMISSIONS_REQUEST_CODE) {
                accessGoogleFit();
            }
        }
    }

    private void accessGoogleFit() {
        Calendar cal = Calendar.getInstance();
        long endTime = cal.getTimeInMillis();
        cal.add(Calendar.WEEK_OF_YEAR, -1);
        long startTime = cal.getTimeInMillis();

        DataReadRequest weekSteps = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_STEP_COUNT_DELTA, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        Fitness.getHistoryClient(getContext(), GoogleSignIn.getLastSignedInAccount(getContext()))
                .readData(weekSteps)
                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse dataReadResponse) {
                        List<BarEntry> barEntries = new ArrayList<>();
                        int index = 0;
                        for (DataPoint dp : dataReadResponse.getDataSet(DataType.TYPE_STEP_COUNT_DELTA).getDataPoints()) { // Each week day
                            float value = 0;
                            for (Field field : dp.getDataType().getFields()) {
                                Log.i(TAG, "\tField: " + field.getName() + " Value: " + dp.getValue(field));
                                if (field.getName().toLowerCase().equals("steps")) {
                                    value += dp.getValue(field).asFloat();
                                }
                            }
                            barEntries.add(new BarEntry(index, value));
                            index++;
                        }
                        barChart.setData(new BarData(new BarDataSet(barEntries, "Semana")));
                        barChart.setVisibleXRangeMaximum(7);
                        barChart.setFitBars(true);
                        barChart.setTouchEnabled(false);
                        barChart.setDescription(null);
                        barChart.invalidate();
                        Log.d(TAG, "Success: " + dataReadResponse.toString());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure()", e);
                        Toast.makeText(getContext(), "Error cargando FitData", Toast.LENGTH_SHORT).show();
                    }
                });

        cal.add(Calendar.WEEK_OF_YEAR, 1);
        cal.add(Calendar.DAY_OF_WEEK, -1);
        startTime = cal.getTimeInMillis();

        DataReadRequest dayHeart = new DataReadRequest.Builder()
                .aggregate(DataType.TYPE_HEART_RATE_BPM, DataType.AGGREGATE_HEART_RATE_SUMMARY)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();

        Fitness.getHistoryClient(getContext(), GoogleSignIn.getLastSignedInAccount(getContext()))
                .readData(dayHeart)
                .addOnSuccessListener(new OnSuccessListener<DataReadResponse>() {
                    @Override
                    public void onSuccess(DataReadResponse dataReadResponse) {
                        Log.d(TAG, "Success: " + dataReadResponse.toString());
                        for (DataPoint dp : dataReadResponse.getDataSet(DataType.TYPE_HEART_RATE_BPM).getDataPoints()) {
                            for (Field field : dp.getDataType().getFields()) {
                                Log.i(TAG, "\tField: " + field.getName() + " Value: " + dp.getValue(field));
                                if (field.getName().toLowerCase().equals("bpm")) {
                                    bpmText.setText(dp.getValue(field).asString());
                                }
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "onFailure()", e);
                        Toast.makeText(getContext(), "Error cargando FitData", Toast.LENGTH_SHORT).show();
                    }
                });

    }

}
