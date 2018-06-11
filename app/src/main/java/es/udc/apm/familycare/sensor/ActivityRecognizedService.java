package es.udc.apm.familycare.sensor;

import android.annotation.SuppressLint;
import android.app.IntentService;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.google.android.gms.location.ActivityTransition;
import com.google.android.gms.location.ActivityTransitionEvent;
import com.google.android.gms.location.ActivityTransitionResult;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import es.udc.apm.familycare.utils.Constants;

/**
 * Created by Gonzalo on 10/06/2018.
 */

@SuppressLint("ApplySharedPref")
public class ActivityRecognizedService extends IntentService {

    public static final int STATE_STILL = 0;
    public static final int STATE_NO_STILL = 1;

    public ActivityRecognizedService() {
        super("ActivityRecognizedService");
        Log.e("ActivityRecognition", "Service");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.e("ActivityRecognition", "Intent");
        if (ActivityTransitionResult.hasResult(intent)) {
            ActivityTransitionResult result = ActivityTransitionResult.extractResult(intent);
            if (result != null) {
                // Prefs refs
                SharedPreferences prefs =
                        this.getSharedPreferences(Constants.Prefs.USER, MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                String uid = prefs.getString(Constants.PREFS_USER_UID, null);

                //Firebase ref
                FirebaseFirestore firestore = FirebaseFirestore.getInstance();

                for (ActivityTransitionEvent event : result.getTransitionEvents()) {
                    if (event.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_ENTER) {
                        Log.e("ActivityRecognition", "Enter");
                        // Update state in prefs
                        editor.putInt(Constants.Prefs.KEY_VIP_STATE, STATE_STILL);

                        // If no still -> Update firebase
                        if (!prefs.getBoolean(Constants.Prefs.KEY_VIP_STILL, false)) {
                            // Update firebase
                            if (uid != null) {
                                Map<String, Object> data = new Hashtable<>();
                                data.put(Constants.Properties.VIP_STILL_SINCE, new Date());
                                firestore.collection(Constants.Collections.USERS).document(uid).update(data);
                                editor.putBoolean(Constants.Prefs.KEY_VIP_STILL, true);
                            }
                        }
                    } else if (event.getTransitionType() == ActivityTransition.ACTIVITY_TRANSITION_EXIT) {
                        Log.e("ActivityRecognition", "Exit");
                        // Update state in prefs
                        editor.putInt(Constants.Prefs.KEY_VIP_STATE, STATE_NO_STILL);

                        // If no timer
                        if (prefs.getBoolean(Constants.Prefs.KEY_VIP_TIMER, false)) {
                            editor.putBoolean(Constants.Prefs.KEY_VIP_TIMER, true);
                            // Update firebase
                            Timer timer = new Timer();
                            timer.schedule(new TimerTask() {
                                @Override
                                public void run() {
                                    // If current state in prefs exit -> clear firebase
                                    if (prefs.getInt(Constants.Prefs.KEY_VIP_STATE, STATE_NO_STILL) == STATE_NO_STILL) {
                                        // Clear firebase
                                        if (uid != null) {
                                            Map<String, Object> data = new Hashtable<>();
                                            data.put(Constants.Properties.VIP_STILL_SINCE, null);
                                            firestore.collection(Constants.Collections.USERS).document().update(data);
                                            editor.putBoolean(Constants.Prefs.KEY_VIP_STILL, false);
                                        }
                                    }
                                    // Update timer state
                                    editor.putBoolean(Constants.Prefs.KEY_VIP_TIMER, false);
                                }
                            }, 15000);
                        }
                    }
                    // Commit prefs
                    editor.commit();
                }
            }
        }
    }
}