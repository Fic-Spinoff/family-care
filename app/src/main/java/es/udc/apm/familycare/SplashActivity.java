package es.udc.apm.familycare;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import es.udc.apm.familycare.login.LoginActivity;
import es.udc.apm.familycare.utils.Constants;

public class SplashActivity extends AppCompatActivity {

    @SuppressLint("ApplySharedPref")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_splash);

        if (mAuth.getCurrentUser() != null) {
            // Update shared prefs if user logged in
            getSharedPreferences(Constants.PREFS_USER, MODE_PRIVATE).edit()
                    .putString(Constants.PREFS_USER_UID, mAuth.getCurrentUser().getUid()).commit();

            FamilyCare.getUser().observe(this, user -> {
                // Init intent to select role activity
                Intent intent = new Intent(SplashActivity.this, RoleActivity.class);

                // If role selected init app with that role
                if(user != null && user.getRole() != null) {
                    if (Constants.ROLE_VIP.equals(user.getRole())) {
                        intent = new Intent(SplashActivity.this, VipActivity.class);
                    } else if (Constants.ROLE_GUARD.equals(user.getRole())) {
                        intent = new Intent(SplashActivity.this, GuardActivity.class);
                    }
                }

                // Start next activity
                startActivity(intent);
                finish();
            });
        } else {
            // If no logged in show splash screen a few millis
            new Handler().postDelayed(() -> {
                // Navigate Login
                startActivity(new Intent(SplashActivity.this, LoginActivity.class));
                finish();
            }, 600);
        }
    }
}
