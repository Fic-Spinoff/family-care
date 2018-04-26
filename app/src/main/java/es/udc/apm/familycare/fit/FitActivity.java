package es.udc.apm.familycare.fit;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import butterknife.ButterKnife;
import es.udc.apm.familycare.R;
import es.udc.apm.familycare.WalkFragment;
import es.udc.apm.familycare.interfaces.RouterActivity;

public class FitActivity extends AppCompatActivity implements RouterActivity {
    private static final String TAG = FitActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vip);
        ButterKnife.bind(this);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.layout_vip, new WalkFragment()).commit();
    }

    @Override
    public void navigate(Fragment fragment, @Nullable String backStack) {

    }

    @Override
    public void setActionBar(Toolbar toolbar) {

    }

    @Override
    public void goBack() {

    }
}
