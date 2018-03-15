package es.udc.apm.familycare;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.Toast;

import butterknife.ButterKnife;
import butterknife.OnClick;

import static android.widget.Toast.LENGTH_SHORT;


public class ActivityTermsAndConditions extends AppCompatActivity {

    @OnClick({R.id.button_deny, R.id.button_accept})
    public void onClickButton(Button button) {
        Toast.makeText(this, "Boton seleccionad: " + button.getText(), LENGTH_SHORT).show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_and_conditions);
        ButterKnife.bind(this);
    }

}
