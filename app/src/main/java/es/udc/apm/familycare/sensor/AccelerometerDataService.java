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

import com.google.firebase.Timestamp;

import java.util.Date;

import es.udc.apm.familycare.R;
import es.udc.apm.familycare.model.Event;
import es.udc.apm.familycare.model.EventType;
import es.udc.apm.familycare.repository.EventRepository;
import es.udc.apm.familycare.utils.Constants;

public class AccelerometerDataService extends Service implements SensorListener.EventListener {

    private SensorListener sensorListener = null;
    private SensorManager sensorManager;
    private EventRepository mRepo = null;

    private int sensorType = Sensor.TYPE_ACCELEROMETER;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {

        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        this.mRepo = new EventRepository();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.e("AccelerometerService", "onStartCommand");
        super.onStartCommand(intent, flags, startId);
        if (sensorManager == null) {
            sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        }

        if (sensorManager != null && sensorListener == null) {
            sensorListener = new SensorListener();
            sensorListener.setEventListener(this);

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

    @Override
    public void onFall(int event) {
        String uid = getSharedPreferences(Constants.Prefs.USER, MODE_PRIVATE)
                .getString(Constants.Prefs.KEY_USER_UID, null);
        if(uid != null && this.mRepo != null) {
            switch (event) {
                case Constants.Events.FALL:
                    this.mRepo.createEvent(uid, new Event(
                            EventType.FALL.getValue(),
                            getString(R.string.caption_potential_fall),
                            getString(R.string.text_potential_fall),
                            new Timestamp(new Date())
                    ));
                    break;
                default:
                    break;
            }
        }
    }
}
