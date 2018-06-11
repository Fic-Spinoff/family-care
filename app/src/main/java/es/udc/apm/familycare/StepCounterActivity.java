package es.udc.apm.familycare;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class StepCounterActivity extends AppCompatActivity implements SensorEventListener {

    private StringBuilder stringBuilder = new StringBuilder();
    private TextView textView;
    private TextView textViewData;
    private SensorManager manager;

    private int sensorType = Sensor.TYPE_STEP_COUNTER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_counter);

        textView = findViewById(R.id.textViewId );
        textViewData = findViewById(R.id.textViewData );
        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        textView.setText( (manager.getSensorList( sensorType).size() == 0)
                ? "No hay contador de pasos" : manager.getSensorList( sensorType ).get(0).toString()  );

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        stringBuilder.setLength(0);
        stringBuilder.append(" Número de pasos realizados: " + sensorEvent.values[0] +"\n");
        textViewData.setText(stringBuilder.toString());

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }


    @Override
    protected void onResume()
    {
        super.onResume();

        manager.registerListener(this, manager.getDefaultSensor(sensorType),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        manager.unregisterListener(this);
    }
}
