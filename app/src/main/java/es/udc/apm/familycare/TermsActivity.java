package es.udc.apm.familycare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class TermsActivity extends AppCompatActivity {

    private String role;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences("StartPrefs", MODE_PRIVATE);
        setContentView(R.layout.activity_terms);
        ButterKnife.bind(this);

        this.role = this.getIntent().getStringExtra(RoleActivity.KEY_ROLE);
        if (this.role == null) {
            this.role = prefs.getString("KEY_ROLE", null);
            if (this.role == null) {
                Toast.makeText(this, "Error: No role selected!", Toast.LENGTH_SHORT).show();
                this.onClickDisagree(null);
            }
        }
    }

    @Override
    public void onBackPressed() {
        this.onClickDisagree(null);
    }

    @OnClick({R.id.btn_terms_disagree})
    public void onClickDisagree(View v) {
        editor = prefs.edit();
        editor.putBoolean("TERMS_AGREED", false);
        editor.apply();
        startActivity(new Intent(this, RoleActivity.class));
        finish();
    }

    @OnClick({R.id.btn_terms_agree})
    public void onClickAgree(View v) {
        editor = prefs.edit();
        editor.putBoolean("TERMS_AGREED", true);
        editor.apply();
        if(this.role.equals(RoleActivity.ROLE_GUARD)) {
            startActivity(new Intent(this, GuardActivity.class));
            finish();
        } else if(this.role.equals(RoleActivity.ROLE_VIP)) {
            startActivity(new Intent(this, VipActivity.class));
            finish();
        }
    }

}
