package es.udc.apm.familycare;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class VigilantContainerActivity extends AppCompatActivity {

    CharSequence text = null;
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_location:
                    text = "Location Button pressed!";
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.navigation_activity:
                    text = "Activity Button pressed!";
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.navigation_statistics:
                    text = "Statistic Button pressed!";
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.navigation_state:
                    text = "State Button pressed!";
                    Toast.makeText(getApplicationContext(), text, Toast.LENGTH_SHORT).show();
                    return true;
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_vigilant_container);
        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);
        navigation.setSelectedItemId(R.id.navigation_activity);
    }

}
