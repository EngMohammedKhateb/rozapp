package rozapp.roz.app.Fcm;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;

import android.graphics.Color;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;

import rozapp.roz.app.R;


public class NotificationHelper extends ContextWrapper {
    private NotificationManager manager;
    public static final String PRIMARY_CHANNEL = "default";
    public static final String SECONDARY_CHANNEL = "second";

    /**
     * Registers notification channels, which can be used later by individual notifications.
     *
     * @param ctx The application context
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public NotificationHelper(Context ctx  ) {
        super(ctx);


        NotificationChannel chan1 = new NotificationChannel(PRIMARY_CHANNEL,
                getString(R.string.default_notification_channel_name), NotificationManager.IMPORTANCE_DEFAULT);
        chan1.setLightColor(Color.GREEN);

        chan1.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(chan1);

        NotificationChannel chan2 = new NotificationChannel(SECONDARY_CHANNEL,
                getString(R.string.default_notification_channel_name), NotificationManager.IMPORTANCE_HIGH);

        chan2.setLightColor(Color.BLUE);
        chan2.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
        getManager().createNotificationChannel(chan2);
    }

    /**
     * Get a notification of type 1
     *
     * Provide the builder rather than the notification it's self as useful for making notification
     * changes.
     *
     * @param title the title of the notification
     * @param body the body text for the notification
     * @return the builder as it keeps a reference to the notification (since API 24)
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getNotification1(String title, String body,PendingIntent intent) {


        Uri path = Uri.parse("android.resource://rozapp.roz.app/" + R.raw.message);


        Notification.Builder builder= new Notification.Builder(getApplicationContext(), PRIMARY_CHANNEL)
                .setContentTitle(title)
                 .setContentText(body)
                .setSmallIcon(getSmallIcon(title))
                .setAutoCancel(true)
                .setSound(path)
                .setContentIntent(intent);

         return builder;


    }

    /**
     * Build notification for secondary channel.
     *
     * @param title Title for notification.
     * @param body Message for notification.
     * @return A Notification.Builder configured with the selected channel and details
     */
    @RequiresApi(api = Build.VERSION_CODES.O)
    public Notification.Builder getNotification2(String title, String body,PendingIntent intent) {

        Uri path = Uri.parse("android.resource://rozapp.roz.app/" + R.raw.message);


        return new Notification.Builder(getApplicationContext(), SECONDARY_CHANNEL)
                .setContentTitle(title)
                .setContentText(body)
                .setSmallIcon(getSmallIcon(title))
                .setAutoCancel(true)
                .setSound(path)
                .setContentIntent(intent);
    }

    /**
     * Send a notification.
     *
     * @param id The ID of the notification
     * @param notification The notification object
     */
    public void notify(int id, Notification.Builder notification) {
        getManager().notify(id, notification.build());
    }

    /**
     * Get the small icon for this app
     *
     * @return The small icon resource id
     */
    private int getSmallIcon(String title) {


            return  R.mipmap.ic_launcher;


    }

    /**
     * Get the notification manager.
     *
     * Utility method as this helper works with it a lot.
     *
     * @return The system service NotificationManager
     */
    private NotificationManager getManager() {
        if (manager == null) {
            manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        }
        return manager;
    }
}