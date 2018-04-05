package es.udc.apm.familycare;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import butterknife.ButterKnife;
import es.udc.apm.familycare.interfaces.RouterActivity;

public class VipActivity extends AppCompatActivity implements RouterActivity {

    public static final String SCREEN_DETAIL = "DETAIL";

    private String currentScreen = null;

    CharSequence text = null;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_zones:
                    text = "Zones Button pressed!";
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.navigation_members:
                    navigate(MembersFragment.newInstance(), null);
                    return true;
                case R.id.navigation_link:
                    startActivity(new Intent(VipActivity.this, LinkActivity.class));
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip_container);

        ButterKnife.bind(this);

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_members);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
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

        if(getSupportActionBar() == null) {
            return;
        }

        updateActionBar();
    }

    private void updateActionBar() {
        if(getSupportActionBar() == null) {
            return;
        }
        if(getSupportFragmentManager().getBackStackEntryCount() == 0) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }
}
