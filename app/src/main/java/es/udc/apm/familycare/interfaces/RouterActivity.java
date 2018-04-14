package es.udc.apm.familycare.interfaces;


import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

/**
 * Created by Gonzalo on 05/04/2018.
 */

public interface RouterActivity {
    void navigate(Fragment fragment, @Nullable String backStack);
    void setActionBar(Toolbar toolbar);
    void goBack();
}
