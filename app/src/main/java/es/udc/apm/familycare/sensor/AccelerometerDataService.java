package es.udc.apm.familycare.sensor;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

public class AccelerometerDataService extends Service {



    private SensorListener sensorListener = null;
    private SensorManager sensorManager;

    private int sensorType = Sensor.TYPE_ACCELEROMETER;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("AccelerometerService", "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        if (sensorManager == null) {
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        }

        if (sensorManager != null && sensorListener == null) {
            sensorListener = new SensorListener(this);


            HandlerThread mHandlerThread = new HandlerThread("sensorThread");
            mHandlerThread.start();
            Handler handler = new Handler(mHandlerThread.getLooper());

            sensorManager.registerListener(sensorListener,
                    sensorManager.getDefaultSensor(sensorType),
                    SensorManager.SENSOR_DELAY_NORMAL,
                    handler);
        }

        return Service.START_STICKY;
    }

    @Override
    public void onDestroy() {
        sensorManager.unregisterListener(sensorListener);
        sensorListener = null;
        super.onDestroy();
    }
}
