
package es.udc.apm.familycare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;
import es.udc.apm.familycare.utils.Constants;

public class RoleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role);

        ButterKnife.bind(this);
    }

    @OnClick(R.id.btnCardGuard) void clickGuard() {
        Intent intent = new Intent(RoleActivity.this, TermsActivity.class);
        intent.putExtra(Constants.PREFS_USER_ROLE, Constants.ROLE_GUARD);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.btnCardVip) void clickVip() {
        Intent intent = new Intent(RoleActivity.this, TermsActivity.class);
        intent.putExtra(Constants.PREFS_USER_ROLE, Constants.ROLE_VIP);
        startActivity(intent);
        finish();
    }
}
