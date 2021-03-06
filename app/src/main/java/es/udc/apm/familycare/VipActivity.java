package es.udc.apm.familycare;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.gms.location.ActivityRecognition;
import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionRequest;
import com.google.android.gms.location.DetectedActivity;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.udc.apm.familycare.interfaces.RouterActivity;
import es.udc.apm.familycare.maps.VipMapFragment;
import es.udc.apm.familycare.link.LinkFragment;
import es.udc.apm.familycare.members.DetailMemberFragment;
import es.udc.apm.familycare.members.MemberListFragment;
import es.udc.apm.familycare.receiver.BatteryLevelReceiver;
import es.udc.apm.familycare.sensor.AccelerometerDataService;
import es.udc.apm.familycare.sensor.ActivityRecognizedService;
import es.udc.apm.familycare.utils.Constants;

public class VipActivity extends AppCompatActivity implements RouterActivity {

    private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 1;

    private static final String KEY_SCREEN = "KEY_SCREEN";

    public static final String SCREEN_DETAIL = "DETAIL";
    private static final String SCREEN_MEMBERS = "MEMBERS";
    private static final String SCREEN_LINK = "LINK";
    private static final String SCREEN_MAP = "MAP";

    private String currentScreen = null;
    @BindView(R.id.navigation) BottomNavigationView navigation = null;

    private final BroadcastReceiver br = new BatteryLevelReceiver();

    private final BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = item -> {
                switch (item.getItemId()) {
                    case R.id.navigation_map_vip:
                        // Don't reload map, heavy operation
                        if(navigation.getSelectedItemId() != item.getItemId()) {
                            navigate(new VipMapFragment(), null);
                        }
                        return true;
                    case R.id.navigation_members:
                        navigate(MemberListFragment.newInstance(), null);
                        //this.startService(new Intent(this, AccelerometerDataService.class));
                        return true;
                    case R.id.navigation_link:
                        navigate(LinkFragment.newInstance(), null);
                        return true;
                }
                return false;
            };

    @Override
    protected void onCreate(Bundle saved) {
        super.onCreate(saved);
        setContentView(R.layout.activity_vip);

        ButterKnife.bind(this);

        if(saved != null) {
            this.currentScreen = saved.getString(KEY_SCREEN);
        }

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

        if(this.currentScreen == null) {
            navigation.setSelectedItemId(R.id.navigation_members);
        }


        // Listen for activity events
        Intent intent = new Intent(FamilyCare.app, ActivityRecognizedService.class);
        PendingIntent pendingIntent = PendingIntent.getService(FamilyCare.app, 0,
                intent, PendingIntent.FLAG_CANCEL_CURRENT);

        // Activity transition listener
        ActivityTransitionRequest request = buildTransitionRequest();
        ActivityRecognition.getClient(FamilyCare.app).requestActivityTransitionUpdates(request, pendingIntent);

        // Start fall detector
        startService(new Intent(FamilyCare.app, AccelerometerDataService.class));

        // Start low battery level broadcast receiver
        this.registerReceiver(br, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(br);
    }

    // Transition Request for Still events
    private ActivityTransitionRequest buildTransitionRequest() {
        List<ActivityTransition> transitions = new ArrayList<>();
        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_ENTER)
                .build());
        transitions.add(new ActivityTransition.Builder()
                .setActivityType(DetectedActivity.STILL)
                .setActivityTransition(ActivityTransition.ACTIVITY_TRANSITION_EXIT)
                .build());
        return new ActivityTransitionRequest(transitions);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putString(KEY_SCREEN, this.currentScreen);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check permissions
        ActivityCompat.requestPermissions(this, Constants.PERMISSIONS,
                REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.general, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_conf:
                Intent intent = new Intent(this, ConfActivityVip.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                return true;
            case android.R.id.home:
                this.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                // Exit if permission not granted
                for (int result : grantResults) {
                    if (result != PackageManager.PERMISSION_GRANTED) {
                        Toast.makeText(this, getResources().getString(
                                R.string.text_permission_toast), Toast.LENGTH_SHORT).show();
                        finish();
                        return;
                    }
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        updateActionBar();
    }

    @Override
    public void navigate(Fragment fragment, @Nullable String backStack) {
        setSupportActionBar(null);

        if(SCREEN_DETAIL.equals(this.currentScreen)) {
            getSupportFragmentManager().popBackStack();
        }

        if(fragment instanceof DetailMemberFragment) {
            this.currentScreen = SCREEN_DETAIL;
        } else if (fragment instanceof MemberListFragment) {
            this.currentScreen = SCREEN_MEMBERS;
        } else if (fragment instanceof VipMapFragment){
            this.currentScreen = SCREEN_MAP;
        } else if (fragment instanceof LinkFragment){
            this.currentScreen = SCREEN_LINK;
        }

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if(backStack != null) {
            transaction.addToBackStack(backStack);
        }
        transaction.replace(R.id.layout_vip, fragment).commit();
    }

    @Override
    public void setActionBar(Toolbar toolbar) {
        setSupportActionBar(toolbar);
        updateActionBar();
    }

    @Override
    public void goBack() {
        getSupportFragmentManager().popBackStack();
    }

    private void updateActionBar() {
        if (getSupportActionBar() == null) {
            return;
        }
        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
