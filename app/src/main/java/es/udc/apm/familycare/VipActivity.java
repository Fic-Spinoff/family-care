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

import butterknife.BindView;
import butterknife.ButterKnife;
import es.udc.apm.familycare.interfaces.RouterActivity;

public class VipActivity extends AppCompatActivity implements RouterActivity {

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
        setContentView(R.layout.activity_vip);

        ButterKnife.bind(this);

        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_members);
    }

    @Override
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
            case android.R.id.home:
                this.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);
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
