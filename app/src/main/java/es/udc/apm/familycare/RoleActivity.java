package es.udc.apm.familycare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class RoleActivity extends AppCompatActivity {

    public final static  String KEY_ROLE = "KEY_ROLE";
    public final static  String ROLE_VIP = "ROLE_VIP";
    public final static  String ROLE_GUARD = "ROLE_GUARD";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role);

        final Button btnGuard = findViewById(R.id.btnCardGuard);
        btnGuard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(RoleActivity.this, TermsActivity.class);
                intent.putExtra(KEY_ROLE, ROLE_GUARD);
                startActivity(intent);
                finish();
            }
        });

        final Button btnVip = findViewById(R.id.btnCardVip);
        btnVip.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(RoleActivity.this, TermsActivity.class);
                intent.putExtra(KEY_ROLE, ROLE_VIP);
                startActivity(intent);
                finish();
            }
        });
    }
}
