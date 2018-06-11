package es.udc.apm.familycare.login;

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
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.Hashtable;

import butterknife.ButterKnife;
import butterknife.OnClick;
import es.udc.apm.familycare.GuardActivity;
import es.udc.apm.familycare.PageOne;
import es.udc.apm.familycare.PageTwo;
import es.udc.apm.familycare.R;
import es.udc.apm.familycare.RoleActivity;
import es.udc.apm.familycare.Transformer;
import es.udc.apm.familycare.VipActivity;
import es.udc.apm.familycare.model.User;
import es.udc.apm.familycare.repository.GroupRepository;
import es.udc.apm.familycare.repository.UserRepository;
import es.udc.apm.familycare.utils.Constants;
import es.udc.apm.familycare.utils.Utils;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";
    private static final int RC_SIGN_IN = 0 ;

    private ViewPager viewPager;
    private LinearLayout dotsLayout;
    private Fragment[] layouts;

    private GoogleApiClient mGoogleApiClient;
    private FirebaseAuth mAuth;

    private UserRepository mRepo;
    private GroupRepository mGroupRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ButterKnife.bind(this);

        initControls();

        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Api client for google sign in
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        // Firebase instance
        mAuth = FirebaseAuth.getInstance();

        // Init repository
        this.mRepo = new UserRepository();
        this.mGroupRepo = new GroupRepository();
    }

    @OnClick(R.id.btn_login_google) void onClickLogin() {
        Utils utils = new Utils(LoginActivity.this);
        if (utils.isNetworkAvailable()) {
            signIn();
        } else {
            Toast.makeText(LoginActivity.this, "Oops! no internet connection!",
                    Toast.LENGTH_SHORT).show();
        }
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
                    firebaseAuthWithGoogle(account);
                    return;
                }
            }

            // Google Sign In failed or no account, update UI appropriately
            Log.e(TAG, "Login Unsuccessful.");
            Toast.makeText(this, "Login Unsuccessful", Toast.LENGTH_SHORT).show();
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount acct) {
        AuthCredential credential = GoogleAuthProvider.getCredential(acct.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    // If sign in fails, display a message to the user.
                    if (!task.isSuccessful() || task.getResult().getUser() == null) {
                        Log.w(TAG, "signInWithCredential", task.getException());
                        Toast.makeText(LoginActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    // Sign in success
                    FirebaseUser firebaseUser = task.getResult().getUser();

                    // Save user uid
                    SharedPreferences prefs = getSharedPreferences(Constants.PREFS_USER, MODE_PRIVATE);
                    prefs.edit().putString(Constants.Prefs.KEY_USER_UID, firebaseUser.getUid()).apply();

                    //TODO: Loading
                    // If first time user create user in firestore
                    if(task.getResult().getAdditionalUserInfo().isNewUser()) {
                        User user = new User(
                                firebaseUser.getUid(),
                                firebaseUser.getDisplayName(),
                                firebaseUser.getPhotoUrl() != null ? firebaseUser.getPhotoUrl().toString() : "",
                                FirebaseInstanceId.getInstance().getToken()
                        );
                        this.mRepo.setUser(user);
                        Intent intent = new Intent(LoginActivity.this, RoleActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        // If user exists get data

                        // Update FCM Instance Id when first login
                        String fcmToken = FirebaseInstanceId.getInstance().getToken();
                        if (fcmToken != null) {
                            Hashtable<String, Object> data = new Hashtable<>();
                            data.put(Constants.Properties.FCM_ID, fcmToken);
                            this.mRepo.editUser(firebaseUser.getUid(), data);
                        }

                        // Get data and open app
                        this.mRepo.getUser(firebaseUser.getUid()).observe(this, user -> {
                            if (user != null) {
                                if(user.getRole() != null) {
                                    // If user has role start activity
                                    Intent intent = new Intent(LoginActivity.this, GuardActivity.class);
                                    if (user.getRole().equals(Constants.ROLE_VIP)) {
                                        intent = new Intent(LoginActivity.this, VipActivity.class);
                                    } else {
                                        // Update message group fcm instance id
                                        if (user.getVip() != null) {
                                            this.mGroupRepo.addGuard(user.getVip(), user.getUid(), fcmToken);
                                        }
                                    }
                                    startActivity(intent);
                                    finish();
                                } else {
                                    // If no role then choose
                                    Intent intent = new Intent(LoginActivity.this, RoleActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            } else {
                                // No user then error
                                Toast.makeText(this, "Error no user found!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });
    }

    // Login UI View
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
