package es.udc.apm.familycare;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import java.util.HashMap;

import butterknife.ButterKnife;
import butterknife.OnClick;
import es.udc.apm.familycare.login.UserRepository;
import es.udc.apm.familycare.utils.Constants;


public class TermsActivity extends AppCompatActivity {

    private String role;
    private SharedPreferences prefs;

    private UserRepository mRepo;
    private String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        prefs = getSharedPreferences(Constants.PREFS_USER, MODE_PRIVATE);
        setContentView(R.layout.activity_terms);
        ButterKnife.bind(this);

        this.role = this.getIntent().getStringExtra(Constants.PREFS_USER_ROLE);
        if (this.role == null) {
            Toast.makeText(this, "Error: No role selected!", Toast.LENGTH_SHORT).show();
            this.onClickDisagree(null);
        }

        this.mRepo = new UserRepository();
        this.uid = prefs.getString(Constants.PREFS_USER_UID, null);
    }

    @Override
    public void onBackPressed() {
        this.onClickDisagree(null);
    }

    @OnClick({R.id.btn_terms_disagree})
    public void onClickDisagree(View v) {
        startActivity(new Intent(this, RoleActivity.class));
        finish();
    }

    @OnClick({R.id.btn_terms_agree})
    public void onClickAgree(View v) {
        if(this.role.equals(Constants.ROLE_GUARD)) {
            this.setRole(Constants.ROLE_GUARD);
            prefs.edit()
                    .putString(Constants.PREFS_USER_ROLE, Constants.ROLE_GUARD)
                    .apply();
            startActivity(new Intent(this, GuardActivity.class));
            finish();
        } else if(this.role.equals(Constants.ROLE_VIP)) {
            this.setRole(Constants.ROLE_VIP);
            prefs.edit()
                    .putString(Constants.PREFS_USER_ROLE, Constants.ROLE_VIP)
                    .apply();
            startActivity(new Intent(this, VipActivity.class));
            finish();
        }
    }

    private void setRole(String role) {
        if(this.uid == null) {
            return;
        }
        HashMap<String, Object> values = new HashMap<>();
        values.put("role", role);
        this.mRepo.editUser(this.uid, values);
    }
}
