package es.udc.apm.familycare.sensor;

import android.annotation.SuppressLint;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

import es.udc.apm.familycare.utils.Constants;

/**
 * Created by Gonzalo on 10/06/2018.
 */

public class SensorListener implements SensorEventListener {

    public interface EventListener {
        void onFall(int event);
    }

    private  static final int FALL_TIME_OFFSET = 15000;

    private static final double sigma = 0.5, th = 10, th1 = 5, th2 = 2;
    private static final int BUFF_SIZE = 50;

    public int i=0;
    private static double[] window = new double[BUFF_SIZE];
    private static String curr_state, prev_state;

    private boolean moveAfterFall = false;
    private boolean fallTimerActive = false;

    private EventListener mListener = null;

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {

    }

    SensorListener() {
        this.initialize();
    }

    private void initialize() {
        for(i = 0; i < BUFF_SIZE; i++){
            window[i] = 0;
        }
        prev_state="none";
        curr_state="none";
    }

    @SuppressLint("ParserError")
    @Override
    public void onSensorChanged(SensorEvent event) {
        int sensorType = Sensor.TYPE_ACCELEROMETER;
        if (event.sensor.getType()== sensorType){
            double ax = event.values[0];
            double ay = event.values[1];
            double az = event.values[2];
            AddData(ax, ay, az);
            posture_recognition(window, ay);
            systemState(curr_state, prev_state);
            prev_state = curr_state;
        }
    }

    private void AddData(double ax2, double ay2, double az2) {
        double a_norm = Math.sqrt(ax2 * ax2 + ay2 * ay2 + az2 * az2);
        for(i = 0; i <= BUFF_SIZE - 2; i++){
            window[i] = window[i + 1];
        }
        window[BUFF_SIZE - 1] = a_norm;
    }

    private void posture_recognition(double[] window2,double ay2) {
        int zrc=compute_zrc(window2);
        if(zrc==0){

            if(Math.abs(ay2)<th1){
                curr_state="sitting";
            }else{
                curr_state="standing";
            }

        }else{

            if(zrc>th2){
                curr_state="walking";
            }else{
                curr_state="fall";
            }

        }
    }

    private int compute_zrc(double[] window2) {
        int count=0;
        for(i=1;i<=BUFF_SIZE-1;i++){

            if((window2[i]-th)<sigma && (window2[i-1]-th)>sigma){
                count=count+1;
            }

        }
        return count;
    }

    private void systemState(String curr_state1, String prev_state1) {
        if(!prev_state1.equalsIgnoreCase(curr_state1)){
            if(curr_state1.equalsIgnoreCase("fall")){
                Log.e("SensorListener", "Potential fall");
                // Start timer to detect falls
                if (!this.fallTimerActive) {
                    // Reset moved
                    this.moveAfterFall = false;
                    this.fallTimerActive = true;
                    Timer timer = new Timer();
                    timer.schedule(new TimerTask() {
                        @Override
                        public void run() {
                            if (!SensorListener.this.moveAfterFall) {
                                Log.e("SensorListener", "fall");
                                if (SensorListener.this.mListener != null) {
                                    mListener.onFall(Constants.Events.FALL);
                                }
                            }
                            SensorListener.this.fallTimerActive = false;
                        }
                    }, SensorListener.FALL_TIME_OFFSET);
                }
            } else {
                this.moveAfterFall = true;
                String text = "UNK";
                if (curr_state1.equalsIgnoreCase("sitting")) {
                    text = "sitting";
                } else if  (curr_state1.equalsIgnoreCase("walking")) {
                    text = "walking";
                } else if (curr_state1.equalsIgnoreCase("standing")) {
                    text = "standing";
                }
                Log.e("SensorListener", "ChangeState: "+text);
            }
        }
    }

    public void setEventListener(EventListener listener) {
        this.mListener = listener;
    }
}
