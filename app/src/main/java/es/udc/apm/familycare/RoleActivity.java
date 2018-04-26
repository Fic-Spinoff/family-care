package es.udc.apm.familycare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

public class RoleActivity extends AppCompatActivity {

    public final static String KEY_ROLE = "KEY_ROLE";
    public final static String ROLE_VIP = "ROLE_VIP";
    public final static String ROLE_GUARD = "ROLE_GUARD";
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences("StartPrefs", MODE_PRIVATE);
        setContentView(R.layout.activity_role);

        final Button btnGuard = findViewById(R.id.btnCardGuard);
        btnGuard.setOnClickListener(v -> {
            Intent intent = new Intent(RoleActivity.this, TermsActivity.class);
            intent.putExtra(KEY_ROLE, ROLE_GUARD);
            editor = prefs.edit();
            editor.putString(KEY_ROLE, ROLE_GUARD);
            editor.apply();
            startActivity(intent);
            finish();
        });

        final Button btnVip = findViewById(R.id.btnCardVip);
        btnVip.setOnClickListener(v -> {
            Intent intent = new Intent(RoleActivity.this, TermsActivity.class);
            intent.putExtra(KEY_ROLE, ROLE_VIP);
            editor = prefs.edit();
            editor.putString(KEY_ROLE, ROLE_VIP);
            editor.apply();
            startActivity(intent);
            finish();
        });
    }
}
