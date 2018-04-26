package es.udc.apm.familycare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SharedPreferences prefs =
                getSharedPreferences("StartPrefs", MODE_PRIVATE);
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        setContentView(R.layout.activity_splash);

        final Handler handler = new Handler();
        handler.postDelayed(() -> {
            Class c = LoginActivity.class;
            if (mAuth.getCurrentUser() != null) {
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
                } else {
                    c = RoleActivity.class;
                }
            }
            startActivity(new Intent(SplashActivity.this, c));
            finish();
        }, 800);
    }
}
