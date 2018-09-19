package es.udc.apm.familycare;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.util.Log;
import android.view.MenuItem;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.FirebaseAuth;

import java.util.HashMap;

import butterknife.BindView;
import butterknife.ButterKnife;
import es.udc.apm.familycare.login.LoginActivity;
import es.udc.apm.familycare.model.User;
import es.udc.apm.familycare.repository.UserRepository;
import es.udc.apm.familycare.utils.Constants;


public class ConfActivityGuard extends PreferenceActivity
{
    @BindView(R.id.confToolbar) Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new ConfActivityGuard.ConfGuardFragment()).commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.onBackPressed();
                return true;
            default:
                return super.onOptionsItemSelected(item);

        }
    }

    public static class ConfGuardFragment extends PreferenceFragment
    {

        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            super.onCreate(savedInstanceState);

            addPreferencesFromResource(R.xml.activity_config_guard);
            Activity act = this.getActivity();
            findPreference("disconnect").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference arg0) {
                    GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                            .requestEmail()
                            .build();
                    GoogleSignInClient mGoogleApiClient = GoogleSignIn.getClient(act, gso);
                    mGoogleApiClient.signOut();
                    FirebaseAuth.getInstance().signOut();
                    Intent intent = new Intent(act, LoginActivity.class);
                    startActivity(intent);
                    act.finishAffinity();
                    return true;
                }
            });
            findPreference("delete").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference arg0) {
                    UserRepository mRepo = new UserRepository();
                    String uid = act.getSharedPreferences(Constants.Prefs.USER, MODE_PRIVATE).getString(Constants.Prefs.KEY_USER_UID, null);
                    HashMap<String, Object> hashMap = new HashMap<String, Object>();
                    hashMap.put("role", null);
                    mRepo.editUser(uid, hashMap);
                    Intent intent = new Intent(act, RoleActivity.class);
                    startActivity(intent);
                    act.finishAffinity();
                    return true;
                }
            });


        }
    }
}
