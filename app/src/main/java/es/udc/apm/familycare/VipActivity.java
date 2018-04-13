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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.udc.apm.familycare.interfaces.RouterActivity;
import es.udc.apm.familycare.utils.Constants;

public class VipActivity extends AppCompatActivity implements RouterActivity {

    private static final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 1;

    public static final String SCREEN_DETAIL = "DETAIL";

    private String currentScreen = null;
    @BindView(R.id.navigation) BottomNavigationView navigation = null;

    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_zones:
                    if(navigation.getSelectedItemId() != item.getItemId()) {
                        navigate(new CustomMapFragment(), null);
                    }
                    return true;
                case R.id.navigation_members:
                    if (findViewById(R.id.vip_list) != null)
                        navigate(DetailMemberFragment.newInstance(0), null);
                    else
                        navigate(MembersFragment.newInstance(), null);
                    return true;
                case R.id.navigation_link:
                    navigate(LinkFragment.newInstance(), null);
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip);

        ButterKnife.bind(this);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_members);

        if (findViewById(R.id.vip_list) != null) {
            if (savedInstanceState != null) {
                Log.i("VipActivity", "savedInstanceState no es null");
                return;
            }
            Log.i("VipActivity", "Dispositivo grande");
            // Create a new Fragment to be placed in the activity layout
            MembersFragment memberlist = new MembersFragment();
            // In case this activity was started with special instructions from an
            // Intent, pass the Intent's extras to the fragment as arguments
            memberlist.setArguments(getIntent().getExtras());
            // Add the fragment to the FrameLayout
            getSupportFragmentManager().beginTransaction().add(R.id.vip_list, memberlist).commit();
        } else {
            Log.i("VipActivity", "Dispositivo pequeño");
        }
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
                startActivity(new Intent(this, ConfActivity.class));
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

        if(SCREEN_DETAIL.equals(currentScreen)) {
            getSupportFragmentManager().popBackStack();
        }

        if(fragment instanceof DetailMemberFragment) {
            this.currentScreen = SCREEN_DETAIL;
        } else {
            this.currentScreen = null;
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
