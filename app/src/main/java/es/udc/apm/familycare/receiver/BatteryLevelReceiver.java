package es.udc.apm.familycare.receiver;

import android.arch.lifecycle.Observer;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.firebase.Timestamp;

import java.util.Date;

import es.udc.apm.familycare.FamilyCare;
import es.udc.apm.familycare.R;
import es.udc.apm.familycare.model.Event;
import es.udc.apm.familycare.model.EventType;
import es.udc.apm.familycare.model.User;
import es.udc.apm.familycare.repository.EventRepository;
import es.udc.apm.familycare.utils.Constants;

public class BatteryLevelReceiver extends BroadcastReceiver {

    private static final int LOW_BATTERY_LEVEL = 15;

    /**
     * Receives Battery low events
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("BatteryLevelReceiver", "onReceive()");
        if (intent.getAction() == null || !Intent.ACTION_BATTERY_LOW.equals(intent.getAction())) {
            return;
        }

        //Send event
        FamilyCare.getUser().observeForever(new Observer<User>() {
            @Override
            public void onChanged(@Nullable User user) {
                if (user != null && Constants.ROLE_VIP.equals(user.getRole())) {
                    Log.d("BatteryLevelReceiver", "onReceive(): Event");
                    new EventRepository().createEvent(user.getUid(), new Event(
                            EventType.BATTERY.getValue(),
                            context.getString(R.string.caption_low_battery),
                            context.getString(R.string.text_low_battery, LOW_BATTERY_LEVEL),
                            new Timestamp(new Date())
                    ));
                    FamilyCare.getUser().removeObserver(this);
                }
            }
        });
    }
}