package es.udc.apm.familycare;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
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
    public void onBackPressed() {
        super.onBackPressed();
        updateActionBar();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.general, menu);

        MenuItem item = menu.findItem(R.id.menu_conf);
        Drawable drawable = item.getIcon();
        drawable = DrawableCompat.wrap(drawable);
        DrawableCompat.setTint(drawable, ContextCompat.getColor(this, R.color.white));
        item.setIcon(drawable);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.menu_conf:
                startActivity(new Intent(this, ConfActivity.class));
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
        if(backStack != null) {
            transaction.addToBackStack(backStack);
        }
        transaction.replace(R.id.layout_guard, fragment).commit();
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
