package es.udc.apm.familycare.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;

public class BatteryLevelReceiver extends BroadcastReceiver {

    /**
     * Receives Battery low events
     */
    @Override
    public void onReceive(Context context, Intent intent) {
        if (intent.getAction() == null || !Intent.ACTION_BATTERY_LOW.equals(intent.getAction())) {
            return;
        }

        if(intent.getExtras() != null) {
            int level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
            if ((level >= 0) && (scale > 0)) {
                int currentLevel = (level * 100) / scale;
                // TODO Event
            }
        }
    }
}