package es.udc.apm.familycare;

import android.content.Intent;
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

import butterknife.BindView;
import butterknife.ButterKnife;
import es.udc.apm.familycare.interfaces.RouterActivity;
import es.udc.apm.familycare.maps.GuardMapFragment;
import es.udc.apm.familycare.link.BindFragment;
import es.udc.apm.familycare.state.StateFragment;
import es.udc.apm.familycare.utils.Constants;

public class GuardActivity extends AppCompatActivity implements RouterActivity {

    private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 1;

    private String currentScreen = null;

    @BindView(R.id.navigation) BottomNavigationView navigation;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_location:
                    if (navigation.getSelectedItemId() != item.getItemId()) {
                        navigate(new GuardMapFragment(), null);
                    }
                    return true;
                case R.id.navigation_activity:
                    navigate(BindFragment.newInstance(), null);
                    return true;
                case R.id.navigation_statistics:
                    navigate(new WalkFragment(), null);
                    return true;
                case R.id.navigation_state:
                    navigate(new StateFragment(), null);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guard);

        ButterKnife.bind(this);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_activity);
    }


    @Override
    protected void onStart() {
        super.onStart();
        // Check permissions
        ActivityCompat.requestPermissions(this, Constants.PERMISSIONS,
                REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
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
                startActivity(new Intent(this, ConfActivity_Guard.class));
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    public void navigate(Fragment fragment, @Nullable String backStack) {
        setSupportActionBar(null);

        this.currentScreen = null;

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (backStack != null) {
            transaction.addToBackStack(backStack);
        }
        transaction.replace(R.id.layout_guard, fragment).commit();
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
