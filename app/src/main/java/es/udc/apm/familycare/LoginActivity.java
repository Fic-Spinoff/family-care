package es.udc.apm.familycare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import butterknife.ButterKnife;
import es.udc.apm.familycare.utils.Utils;

public class LoginActivity extends AppCompatActivity {

    private ViewPager viewPager;
    private LinearLayout dotsLayout;
    private Fragment[] layouts;
    private static final String TAG = "LoginActivity";
    private GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 0 ;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseUser user;
    private SharedPreferences prefs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences("StartPrefs", MODE_PRIVATE);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        initControls();

        Log.d(TAG, "default_web_client_id: " + getString(R.string.default_web_client_id));
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this , connectionResult -> {

                } /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                ///// Acá podemos llamar al método que crea usuario en ua BD de Firebase
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
            } else {
                // User is signed out
                Log.d(TAG, "onAuthStateChanged:signed_out");
            }
        };

        View signInButton = findViewById(R.id.btn_login_google);
        signInButton.setOnClickListener(v -> {
            Utils utils = new Utils(LoginActivity.this);
            int id = v.getId();
            if (id == R.id.btn_login_google) {
                if (utils.isNetworkAvailable()) {
                    signIn();
                } else {
                    Toast.makeText(LoginActivity.this, "Oops! no internet connection!",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();
                if (account != null) {
                    SharedPreferences.Editor editor = prefs.edit();
                    editor.putBoolean("IS_LOGGED_IN", true);
                    editor.putString("GOOGLE_ID_TOKEN", account.getIdToken());
                    editor.putString("GOOGLE_EMAIL", account.getEmail());
                    editor.putString("USER_NAME", account.getDisplayName());
                    editor.apply();
                    firebaseAuthWithGoogle(account);
                }
            } else {
                // Google Sign In failed, update UI appropriately
                Log.e(TAG, "Login Unsuccessful.");
                Toast.makeText(this, "Login Unsuccessful", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        Log.d(TAG, "firebaseAuthWithGoogle:" + acct.getId());

        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    Log.d(TAG, "signInWithCredential:onComplete:" + task.isSuccessful());
                    // If sign in fails, display a message to the user.
                    if (!task.isSuccessful() || user == null) {
                        Log.w(TAG, "signInWithCredential", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    /* If sign in succeeds the auth state listener will be notified and logic
                    to handle the signed in user can be handled in the listener.*/
                    Toast.makeText(LoginActivity.this, "Signed in as " + user.getEmail(),
                            Toast.LENGTH_LONG).show();
                    Class c = RoleActivity.class;
                    String role = prefs.getString("KEY_ROLE", null);
                    Boolean terms = prefs.getBoolean("TERMS_AGREED", false);
                    if (role != null) {
                        if (terms) {
                            switch (role) {
                                case RoleActivity.ROLE_VIP:
                                    c = VipActivity.class;
                                    break;
                                case RoleActivity.ROLE_GUARD:
                                    c = GuardActivity.class;
                                    break;
                            }
                        } else {
                            c = TermsActivity.class;
                        }
                    }
                    Intent intent = new Intent(LoginActivity.this, c);
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent);
                    finish();
                });
    }

    private void initControls() {
        viewPager = findViewById(R.id.viewPager);
        dotsLayout = findViewById(R.id.layoutDots);

        initViewPagerControls();
    }

    private void initViewPagerControls() {
        // layouts of all welcome sliders
        // add few more layouts if you want
        layouts = new Fragment[]{PageOne.newInstance(), PageTwo.newInstance()};
        // adding bottom dots
        addBottomDots(0);

        Transformer mTransformer = new Transformer((PageOne) layouts[0], (PageTwo) layouts[1]);

        ViewPagerAdapter pagerAdapter = new ViewPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(pagerAdapter);
        viewPager.setPageTransformer(true, mTransformer);
        viewPager.addOnPageChangeListener(viewPagerPageChangeListener);
    }

    private void addBottomDots(int currentPage) {
        TextView[] dots = new TextView[layouts.length];
        dotsLayout.removeAllViews();

        for (int i = 0; i < dots.length; i++) {
            dots[i] = new TextView(this);
            dots[i].setText(Html.fromHtml("•"));
            dots[i].setTextSize(35);
            dots[i].setTextColor(getResources().getColor(R.color.colorPrimaryDark));
            dotsLayout.addView(dots[i]);
        }

        if (dots.length >= currentPage)
            dots[currentPage].setTextColor(getResources().getColor(R.color.white));
    }

    //  viewpager change listener
    private final ViewPager.OnPageChangeListener viewPagerPageChangeListener = new ViewPager.OnPageChangeListener() {
        @Override
        public void onPageSelected(int position) {
            addBottomDots(position);
        }

        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {

        }

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }
    };

    // View pager adapter
    class ViewPagerAdapter extends FragmentPagerAdapter {

        ViewPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return layouts[position];
        }

        @Override
        public int getCount() {
            return layouts.length;
        }
    }
}
