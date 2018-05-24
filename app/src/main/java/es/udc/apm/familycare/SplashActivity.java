package es.udc.apm.familycare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

import es.udc.apm.familycare.login.LoginActivity;
import es.udc.apm.familycare.utils.Constants;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs = getSharedPreferences(Constants.PREFS_USER, MODE_PRIVATE);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_splash);

        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            Class c = LoginActivity.class;
            if (mAuth.getCurrentUser() != null) {
                // TODO: Role from user, not from prefs
                String role = prefs.getString(Constants.PREFS_USER_ROLE, null);
                if (role != null) {
                    switch (role) {
                        case Constants.ROLE_VIP:
                            c = VipActivity.class;
                            break;
                        case Constants.ROLE_GUARD:
                            c = GuardActivity.class;
                            break;
                    }
                } else {
                    c = RoleActivity.class;
                }
            }
            startActivity(new Intent(SplashActivity.this, c));
            finish();
        }, 800);
    }
}
