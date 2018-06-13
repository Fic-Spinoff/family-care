package es.udc.apm.familycare.messages;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import es.udc.apm.familycare.R;
import es.udc.apm.familycare.SplashActivity;
import es.udc.apm.familycare.utils.Constants;

public class MessagingService extends FirebaseMessagingService {

    public static final String TAG = "MessagingService";

    public MessagingService() {
        super();
    }

    @Override
    public void onDeletedMessages() {
        super.onDeletedMessages();
    }

    @Override
    public void onMessageSent(String s) {
        super.onMessageSent(s);
    }

    @Override
    public void onSendError(String s, Exception e) {
        super.onSendError(s, e);
    }

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        Log.d(TAG, "From: " + remoteMessage.getFrom());

        // Our notification, always using payload to customize notification and show heads up
        if (remoteMessage.getData() != null && remoteMessage.getData().size() > 0) {
            int type = Integer.parseInt(remoteMessage.getData().get(Constants.Properties.TYPE));
            String title = remoteMessage.getData().get(Constants.Properties.TITLE);
            String body = remoteMessage.getData().get(Constants.Properties.BODY);
            showNotification(type, title, body);
        }
    }

    /**
     * Creates a notification with heads up
     */
    private void showNotification(int type, String title, String content) {
        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if (mNotificationManager == null) {
            return;
        }

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("default",
                    "DEFAULT",
                    NotificationManager.IMPORTANCE_DEFAULT);
            channel.setDescription("Default Notifications");
            mNotificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(getApplicationContext(), "default")
                .setSmallIcon(R.drawable.ic_notification)
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Heads up
                .setContentTitle(title)
                .setContentText(content)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setAutoCancel(true); // clear notification after click
        Intent intent = new Intent(getApplicationContext(), SplashActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pi);
        // Use type as id, just one notification of each kind
        mNotificationManager.notify(type, mBuilder.build());
    }
}
