package es.udc.apm.familycare;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;


public class TermsActivity extends AppCompatActivity {

    private String role;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);
        ButterKnife.bind(this);

        this.role = this.getIntent().getStringExtra(RoleActivity.KEY_ROLE);
        if(this.role == null) {
            Toast.makeText(this, "Error: No role selected!", Toast.LENGTH_SHORT).show();
            this.onClickDisagree(null);
        }
    }

    @OnClick({R.id.btn_terms_disagree})
    public void onClickDisagree(View v) {
        startActivity(new Intent(this, RoleActivity.class));
        finish();
    }

    @OnClick({R.id.btn_terms_agree})
    public void onClickAgree(View v) {
        // TODO: Start activity
    }

}
