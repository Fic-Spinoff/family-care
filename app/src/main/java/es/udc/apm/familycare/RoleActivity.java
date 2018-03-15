package es.udc.apm.familycare;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class RoleActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_role);

        final Button btnGuard = findViewById(R.id.btnCardGuard);
        btnGuard.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onClickRolVigilante(v);
            }
        });

        final Button btnVip = findViewById(R.id.btnCardVip);
        btnVip.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                onClickRolVigilado(v);
            }
        });
    }

    public void onClickRolVigilante(View v){
        Context context = getApplicationContext();
        CharSequence text = getString(R.string.text_toast_vigilante);
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }

    public void onClickRolVigilado(View v){
        Context context = getApplicationContext();
        CharSequence text = getString(R.string.text_toast_vigilado);
        int duration = Toast.LENGTH_SHORT;

        Toast toast = Toast.makeText(context, text, duration);
        toast.show();
    }
}
