package es.udc.apm.familycare;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class UnbindActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = UnbindActivity.class.getSimpleName();
    Button unbindButton;
    Toolbar unbindToolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_unbind);
        unbindButton = (Button) findViewById(R.id.unbindButton);
        unbindButton.setOnClickListener(this);
        unbindToolbar = (Toolbar) findViewById(R.id.unbindToolbar);
        unbindToolbar.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        // Realmente lo que hay que hacer al hacer click en la toolbar es ir hacia la
        // actividad anterior es decir, la pantalla de configuración)
        // Por ahora, lo que hacemos es imprimir en el log hasta que esté hecha esa activity
        String tipo = "";
        switch (view.getId()) {
            case R.id.unbindButton:
                tipo = "button";
                break;
            case R.id.unbindToolbar:
                tipo = "toolbar";
                break;
        }
        Log.d(TAG, "onClick() Unbind device to account, clicked on " + tipo);
    }
}
