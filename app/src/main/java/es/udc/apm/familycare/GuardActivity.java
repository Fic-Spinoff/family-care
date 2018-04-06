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

import butterknife.BindView;
import es.udc.apm.familycare.interfaces.RouterActivity;

public class GuardActivity extends AppCompatActivity implements RouterActivity {

    private String currentScreen = null;


    @BindView(R.id.navigation) BottomNavigationView navigation;

    CharSequence text = null;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_location:
                    if(navigation.getSelectedItemId() != item.getItemId()) {
                        navigate(new CustomMapFragment(), null);
                    }
                    return true;
                case R.id.navigation_activity:
                    text = "Activity Button pressed!";
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.navigation_statistics:
                    text = "Statistic Button pressed!";
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.navigation_state:
                    startActivity(new Intent(GuardActivity.this, StateActivity.class));
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vigilant_container);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_activity);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        updateActionBar();
    }

    @Override
    public void navigate(Fragment fragment, @Nullable String backStack) {
        setSupportActionBar(null);


        this.currentScreen = null;

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
        if(getSupportActionBar() == null) {
            return;
        }
        if(getSupportFragmentManager().getBackStackEntryCount() == 0) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        } else {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}
