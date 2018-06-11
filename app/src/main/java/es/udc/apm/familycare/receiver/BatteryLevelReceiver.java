package es.udc.apm.familycare.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.widget.Toast;

import es.udc.apm.familycare.R;

public class BatteryLevelReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int level, scale;
        float totallevel = 0;
        if(intent.getExtras() != null) {
            level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL, 0);
            scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE, 100);
            if ((level >= 0) && (scale > 0)) {
                totallevel = (level*100)/scale;
                if (totallevel <= 15) {
                    String message = context.getString(R.string.battery_low) + ": " + totallevel + "%";
                    Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}