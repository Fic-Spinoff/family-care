package es.udc.apm.familycare;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class AccelerometerActivity extends AppCompatActivity implements SensorEventListener {

    private StringBuilder stringBuilder = new StringBuilder();
    private TextView textView;
    private TextView textViewData;
    private SensorManager manager;
    private int sensorType = Sensor.TYPE_ACCELEROMETER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_accelerometer);
        textView = (TextView) findViewById(R.id.textViewId );
        textViewData = (TextView) findViewById(R.id.textViewData );
        manager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        textView.setText( (manager.getSensorList( sensorType ).size() == 0)
                ? "No hay acelerometro" : manager.getSensorList( sensorType).get(0).toString()  );

        Button giroscopioButton = (Button) findViewById(R.id.giroscopioButton);
        giroscopioButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AccelerometerActivity.this, GyroscopeSensorActivity.class);
                startActivity(intent);
                //finish();
            }
        });

    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {

        stringBuilder.setLength(0);
        stringBuilder.append(" Eje X: " + sensorEvent.values[0] +"\n"+" Eje Y: " + sensorEvent.values[1] +"\n"+ " Eje Z: " + sensorEvent.values[2]);
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

