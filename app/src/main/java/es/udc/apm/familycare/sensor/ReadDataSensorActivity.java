package es.udc.apm.familycare.sensor;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import java.util.Calendar;

import es.udc.apm.familycare.R;

public class ReadDataSensorActivity extends Activity implements SensorEventListener {

    public final static String EXTRA_MESSAGE = "com.example.sensefall.MESSAGE";
    public static final String PREFS_NAME_SITTING = "SittingPrefFile";
    public static final String PREFS_NAME_FALL = "FallPrefFile";
    public static final String PREFS_NAME_WALKING = "WalkingPrefFile";
    public static final String PREFS_NAME_STANDING = "StandingPrefFile";
    public double ax,ay,az;
    public double a_norm;
    public int i=0;
    static int BUFF_SIZE=50;
    static public double[] window = new double[BUFF_SIZE];
    double sigma=0.5,th=10,th1=5,th2=2;
    private SensorManager sensorManager;
    public static String curr_state,prev_state;
    private int sensorType = Sensor.TYPE_ACCELEROMETER;
    private SharedPreferences sharedPrefSitting,sharedPrefFall,sharedPrefWalking,sharedPrefStanding;


    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_data_sensor);
        sharedPrefSitting = getApplicationContext().getSharedPreferences(PREFS_NAME_SITTING, Context.MODE_PRIVATE);
        sharedPrefFall = getApplicationContext().getSharedPreferences(PREFS_NAME_FALL, Context.MODE_PRIVATE);
        sharedPrefWalking = getApplicationContext().getSharedPreferences(PREFS_NAME_WALKING, Context.MODE_PRIVATE);
        sharedPrefStanding = getApplicationContext().getSharedPreferences(PREFS_NAME_STANDING, Context.MODE_PRIVATE);

        sensorManager=(SensorManager) getSystemService(SENSOR_SERVICE);
        sensorManager.registerListener(this, sensorManager.getDefaultSensor(sensorType), SensorManager.SENSOR_DELAY_UI);
        initialize();
    }
    private void initialize() {
        // TODO Auto-generated method stub
        for(i=0;i<BUFF_SIZE;i++){
            window[i]=0;
        }
        prev_state="none";
        curr_state="none";

    }
    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {

    }

    @SuppressLint("ParserError")
    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType()==sensorType){
            ax=event.values[0];
            ay=event.values[1];
            az=event.values[2];
            AddData(ax,ay,az);
            posture_recognition(window,ay);
            systemState(curr_state,prev_state);
            if(!prev_state.equalsIgnoreCase(curr_state)){
                prev_state=curr_state;
            }

        }
    }
    private void posture_recognition(double[] window2,double ay2) {
        // TODO Auto-generated method stub
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
                curr_state="none";
            }

        }



    }
    private int compute_zrc(double[] window2) {
        // TODO Auto-generated method stub
        int count=0;
        for(i=1;i<=BUFF_SIZE-1;i++){

            if((window2[i]-th)<sigma && (window2[i-1]-th)>sigma){
                count=count+1;
            }

        }
        return count;
    }
    private void systemState(String curr_state1, String prev_state1) {
        // TODO Auto-generated method stub

        //Fall !!
        if(!prev_state1.equalsIgnoreCase(curr_state1)){
            if(curr_state1.equalsIgnoreCase("fall")){
                saveDataNumberFallUser(Calendar.getInstance().getTimeInMillis());
            }
            if(curr_state1.equalsIgnoreCase("sitting")){
                saveDataNumberSittingUser(Calendar.getInstance().getTimeInMillis());
            }
            if(curr_state1.equalsIgnoreCase("standing")){
                saveDataNumberStandingUser(Calendar.getInstance().getTimeInMillis());
            }
            if(curr_state1.equalsIgnoreCase("walking")){
                saveDataNumberWalkingUser(Calendar.getInstance().getTimeInMillis());
            }
        }


    }
    private void AddData(double ax2, double ay2, double az2) {
        // TODO Auto-generated method stub
        a_norm=Math.sqrt(ax*ax+ay*ay+az*az);
        for(i=0;i<=BUFF_SIZE-2;i++){
            window[i]=window[i+1];
        }
        window[BUFF_SIZE-1]=a_norm;

    }

    @Override
    protected void onResume()
    {
        super.onResume();

        sensorManager.registerListener(this, sensorManager.getDefaultSensor(sensorType),
                SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause()
    {
        super.onPause();

        sensorManager.unregisterListener(this);
    }

    private void saveDataNumberSittingUser(long value){

        // Save preference
        SharedPreferences.Editor editor = sharedPrefSitting.edit();
        editor.putLong(PREFS_NAME_SITTING, value);
        editor.commit();
    }

    private void saveDataNumberFallUser(long value){

        // Save preference
        SharedPreferences.Editor editor = sharedPrefFall.edit();
        editor.putLong(PREFS_NAME_FALL, value);
        editor.commit();;
    }

    private void saveDataNumberWalkingUser(long value){

        // Save preference
        SharedPreferences.Editor editor = sharedPrefWalking.edit();
        editor.putLong(PREFS_NAME_WALKING, value);
        editor.commit();
    }

    private void saveDataNumberStandingUser(long value){

        // Save preference
        SharedPreferences.Editor editor = sharedPrefStanding.edit();
        editor.putLong(PREFS_NAME_STANDING, value);
        editor.commit();
    }

}
